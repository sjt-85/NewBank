package newbank.server.Commands;

import newbank.server.Account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewAccountTypeCommand extends newbank.server.Commands.NewBankCommand {

  @Override
  public String getCommandName() {
    return "VIEWACCOUNTTYPE";
  }

  @Override
  public String getDescription() {
    return "<account type> -> Prints details of specified account type e.g. VIEWACCOUNTTYPE \"Cash ISA\"";
  }

  @Override
  public newbank.server.Commands.NewBankCommandResponse run(
      newbank.server.Commands.NewBankCommandParameter parameter) {

    Account.AccountType accountType = parseArgument(parameter.getCommandArgument());

    if (accountType == Account.AccountType.NONE)
      return newbank.server.Commands.NewBankCommandResponse.invalidRequest("FAIL");

    newbank.server.AccountTypeInfo info =
        newbank.server.AccountTypeInfo.getAccountTypeInfo(accountType);
    if (info == null) return newbank.server.Commands.NewBankCommandResponse.failed("FAIL");

    return newbank.server.Commands.NewBankCommandResponse.succeeded(info.toString());
  }

  private static Account.AccountType parseArgument(String argument) {
    // use regex to obtain account type and name
    Pattern p = Pattern.compile("(?<accType>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)$");
    Matcher m = p.matcher(argument);

    if (!m.matches()) return Account.AccountType.NONE;

    // get account type from regex result
    String accountTypeStr = m.group("accType");

    if (accountTypeStr == null) return Account.AccountType.NONE;

    accountTypeStr = accountTypeStr.replace("\"", ""); // remove enclosing "" if present

    return Account.AccountType.getAccountTypeFromString(accountTypeStr);
  }
}
