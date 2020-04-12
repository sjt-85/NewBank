package newbank.server;

import java.math.BigDecimal;

public class Loan {
  private int offerNumber;
  private final BigDecimal totalAmount;
  private BigDecimal totalAmountRepaid;
  private int borrowingAccountNumber;
  private final int borrowingLength;
  private RepaymentCalculator rc;

  public Loan(
      Offer offer, BigDecimal totalAmount, BigDecimal totalAmountRepaid, Account borrowingAccount) {
    this(
        offer.getOfferNumber(),
        totalAmount,
        totalAmountRepaid,
        borrowingAccount.getAccountNumber(),
        offer.getBorrowingLengthInMonth());
    rc = new RepaymentCalculator();
  }

  public Loan(
      Offer offer,
      BigDecimal totalAmount,
      BigDecimal totalAmountRepaid,
      Account borrowingAccount,
      int borrowingLength) {
    this(
        offer.getOfferNumber(),
        totalAmount,
        totalAmountRepaid,
        borrowingAccount.getAccountNumber(),
        borrowingLength);
    rc = new RepaymentCalculator();
  }

  public Loan(
      int offerNumber,
      BigDecimal totalAmount,
      BigDecimal totalAmountRepaid,
      int borrowingAccountNumber,
      int borrowingLength) {

    this.offerNumber = offerNumber;
    this.totalAmount = totalAmount;
    this.totalAmountRepaid = totalAmountRepaid;
    this.borrowingAccountNumber = borrowingAccountNumber;
    this.borrowingLength = borrowingLength;
    rc = new RepaymentCalculator();
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public BigDecimal getTotalAmountRepaid() {
    return totalAmountRepaid;
  }

  public BigDecimal getRate() {
    return this.getOffer().getInterestRate();
  }

  public void setTotalAmountRepaid(BigDecimal totalAmountRepaid) {
    this.totalAmountRepaid = totalAmountRepaid;
  }

  public Account getBorrowingAccount() {
    return NewBank.getBank().getAccounts().getOrDefault(borrowingAccountNumber, null);
  }

  public Offer getOffer() {
    return MicroLoanMarketPlace.getInstance().getOffers().containsKey(offerNumber)
        ? MicroLoanMarketPlace.getInstance().getOffers().getOrDefault(offerNumber, null)
        : MicroLoanMarketPlace.getInstance().getTakenOffers().getOrDefault(offerNumber, null);
  }

  /** this method might not be used, however put here to show semantics */
  public BigDecimal getAmountLeftToPay() {
    return totalAmount.subtract(totalAmountRepaid);
  }
  /** this method might not be used, however put here to show semantics */
  private void pay(BigDecimal amountToPay) {
    this.totalAmountRepaid = this.totalAmountRepaid.subtract(amountToPay);
  }

  @Override
  public String toString() {
    return "Loan "
        + offerNumber
        + System.lineSeparator()
        + "Total Amount: "
        + totalAmount
        + "GBP"
        + System.lineSeparator()
        + "Total Amount Repaid: "
        + totalAmountRepaid.toPlainString()
        + "GBP"
        + System.lineSeparator()
        + "Term of loan: "
        + borrowingLength
        + " months"
        + System.lineSeparator()
        + "Monthly repayment: "
        + rc.calculateRepayments(totalAmount, this.getRate(), borrowingLength)
        + "GBP";
  }
}
