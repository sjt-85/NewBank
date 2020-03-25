package newbank.server.Commands;

import newbank.server.CustomerID;

import java.util.Arrays;
import java.util.List;

public class NewBankCommandParameter {

  /** factory method */
  public static NewBankCommandParameter create(CustomerID id, String request) {
    String commandName = getCommandName(request);
    if (commandName == null) return null;

    NewBankCommandParameter parameter = new NewBankCommandParameter();
    parameter.commandName = commandName;
    parameter.commandArgument =
        (commandName.length() == request.length())
            ? ""
            : request.substring(commandName.length() + 1);
    parameter.id = id;

    return parameter;
  }

  private String commandName;
  private String commandArgument;
  private CustomerID id;

  protected NewBankCommandParameter() {} // allow only the parse method for instantiation.

  public String getCommandName() {
    return this.commandName;
  }

  public CustomerID getId() {
    return this.id;
  }

  private static String getCommandName(String request) {
    List<String> tokens = Arrays.asList(request.split("\\s+"));
    return (tokens.size() <= 0) ? null : tokens.get(0);
  }

  public String getCommandArgument() {
    return this.commandArgument;
  }
}
