package newbank.server.Commands;

import newbank.server.Account;
import newbank.server.Customer;

import java.math.BigDecimal;
import java.util.regex.Matcher;

public class MoveCommand extends NewBankCommand {

  @Override
  public String getCommandName() {
    return "MOVE";
  }

  @Override
  public String getDescription() {
    return "<Amount> <Account Name> <Account Name>" + System.lineSeparator()
        + "-> Move money from the first listed account into the second." + System.lineSeparator()
        + "   To format add \"/\" between accounts and amount eg MOVE account 1/account 2/100.0.";
  }

  @Override
  public NewBankCommandResponse run(NewBankCommandParameter param) {
    Customer customer = param.getCustomer();
    Matcher m = 
      param.matchCommandArgument(
        "(?<amount>[0-9]+|[0-9]+\\.[0-9][0-9])(?:[\\s]+)(?<fromAccount>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)(?:[\\s]+)(?<toAccount>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)$");
    
    if (!m.matches()) return NewBankCommandResponse.invalidRequest("Not enough arguments. Please try again.");

    Account debitedAccount = customer.getAccountFromName(parseAccountName(m.group("fromAccount")));
    Account creditedAccount = customer.getAccountFromName(parseAccountName(m.group("toAccount")));

    if (debitedAccount == null) {
      return NewBankCommandResponse.failed(
          "Account to be debited does not exist. Please try again.");
    }
    if (creditedAccount == null) {
      return NewBankCommandResponse.failed(
          "Account to be credited does not exist. Please try again.");
    }
    if (m.group("fromAccount").equals(m.group("toAccount"))) {
      return NewBankCommandResponse.failed(
          "The debiting and crediting accounts are the same. Please try again.");
    }

    if (!debitedAccount.getCurrency().equals(creditedAccount.getCurrency())) {
      return NewBankCommandResponse.failed(
          "The currency of each account is not the same. Please try again.");
    }

    if (!validAmount(m.group("amount"))) {
      return NewBankCommandResponse.failed("Amount is invalid. Please try again.");
    }

    BigDecimal amount = convertDoubleToBigDecimal(Double.parseDouble(m.group("amount")));

    if (debitedAccount.getBalance().compareTo(amount) < 0) {
      return NewBankCommandResponse.failed(
          "Not enough funds in account to be debited. Please try again.");
    }

    debitedAccount.moneyOut(amount);
    creditedAccount.moneyIn(amount);

    return NewBankCommandResponse.succeeded(
        "Move successful." + System.lineSeparator() + "The balance of "
            + creditedAccount.getAccountName()
            + " is now "
            + creditedAccount.getBalance().toPlainString()
            + "." + System.lineSeparator() + "The balance of "
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

  private static String parseAccountName(String accountName) {
    return accountName.replace("\"", "");
  }
}
