package newbank.server;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Offer implements Comparable<Offer> {

  private int offerNumber;
  private BigDecimal interestRate;
  private BigDecimal amount;
  private int lendingAccountNumber;
  private int borrowingLengthInMonth;
  private Customer customer;

  public Offer(
      BigDecimal interestRate,
      BigDecimal amount,
      Account lendingAccount,
      int borrowingLengthInMonth,
      Customer customer) {

    this(
        Offer.getNextOfferNumber(),
        interestRate,
        amount,
        lendingAccount.getAccountNumber(),
        borrowingLengthInMonth,
        customer);
  }

  private Offer(
      int offerNumber,
      BigDecimal interestRate,
      BigDecimal amount,
      int lendingAccountNumber,
      int borrowingLengthInMonth,
      Customer customer) {

    this.offerNumber = offerNumber;
    this.interestRate = interestRate;
    this.amount = amount;
    this.lendingAccountNumber = lendingAccountNumber;
    this.borrowingLengthInMonth = borrowingLengthInMonth;
    this.customer = customer;
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

  public Customer getCustomer() {
    return customer;
  }

  public void borrow(BigDecimal amountToBorrow) {
    this.amount = this.amount.subtract(amountToBorrow);
  }

  private static int getNextOfferNumber() {
    return MicroLoanMarketPlace.getNextOfferNumber();
  }

  @Override
  public String toString() {
    return String.format(
        "%s: %d"
            + System.lineSeparator()
            + "%s: %s"
            + System.lineSeparator()
            + "%s: %s"
            + System.lineSeparator()
            + "%s: %d"
            + System.lineSeparator(),
        "Offer Number",
        this.offerNumber,
        "Interest Rate (%)",
        this.interestRate.multiply(BigDecimal.valueOf(100)).toString(),
        "Amount (GBP)",
        new DecimalFormat("0.00").format(this.amount),
        "Term (months)",
        this.borrowingLengthInMonth);
  }

  @Override
  public int compareTo(Offer o) {
    int result = this.getInterestRate().compareTo(o.getInterestRate()); // low->high
    if (result == 0)
      result = o.getBorrowingLengthInMonth() - this.getBorrowingLengthInMonth(); // high->low
    if (result == 0) result = this.getAmount().compareTo(o.getAmount()); // low->high
    return result;
  }
}
