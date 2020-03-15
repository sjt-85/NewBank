package newbank.test;

import newbank.server.NewBank;

// How to implement test:
// 1. Define a class
// 2. Define test methods where:
//    accessibility is public
//    no parameters
//    return value type is void
// 3. register the test class in the runInTestMode method.
public class ServerTestScenarios {

  public void ShowMyAccountsReturnsListOfAllCustomersAccountsAlongWithCurrentBalance() {

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
