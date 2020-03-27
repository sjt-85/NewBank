package newbank.test;

import newbank.server.Commands.*;
import newbank.server.NewBank;

import java.util.Objects;

import static newbank.test.NBUnit.AssertEqual;
import static newbank.test.NBUnit.runServerCommand;

// How to implement test:
// 1. Define a class
// 2. Define a test method which:
//    has newbank.test.NBUnit.Test annotation
//    has no parameters
//    return value type is void
public class ServerTestScenarios {

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

  @NBUnit.Test
  private void viewAccountTypeReturnDescriptionOfAccountType() {

    var command = new ViewAccountTypeCommand();

    var cashISA =
        command.run(
            NewBankCommandParameter.create(
                NewBank.getBank().checkLogInDetails("Bhagy", "1"), "VIEWACCOUNTTYPE \"Cash ISA\""));

    NBUnit.AssertEqual(
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

    var currentAccount =
        command.run(
            NewBankCommandParameter.create(
                NewBank.getBank().checkLogInDetails("Bhagy", "1"),
                "VIEWACCOUNTTYPE \"Current Account\""));

    NBUnit.AssertEqual(
        buildAccountTypeString(
            "Current Account", 0.25, 0, null, 250, 0, "No additional features", "GBP"),
        currentAccount.getDescription());
  }

  @NBUnit.Test
  private void showMyAccountsReturnsListOfAllCustomersAccountsAlongWithCurrentBalance() {

    var command = new ShowMyAccountsCommand();

    var bhagy =
        command.run(
            Objects.requireNonNull(
                NewBankCommandParameter.create(
                    NewBank.getBank().checkLogInDetails("Bhagy", "1"), "SHOWMYACCOUNTS")));

    NBUnit.AssertEqual(
        "Current Account: Main 1: 1000.00 GBP" + System.lineSeparator(), bhagy.getDescription());

    var christina =
        command.run(
            Objects.requireNonNull(
                NewBankCommandParameter.create(
                    NewBank.getBank().checkLogInDetails("Christina", "2"), "SHOWMYACCOUNTS")));

    NBUnit.AssertEqual(
        "Savings Account: Savings 1: 1500.00 GBP" + System.lineSeparator(),
        christina.getDescription());
  }

  @NBUnit.Test
  private void createNewAccountWithOnlyAccountNameReturnsSuccess() {
    var id = NewBank.getBank().checkLogInDetails("John", "3");

    var command = new NewAccountCommand();

    NewBankCommandResponse response =
        command.run(NewBankCommandParameter.create(id, "NEWACCOUNT \"Savings Account\" Saving"));

    AssertEqual(NewBankCommandResponse.ResponseType.Succeeded, response.getType());

    NBUnit.AssertEqual(
        "SUCCESS: Opened account TYPE:\"Savings Account\" NAME:\"Saving\" CURRENCY:GBP",
        response.getDescription());
  }

  @NBUnit.Test
  private void createNewAccountWithAccountNameAndAcceptedCurrencyReturnsSuccess() {
    var id = NewBank.getBank().checkLogInDetails("John", "3");

    var command = new NewAccountCommand();

    NewBankCommandResponse response =
        command.run(
            NewBankCommandParameter.create(id, "NEWACCOUNT \"Savings Account\" Travel eur"));

    AssertEqual(NewBankCommandResponse.ResponseType.Succeeded, response.getType());

    NBUnit.AssertEqual(
        "SUCCESS: Opened account TYPE:\"Savings Account\" NAME:\"Travel\" CURRENCY:EUR",
        response.getDescription());
  }

  @NBUnit.Test
  private void createNewAccountWithWrongCurrencyReturnsFailWithMessage() {

    var id = NewBank.getBank().checkLogInDetails("Christina", "2");

    var command = new NewAccountCommand();

    NewBankCommandResponse response =
        command.run(NewBankCommandParameter.create(id, "NEWACCOUNT \"Savings Account\" Other sar"));

    NBUnit.AssertEqual(
        "FAIL: Currency not allowed. Accepted currencies: GBP, EUR, USD ",
        response.getDescription());
  }

  @NBUnit.Test
  private void userCanLogIn() {

    String userName = "Bhagy";
    String password = "1";

    String outputString = runServerCommand(userName, password, "");

    AssertEqual(initialResponse, outputString);
  }

  @NBUnit.Test
  private void userCanLogOff() {

    String userName = "Bhagy";
    String password = "1";

    String outputString = runServerCommand(userName, password, "LOGOUT");

    AssertEqual(
        initialResponse + "Log out successful. Goodbye Bhagy" + System.lineSeparator(),
        outputString);
  }

  @NBUnit.Test
  private void userCanLongInAndRunCOMMANDW() {

    String userName = "Bhagy";
    String password = "1";

    String outputString = runServerCommand(userName, password, "COMMANDS");

    AssertEqual(initialResponse + commandList, outputString);
  }

  // todo: refactor to improve maintainability
  final String commandList =
      "SHOWMYACCOUNTS -> Lists all of your active accounts."
          + "\n"
          + "NEWACCOUNT <account type> <optional: account name> <optional: currency> \n"
          + "-> Creates a new account of specified type e.g. NEWACCOUNT \"Savings Account\" \"my savings\" EUR. \n"
          + "   Standard currency is GBP, please specify an account name and currency to create an account with a different currency."
          + "\n"
          + "VIEWACCOUNTTYPE <account type> -> Prints details of specified account type e.g. VIEWACCOUNTTYPE \"Cash ISA\"."
          + "\n"
          + "TRANSFER <Account Name>/<Account Name>/<Amount>"
          + "\n"
          + "-> Transfer from the first listed account into the second."
          + "\n"
          + "   To format add \"/\" between accounts and amount eg TRANSFER account 1/account 2/100.0."
          + "\n"
          + "HELP / COMMANDS -> Show command list."
          + "\n"
          + "LOGOUT -> Ends the current banking session and logs you out of NewBank."
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
}
