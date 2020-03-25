package newbank.server.Commands;

import newbank.server.Account;
import newbank.server.Currency;
import newbank.server.Customer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    var args = new NewAccountCommandArgument();

    if (!args.parse(param.getCommandArgument(), param.getCustomer()))
      return newbank.server.Commands.NewBankCommandResponse.failed("FAIL");

    Currency currency = args.getCurrency();

    if (currency == null)
      return newbank.server.Commands.NewBankCommandResponse.failed(
          "FAIL: Currency not allowed. Accepted currencies: " + Currency.listAllCurrencies());

    // requested currency is allowed
    param.getCustomer().addAccount(new Account(args.accountType, args.accountName, 0, currency));

    return (param.getCustomer().hasAccount(args.accountType, args.accountName))
        ? newbank.server.Commands.NewBankCommandResponse.succeeded(
            "SUCCESS: Opened account TYPE:\""
                + args.accountType.toString()
                + "\" NAME:\""
                + args.accountName
                + "\""
                + " CURRENCY:"
                + currency.name())
        : newbank.server.Commands.NewBankCommandResponse.failed("FAIL");
  }

  private static class NewAccountCommandArgument {
    public String accountName; // get account name from regex result
    public Account.AccountType accountType;
    private Currency currency;

    public Currency getCurrency() {
      return currency;
    }

    public boolean parse(String request, newbank.server.Customer customer) {
      // use regex to obtain account type and name
      Pattern p =
          Pattern.compile(
              "(?<accType>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)(?:[\\s]+|$)(?<accName>\"[a-zA-Z0-9 ]*\"|[a-zA-Z0-9]*)(?:[\\s]+|$)(?<currency>[a-zA-Z]*)$");

      Matcher m = p.matcher(request);

      if (!m.matches()) return false;

      // get currency from regex result
      currency = parseCurrency(m.group("currency"));

      // get account type from regex result
      accountType = parseAccountType(m.group("accType"));

      if (accountType == Account.AccountType.NONE) return false;

      String accountName = parseAccountName(m.group("accName"), customer, accountType);
      if (customer.hasAccount(accountName)) return false;

      this.accountName = accountName;

      return true;
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

    public static Account.AccountType parseAccountType(String accountTypeStr) {
      return accountTypeStr == null
          ? Account.AccountType.NONE
          : Account.AccountType.getAccountTypeFromString(
              accountTypeStr.replace("\"", "") /* remove enclosing "" if present */);
    }
  }
}
