package newbank.server;

import java.util.ArrayList;

public class Customer {

  private ArrayList<Account> accounts;
  private String password;

  public Customer() {
    accounts = new ArrayList<>();
  }

  public String accountsToString() {
    String s = "";
    String newLine = System.lineSeparator();
    for (Account a : accounts) {
      s += a.toString();
      s += newLine;
    }
    return s;
  }

  public void addAccount(Account account) {
    accounts.add(account);
  }

  public boolean hasAccount(String accountName) {
    for (Account a : accounts) {
      if (a.getAccountName().equals(accountName)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean hasAccount(String accountType, String accountName) {
    for (Account a : accounts) {
      if ((a.getAccountName().equals(accountName))
          && (a.getAccountType().equals(accountType))) {
        return true;
      }
    }
    return false;
  }

  public void assignPassword(String password) {
    this.password = password;
  }

  public String retrievePassword() {
    return password;
  }
}
