package newbank.server;

public class AccountNumberGenerator {

  private Integer seed;

  public synchronized int getNextAccountNumber() {
    if (seed == null)
      // only fall here the first time this method is called initialize the seed value
      seed = NewBank.getBank().getAccounts().keySet().stream().max(Integer::compareTo).get();

    return ++seed;
  }
}
