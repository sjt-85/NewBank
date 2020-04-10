package newbank.server.Commands;

public class BorrowCommand extends NewBankCommand {

  @Override
  public String getCommandName() {
    return "BORROW";
  }

  @Override
  public String getDescription() {
    return "<Offer number> <Repayment length in months>";
  }

  @Override
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {}
}
