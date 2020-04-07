package newbank.server;

import java.math.BigDecimal;

public class Loan {
  private int offerNumber;
  private BigDecimal totalAmount;
  private BigDecimal totalAmountRepaid;
  private int borrowingAccountNumber;

  public Loan(
      Offer offer, BigDecimal totalAmount, BigDecimal totalAmountRepaid, Account borrowingAccount) {
    this(
        offer.getOfferNumber(),
        totalAmount,
        totalAmountRepaid,
        borrowingAccount.getAccountNumber());
  }

  public Loan(
      int offerNumber,
      BigDecimal totalAmount,
      BigDecimal totalAmountRepaid,
      int borrowingAccountNumber) {

    this.offerNumber = offerNumber;
    this.totalAmount = totalAmount;
    this.totalAmountRepaid = totalAmountRepaid;
    this.borrowingAccountNumber = borrowingAccountNumber;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public BigDecimal getTotalAmountRepaid() {
    return totalAmountRepaid;
  }

  public void setTotalAmountRepaid(BigDecimal totalAmountRepaid) {
    this.totalAmountRepaid = totalAmountRepaid;
  }

  public Account getBorrowingAccount() {
    return NewBank.getBank().getAccounts().getOrDefault(borrowingAccountNumber, null);
  }

  public Offer getOffer() {
    return MicroLoanMarketPlace.getInstance().getOffers().getOrDefault(offerNumber, null);
  }
}
