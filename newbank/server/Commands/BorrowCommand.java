package newbank.server.Commands;

public class BorrowCommand extends NewBankCommand {

  @Override
  public String getCommandName() {
    return "BORROW";
  }

  @Override
  public String getDescription() {
    return "<Offer number> <Optional: Repayment length in months>";
  }

  @Override
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {
    String[] input = request.getCommandArgument().split(" ");

    if (input.length > 2) {
      response.invalidRequest("Incorrect number of arguments. Please try again");
      return;
    }
  }

  private boolean validInt(String amountInput) {
    double amount;
    try {
      amount = Integer.parseInt(amountInput);
    } catch (NumberFormatException e) {
      return false;
    }
    return amount > 0;
  }
}
