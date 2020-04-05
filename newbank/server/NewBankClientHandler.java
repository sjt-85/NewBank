package newbank.server;

import newbank.server.Commands.INewBankCommand;
import newbank.server.Commands.NewBankCommandRequest;
import newbank.server.Commands.NewBankCommandResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static newbank.server.Commands.NewBankCommandResponse.createInvalidRequest;
import static newbank.server.Commands.NewBankCommandResponse.createSucceeded;

public class NewBankClientHandler extends Thread {

  private static CommandInvoker invoker;

  public NewBankClientHandler(Socket s) throws IOException {
    invoker =
        new CommandInvoker(
            new BufferedReader(new InputStreamReader(s.getInputStream())),
            new PrintWriter(s.getOutputStream(), true),
            NewBankServer.DefaultCommandList);
  }

  public void run() {
    try {
      invoker.run();
      invoker.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      invoker.close();
    }
  }

  public static class CommandInvoker {

    // hold the original order of command names to give control of the command order of the COMMANDS
    // command to a caller.
    private Collection<String> commandsInOriginalOrder;

    private Map<String, INewBankCommand> commands;
    private BufferedReader in;
    private PrintWriter out;

    protected CommandInvoker(BufferedReader in, PrintWriter out, INewBankCommand[] commands) {
      this.in = in;
      this.out = out;
      this.commandsInOriginalOrder =
          Arrays.stream(commands).map(INewBankCommand::getCommandName).collect(Collectors.toList());
      this.commands =
          Arrays.stream(commands)
              .collect(Collectors.toMap(INewBankCommand::getCommandName, command -> command));
    }

    public CommandInvoker(InputStream in, OutputStream out, INewBankCommand[] commands) {
      this(new BufferedReader(new InputStreamReader(in)), new PrintWriter(out, true), commands);
    }

    public void run() throws IOException {
      CustomerID customer = readCustomerID();

      // If max user attempts
      if (customer == null) {
        out.println("Maximum login attempts exceeded. Please contact the User Helpdesk");
        return;
      }

      printMenu();

      // if the user is authenticated then get requests from the user and process them
      processRequests(customer);
    }

    public void processRequests(CustomerID id) throws IOException {
      // keep getting requests from the client and processing them
      while (true) {

        String line = in.readLine();
        if (line == null) break; // fall here when called by test.

        var request = NewBankCommandRequest.create(id, line);
        if (request == null) continue;

        out.println(dispatch(request).format());

        if (request.getCommandName().equals("LOGOUT")) return;
      }
    }

    private static class DispatchResult {

      public DispatchResult(INewBankCommand command, NewBankCommandResponse response) {
        this.command = command;
        this.response = response;
      }

      private INewBankCommand command;
      private NewBankCommandResponse response;

      public String format() {

        if (response.getType() == null) return "";

        switch (response.getType()) {
          case HELP:
          case INVALIDREQUEST:
            return response.getDescription().isBlank()
                ? getHelpInfo()
                : response.getDescription()
                    + System.lineSeparator()
                    + System.lineSeparator()
                    + getHelpInfo();
          default:
            return response.getDescription();
        }
      }

      private String getHelpInfo() {
        return (this.command != null)
            ? this.command.getCommandName() + " " + this.command.getDescription()
            : "Unrecognised command";
      }
    }

    private DispatchResult dispatch(NewBankCommandRequest request) {

      switch (request.getCommandName()) {
        case "LOGOUT":
          return new DispatchResult(
              null, createSucceeded("Log out successful. Goodbye " + request.getId().getKey()));
        case "COMMANDS":
        case "HELP":
          return new DispatchResult(null, createSucceeded(formatCommands()));
        default:
          return runCommand(request);
      }
    }

    private DispatchResult runCommand(NewBankCommandRequest request) {
      if (commands.containsKey(request.getCommandName())) {

        INewBankCommand command = commands.get(request.getCommandName());

        // check if user is requesting help
        if (request.getCommandArgument().matches("\\s*-([hH?]|help|HELP)\\s*$"))
          return new DispatchResult(command, NewBankCommandResponse.createHelp());

        NewBankCommandResponse response = new NewBankCommandResponse(in, out);

        command.run(request, response);

        return new DispatchResult(command, response);

      } else if (request.getCommandName().isBlank()) {
        return new DispatchResult(null, NewBankCommandResponse.EMPTY);
      } else {
        return new DispatchResult(null, createInvalidRequest("FAIL: Invalid command."));
      }
    }

    public void close() {
      try {
        in.close();
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }

    private String formatCommands() {
      return commandsInOriginalOrder.stream()
              .map(commandName -> commands.get(commandName))
              .map(command -> "> " + command.getCommandName())
              .reduce((s1, s2) -> s1 + System.lineSeparator() + s2)
              .orElse("")
          + System.lineSeparator()
          + "> HELP / COMMANDS -> Show command list."
          + System.lineSeparator()
          + "> LOGOUT -> Ends the current banking session and logs you out of NewBank."
          + System.lineSeparator()
          + System.lineSeparator()
          + "Append -?, -h or -help for command description e.g. \"NEWACCOUNT -help\".";
    }

    private void printMenu() {
      out.println("Log In Successful. What do you want to do?");
      out.println();
      out.println("COMMANDS:");
      out.println(formatCommands());
    }

    private CustomerID readCustomerID() throws IOException {
      String userName = null;
      // Loop until correct user name entered
      while (userName == null) {
        userName = checkUserName();
      }

      CustomerID customer = authenticate(userName);
      // Loop continues until user gets correct password or has 3 login attempts
      for (int loginAttempts = 1; customer == null && loginAttempts < 3; loginAttempts++) {
        out.println("Log In Failed");
        customer = authenticate(userName);
      }

      return customer;
    }

    private String checkUserName() throws IOException {
      // ask for user name
      out.println("Enter Username");
      String userName = in.readLine();
      if (NewBank.getBank().isValidUserName(userName)) {
        return userName;
      } else {
        out.println("Invalid Username - please try again");
      }
      return null;
    }

    private CustomerID authenticate(String userName) throws IOException {
      // ask for password
      out.println("Enter Password");
      String password = in.readLine();
      out.println("Checking Details...");
      // authenticate user and get customer ID token from bank for use in subsequent requests
      return NewBank.getBank().checkLogInDetails(userName, password);
    }
  }
}
