package newbank.server;

import java.util.HashMap;

public class NewBank {
  
  private static final NewBank bank = new NewBank();
  private HashMap<String,Customer> customers;
  
  private NewBank() {
    customers = new HashMap<>();
    addTestData();
  }
  
  private void addTestData() {
    Customer bhagy = new Customer();
    bhagy.addAccount(new Account("Main", 1000.0));
    customers.put("Bhagy", bhagy);
    
    Customer christina = new Customer();
    christina.addAccount(new Account("Savings", 1500.0));
    customers.put("Christina", christina);
    
    Customer john = new Customer();
    john.addAccount(new Account("Checking", 250.0));
    customers.put("John", john);
  }
  
  public static NewBank getBank() {
    return bank;
  }
  
  public synchronized CustomerID checkLogInDetails(String userName, String password) {
    if(customers.containsKey(userName)) {
      return new CustomerID(userName);
    }
    return null;
  }

  // commands from the NewBank customer are processed in this method
  public synchronized String processRequest(CustomerID customer, String request) {
    if (customers.containsKey(customer.getKey())) {
      
      String[] tokens = request.split("\\s+");
      
      if (tokens.length > 0) {       
        switch(tokens[0]) {
        case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
        case "NEWACCOUNT" : return addNewAccount(customer, tokens);
        default : return "FAIL";
        }
      }
    }
    return "FAIL";
  }
  
  private String showMyAccounts(CustomerID customer) {
    return (customers.get(customer.getKey())).accountsToString();
  }
  
  private String addNewAccount(CustomerID customerID, String[] request) {
    String result = "FAIL";
    Customer customer = customers.get(customerID.getKey());
    
    if ((customer != null) && (request.length == 2) && (!customer.hasAccountByName(request[1]))) {
      customer.addAccount(new Account(request[1], 0));
      result = customer.hasAccountByName(request[1]) ? "SUCCESS" : "FAIL";
    }
    return result;
  }

}
