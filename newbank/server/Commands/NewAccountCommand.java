package newbank.server.Commands;

import newbank.server.Account;
import newbank.server.Currency;
import newbank.server.Customer;

import java.util.regex.Matcher;

public class NewAccountCommand extends newbank.server.Commands.NewBankCommand {

  @Override
  public String getCommandName() {
    return "NEWACCOUNT";
  }

  @Override
  public String getDescription() {
    return "<account type> <optional: account name> <optional: currency> \n"
        + "-> Creates a new account of specified type e.g. NEWACCOUNT \"Savings Account\" \"my savings\" EUR \n"
        + "Standard currency is GBP, please specify an account name and currency to create an account with a different currency.";
  }

  @Override
  public newbank.server.Commands.NewBankCommandResponse run(
      newbank.server.Commands.NewBankCommandParameter param) {

    var args = NewAccountCommandArgument.parse(param);

    if (args == null) return newbank.server.Commands.NewBankCommandResponse.invalidRequest("FAIL");

    if (args.getCurrency() == null)
      return newbank.server.Commands.NewBankCommandResponse.failed(
          "FAIL: Currency not allowed. Accepted currencies: " + Currency.listAllCurrencies());

    // requested currency is allowed
    param
        .getCustomer()
        .addAccount(new Account(args.getAccountType(), args.getAccountName(), 0, args.getCurrency()));

    return (param.getCustomer().hasAccount(args.getAccountType(), args.getAccountName()))
        ? newbank.server.Commands.NewBankCommandResponse.succeeded(
            "SUCCESS: Opened account TYPE:\""
                + args.getAccountType().toString()
                + "\" NAME:\""
                + args.getAccountName()
                + "\""
                + " CURRENCY:"
                + args.getCurrency().name())
        : newbank.server.Commands.NewBankCommandResponse.failed("FAIL");
  }

  private static class NewAccountCommandArgument {

    public static NewAccountCommandArgument parse(
        newbank.server.Commands.NewBankCommandParameter param) {

      NewAccountCommandArgument argument = new NewAccountCommandArgument();

      // use regex to obtain account type and name
      Matcher m =
          param.matchCommandArgument(
              "(?<accType>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)(?:[\\s]+|$)(?<accName>\"[a-zA-Z0-9 ]*\"|[a-zA-Z0-9]*)(?:[\\s]+|$)(?<currency>[a-zA-Z]*)$");

      if (!m.matches()) return null;

      // get currency from regex result
      argument.currency = parseCurrency(m.group("currency"));

      // get account type from regex result
      argument.accountType = parseAccountType(m.group("accType"));

      if (argument.accountType == Account.AccountType.NONE) return null;

      argument.accountName =
          parseAccountName(m.group("accName"), param.getCustomer(), argument.accountType);

      if (param.getCustomer().hasAccount(argument.accountName)) return null;

      return argument;
    }

    private String accountName; // get account name from regex result
    private Account.AccountType accountType;
    private Currency currency;

    public Currency getCurrency() {
      return currency;
    }

    public String getAccountName() {
      return accountName;
    }

    public Account.AccountType getAccountType() {
      return accountType;
    }

    private static Currency parseCurrency(String currencyStr) {
      return currencyStr == null || currencyStr.isBlank()
          ? Currency.GBP
          : Currency.createCurrency(currencyStr);
    }

    private static String parseAccountName(
        String accountName, Customer customer, Account.AccountType accountType) {

      return accountName == null || accountName.isBlank()
          ? generateAccountName(customer, accountType)
          : accountName.replace("\"", ""); // remove enclosing "" if present
    }

    private static String generateAccountName(
        newbank.server.Customer customer, Account.AccountType accountType) {

      int accountNameSuffix = 1;

      String accountName;

      do {
        accountName = (accountType.toString() + " " + (accountNameSuffix++));
      } while (customer.hasAccount(accountName));

      return accountName;
    }

    private static Account.AccountType parseAccountType(String accountTypeStr) {
      return accountTypeStr == null
          ? Account.AccountType.NONE
          : Account.AccountType.getAccountTypeFromString(
              accountTypeStr.replace("\"", "") /* remove enclosing "" if present */);
    }

  }
}
