package newbank.test;

import newbank.server.NewBank;

// How to implement test:
// 1. Define a class
// 2. Define a test method which:
//    has newbank.test.NBUnit.Test annotation
//    has no parameters
//    return value type is void
public class ServerTestScenarios {

  @newbank.test.NBUnit.Test
  private void ShowMyAccountsReturnsListOfAllCustomersAccountsAlongWithCurrentBalance() {

    String bhagy =
        NewBank.getBank()
            .processRequest(NewBank.getBank().checkLogInDetails("Bhagy", "1"), "SHOWMYACCOUNTS");

    newbank.test.NBUnit.AssertEqual("Main: 1000.00 GBP" + System.lineSeparator(), bhagy);

    String christina =
        NewBank.getBank()
            .processRequest(
                NewBank.getBank().checkLogInDetails("Christina", "2"), "SHOWMYACCOUNTS");

    newbank.test.NBUnit.AssertEqual("Savings: 1500.00 GBP" + System.lineSeparator(), christina);
  }

  @newbank.test.NBUnit.Test
  private void createNewAccountWithOnlyAccountNameReturnsSuccess() {
    String john =
            NewBank.getBank()
                    .processRequest(NewBank.getBank().checkLogInDetails("John", "3"), "NEWACCOUNT Savings");

    newbank.test.NBUnit.AssertEqual("SUCCESS", john);
  }

  @newbank.test.NBUnit.Test
  private void createNewAccountWithAccountNameAndAcceptedCurrencyReturnsSuccess() {
    String john =
            NewBank.getBank()
                    .processRequest(NewBank.getBank().checkLogInDetails("John", "3"), "NEWACCOUNT Travel eur");

    newbank.test.NBUnit.AssertEqual("SUCCESS", john);
  }

  @newbank.test.NBUnit.Test
  private void createNewAccountWithWrongCurrencyReturnsFailWithMessage() {
    String christina =
            NewBank.getBank()
                    .processRequest(
                            NewBank.getBank().checkLogInDetails("Christina", "2"), "NEWACCOUNT Other sar");

    newbank.test.NBUnit.AssertEqual("FAIL: Currency not allowed. Accepted currencies: GBP, EUR, USD ", christina);
  }


}
