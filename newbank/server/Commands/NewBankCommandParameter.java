package newbank.server.Commands;

import newbank.server.CustomerID;

import java.util.Arrays;
import java.util.List;

public class NewBankCommandParameter {

  /** factory method */
  public static NewBankCommandParameter create(CustomerID id, String request) {
    String commandName = getCommandName(request);
    if (commandName == null) return null;

    NewBankCommandParameter parameter =
        new NewBankCommandParameter(
            id,
            commandName,
            (commandName.length() == request.length())
                ? ""
                : request.substring(commandName.length() + 1));

    return parameter;
  }

  private String commandName;
  private String commandArgument;
  private CustomerID id;

  // allow only the parse method for instantiation.
  protected NewBankCommandParameter(CustomerID id, String commandName, String commandArgument) {
    this.commandName = commandName;
    this.commandArgument = commandArgument;
    this.id = id;
  }

  public String getCommandName() {
    return this.commandName;
  }

  public CustomerID getId() {
    return this.id;
  }

  public newbank.server.Customer getCustomer() {
    return newbank.server.NewBank.getBank().getCustomers().get(getId().getKey());
  }

  private static String getCommandName(String request) {
    List<String> tokens = Arrays.asList(request.split("\\s+"));
    return (tokens.size() <= 0) ? null : tokens.get(0);
  }

  public String getCommandArgument() {
    return this.commandArgument;
  }
}
