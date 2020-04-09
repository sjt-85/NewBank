package newbank.server.Commands;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class OfferCommand extends NewBankCommand {

  private final double MAXOFFER = 2500;
  private final double MAXRATE = 0.2;
  private final int MAXLENGTH = 24;

  @Override
  public String getCommandName() {
    return "OFFER";
  }

  @Override
  public String getDescription() {
    return "<amount> <interest rate as percentage> <max borrowing length in months> "
        + System.lineSeparator()
        + "-> Offer a loan to borrowers eg OFFER 500 10% 6. "
        + System.lineSeparator()
        + "   Standard currency is GBP, please specify an account name and currency to create an account with a different currency.";
  }

  @Override
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {
    String[] input = request.getCommandArgument().split(" ");

    if (input.length != 3) {
      response.invalidRequest("Incorrect number of arguments. Please try again");
      return;
    }

    if (!input[1].contains("%")) {
      response.invalidRequest(
          "Please enter correct format for amounts. Please see help and try again.");
    }

    String percentInput[] = input[1].split("%");

    if (!validDouble(input[0]) || !validDouble(percentInput[0]) || !validInt(input[2])) {
      response.invalidRequest(
          "Please enter correct format for amounts. Please see help and try again.");
      return;
    }

    BigDecimal amount =
        BigDecimal.valueOf(Double.parseDouble(input[0])).setScale(2, RoundingMode.HALF_EVEN);

    BigDecimal percent = BigDecimal.valueOf(Double.parseDouble(percentInput[0]));
    BigDecimal rate = percent.divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_EVEN);

    BigInteger length = BigInteger.valueOf(Integer.parseInt(input[2]));

    if (amount.compareTo(BigDecimal.valueOf(MAXOFFER)) > 0) {
      response.failed("Amount offered greater than " + MAXOFFER + "GBP. Please try again.");
      return;
    }

    if (amount.compareTo(BigDecimal.valueOf(0)) == 0) {
      response.failed("Amount cannot be zero. Please try again.");
      return;
    }

    if (rate.compareTo(BigDecimal.valueOf(MAXRATE)) > 0) {
      response.failed("Rate offered greater than " + MAXRATE * 100 + "%. Please try again.");
      return;
    }

    if (length.compareTo(BigInteger.valueOf(MAXLENGTH)) > 0) {
      response.failed("Length offered greater than " + MAXLENGTH + " months. Please try again.");
      return;
    }
  }

  private boolean validDouble(String amountInput) {
    double amount;
    try {
      amount = Double.parseDouble(amountInput);
    } catch (NumberFormatException e) {
      return false;
    }
    return amount >= 0;
  }

  private boolean validInt(String amountInput) {
    double amount;
    try {
      amount = Integer.parseInt(amountInput);
    } catch (NumberFormatException e) {
      return false;
    }
    return amount > 0;
  }
}
