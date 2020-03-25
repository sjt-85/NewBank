package newbank.test;

import newbank.server.Commands.NewBankCommandParameter;
import newbank.server.Commands.NewBankCommandResponse;
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
    var id = NewBank.getBank().checkLogInDetails("John", "3");

    var command = new newbank.server.Commands.NewAccountCommand();

    newbank.server.Commands.NewBankCommandResponse response =
        command.run(NewBankCommandParameter.create(id, "NEWACCOUNT \"Savings Account\" Saving"));

    AssertEqual(NewBankCommandResponse.ResponseType.Succeeded, response.getType());

    newbank.test.NBUnit.AssertEqual(
        "SUCCESS: Opened account TYPE:\"Savings Account\" NAME:\"Saving\" CURRENCY:GBP",
        response.getDescription());
  }

  @newbank.test.NBUnit.Test
  private void createNewAccountWithAccountNameAndAcceptedCurrencyReturnsSuccess() {
    var id = NewBank.getBank().checkLogInDetails("John", "3");

    var command = new newbank.server.Commands.NewAccountCommand();

    newbank.server.Commands.NewBankCommandResponse response =
        command.run(
            NewBankCommandParameter.create(id, "NEWACCOUNT \"Savings Account\" Travel eur"));

    AssertEqual(NewBankCommandResponse.ResponseType.Succeeded, response.getType());

    newbank.test.NBUnit.AssertEqual(
        "SUCCESS: Opened account TYPE:\"Savings Account\" NAME:\"Travel\" CURRENCY:EUR",
        response.getDescription());
  }

  @newbank.test.NBUnit.Test
  private void createNewAccountWithWrongCurrencyReturnsFailWithMessage() {

    var id = NewBank.getBank().checkLogInDetails("Christina", "2");

    var command = new newbank.server.Commands.NewAccountCommand();

    newbank.server.Commands.NewBankCommandResponse response =
        command.run(NewBankCommandParameter.create(id, "NEWACCOUNT \"Savings Account\" Other sar"));

    newbank.test.NBUnit.AssertEqual(
        "FAIL: Currency not allowed. Accepted currencies: GBP, EUR, USD ",
        response.getDescription());
  }

  @newbank.test.NBUnit.Test
  private void testLoginSequence() {

    String userName = "Bhagy";
    String password = "1";

    String outputString = runServerCommand(userName, password, "");

    // todo: refactor to improve maintainability
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
            + "VIEWACCOUNTTYPE <account type> -> Prints details of specified account type e.g. VIEWACCOUNTTYPE \"Cash ISA\""
            + "\n"
            + "NEWACCOUNT <account type> <optional: account name> <optional: currency> \n"
            + "-> Creates a new account of specified type e.g. NEWACCOUNT \"Savings Account\" \"my savings\" EUR \n"
            + "Standard currency is GBP, please specify an account name and currency to create an account with a different currency."
            + "\n"
            + "SHOWMYACCOUNTS -> Lists all of your active accounts."
            + "\n"
            + "LOGOUT -> Ends the current banking session and logs you out of NewBank."
            + System.lineSeparator();

    AssertEqual(initialResponse, outputString);
  }

  @newbank.test.NBUnit.Test
  private void testLogoffSequence() {

    String userName = "Bhagy";
    String password = "1";

    String outputString = runServerCommand(userName, password, "LOGOUT");

    // todo: refactor to improve maintainability
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
            + "VIEWACCOUNTTYPE <account type> -> Prints details of specified account type e.g. VIEWACCOUNTTYPE \"Cash ISA\""
            + "\n"
            + "NEWACCOUNT <account type> <optional: account name> <optional: currency> \n"
            + "-> Creates a new account of specified type e.g. NEWACCOUNT \"Savings Account\" \"my savings\" EUR \n"
            + "Standard currency is GBP, please specify an account name and currency to create an account with a different currency."
            + "\n"
            + "SHOWMYACCOUNTS -> Lists all of your active accounts."
            + "\n"
            + "LOGOUT -> Ends the current banking session and logs you out of NewBank."
            + System.lineSeparator();

    AssertEqual(
        initialResponse + "Log out successful. Goodbye Bhagy" + System.lineSeparator(),
        outputString);
  }

  @newbank.test.NBUnit.Test
  private void testCommandSequence() {

    String userName = "Bhagy";
    String password = "1";

    String outputString = runServerCommand(userName, password, "COMMANDS");

    // todo: refactor to improve maintainability
    String commandCommandResponse =
        System.lineSeparator()
            + "VIEWACCOUNTTYPE <account type> -> Prints details of specified account type e.g. VIEWACCOUNTTYPE \"Cash ISA\""
            + "\n"
            + "NEWACCOUNT <account type> <optional: account name> <optional: currency> \n"
            + "-> Creates a new account of specified type e.g. NEWACCOUNT \"Savings Account\" \"my savings\" EUR \n"
            + "Standard currency is GBP, please specify an account name and currency to create an account with a different currency."
            + "\n"
            + "SHOWMYACCOUNTS -> Lists all of your active accounts."
            + "\n"
            + "LOGOUT -> Ends the current banking session and logs you out of NewBank.";

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
            + commandCommandResponse
            + commandCommandResponse
            + System.lineSeparator();

    AssertEqual(initialResponse, outputString);
  }
}
