package newbank.server;

import newbank.server.Account.AccountType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static newbank.server.Account.compareAccountName;

public class Customer {

  private ArrayList<Account> accounts;
  private String password;

  public Customer() {
    accounts = new ArrayList<>();
  }

  public String accountsToString() {
    StringBuilder s = new StringBuilder();
    String newLine = System.lineSeparator();
    for (Account a : accounts) {
      s.append(a.toString());
      s.append(newLine);
    }
    return s.toString();
  }

  public void addAccount(Account account) {
    accounts.add(account);
  }

  public boolean hasAccount(String accountName) {
    return findAccount(account -> compareAccountName(account.getAccountName(), accountName))
        != null;
  }

  public boolean hasAccount(AccountType accountType, String accountName) {
    return findAccount(
            account ->
                (compareAccountName(account.getAccountName(), accountName))
                    && (account.getAccountType().equals(accountType)))
        != null;
  }

  public void assignPassword(String password) {
    this.password = password;
  }

  public String retrievePassword() {
    return password;
  }

  public Account getAccountFromName(String name) {
    return findAccount(account -> account.getAccountName().equals(name));
  }

  public List<Integer> enumAccountNumbers() {
    return accounts.stream().map(Account::getAccountNumber).collect(Collectors.toList());
  }

  public List<Account> collectAccountsByType(AccountType type) {
    return accounts.stream()
        .filter(account -> account.getAccountType().equals(type))
        .collect(Collectors.toList());
  }

  public Account getAccountFromNumber(Integer accountNumber) {
    return findAccount(account -> account.getAccountNumber() == accountNumber);
  }

  private Account findAccount(Predicate<Account> predicate) {
    return accounts.stream().filter(predicate).findFirst().orElse(null);
  }
}
