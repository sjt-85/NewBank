package newbank.server.Commands;

public class ShowMyLoansCommand extends NewBankCommand {

  @Override
  public String getCommandName() {
    return "SHOWMYLOANS";
  }

  @Override
  public String getDescription() {
    return "-> Lists all of your active loans.";
  }

  @Override
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {
    response.succeeded(request.getCustomer().loansToString());
  }
}
