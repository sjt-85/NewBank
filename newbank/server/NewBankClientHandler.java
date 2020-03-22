package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread {

  ClientThreadTarget target;

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

    public BufferedReader in;
    public PrintWriter out;

    public ClientThreadTarget(BufferedReader in, PrintWriter out) {
      this.in = in;
      this.out = out;
    }

    public void run() throws IOException {
      CustomerID customer = readCustomerID();

      // If max user attempts
      if (customer == null) {
        out.println("Maximum login attempts exceeded. Please contact the User Helpdesk");
        return;
      }

      printMenu(customer);

      // if the user is authenticated then get requests from the user and process them
      processRequests(customer);
    }

    public void processRequests(CustomerID customer) throws IOException {
      // keep getting requests from the client and processing them
      while (true) {
        String request = in.readLine();
        if (request == null) break;

        out.println(NewBank.getBank().processRequest(customer, request));

        // Test whether client would like to logout
        if (!customer.isLoggedIn()) break;
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

    private void printMenu(CustomerID customer) {
      out.println("Log In Successful. What do you want to do?");
      out.println();
      out.println("COMMANDS:");
      out.println(NewBank.getBank().processRequest(customer, "COMMANDS"));
    }

    private CustomerID readCustomerID() throws IOException {
      CustomerID customer = authenticate();

      // Loop continues until user gets correct password or has 3 login attempts
      for (int loginAttempts = 1; customer == null && loginAttempts < 3; loginAttempts++) {
        out.println("Log In Failed");
        customer = authenticate();
      }

      return customer;
    }

    private CustomerID authenticate() throws IOException {
      // ask for user name
      out.println("Enter Username");
      String userName = in.readLine();
      // ask for password
      out.println("Enter Password");
      String password = in.readLine();
      out.println("Checking Details...");
      // authenticate user and get customer ID token from bank for use in subsequent requests
      return NewBank.getBank().checkLogInDetails(userName, password);
    }
  }
}
