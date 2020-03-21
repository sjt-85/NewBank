package newbank.test;

import newbank.server.NewBank;

import java.io.*;

import static newbank.test.NBUnit.Assert;
import static newbank.test.NBUnit.AssertEqual;

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

    AssertEqual("Main: 1000.0" + System.lineSeparator(), bhagy);

    String christina =
        NewBank.getBank()
            .processRequest(
                NewBank.getBank().checkLogInDetails("Christina", "2"), "SHOWMYACCOUNTS");

    AssertEqual("Savings: 1500.0" + System.lineSeparator(), christina);
  }

  @newbank.test.NBUnit.Test
  private void testLoginSequence() {

    String userName = "Bhagy";
    String password = "1";

    String outputString = runServer(userName, password, "");

    final String initialResponse =
        "Enter Username"
            + System.lineSeparator()
            + "Enter Password"
            + System.lineSeparator()
            + "Checking Details..."
            + System.lineSeparator()
            + "Log In Successful. What do you want to do?"
            + System.lineSeparator();

    AssertEqual(initialResponse, outputString);
  }

  private static String runServer(String userName, String password, String command) {

    String inputString =
        userName + "\n" + password + "\n" + (command + (command.length() == 0 ? "" : "\n"));

    var outputStream = new ByteArrayOutputStream();
    var writer = new PrintWriter(outputStream);

    var target =
        new newbank.server.NewBankClientHandler.ClientThreadTarget(
            new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(inputString.getBytes()))),
            writer);

    target.run();

    writer.flush();

    return outputStream.toString();
  }
}
