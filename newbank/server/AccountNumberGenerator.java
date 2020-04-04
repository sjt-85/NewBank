package newbank.server;

public class AccountNumberGenerator {
  public int getNextAccountNumber() {
    return NewBank.getBank().getAccounts().keySet().stream().max(Integer::compareTo).get() + 1;
  }
}
