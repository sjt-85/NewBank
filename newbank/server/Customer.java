package newbank.server;

import newbank.server.Account.AccountType;

import java.util.ArrayList;
import java.util.Locale;

public class Customer {

  private ArrayList<Account> accounts;
  private String password;
  private Locale accountNameLocale = Locale.ENGLISH;

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
    accountName = accountName.toLowerCase(accountNameLocale);
    for (Account a : accounts) {
      if (a.getAccountName().toLowerCase(accountNameLocale).equals(accountName)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasAccount(AccountType accountType, String accountName) {
    accountName = accountName.toLowerCase(accountNameLocale);
    for (Account a : accounts) {
      if ((a.getAccountName().toLowerCase(accountNameLocale).equals(accountName))
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

  public Account getAccoutFromName(String name) {
    for (Account a : accounts) {
      if (a.getAccountName().equals(name)) {
        return a;
      }
    }
    return null;
  }
}
