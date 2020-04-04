package newbank.server;

import newbank.server.Account.AccountType;

import java.util.Arrays;
import java.util.HashMap;

public class NewBank {

  private static final NewBank bank = new NewBank();
  private HashMap<String, Customer> customers;
  private HashMap<Integer, Account> accounts;

  private NewBank() {
    addTestData();
  }

  private void addTestData() {
    customers = new HashMap<>();
    accounts = new HashMap<>();

    Account[] accountTable =
        new Account[] {
          new Account(AccountType.CURRENT, "Main 1", 1000.0, 1),
          new Account(AccountType.SAVINGS, "Savings 1", 1500.0, 2),
          new Account(AccountType.CURRENT, "Checking 1", 250.0, 3),
          new Account(AccountType.SAVINGS, "Saving 1", 500.0, 4)
        };

    Arrays.stream(accountTable)
        .forEach(account -> this.accounts.put(account.getAccountNumber(), account));

    // Password = 1
    Customer bhagy = new Customer();
    bhagy.addAccount(this.accounts.get(1));
    bhagy.assignPassword("c4ca4238a0b923820dcc509a6f75849b");
    getCustomers().put("Bhagy", bhagy);

    // Password = 2
    Customer christina = new Customer();
    christina.addAccount(this.accounts.get(2));
    christina.assignPassword("c81e728d9d4c2f636f067f89cc14862c");
    getCustomers().put("Christina", christina);

    // Password = 3
    Customer john = new Customer();
    john.addAccount(this.accounts.get(3));
    john.addAccount(this.accounts.get(4));
    john.assignPassword("eccbc87e4b5ce2fe28308fd9f2a7baf3");
    getCustomers().put("John", john);
  }

  public void LoadData() {
    addTestData();
  }

  public static NewBank getBank() {
    return bank;
  }

  public synchronized boolean isValidUserName(String userName) {
    return customers.containsKey(userName);
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

  public synchronized HashMap<String, Customer> getCustomers() {
    return customers;
  }

  public synchronized HashMap<Integer, Account> getAccounts() {
    return accounts;
  }
}
