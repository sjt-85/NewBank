package newbank.server.Commands;

import newbank.server.Account;
import newbank.server.AccountTypeInfo;

import java.util.regex.Matcher;

public class ViewAccountTypeCommand extends NewBankCommand {

  @Override
  public String getCommandName() {
    return "VIEWACCOUNTTYPE";
  }

  @Override
  public String getDescription() {
    return "<account type> -> Prints details of specified account type e.g. VIEWACCOUNTTYPE \"Cash ISA\".";
  }

  @Override
  public NewBankCommandResponse run(NewBankCommandParameter param) {

    Account.AccountType accountType = getAccountType(param);

    if (accountType == Account.AccountType.NONE)
      return NewBankCommandResponse.invalidRequest("FAIL");

    AccountTypeInfo info = AccountTypeInfo.getAccountTypeInfo(accountType);
    if (info == null) return NewBankCommandResponse.failed("FAIL");

    return NewBankCommandResponse.succeeded(info.toString());
  }

  public static Account.AccountType getAccountType(NewBankCommandParameter param) {

    Matcher m = param.matchCommandArgument("(?<accType>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)$");

    return !m.matches()
        ? Account.AccountType.NONE
        : m.group("accType") == null
            ? Account.AccountType.NONE
            : Account.AccountType.getAccountTypeFromString(m.group("accType").replace("\"", ""));
  }
}
