package newbank.server.Commands;

import static newbank.server.Commands.NewBankCommandResponse.succeeded;

public class ShowMyAccountsCommand extends NewBankCommand {

  @Override
  public String getCommandName() {
    return "SHOWMYACCOUNTS";
  }

  @Override
  public String getDescription() {
    return "-> Lists all of your active accounts.";
  }

  @Override
  public NewBankCommandResponse run(NewBankCommandParameter param) {
    return succeeded(param.getCustomer().accountsToString());
  }
}
