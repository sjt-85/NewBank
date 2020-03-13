package newbank.test;

import newbank.server.NewBank;

// How to make test:
// 1. Define a class
// 2. Define public methods without parameters and a return value, which implement tests. All the public methods are invoked by NBUnit.
// 3. register the test class the runInTestMode method.
public class ServerTestScenarios {

  public void ShowMyAccountsReturnsListOfAllCustomersAccountsAlongWithCurrentBalance() {

    String bhagy = NewBank.getBank().processRequest(
            NewBank.getBank().checkLogInDetails("Bhagy", null),
            "SHOWMYACCOUNTS");

    newbank.test.NBUnit.AssertEqual("Main: 1000.0" + System.lineSeparator(), bhagy);

    String christina = NewBank.getBank().processRequest(
            NewBank.getBank().checkLogInDetails("Christina", null),
            "SHOWMYACCOUNTS");

    newbank.test.NBUnit.AssertEqual("Savings: 1500.0" + System.lineSeparator(), christina);
  }

}
