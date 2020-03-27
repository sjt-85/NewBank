package newbank.server.Commands;

import newbank.server.Customer;

public class TransferCommand extends NewBankCommand {

  @Override
  public String getCommandName() {
    return "TRANSFER";
  }

  @Override
  public String getDescription() {
    return "<Account Name>/<Account Name>/<Amount> \n"
        + "-> Transfer from the first listed account into the second. \n"
        + "   To format add \"/\" between accounts and amount eg TRANSFER account 1/account 2/100.0";
  }

  @Override
  public NewBankCommandResponse run(NewBankCommandParameter param) {
    double amount;
    Customer customer = param.getCustomer();
    String input = param.getCommandArgument();
    // TODO make / a forbidden character in the new account command method
    String[] request = input.split("/");
    if (!customer.hasAccount(request[0])) {
      return NewBankCommandResponse.failed(
          "Unable to find account to be debited. Please try again.");
    }
    if (!customer.hasAccount(request[1])) {
      return NewBankCommandResponse.failed(
          "Unable to find account to be credited. Please try again.");
    }
    if (request[0].equals(request[1])) {
      return NewBankCommandResponse.failed(
          "The debiting and crediting accounts are the same. Please try again");
    }
    try {
      amount = Double.parseDouble(request[2]);
    } catch (NumberFormatException e) {
      return NewBankCommandResponse.failed("Amount not valid. Please try again.");
    }

    if (amount <= 0) {
      return NewBankCommandResponse.failed("Amount not valid. Please try again.");
    }

    return NewBankCommandResponse.failed("Test");
  }
}
