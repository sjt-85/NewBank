package newbank.server.Commands;

import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewBankCommandRequest {

  /** factory method */
  public static NewBankCommandRequest create(CustomerID id, String request) {
    String commandName = getCommandName(request);
    if (commandName == null) return null;

    NewBankCommandRequest parameter =
        new NewBankCommandRequest(
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

  // only parse method can instantiate
  protected NewBankCommandRequest(CustomerID id, String commandName, String commandArgument) {
    this.commandName = commandName.toUpperCase();
    this.commandArgument = commandArgument.trim();
    this.id = id;
  }

  public Matcher matchCommandArgument(String regex) {
    // use regex to obtain account type and name
    Pattern p = Pattern.compile(regex);
    return p.matcher(getCommandArgument());
  }

  public String getCommandName() {
    return this.commandName;
  }

  public CustomerID getId() {
    return this.id;
  }

  public Customer getCustomer() {
    return NewBank.getBank().getCustomers().get(getId().getKey());
  }

  private static String getCommandName(String request) {
    List<String> tokens = Arrays.asList(request.split("\\s+"));
    return (tokens.size() <= 0) ? null : tokens.get(0);
  }

  public String getCommandArgument() {
    return this.commandArgument;
  }
}
