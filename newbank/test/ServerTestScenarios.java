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

    newbank.test.NBUnit.AssertEqual("Main: 1000.0" + System.lineSeparator(), bhagy);

    String christina =
        NewBank.getBank()
            .processRequest(
                NewBank.getBank().checkLogInDetails("Christina", "2"), "SHOWMYACCOUNTS");

    newbank.test.NBUnit.AssertEqual("Savings: 1500.0" + System.lineSeparator(), christina);
  }
}
