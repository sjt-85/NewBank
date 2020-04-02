package newbank.test;

import newbank.server.Commands.*;
import newbank.server.NewBank;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Objects;

import static newbank.test.NBUnit.*;

// How to implement test:
// 1. Define a class
// 2. Define a test method which:
//    has newbank.test.NBUnit.Test annotation
//    has no parameters
//    return value type is void

public class ServerTestScenarios {

  class PromptStub extends NewBankCommand {
    @Override
    public String getCommandName() {
      return "PROMPTSTUB";
    }

    @Override
    public String getDescription() {
      return "";
    }

    @Override
    public void run(NewBankCommandRequest request, NewBankCommandResponse response) {

      Assert(false);
      response.createSucceeded("");
    }
  }

  @Test
  private void commandCanShowPrompt() {
    String userName = "Bhagy";
    String password = "1";

    var out = new ByteArrayOutputStream();

    runServerCommand(
        buildInputStream(userName, password, "PROMPTSTUB", "Y"),
        out,
        new INewBankCommand[] {new PromptStub()});
  }

  private static String buildAccountTypeString(
      String type,
      double interestRate,
      int termLength,
      String withdrawalLimit,
      double overdraft,
      double fee,
      final String otherFeatures,
      String currency) {

    return String.format("Account Type: %s", type)
        + System.lineSeparator()
        + String.format("Interest Rate (%%): %.2f", interestRate)
        + System.lineSeparator()
        + String.format("Overdraft Limit (%s): %.2f", "GBP", overdraft)
        + System.lineSeparator()
        + String.format("Fee (%s/month): %.2f", currency, fee)
        + System.lineSeparator()
        + String.format("Term (months): %d", termLength)
        + System.lineSeparator()
        + (withdrawalLimit == null ? "" : "> " + withdrawalLimit + System.lineSeparator())
        + "> "
        + otherFeatures;
  }

  @Test
  private void viewAccountTypeReturnDescriptionOfAccountType() {

    var command = new ViewAccountTypeCommand();

    var cashISA = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(
            NewBank.getBank().checkLogInDetails("Bhagy", "1"), "VIEWACCOUNTTYPE \"Cash ISA\""),
        cashISA);

    AssertEqual(
        buildAccountTypeString(
            "Cash ISA",
            2.25,
            0,
            "No withdrawal limit, but may affect tax-free allowance",
            0.00,
            0.00,
            "Tax-free up to £20,000 allowance",
            "GBP"),
        cashISA.getDescription());

