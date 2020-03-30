package newbank.server;

import newbank.server.Account.AccountType;

import java.util.HashMap;

public class NewBank {

  private static final NewBank bank = new NewBank();
  private HashMap<String, Customer> customers;

  private NewBank() {
    setCustomers(new HashMap<>());
    addTestData();
  }

  private void addTestData() {
    // Password = 1
    Customer bhagy = new Customer();
    bhagy.addAccount(new Account(AccountType.CURRENT, "Main 1", 1000.0));
    bhagy.assignPassword("c4ca4238a0b923820dcc509a6f75849b");
    getCustomers().put("Bhagy", bhagy);

    // Password = 2
    Customer christina = new Customer();
    christina.addAccount(new Account(AccountType.SAVINGS, "Savings 1", 1500.0));
    christina.assignPassword("c81e728d9d4c2f636f067f89cc14862c");
    getCustomers().put("Christina", christina);

    // Password = 3
    Customer john = new Customer();
    john.addAccount(new Account(AccountType.CURRENT, "Checking 1", 250.0));
    john.addAccount(new Account(AccountType.SAVINGS, "Saving 1", 500.0));
    john.assignPassword("eccbc87e4b5ce2fe28308fd9f2a7baf3");
    getCustomers().put("John", john);
  }

  public static NewBank getBank() {
    return bank;
  }

  public synchronized boolean isValidUserName(String userName) {
    return customers.containsKey(userName) ? true : false;
  }

  public synchronized CustomerID checkLogInDetails(String userName, String password) {
    if (getCustomers().containsKey(userName)) {
      HashGenerator generator = new HashGenerator();
      String hashedPassword = generator.generateHash(password);
      String storedPassword = getCustomers().get(userName).retrievePassword();
      if (hashedPassword.equals(storedPassword)) {
        return new CustomerID(userName);
      }
    }
    return null;
  }

  public HashMap<String, Customer> getCustomers() {
    return customers;
  }

  private void setCustomers(HashMap<String, Customer> customers) {
    this.customers = customers;
  }
}
