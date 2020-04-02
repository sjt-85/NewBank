package newbank.server.Commands;

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
    return NewBankCommandResponse.succeeded(this, param.getCustomer().accountsToString());
  }
}