    var currentAccount = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(
            NewBank.getBank().checkLogInDetails("Bhagy", "1"),
            "VIEWACCOUNTTYPE \"Current Account\""),
        currentAccount);

    AssertEqual(
        buildAccountTypeString(
            "Current Account", 0.25, 0, null, 250, 0, "No additional features", "GBP"),
        currentAccount.getDescription());
  }

  @Test
  private void showMyAccountsReturnsListOfAllCustomersAccountsAlongWithCurrentBalance() {

    var command = new ShowMyAccountsCommand();

    var bhagy = new NewBankCommandResponse();
    command.run(
        Objects.requireNonNull(
            NewBankCommandRequest.create(
                NewBank.getBank().checkLogInDetails("Bhagy", "1"), "SHOWMYACCOUNTS")),
        bhagy);

    AssertEqual(
        "Current Account: Main 1: 1000.00 GBP" + System.lineSeparator(), bhagy.getDescription());

    var christina = new NewBankCommandResponse();
    command.run(
        Objects.requireNonNull(
            NewBankCommandRequest.create(
                NewBank.getBank().checkLogInDetails("Christina", "2"), "SHOWMYACCOUNTS")),
        christina);

    AssertEqual(
        "Savings Account: Savings 1: 1500.00 GBP" + System.lineSeparator(),
        christina.getDescription());
  }

  @Test
  private void createNewAccountWithOnlyAccountNameReturnsSuccess() {

    var id = NewBank.getBank().checkLogInDetails("John", "3");

    var command = new NewAccountCommand();

    NewBankCommandResponse response = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(id, "NEWACCOUNT \"Savings Account\" Saving"), response);

    AssertEqual(NewBankCommandResponse.ResponseType.SUCCEEDED, response.getType());

    AssertEqual(
        "SUCCESS: Opened account TYPE:\"Savings Account\" NAME:\"Saving\" CURRENCY:GBP",
        response.getDescription());
  }

  @Test
  private void createNewAccountWithAccountNameAndAcceptedCurrencyReturnsSuccess() {

    var id = NewBank.getBank().checkLogInDetails("John", "3");

    var command = new NewAccountCommand();

    NewBankCommandResponse response = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(id, "NEWACCOUNT \"Savings Account\" Travel eur"), response);

    AssertEqual(NewBankCommandResponse.ResponseType.SUCCEEDED, response.getType());

    AssertEqual(
        "SUCCESS: Opened account TYPE:\"Savings Account\" NAME:\"Travel\" CURRENCY:EUR",
        response.getDescription());
  }

  @Test
  private void createNewAccountWithWrongCurrencyReturnsFailWithMessage() {

    var id = NewBank.getBank().checkLogInDetails("Christina", "2");

    var command = new NewAccountCommand();

    NewBankCommandResponse response = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(id, "NEWACCOUNT \"Savings Account\" Other sar"), response);

    AssertEqual(
        "FAIL: Currency not allowed. Accepted currencies: GBP, EUR, USD.",
        response.getDescription());
  }

  @Test
  private void createNewAccountWithDuplicateNameThenIncorrectType() {

    var id = NewBank.getBank().checkLogInDetails("Christina", "2");

    var command = new NewAccountCommand();

    NewBankCommandResponse response = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(id, "NEWACCOUNT \"Savings Account\" \"Savings 1\""), response);

    AssertEqual("FAIL: Please choose a unique name.", response.getDescription());

    NewBankCommandResponse response2 = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(id, "NEWACCOUNT \"Saving Account\" \"Savings 1\""), response2);

    AssertEqual(
        "FAIL: Account type must be specified. Accepted account types: Current Account, Savings Account, Cash ISA.",
        response2.getDescription());
  }

  @Test
  private void userCanLogIn() {

    String userName = "Bhagy";

    String password = "1";

    String outputString = runServerCommand(userName, password, "");

    AssertEqual(initialResponse, outputString);
  }

  @Test
  private void userCanLogOff() {

    String userName = "Bhagy";

    String password = "1";

    String outputString = runServerCommand(userName, password, "LOGOUT");

    AssertEqual(
        initialResponse + "Log out successful. Goodbye Bhagy" + System.lineSeparator(),
        outputString);
  }

  @Test
  private void userCanLongInAndRunCOMMANDW() {

    String userName = "Bhagy";

    String password = "1";

    String outputString = runServerCommand(userName, password, "COMMANDS");

    AssertEqual(initialResponse + commandList, outputString);
  }

  @Test
  private void userTriesToTransferWithBadArgument() {

    String userName = "John";

    String password = "3";

    // Test 1

    String outputString =
        runServerCommand(userName, password, "TRANSFER Checking 1/Checking 2/200");

    AssertEqual(
        initialResponse
            + "Account to be credited does not exist. Please try again."
            + System.lineSeparator(),
        outputString);

    // Test 2

    String outputString2 =
        runServerCommand(userName, password, "TRANSFER Checking 2/Checking 1/200");

    AssertEqual(
        initialResponse
            + "Account to be debited does not exist. Please try again."
            + System.lineSeparator(),
        outputString2);

    // Test 3

    String outputString3 =
        runServerCommand(userName, password, "TRANSFER \"Checking 1\"/\"Checking 1\"/100");

    AssertEqual(
        initialResponse
            + "The debiting and crediting accounts are the same. Please try again."
            + System.lineSeparator(),
        outputString3);

    // Test 4

    String outputString4 =
        runServerCommand(userName, password, "TRANSFER Saving 1/Checking 1/-100");

    AssertEqual(
        initialResponse + "Amount is invalid. Please try again." + System.lineSeparator(),
        outputString4);

    // Test 5

    String outputString5 =
        runServerCommand(userName, password, "TRANSFER \"Saving 1\"/\"Checking 1\"/t");

    AssertEqual(
        initialResponse + "Amount is invalid. Please try again." + System.lineSeparator(),
        outputString5);

    // Test 6

    String outputString6 = runServerCommand(userName, password, "TRANSFER Saving 1/Checking 1");

    AssertEqual(
        initialResponse
            + "Not enough arguments. Please try again."
            + System.lineSeparator()
            + System.lineSeparator()
            + "TRANSFER "
            + commandDescriptions.get("TRANSFER")
            + System.lineSeparator(),
        outputString6);
  }

  // separate from bad arguments in case overdraft functionality added

  @Test
  private void userTriesToTransferWithoutEnoughFunds() {

    String userName = "John";

    String password = "3";

    String outputString =
        runServerCommand(userName, password, "TRANSFER Saving 1/Checking 1/500.01");

    AssertEqual(
        initialResponse
            + "Not enough funds in account to be debited. Please try again."
            + System.lineSeparator(),
        outputString);
  }

  @Test
  private void userSuccessfullyTransfers() {

    String userName = "John";

    String password = "3";

    String outputString =
        runServerCommand(userName, password, "TRANSFER Saving 1/Checking 1/321.62");

    AssertEqual(
        initialResponse
            + "Transfer successful."
            + System.lineSeparator()
            + "The balance of Checking 1 is now 571.62."
            + System.lineSeparator()
            + "The balance of Saving 1 is now 178.38."
            + System.lineSeparator(),
        outputString);
  }

  // todo: refactor to improve maintainability

  final String commandList =
      "> SHOWMYACCOUNTS"
          + System.lineSeparator()
          + "> NEWACCOUNT"
          + System.lineSeparator()
          + "> VIEWACCOUNTTYPE"
          + System.lineSeparator()
          + "> TRANSFER"
          + System.lineSeparator()
          + "> HELP / COMMANDS -> Show command list."
          + System.lineSeparator()
          + "> LOGOUT -> Ends the current banking session and logs you out of NewBank."
          + System.lineSeparator()
          + System.lineSeparator()
          + "Append -?, -h or -help for command description e.g. \"NEWACCOUNT -help\"."
          + System.lineSeparator();

  final String initialResponse =
      "Enter Username"
          + System.lineSeparator()
          + "Enter Password"
          + System.lineSeparator()
          + "Checking Details..."
          + System.lineSeparator()
          + "Log In Successful. What do you want to do?"
          + System.lineSeparator()
          + System.lineSeparator()
          + "COMMANDS:"
          + System.lineSeparator()
          + commandList;

  final Map<String, String> commandDescriptions =
      Map.of(
          "SHOWMYACCOUNTS",
          "-> Lists all of your active accounts.",
          "NEWACCOUNT",
          "<account type> <optional: account name> <optional: currency> "
              + System.lineSeparator()
              + "-> Creates a new account of specified type e.g. NEWACCOUNT \"Savings Account\" \"my savings\" EUR. "
              + System.lineSeparator()
              + "   Standard currency is GBP, please specify an account name and currency to create an account with a different currency.",
          "VIEWACCOUNTTYPE",
          "<account type> -> Prints details of specified account type e.g. VIEWACCOUNTTYPE \\\"Cash ISA\\\".",
          "TRANSFER",
          "<Account Name>/<Account Name>/<Amount>"
              + System.lineSeparator()
              + "-> Transfer from the first listed account into the second."
              + System.lineSeparator()
              + "   To format add \"/\" between accounts and amount eg TRANSFER account 1/account 2/100.0.");
}
