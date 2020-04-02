package newbank.server;

import newbank.server.Commands.INewBankCommand;
import newbank.server.Commands.NewBankCommandParameter;
import newbank.server.Commands.NewBankCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class NewBankClientHandler extends Thread {

  private static CommandInvoker invoker;

  public NewBankClientHandler(Socket s) throws IOException {
    InputStream inputStream = s.getInputStream();
    OutputStream outputStream = s.getOutputStream();
    invoker = new CommandInvoker(inputStream, outputStream, NewBankServer.DefaultCommandList);
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

        String request = in.readLine();
        if (request == null) break; // fall here when called by test.

        var parameter = NewBankCommandParameter.create(id, request);
        if (parameter == null) continue;

        out.println(formatResponse(dispatch(parameter)));

        if (parameter.getCommandName().equals("LOGOUT")) return;
      }
    }

    private NewBankCommandResponse dispatch(NewBankCommandParameter parameter) {

      switch (parameter.getCommandName()) {
        case "LOGOUT":
          return NewBankCommandResponse.succeeded(
              "Log out successful. Goodbye " + parameter.getId().getKey());
        case "COMMANDS":
        case "HELP":
          return NewBankCommandResponse.succeeded(formatCommands());
        default:
          if (commands.containsKey(parameter.getCommandName()))
            return commands.get(parameter.getCommandName()).run(parameter);
          else return NewBankCommandResponse.invalidRequest("FAIL");
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
              .map(command -> command.getCommandName() + " " + command.getDescription())
              .reduce((s1, s2) -> s1 + System.lineSeparator() + s2)
              .orElse("")
          + System.lineSeparator()
          + "HELP / COMMANDS -> Show command list."
          + System.lineSeparator()
          + "LOGOUT -> Ends the current banking session and logs you out of NewBank.";
    }

    private static String formatResponse(NewBankCommandResponse response) {
      // todo: place holder for formatting a response
      return response.getDescription();
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
      return newbank.server.NewBank.getBank().checkLogInDetails(userName, password);
    }
  }
}
