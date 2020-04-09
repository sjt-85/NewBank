package newbank.server.Commands;

public class OfferCommand extends NewBankCommand {
  @Override
  public String getCommandName() {
    return "OFFER";
  }

  @Override
  public String getDescription() {
    return "<amount> <interest rate as percentage> <max borrowing length in months> "
        + System.lineSeparator()
        + "-> Offer a loan to borrowers eg OFFER 500 10% 6. "
        + System.lineSeparator()
        + "   Standard currency is GBP, please specify an account name and currency to create an account with a different currency.";
  }

  @Override
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {


  }
}
