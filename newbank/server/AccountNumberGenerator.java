package newbank.server;

public class AccountNumberGenerator {

  private int nextAccountNumber = 1;

  public int getNextAccountNumber() {
    int currentNumber = nextAccountNumber;
    nextAccountNumber = nextAccountNumber + 1;
    return currentNumber;
  }
}
