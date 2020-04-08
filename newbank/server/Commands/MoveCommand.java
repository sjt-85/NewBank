package newbank.server.Commands;

import newbank.server.Account;
import newbank.server.Customer;

import java.math.BigDecimal;
import java.util.regex.Matcher;

import static newbank.server.NewBank.createDecimal;

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

    if (!debitedAccount.getCurrency().equals(creditedAccount.getCurrency())) {
      response.failed("The currency of each account is not the same. Please try again.");
      return;
    }

    if (!validAmount(m.group("amount"))) {
      response.failed("Amount is invalid. Please try again.");
      return;
    }

    BigDecimal amount = createDecimal(Double.parseDouble(m.group("amount")));

    if (debitedAccount.getBalance().compareTo(amount) < 0) {
      response.failed("Not enough funds in account to be debited. Please try again.");
      return;
    }

    debitedAccount.moneyOut(amount);
    creditedAccount.moneyIn(amount);

    response.succeeded(
        "Move successful."
            + System.lineSeparator()
            + "The balance of "
            + creditedAccount.getAccountName()
            + " is now "
            + creditedAccount.getBalance().toPlainString()
            + "."
            + System.lineSeparator()
            + "The balance of "
            + debitedAccount.getAccountName()
            + " is now "
            + debitedAccount.getBalance().toPlainString()
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

  private static String parseAccountName(String accountName) {
    return accountName.replace("\"", "");
  }
}
