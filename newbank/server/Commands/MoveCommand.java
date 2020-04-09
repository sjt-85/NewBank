package newbank.server.Commands;

import newbank.server.Account;
import newbank.server.CurrencyConverter;
import newbank.server.Customer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;

public class MoveCommand extends NewBankCommand {

  @Override
  public String getCommandName() {
    return "MOVE";
  }

  @Override
  public String getDescription() {
    return "<Amount> <Account Name> <Account Name>"
        + System.lineSeparator()
        + "-> Move money from the first listed account into the second."
        + System.lineSeparator()
        + "   e.g. MOVE 100 \"Current Account\" \"Savings Account\"";
  }

  @Override
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {

    Customer customer = request.getCustomer();

    Matcher m =
        request.matchCommandArgument(
            "(?<amount>-?[0-9]+|[0-9]+\\.[0-9][0-9])(?:[\\s]+)(?<fromAccount>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)(?:[\\s]+)(?<toAccount>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)$");

    if (!m.matches()) {
      response.invalidRequest("Not enough arguments. Please try again.");
      return;
    }

    Account debitedAccount = customer.getAccountFromName(parseAccountName(m.group("fromAccount")));
    Account creditedAccount = customer.getAccountFromName(parseAccountName(m.group("toAccount")));

    if (debitedAccount == null) {
      response.failed("Account to be debited does not exist. Please try again.");
      return;
    }

    if (creditedAccount == null) {
      response.failed("Account to be credited does not exist. Please try again.");
      return;
    }

    if (m.group("fromAccount").equals(m.group("toAccount"))) {
      response.failed("The debiting and crediting accounts are the same. Please try again.");
      return;
    }

    if (!validAmount(m.group("amount"))) {
      response.failed("Amount is invalid. Please try again.");
      return;
    }

    BigDecimal amount = convertDoubleToBigDecimal(Double.parseDouble(m.group("amount")));

    if (debitedAccount.getBalance().compareTo(amount) < 0) {
      response.failed("Not enough funds in account to be debited. Please try again.");
      return;
    }

    BigDecimal convertedAmount = convertAmount(amount, debitedAccount, creditedAccount);

    debitedAccount.moneyOut(amount);
    creditedAccount.moneyIn(convertedAmount);

    response.succeeded(
        "Move successful."
            + System.lineSeparator()
            + "The balance of "
            + creditedAccount.getAccountName()
            + " is now "
            + creditedAccount.getBalance().toPlainString()
            + creditedAccount.getCurrency().toString()
            + "."
            + System.lineSeparator()
            + "The balance of "
            + debitedAccount.getAccountName()
            + " is now "
            + debitedAccount.getBalance().toPlainString()
            + debitedAccount.getCurrency().toString()
            + ".");
  }

  private boolean validAmount(String amountInput) {
    double amount;
    try {
      amount = Double.parseDouble(amountInput);
    } catch (NumberFormatException e) {
      return false;
    }
    return amount > 0;
  }

  private BigDecimal convertDoubleToBigDecimal(double amount) {
    BigDecimal bd = BigDecimal.valueOf(amount);
    return bd.setScale(2);
  }

  private static String parseAccountName(String accountName) {
    return accountName.replace("\"", "");
  }

  private BigDecimal convertAmount(BigDecimal amount, Account debited, Account credited) {
    CurrencyConverter cc = new CurrencyConverter();
    switch (credited.getCurrency()) {
        // Without rounding mode program was crashing
      case GBP:
        return cc.convertToGBP(debited.getCurrency(), amount).setScale(2, RoundingMode.HALF_EVEN);
      case EUR:
        return cc.convertToEur(debited.getCurrency(), amount).setScale(2, RoundingMode.HALF_EVEN);
      case USD:
        return cc.convertToUsd(debited.getCurrency(), amount).setScale(2, RoundingMode.HALF_EVEN);
    }
    return null;
  }
}
