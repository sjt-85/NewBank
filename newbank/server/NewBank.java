package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private ArrayList<String> commands = new ArrayList<String>();
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
		addCommands(commands);
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
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
			switch(request) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			case "COMMANDS" : return listCommands(commands);
			case "HELP" : return listCommands(commands);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	private String listCommands(ArrayList<String> commands) {
		String printCommands = new String();
		for (String command : commands) {
			printCommands += command;
			printCommands += "\n";
		}
//		printCommands = printCommands.substring(0, printCommands.length()-1);
		return printCommands;
	}

}
