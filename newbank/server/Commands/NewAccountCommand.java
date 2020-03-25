package newbank.server.Commands;

import newbank.server.Account;
import newbank.test.NBUnit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewAccountCommand extends newbank.server.Commands.NewBankCommand {

  public static newbank.server.Commands.NewBankCommandResponse addNewAccountInternal(
      newbank.server.Customer customer, String request) {
    // use regex to obtain account type and name
    Pattern p =
        Pattern.compile(
            "NEWACCOUNT[\\s]+(?<accType>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)(?:[\\s]+|$)(?<accName>\"[a-zA-Z0-9 ]*\"|[a-zA-Z0-9]*)(?:[\\s]+|$)(?<currency>[a-zA-Z]*)$");
    Matcher m = p.matcher(request.trim());

    if (!m.matches()) return newbank.server.Commands.NewBankCommandResponse.failed("FAIL");

    String accountName = m.group("accName"); // get account name from regex result
    String accountTypeStr = m.group("accType"); // get account type from regex result
    String currencyStr = m.group("currency"); // get currency from regex result

    if (accountTypeStr == null)
      return newbank.server.Commands.NewBankCommandResponse.failed("FAIL");

    accountTypeStr = accountTypeStr.replace("\"", ""); // remove enclosing "" if present
    Account.AccountType accountType = Account.AccountType.getAccountTypeFromString(accountTypeStr);

    if (accountType == Account.AccountType.NONE)
      return newbank.server.Commands.NewBankCommandResponse.failed("FAIL");

    if (accountName == null || accountName.isBlank()) {
      // no name provided so build our own
      int accountNameSuffix = 1;
      accountName = (accountType.toString() + " " + accountNameSuffix);
      while (customer.hasAccount(accountName)) {
        accountName = (accountType.toString() + " " + (++accountNameSuffix));
      }
    } else {
      // remove enclosing "" if present
      accountName = accountName.replace("\"", "");
    }

    if (customer.hasAccount(accountName))
      return newbank.server.Commands.NewBankCommandResponse.failed("FAIL");

    if (currencyStr == null || currencyStr.isBlank()) {
      customer.addAccount(new Account(accountType, accountName, 0));
      return (customer.hasAccount(accountType, accountName))
          ? createAccountDescriptionWhenSuccessful(
              accountName, accountType, newbank.server.Currency.GBP)
          : newbank.server.Commands.NewBankCommandResponse.failed("FAIL");
    } else {
      newbank.server.Currency acceptedCurrency =
          newbank.server.Currency.createCurrency(currencyStr);
      if (acceptedCurrency != null) { // requested currency is allowed
        customer.addAccount(new Account(accountType, accountName, 0, acceptedCurrency));
        return (customer.hasAccount(accountType, accountName))
            ? createAccountDescriptionWhenSuccessful(accountName, accountType, acceptedCurrency)
            : newbank.server.Commands.NewBankCommandResponse.failed("FAIL");
      } else {
        return newbank.server.Commands.NewBankCommandResponse.failed(
            "FAIL: Currency not allowed. Accepted currencies: "
                + newbank.server.Currency.listAllCurrencies());
      }
    }
  }

  private static newbank.server.Commands.NewBankCommandResponse
      createAccountDescriptionWhenSuccessful(
          String accountName,
          Account.AccountType accountType,
          newbank.server.Currency acceptedCurrency) {
    return newbank.server.Commands.NewBankCommandResponse.succeeded(
        "SUCCESS: Opened account TYPE:\""
            + accountType.toString()
            + "\" NAME:\""
            + accountName
            + "\""
            + " CURRENCY:"
            + acceptedCurrency.name());
  }

  @Override
  public String getCommandName() {
    return "NEWACCOUNT";
  }

  @Override
  public newbank.server.Commands.NewBankCommandResponse run(
      newbank.server.Commands.NewBankCommandParameter parameter) {

    newbank.server.Customer customer =
        newbank.server.NewBank.getBank().getCustomers().get(parameter.getId().getKey());

    return addNewAccountInternal(
        customer, parameter.getCommandName() + " " + parameter.getCommandArgument());
  }
}
