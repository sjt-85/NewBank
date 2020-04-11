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
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {
    response.viewed(request.getCustomer().accountsToString());
  }
}
