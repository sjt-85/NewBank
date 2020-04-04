package newbank.test;

import newbank.server.Commands.INewBankCommand;
import newbank.server.Commands.NewAccountCommand;
import newbank.server.Commands.NewBankCommand;
import newbank.server.Commands.NewBankCommandRequest;
import newbank.server.Commands.NewBankCommandResponse;
import newbank.server.Commands.PayCommand;
import newbank.server.Commands.ShowMyAccountsCommand;
import newbank.server.Commands.ViewAccountTypeCommand;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.NewBankServer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static newbank.test.NBUnit.Assert;
import static newbank.test.NBUnit.AssertEqual;
import static newbank.test.NBUnit.Setup;
import static newbank.test.NBUnit.Test;
import static newbank.test.NBUnit.buildInputStream;
import static newbank.test.NBUnit.runServerCommand;

// How to implement test:
// 1. Define a class
// 2. Define a test method which:
//    has newbank.test.NBUnit.Test annotation
//    has no parameters
//    return value type is void
public class ServerTestScenarios {

  private final CustomerID bhagy = NewBank.getBank().checkLogInDetails("Bhagy", "1");
  private final CustomerID christina = NewBank.getBank().checkLogInDetails("Christina", "2");
  private final CustomerID john = NewBank.getBank().checkLogInDetails("John", "3");

  @Setup
  private void setup() {
    // reset data for each test to avoid conflicts between test methods.
    NewBank.getBank().LoadData();
  }

