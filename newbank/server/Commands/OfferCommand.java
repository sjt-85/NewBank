package newbank.server.Commands;

public class OfferCommand extends NewBankCommand {
  @Override
  public String getCommandName() {
    return "OFFER";
  }

  @Override
  public String getDescription() {
    return "<account type> <optional: account name> <optional: currency> "
        + System.lineSeparator()
        + "-> Offer a loan to borrowers eg OFFER \"Savings Account\" \"my savings\" EUR. "
        + System.lineSeparator()
        + "   Standard currency is GBP, please specify an account name and currency to create an account with a different currency.";
  }

  @Override
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {}
}
