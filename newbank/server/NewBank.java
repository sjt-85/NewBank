package newbank.server;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewBank {

  private static final NewBank bank = new NewBank();
  private HashMap<String, Customer> customers;
  private ArrayList<String> commands = new ArrayList<String>();

  private NewBank() {
    customers = new HashMap<>();
    addTestData();
    addCommands;
  }

  private void addTestData() {
    // Password = 1
    Customer bhagy = new Customer();
    bhagy.addAccount(new Account("Main", 1000.0));
    bhagy.assignPassword("c4ca4238a0b923820dcc509a6f75849b");
    customers.put("Bhagy", bhagy);

    // Password = 2
    Customer christina = new Customer();
    christina.addAccount(new Account("Savings", 1500.0));
    christina.assignPassword("c81e728d9d4c2f636f067f89cc14862c");
    customers.put("Christina", christina);

    // Password = 3
    Customer john = new Customer();
    john.addAccount(new Account("Checking", 250.0));
    john.assignPassword("eccbc87e4b5ce2fe28308fd9f2a7baf3");
    customers.put("John", john);
  }
  
  private void addCommands(ArrayList<String> commands) {
    // user command and description
    commands.add("SHOWMYACCOUNTS -> Lists all of your active accounts.");
    commands.add("NEWACCOUNT <name of account> -> Creates a new account under specified name e.g. NEWACCOUNT Savings");
    commands.add("LOGOUT -> Ends the current banking session and logs you out of NewBank.");
  }
  
  public static NewBank getBank() {
    return bank;
  }

  public synchronized CustomerID checkLogInDetails(String userName, String password) {
    if (customers.containsKey(userName)) {
      HashGenerator generator = new HashGenerator();
      String hashedPassword = generator.generateHash(password);
      String storedPassword = customers.get(userName).retrievePassword();
      if (hashedPassword.equals(storedPassword)) {
        return new CustomerID(userName);
      }
    }
    return null;
  }

  // commands from the NewBank customer are processed in this method
  public synchronized String processRequest(CustomerID customer, String request) {
    if (customers.containsKey(customer.getKey())) {

      List<String> tokens = Arrays.asList(request.split("\\s+"));

      if (tokens.size() > 0) {
        switch (tokens.get(0)) {
          case "SHOWMYACCOUNTS":
            return showMyAccounts(customer);
          case "NEWACCOUNT":
            return addNewAccount(customer, tokens);
          case "LOGOUT":
            customer.logOut();
            return logOut(customer);
          case "COMMANDS" :
            return listCommands(commands);
          case "HELP" : 
            return listCommands(commands);
          default:
            return "FAIL";
        }
      }
    }
    return "FAIL";
  }

  private String showMyAccounts(CustomerID customer) {
    return (customers.get(customer.getKey())).accountsToString();
  }

  private String addNewAccount(CustomerID customerID, List<String> request) {
    String result = "FAIL";
    Customer customer = customers.get(customerID.getKey());

    if ((customer != null) // customer found
        && (request.size() == 2) // request is correct length
        && (!customer.hasAccountByName(request.get(1)))) { // no existing account by requested name
      customer.addAccount(new Account(request.get(1), 0));
      result = (customer.hasAccountByName(request.get(1))) ? "SUCCESS" : "FAIL";
    }
    return result;
  }
  
  private String listCommands(ArrayList<String> commands) {
    String printCommands = new String();
    for (String command : commands) {
      printCommands += command;
      printCommands += "\n";
    }
    return printCommands.substring(0, printCommands.length()-1);
  }

  private String logOut(CustomerID customerID) {
    return "Log out successful. Goodbye " + customerID.getKey();
  }
}
