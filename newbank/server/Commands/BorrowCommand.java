package newbank.server.Commands;

import newbank.server.Account;
import newbank.server.Customer;
import newbank.server.Loan;
import newbank.server.MicroLoanMarketPlace;
import newbank.server.Offer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class BorrowCommand extends NewBankCommand {

  @Override
  public String getCommandName() {
    return "BORROW";
  }

  @Override
  public String getDescription() {
    return "<Offer number> <Optional: Repayment length in months>";
  }

  @Override
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {
    int offerNumber;
    int borrowingLength;

    String input[] = request.getCommandArgument().split(" ");

    if (input.length > 2) {
      response.invalidRequest("Incorrect number of arguments. Please try again");
      return;
    }

    if (!validInt(input[0])) {
      response.invalidRequest(
          "Please enter correct format for amounts. Please see help and try again.");
      return;
    }

    offerNumber = Integer.parseInt(input[0]);

    if (!MicroLoanMarketPlace.getInstance().getOffers().containsKey(offerNumber)) {
      response.failed("Offer does not exist. Please try again.");
      return;
    }

    Offer offer = MicroLoanMarketPlace.getInstance().getOffers().get(offerNumber);
    borrowingLength = offer.getBorrowingLengthInMonth();

    if (input.length == 2) {
      if (!validInt(input[1])) {
        response.invalidRequest(
            "Please enter correct format for amounts. Please see help and try again.");
        return;
      }
      borrowingLength = Integer.parseInt(input[1]);
    }

    if (borrowingLength > offer.getBorrowingLengthInMonth()) {
      response.failed("Length selected is longer than max allowed. Please try again.");
      return;
    }

    Customer customer = request.getCustomer();
    Account creditedAccount = findCreditingAccount(customer, response);

    if (creditedAccount == null) {
      response.failed("No account available to credit. Please try again.");
      return;
    }

    String confirmationMessage =
        "Please confirm your loan:"
            + System.lineSeparator()
            + "Loan Amount: "
            + offer.getAmount().toPlainString()
            + "GBP"
            + System.lineSeparator()
            + "Interest rate: "
            + offer.getInterestRate().multiply(BigDecimal.valueOf(100)).toPlainString()
            + "%"
            + System.lineSeparator()
            + "Borrowing length: "
            + borrowingLength
            + System.lineSeparator()
            + "Your monthly repayment will be: "
            + calculateRepayments(offer.getAmount(), offer.getInterestRate(), borrowingLength)
                .toPlainString()
            + System.lineSeparator()
            + "Do you wish to proceed?";

    if (!response.confirm(confirmationMessage)) {
      response.failed("Action cancelled.");
      return;
    }

    Loan loan =
        new Loan(offer, offer.getAmount(), BigDecimal.valueOf(0), creditedAccount, borrowingLength);

    creditedAccount.moneyIn(offer.getAmount());
    MicroLoanMarketPlace.getInstance().removeOffer(offerNumber);

    response.succeeded(
        "You have successfully borrowed " + offer.getAmount().toPlainString() + "GBP.");
  }

  private static Account findCreditingAccount(Customer customer, NewBankCommandResponse response) {
    List<Account> accounts =
        customer.enumAccountNumbers().stream()
            .map(customer::getAccountFromNumber)
            .filter(account -> !account.getAccountType().equals(Account.AccountType.LENDING))
            .collect(Collectors.toList());
    return accounts.size() == 0
        ? null
        : accounts.size() == 1 ? accounts.get(0) : queryAccount(customer, response, accounts);
  }

  private static Account queryAccount(
      Customer customer, NewBankCommandResponse response, List accountList) {

    Account account;
    do {
      account =
          customer.getAccountFromName(
              response.query(
                  String.format(
                      "Please input the account name:%s",
                      accountList.stream()
                          .map(accountName -> "\"" + accountName + "\"")
                          .reduce((name1, name2) -> name1 + "," + name2)
                          .orElse(""))));

    } while (account == null);

    return account;
  }

  private boolean validInt(String amountInput) {
    int amount;
    try {
      amount = Integer.parseInt(amountInput);
    } catch (NumberFormatException e) {
      return false;
    }
    return amount > 0;
  }
  // TODO method should be a class, so other clients can use it
  private BigDecimal calculateRepayments(BigDecimal amount, BigDecimal rate, int length) {
    BigDecimal periodRate = rate.divide(BigDecimal.valueOf(length), 4, RoundingMode.HALF_EVEN);
    BigDecimal addOneToPRate = periodRate.add(BigDecimal.valueOf(1));
    BigDecimal toPowerN = addOneToPRate.pow(length);
    BigDecimal denominator = toPowerN.subtract(BigDecimal.valueOf(1));
    BigDecimal fraction = periodRate.divide(denominator, 4, RoundingMode.HALF_EVEN);
    BigDecimal fractionPlusPR = periodRate.add(fraction);
    return fractionPlusPR.multiply(amount).setScale(2, RoundingMode.HALF_EVEN);
  }
}
