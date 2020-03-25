package newbank.server.Commands;

import java.util.Arrays;
import java.util.List;

public class NewBankCommandParameter {

  /** factory method */
  public static NewBankCommandParameter parse(String request) {
    String commandName = getCommandName(request);
    if (commandName == null) return null;

    NewBankCommandParameter parameter = new NewBankCommandParameter();
    parameter.commandName = commandName;

    return parameter;
  }

  private String commandName;

  protected NewBankCommandParameter() {} // allow only the parse method for instantiation.

  public String getCommandName() {
    return this.commandName;
  }

    private static String getCommandName(String request) {
        List<String> tokens = Arrays.asList(request.split("\\s+"));
        return (tokens.size() <= 0) ? null : tokens.get(0);
    }
}
