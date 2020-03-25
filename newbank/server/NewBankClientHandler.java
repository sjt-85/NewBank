package newbank.server;

import newbank.server.Commands.NewBankCommandParameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class NewBankClientHandler extends Thread {

  // todo: change private instance field later
  public static ClientThreadTarget target;

  public NewBankClientHandler(Socket s) throws IOException {
    target =
        new ClientThreadTarget(
            new BufferedReader(new InputStreamReader(s.getInputStream())),
            new PrintWriter(s.getOutputStream(), true));
  }

  public void run() {
    try {
      target.run();
      target.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      target.close();
    }
  }

  public static class ClientThreadTarget {

    private static ArrayList<String> commands = new ArrayList<String>();
    public BufferedReader in;
    public PrintWriter out;

    static {
      newbank.server.NewBankClientHandler.ClientThreadTarget.addCommands(
          newbank.server.NewBankClientHandler.ClientThreadTarget.commands);
    }

    public ClientThreadTarget(BufferedReader in, PrintWriter out) {
      this.in = in;
      this.out = out;
    }

    private static String listCommands(ArrayList<String> commands) {
      String printCommands = new String();
      for (String command : commands) {
        printCommands += command;
        printCommands += "\n";
      }
      return printCommands.substring(0, printCommands.length() - 1);
    }

    private static void addCommands(ArrayList<String> commands) {
      // user command and description
      commands.add("SHOWMYACCOUNTS -> Lists all of your active accounts.");
      commands.add(
          "NEWACCOUNT <account type> <optional: account name> <optional: currency> \n"
              + "-> Creates a new account of specified type e.g. NEWACCOUNT \"Savings Account\" \"my savings\" EUR \n"
              + "Standard currency is GBP, please specify an account name and currency to create an account with a different currency.");
      commands.add("LOGOUT -> Ends the current banking session and logs you out of NewBank.");
      commands.add(
          "VIEWACCOUNTTYPE <account type> -> Prints details of specified account type e.g. VIEWACCOUNTTYPE \"Cash ISA\"");
    }

    public void run() throws IOException {
      newbank.server.CustomerID customer = readCustomerID();

      // If max user attempts
      if (customer == null) {
        out.println("Maximum login attempts exceeded. Please contact the User Helpdesk");
        return;
      }

      printMenu();

      // if the user is authenticated then get requests from the user and process them
      processRequests(customer);
    }

    public void processRequests(newbank.server.CustomerID id) throws IOException {
      // keep getting requests from the client and processing them
      while (true) {

        String request = in.readLine();
        if (request == null) break; // fall here when called by test.

        var parameter = newbank.server.Commands.NewBankCommandParameter.create(id, request);
        if (parameter == null) continue;

        out.println(dispatch(request, parameter));

        if (parameter.getCommandName().equals("LOGOUT")) return;
      }
    }

    private static String dispatch(String request, NewBankCommandParameter parameter) {

      switch (parameter.getCommandName()) {
        case "LOGOUT":
          return "Log out successful. Goodbye " + parameter.getId().getKey();
        case "COMMANDS":
        case "HELP":
          return listCommands(commands);
        default:
          return invokeLegacyDispatcher(request, parameter);
      }
    }

    // todo: remove this method and its call when the Command Pattern refactoring is done.
    private static String invokeLegacyDispatcher(
        String request, NewBankCommandParameter parameter) {
      return newbank.server.NewBank.getBank().processRequest(parameter.getId(), request);
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

    private void printMenu() {
      out.println("Log In Successful. What do you want to do?");
      out.println();
      out.println("COMMANDS:");
      out.println(listCommands(commands));
    }

    private newbank.server.CustomerID readCustomerID() throws IOException {
      newbank.server.CustomerID customer = authenticate();

      // Loop continues until user gets correct password or has 3 login attempts
      for (int loginAttempts = 1; customer == null && loginAttempts < 3; loginAttempts++) {
        out.println("Log In Failed");
        customer = authenticate();
      }

      return customer;
    }

    private newbank.server.CustomerID authenticate() throws IOException {
      // ask for user name
      out.println("Enter Username");
      String userName = in.readLine();
      // ask for password
      out.println("Enter Password");
      String password = in.readLine();
      out.println("Checking Details...");
      // authenticate user and get customer ID token from bank for use in subsequent requests
      return newbank.server.NewBank.getBank().checkLogInDetails(userName, password);
    }
  }
}
