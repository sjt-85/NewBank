package newbank.server.Commands;

import newbank.server.Account;
import newbank.server.Customer;

import java.math.BigDecimal;

import static newbank.server.Commands.NewBankCommandResponse.*;

public class TransferCommand extends NewBankCommand {

  @Override
  public String getCommandName() {
    return "TRANSFER";
  }

  @Override
  public String getDescription() {
    return "<Account Name>/<Account Name>/<Amount>"
        + System.lineSeparator()
        + "-> Transfer from the first listed account into the second."
        + System.lineSeparator()
        + "   To format add \"/\" between accounts and amount eg TRANSFER account 1/account 2/100.0.";
  }

  @Override
  public NewBankCommandResponse run(NewBankCommandParameter param) {
    Customer customer = param.getCustomer();
    String input = param.getCommandArgument();
    // TODO make / a forbidden character when naming new accounts
    String[] request = input.split("/");

    if (request.length != 3) return invalidRequest("Not enough arguments. Please try again.");

    Account debitedAccount = customer.getAccountFromName(request[0].replaceAll("\"", ""));
    Account creditedAccount = customer.getAccountFromName(request[1].replaceAll("\"", ""));

    if (debitedAccount == null)
      return failed("Account to be debited does not exist. Please try again.");

    if (creditedAccount == null)
      return failed("Account to be credited does not exist. Please try again.");

    if (request[0].equals(request[1]))
      return failed("The debiting and crediting accounts are the same. Please try again.");

    if (!debitedAccount.getCurrency().equals(creditedAccount.getCurrency()))
      return failed("The currency of each account is not the same. Please try again.");

    if (!validAmount(request[2])) return failed("Amount is invalid. Please try again.");

    BigDecimal amount = convertDoubleToBigDecimal(Double.parseDouble(request[2]));

    if (debitedAccount.getBalance().compareTo(amount) < 0)
      return failed("Not enough funds in account to be debited. Please try again.");

    debitedAccount.moneyOut(amount);
    creditedAccount.moneyIn(amount);

    return succeeded(
        "Transfer successful."
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

  private BigDecimal convertDoubleToBigDecimal(double amount) {
    BigDecimal bd = BigDecimal.valueOf(amount);
    return bd.setScale(2);
  }
}
