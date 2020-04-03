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
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {

    Account.AccountType accountType = getAccountType(request);

    if (request.getCommandArgument().isEmpty()) {
      response.succeeded(AccountTypeInfo.getAllAccountTypeDescriptions());
      return;
    }

    if (accountType == Account.AccountType.NONE) {
      response.invalidRequest("Invalid account type.");
      return;
    }

    AccountTypeInfo info = AccountTypeInfo.getAccountTypeInfo(accountType);
    if (info == null) {
      response.failed("Could not retrieve account info.");
      return;
    }

    response.succeeded(info.toString());
  }

  public static Account.AccountType getAccountType(NewBankCommandRequest param) {

    Matcher m = param.matchCommandArgument("(?<accType>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)$");

    return !m.matches()
        ? Account.AccountType.NONE
        : m.group("accType") == null
            ? Account.AccountType.NONE
            : Account.AccountType.getAccountTypeFromString(m.group("accType").replace("\"", ""));
  }
}
