package newbank.server.Commands;

import java.util.regex.Matcher;

public class PayCommand extends NewBankCommand {
  @Override
  public String getCommandName() {
    return "Pay";
  }

  @Override
  public String getDescription() {
    return "PAY <Account Number> <Amount>";
  }

  @Override
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {

    Matcher m =
        request.matchCommandArgument(
            "(?<accountNumber>-?[0-9]+)(?:[\\s]+)(?<amount>-?[0-9]+|[0-9]+\\.[0-9][0-9])$");

    response.failed("FAIL");
  }
}
