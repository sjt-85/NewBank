package newbank.server;

import java.math.BigDecimal;

public class Offer {

  private int offerNumber;
  private BigDecimal interestRate;
  private BigDecimal amount;
  private int lendingAccountNumber;
  private int borrowingLengthInMonth;

  public Offer(
      int offerNumber,
      BigDecimal interestRate,
      BigDecimal amount,
      Account lendingAccount,
      int borrowingLengthInMonth) {

    this(
        offerNumber,
        interestRate,
        amount,
        lendingAccount.getAccountNumber(),
        borrowingLengthInMonth);
  }

  public Offer(
      int offerNumber,
      BigDecimal interestRate,
      BigDecimal amount,
      int lendingAccountNumber,
      int borrowingLengthInMonth) {

    this.offerNumber = offerNumber;
    this.interestRate = interestRate;
    this.amount = amount;
    this.lendingAccountNumber = lendingAccountNumber;
    this.borrowingLengthInMonth = borrowingLengthInMonth;
  }

  public int getOfferNumber() {
    return offerNumber;
  }

  public BigDecimal getInterestRate() {
    return interestRate;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Account getLendingAccount() {
    return NewBank.getBank().getAccounts().getOrDefault(lendingAccountNumber, null);
  }

  public int getBorrowingLengthInMonth() {
    return borrowingLengthInMonth;
  }

  public void borrow(BigDecimal amountToBorrow) {
    this.amount = this.amount.subtract(amountToBorrow);
  }
}