  @Test
  private void commandCanShowPrompt() {

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
        // this is a sample sequence of a user interaction inside a command

        // you can ask a question to a user by providing a message
        String stringResponse = response.query("What are you doing?");
        // and get a string response from the user.
        AssertEqual("I'm coding.", stringResponse);

        // you can also ask a close-ended question to a user
        boolean boolResponse = response.confirm("Do you finish the feature?");
        // and get a boolean response.
        AssertEqual(false, boolResponse);

        response.succeeded("Good luck!");
      }
    }

    var out = new ByteArrayOutputStream();

    runServerCommand(
        buildInputStream("Bhagy", "1", "PROMPTSTUB", "I'm coding.", "N"),
        out,
        new INewBankCommand[] {new PromptStub()});

    AssertEqual(
        initialResponseCore
            + "> PROMPTSTUB"
            + System.lineSeparator()
            + "> HELP / COMMANDS -> Show command list."
            + System.lineSeparator()
            + "> LOGOUT -> Ends the current banking session and logs you out of NewBank."
            + System.lineSeparator()
            + System.lineSeparator()
            + "Append -?, -h or -help for command description e.g. \"NEWACCOUNT -help\"."
            + System.lineSeparator()
            + "What are you doing? :"
            + System.lineSeparator()
            + "Do you finish the feature? [Y]es/[N]o :"
            + System.lineSeparator()
            + "Good luck!"
            + System.lineSeparator(),
        out.toString());
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
    command.run(NewBankCommandRequest.create(bhagy, "VIEWACCOUNTTYPE \"Cash ISA\""), cashISA);

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
        NewBankCommandRequest.create(bhagy, "VIEWACCOUNTTYPE \"Current Account\""), currentAccount);

    AssertEqual(
        buildAccountTypeString(
            "Current Account", 0.25, 0, null, 250, 0, "No additional features", "GBP"),
        currentAccount.getDescription());
  }

  @Test
  private void userWithSingleAccountCanPay() {

    // check initial state
    validateShowMyAccount(bhagy, "Current Account", 1, "Main 1", 1000);
    validateShowMyAccount(christina, "Savings Account", 2, "Savings 1", 1500);

    // run
    NewBankCommandResponse response = runPay(bhagy, "PAY 2 100", "Y");

    // validate
    AssertEqual(
        "PAY Successful"
            + System.lineSeparator()
            + "You have made a payment of £100.00 to the Account(Number:002 Type:[Savings Account] Name:\"Savings 1\")"
            + System.lineSeparator()
            + "Your new balance is: £900.00",
        response.getDescription());

    validateShowMyAccount(bhagy, "Current Account", 1, "Main 1", 900);
    validateShowMyAccount(christina, "Savings Account", 2, "Savings 1", 1600);
  }

  @Test
  private void userCanCancelToPay() {

    // check initial state
    validateShowMyAccount(bhagy, "Current Account", 1, "Main 1", 1000);
    validateShowMyAccount(christina, "Savings Account", 2, "Savings 1", 1500);

    // run
    NewBankCommandResponse response = runPay(bhagy, "PAY 2 100", "N");

    // validate
    AssertEqual("FAIL", response.getDescription());

    validateShowMyAccount(bhagy, "Current Account", 1, "Main 1", 1000);
    validateShowMyAccount(christina, "Savings Account", 2, "Savings 1", 1500);
  }

  @Test
  private void userWithMultipleAccountsCanPay() {

    // check initial state
    validateShowMyAccount(john, "Savings Account", 4, "Saving 1", 500);
    validateShowMyAccount(christina, "Savings Account", 2, "Savings 1", 1500);

    String inputSequence = "Saving 1" + System.lineSeparator() + "Y" + System.lineSeparator();

    // run
    NewBankCommandResponse response = runPay(john, "PAY 2 100", inputSequence);

    // validate
    AssertEqual(
        "PAY Successful"
            + System.lineSeparator()
            + "You have made a payment of £100.00 to the Account(Number:002 Type:[Savings Account] Name:\"Savings 1\")"
            + System.lineSeparator()
            + "Your new balance is: £400.00",
        response.getDescription());

    validateShowMyAccount(john, "Savings Account", 4, "Saving 1", 400);
    validateShowMyAccount(christina, "Savings Account", 2, "Savings 1", 1600);
  }

  private static NewBankCommandResponse runPay(
      CustomerID customerID, String commandString, String inputSequence) {

    var command = new PayCommand();

    var response =
        new NewBankCommandResponse(
            new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(inputSequence.getBytes()))),
            new PrintWriter(new ByteArrayOutputStream()));

    command.run(NewBankCommandRequest.create(customerID, commandString), response);

    return response;
  }

  @Test
  private void showMyAccountsReturnsListOfAllCustomersAccountsAlongWithCurrentBalance() {

    validateShowMyAccount(bhagy, "Current Account", 1, "Main 1", 1000);

    validateShowMyAccount(christina, "Savings Account", 2, "Savings 1", 1500);
  }

  private void validateShowMyAccount(
      CustomerID customerID,
      String accountType,
      int accountNumber,
      String accountName,
      double balance) {

    var command = new ShowMyAccountsCommand();

    var response = new NewBankCommandResponse();

    command.run(
        Objects.requireNonNull(NewBankCommandRequest.create(customerID, "SHOWMYACCOUNTS")),
        response);

    String accountDescription =
        String.format("%s: %s (%03d): %.2f GBP", accountType, accountName, accountNumber, balance);

    var lines = response.getDescription().split(System.lineSeparator());

    Assert(
        Arrays.asList(lines).contains(accountDescription),
        response.getDescription()
            + System.lineSeparator()
            + " doesn't contain "
            + System.lineSeparator()
            + accountDescription);
  }

  @Test
  private void createNewAccountGenerateUniqueAccountNumber() {

    var originalKeys =
        NewBank.getBank().getAccounts().keySet().stream().collect(Collectors.toSet());

    var command = new NewAccountCommand();

    NewBankCommandResponse response = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(john, "NEWACCOUNT \"Savings Account\" UniqueAccountNumber"),
        response);

    AssertEqual(NewBankCommandResponse.ResponseType.SUCCEEDED, response.getType());

    Assert(
        !originalKeys.contains(
            NewBank.getBank().getAccounts().values().stream()
                .filter(account -> account.getAccountName().equals("UniqueAccountNumber"))
                .findFirst()
                .get()
                .getAccountNumber()));
  }

  @Test
  private void createNewAccountWithOnlyAccountNameReturnsSuccess() {

    var command = new NewAccountCommand();

    NewBankCommandResponse response = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(john, "NEWACCOUNT \"Savings Account\" Saving"), response);

    AssertEqual(NewBankCommandResponse.ResponseType.SUCCEEDED, response.getType());

    AssertEqual(
        "SUCCESS: Opened account TYPE:\"Savings Account\" NAME:\"Saving\" CURRENCY:GBP",
        response.getDescription());
  }

  @Test
  private void createNewAccountWithAccountNameAndAcceptedCurrencyReturnsSuccess() {

    var command = new NewAccountCommand();

    NewBankCommandResponse response = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(john, "NEWACCOUNT \"Savings Account\" Travel eur"), response);

    AssertEqual(NewBankCommandResponse.ResponseType.SUCCEEDED, response.getType());

    AssertEqual(
        "SUCCESS: Opened account TYPE:\"Savings Account\" NAME:\"Travel\" CURRENCY:EUR",
        response.getDescription());
  }

  @Test
  private void createNewAccountWithWrongCurrencyReturnsFailWithMessage() {

    var command = new NewAccountCommand();

    NewBankCommandResponse response = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(christina, "NEWACCOUNT \"Savings Account\" Other sar"),
        response);

    AssertEqual(
        "FAIL: Currency not allowed. Accepted currencies: GBP, EUR, USD.",
        response.getDescription());
  }

  @Test
  private void createNewAccountWithDuplicateNameThenIncorrectType() {

    var command = new NewAccountCommand();

    NewBankCommandResponse response = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(christina, "NEWACCOUNT \"Savings Account\" \"Savings 1\""),
        response);

    AssertEqual("FAIL: Please choose a unique name.", response.getDescription());

    NewBankCommandResponse response2 = new NewBankCommandResponse();
    command.run(
        NewBankCommandRequest.create(christina, "NEWACCOUNT \"Saving Account\" \"Savings 1\""),
        response2);

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

    String outputString = runServerCommand("Bhagy", "1", "COMMANDS");

    AssertEqual(initialResponse + commandList, outputString);
  }

  @Test
  private void userTriesToMoveWithBadArgument() {

    String userName = "John";
    String password = "3";

    // Test 1
    String outputString =
        runServerCommand(userName, password, "MOVE 200 \"Checking 1\" \"Checking 2\"");
    AssertEqual(
        initialResponse
            + "Account to be credited does not exist. Please try again."
            + System.lineSeparator(),
        outputString);

    // Test 2
    String outputString2 =
        runServerCommand(userName, password, "MOVE 200 \"Checking 2\" \"Checking 1\"");
    AssertEqual(
        initialResponse
            + "Account to be debited does not exist. Please try again."
            + System.lineSeparator(),
        outputString2);

    // Test 3
    String outputString3 =
        runServerCommand(userName, password, "MOVE 200 \"Checking 1\" \"Checking 1\"");
    AssertEqual(
        initialResponse
            + "The debiting and crediting accounts are the same. Please try again."
            + System.lineSeparator(),
        outputString3);

    // Test 4
    String outputString4 =
        runServerCommand(userName, password, "MOVE -200 \"Saving 1\" \"Checking 1\"");
    AssertEqual(
        initialResponse + "Amount is invalid. Please try again." + System.lineSeparator(),
        outputString4);

    // Test 5
    String outputString5 =
        runServerCommand(userName, password, "MOVE /t \"Saving 1\" \"Checking 1\"");
    AssertEqual(
        initialResponse
            + "Not enough arguments. Please try again."
            + System.lineSeparator()
            + System.lineSeparator()
            + "MOVE "
            + commandDescriptions.get("MOVE")
            + System.lineSeparator(),
        outputString5);

    // Test 6
    String outputString6 = runServerCommand(userName, password, "MOVE \"Saving 1\" \"Checking 1\"");
    AssertEqual(
        initialResponse
            + "Not enough arguments. Please try again."
            + System.lineSeparator()
            + System.lineSeparator()
            + "MOVE "
            + commandDescriptions.get("MOVE")
            + System.lineSeparator(),
        outputString6);
  }

  // separate from bad arguments in case overdraft functionality added
  @Test
  private void userTriesToMoveWithoutEnoughFunds() {

    String outputString = runServerCommand("John", "3", "MOVE 500.01 \"Saving 1\" \"Checking 1\"");
    AssertEqual(
        initialResponse
            + "Not enough funds in account to be debited. Please try again."
            + System.lineSeparator(),
        outputString);
  }

  @Test
  private void userSuccessfullyMoves() {

    String outputString = runServerCommand("John", "3", "MOVE 321.62 \"Saving 1\" \"Checking 1\"");
    AssertEqual(
        initialResponse
            + "Move successful."
            + System.lineSeparator()
            + "The balance of Checking 1 is now 571.62."
            + System.lineSeparator()
            + "The balance of Saving 1 is now 178.38."
            + System.lineSeparator(),
        outputString);
  }

  final String commandList =
      Arrays.stream(NewBankServer.DefaultCommandList)
              .map(INewBankCommand::getCommandName)
              .reduce(
                  "",
                  (acc, cmd) ->
                      acc + (acc.length() == 0 ? "" : System.lineSeparator()) + "> " + cmd)
          + System.lineSeparator()
          + "> HELP / COMMANDS -> Show command list."
          + System.lineSeparator()
          + "> LOGOUT -> Ends the current banking session and logs you out of NewBank."
          + System.lineSeparator()
          + System.lineSeparator()
          + "Append -?, -h or -help for command description e.g. \"NEWACCOUNT -help\"."
          + System.lineSeparator();

  public static final String initialResponseCore =
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
          + System.lineSeparator();

  final String initialResponse = initialResponseCore + commandList;

  final Map<String, String> commandDescriptions =
      Arrays.stream(NewBankServer.DefaultCommandList)
          .collect(
              Collectors.toMap(INewBankCommand::getCommandName, INewBankCommand::getDescription));
}
