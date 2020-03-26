package newbank.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import newbank.server.Account.AccountType;

public class NewBank {

  private static final NewBank bank = new NewBank();
  private HashMap<String, newbank.server.Customer> customers;

  private NewBank() {
    setCustomers(new HashMap<>());
    addTestData();
  }

  private void addTestData() {
    // Password = 1
    newbank.server.Customer bhagy = new newbank.server.Customer();
    bhagy.addAccount(new newbank.server.Account(AccountType.CURRENT, "Main 1", 1000.0));
    bhagy.assignPassword("c4ca4238a0b923820dcc509a6f75849b");
    getCustomers().put("Bhagy", bhagy);

    // Password = 2
    newbank.server.Customer christina = new newbank.server.Customer();
    christina.addAccount(new newbank.server.Account(AccountType.SAVINGS, "Savings 1", 1500.0));
    christina.assignPassword("c81e728d9d4c2f636f067f89cc14862c");
    getCustomers().put("Christina", christina);

    // Password = 3
    newbank.server.Customer john = new newbank.server.Customer();
    john.addAccount(new newbank.server.Account(AccountType.CURRENT, "Checking 1", 250.0));
    john.assignPassword("eccbc87e4b5ce2fe28308fd9f2a7baf3");
    getCustomers().put("John", john);
  }

  public static NewBank getBank() {
    return bank;
  }

  public synchronized newbank.server.CustomerID checkLogInDetails(
      String userName, String password) {
    if (getCustomers().containsKey(userName)) {
      newbank.server.HashGenerator generator = new newbank.server.HashGenerator();
      String hashedPassword = generator.generateHash(password);
      String storedPassword = getCustomers().get(userName).retrievePassword();
      if (hashedPassword.equals(storedPassword)) {
        return new newbank.server.CustomerID(userName);
      }
    }
    return null;
  }

  public HashMap<String, newbank.server.Customer> getCustomers() {
    return customers;
  }

  private void setCustomers(HashMap<String, newbank.server.Customer> customers) {
    this.customers = customers;
  }
}
