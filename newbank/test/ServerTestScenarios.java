package newbank.test;

import newbank.server.NewBank;

import static newbank.test.NBUnit.AssertEqual;
import static newbank.test.NBUnit.runServerCommand;

// How to implement test:
// 1. Define a class
// 2. Define a test method which:
//    has newbank.test.NBUnit.Test annotation
//    has no parameters
//    return value type is void
public class ServerTestScenarios {

  @newbank.test.NBUnit.Test
  private void showMyAccountsReturnsListOfAllCustomersAccountsAlongWithCurrentBalance() {

    String bhagy =
        NewBank.getBank()
            .processRequest(NewBank.getBank().checkLogInDetails("Bhagy", "1"), "SHOWMYACCOUNTS");

    newbank.test.NBUnit.AssertEqual(
        "Current Account: Main 1: 1000.00 GBP" + System.lineSeparator(), bhagy);

    String christina =
        NewBank.getBank()
            .processRequest(
                NewBank.getBank().checkLogInDetails("Christina", "2"), "SHOWMYACCOUNTS");

    newbank.test.NBUnit.AssertEqual(
        "Savings Account: Savings 1: 1500.00 GBP" + System.lineSeparator(), christina);
  }

  @newbank.test.NBUnit.Test
  private void createNewAccountWithOnlyAccountNameReturnsSuccess() {
    String john =
        NewBank.getBank()
            .processRequest(
                NewBank.getBank().checkLogInDetails("John", "3"),
                "NEWACCOUNT \"Savings Account\" Saving");

    newbank.test.NBUnit.AssertEqual(
        "SUCCESS: Opened account TYPE:\"Savings Account\" NAME:\"Saving\" CURRENCY:GBP", john);
  }

  @newbank.test.NBUnit.Test
  private void createNewAccountWithAccountNameAndAcceptedCurrencyReturnsSuccess() {
    String john =
        NewBank.getBank()
            .processRequest(
                NewBank.getBank().checkLogInDetails("John", "3"),
                "NEWACCOUNT \"Savings Account\" Travel eur");

    newbank.test.NBUnit.AssertEqual(
        "SUCCESS: Opened account TYPE:\"Savings Account\" NAME:\"Travel\" CURRENCY:EUR", john);
  }

  @newbank.test.NBUnit.Test
  private void createNewAccountWithWrongCurrencyReturnsFailWithMessage() {
    String christina =
        NewBank.getBank()
            .processRequest(
                NewBank.getBank().checkLogInDetails("Christina", "2"),
                "NEWACCOUNT \"Savings Account\" Other sar");

    newbank.test.NBUnit.AssertEqual(
        "FAIL: Currency not allowed. Accepted currencies: GBP, EUR, USD ", christina);
  }

  @newbank.test.NBUnit.Test
  private void testLoginSequence() {

    String userName = "Bhagy";
    String password = "1";

    String outputString = runServerCommand(userName, password, "");

    // todo: refactor to improve maintanability
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
            + "SHOWMYACCOUNTS -> Lists all of your active accounts."
            + "\n"
            + "NEWACCOUNT <account type> <optional: account name> <optional: currency> \n"
            + "-> Creates a new account of specified type e.g. NEWACCOUNT \"Savings Account\" \"my savings\" EUR. \n"
            + "   Standard currency is GBP, please specify an account name and currency to create an account with a different currency."
            + "\n"
            + "VIEWACCOUNTTYPE <account type> -> Prints details of specified account type e.g. VIEWACCOUNTTYPE \"Cash ISA\"."
            + "\n"
            + "HELP / COMMANDS -> Show command list."
            + "\n"
            + "LOGOUT -> Ends the current banking session and logs you out of NewBank."
            + System.lineSeparator();

    AssertEqual(initialResponse, outputString);
  }
}
