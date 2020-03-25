package newbank.server.Commands;

import newbank.test.NBUnit;

public class NewAccountCommand extends newbank.server.Commands.NewBankCommand {
  @Override
  public String getCommandName() {
    return "NEWACCOUNT";
  }

  @Override
  public newbank.server.Commands.NewBankCommandResponse run(
      newbank.server.Commands.NewBankCommandParameter parameter) {

    NBUnit.AssertEqual("\"Savings Account\" Saving", parameter.getCommandArgument());


    String description = "SUCCESS: Opened account TYPE:\"Savings Account\" NAME:\"Saving\" CURRENCY:GBP";

    return newbank.server.Commands.NewBankCommandResponse.succeeded(description);
  }
}
