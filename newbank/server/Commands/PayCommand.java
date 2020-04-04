package newbank.server.Commands;

import newbank.server.Account;
import newbank.server.Customer;
import newbank.server.NewBank;

import java.math.BigDecimal;
import java.util.regex.Matcher;

public class PayCommand extends NewBankCommand {
  @Override
  public String getCommandName() {
    return "PAY";
  }

  @Override
  public String getDescription() {
    return "<Account Number> <Amount>";
  }

  @Override
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {

    var args = PayArguments.parse(request);
    if (args == null) {
      response.invalidRequest("FAIL");
      return;
    }

    var customer = request.getCustomer();

    if (args.amount.doubleValue() <= 0) {
      response.invalidRequest("FAIL");
      return;
    }

    Account debitedAccount = findDebitedAccount(customer, response);

    if (debitedAccount == null || debitedAccount.getBalance().compareTo(args.amount) < 0) {
      response.invalidRequest("FAIL");
      return;
    }

    Account creditedAccount = NewBank.getBank().getAccounts().get(args.accountNumber);

    if (creditedAccount == null || creditedAccount == debitedAccount) {
      response.invalidRequest("FAIL");
      return;
    }

    if (!debitedAccount.getCurrency().equals(creditedAccount.getCurrency())) {
      response.invalidRequest("FAIL");
      return;
    }

    String confirmationMessage =
        String.format(
            "From: %s"
                + System.lineSeparator()
                + "To: %s"
                + System.lineSeparator()
                + "Amount: %s"
                + System.lineSeparator()
                + "Do you proceed?",
            debitedAccount.toString(),
            creditedAccount.toString(),
            args.amount.toString());

    if (!response.confirm(confirmationMessage)) {
      response.invalidRequest("FAIL");
      return;
    }

    debitedAccount.moneyOut(args.amount);
    creditedAccount.moneyIn(args.amount);

    response.succeeded(
        "PAY Successful"
            + System.lineSeparator()
            + String.format(
                "You have made a payment of £%s to the Account(Number:%03d Type:[%s] Name:\"%s\")",
                args.amount.toPlainString(),
                creditedAccount.getAccountNumber(),
                creditedAccount.getAccountType(),
                creditedAccount.getAccountName())
            + System.lineSeparator()
            + String.format(
                "Your new balance is: £%s", debitedAccount.getBalance().toPlainString()));
  }

  static class PayArguments {

    public static PayArguments parse(NewBankCommandRequest request) {

      Matcher m =
          request.matchCommandArgument(
              "(?<accountNumber>-?[0-9]+)(?:[\\s]+)(?<amount>-?[0-9]+|[0-9]+\\.[0-9][0-9])$");

      if (!m.matches()) {
        return null;
      }

      var args = new PayArguments();
      args.amount = parseAmount(m.group("amount"));
      args.accountNumber = parseAccountNumber(m.group("accountNumber"));

      return args;
    }

    private BigDecimal amount;
    private int accountNumber;

    private static BigDecimal parseAmount(String amount) {
      try {
        BigDecimal bd = BigDecimal.valueOf(Double.parseDouble(amount));
        return bd.setScale(2);
      } catch (NumberFormatException e) {
        return BigDecimal.ZERO;
      }
    }

    private static int parseAccountNumber(String accountNumber) {
      try {
        return Integer.parseInt(accountNumber);
      } catch (NumberFormatException e) {
        return -1;
      }
    }
  }

  private static Account findDebitedAccount(Customer customer, NewBankCommandResponse response) {
    var accountNumbers = customer.enumAccountNumbers();
    return accountNumbers.size() == 0
        ? null
        : accountNumbers.size() == 1
            ? customer.getAccountFromNumber(accountNumbers.get(0))
            : queryAccount(customer, response);
  }

  private static Account queryAccount(Customer customer, NewBankCommandResponse response) {

    Account account;
    do {
      account =
          customer.getAccountFromName(
              response.query(
                  String.format(
                      "Please input the account name:%s",
                      customer.enumAccountNumbers().stream()
                          .map(customer::getAccountFromNumber)
                          .map(Account::getAccountName)
                          .map(accountName -> "\"" + accountName + "\"")
                          .reduce((name1, name2) -> name1 + "," + name2)
                          .orElse(""))));

    } while (account == null);

    return account;
  }
}
