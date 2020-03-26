package newbank.server.Commands;

import newbank.server.Account;

import java.util.regex.Matcher;

public class ViewAccountTypeCommand extends newbank.server.Commands.NewBankCommand {

  @Override
  public String getCommandName() {
    return "VIEWACCOUNTTYPE";
  }

  @Override
  public String getDescription() {
    return "<account type> -> Prints details of specified account type e.g. VIEWACCOUNTTYPE \"Cash ISA\".";
  }

  @Override
  public newbank.server.Commands.NewBankCommandResponse run(
      newbank.server.Commands.NewBankCommandParameter param) {

    Account.AccountType accountType = getAccountType(param);

    if (accountType == Account.AccountType.NONE)
      return newbank.server.Commands.NewBankCommandResponse.invalidRequest("FAIL");

    newbank.server.AccountTypeInfo info =
        newbank.server.AccountTypeInfo.getAccountTypeInfo(accountType);
    if (info == null) return newbank.server.Commands.NewBankCommandResponse.failed("FAIL");

    return newbank.server.Commands.NewBankCommandResponse.succeeded(info.toString());
  }

  public static Account.AccountType getAccountType(
      newbank.server.Commands.NewBankCommandParameter param) {

    Matcher m = param.matchCommandArgument("(?<accType>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)$");

    return !m.matches()
        ? Account.AccountType.NONE
        : m.group("accType") == null
            ? Account.AccountType.NONE
            : Account.AccountType.getAccountTypeFromString(m.group("accType").replace("\"", ""));
  }
}
