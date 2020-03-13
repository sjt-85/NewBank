package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread {

  private NewBank bank;
  private BufferedReader in;
  private PrintWriter out;
  private String userName;
  private String password;

  public NewBankClientHandler(Socket s) throws IOException {
    bank = NewBank.getBank();
    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    out = new PrintWriter(s.getOutputStream(), true);
  }

  public void run() {
    // keep getting requests from the client and processing them
    try {
      // Ask for Username and password
      requestLoginDetails();
      // record how many login attempts
      int loginAttempts = 1;
      // authenticate user and get customer ID token from bank for use in subsequent requests
      CustomerID customer = bank.checkLogInDetails(userName, password);
      // Loop continues until user gets correct password or has 3 login attempts
      while (customer == null && loginAttempts < 3) {
        out.println("Log In Failed");
        requestLoginDetails();
        customer = bank.checkLogInDetails(userName, password);
        loginAttempts++;
      }
      // If max user attempts
      if (customer == null) {
        out.println("Maximum login attempts exceeded. Please contact the User Helpdesk");
      } else {
        // if the user is authenticated then get requests from the user and process them
        out.println("Log In Successful. What do you want to do?");
        while (true) {
          String request = in.readLine();
          System.out.println("Request from " + customer.getKey());
          String responce = bank.processRequest(customer, request);
          out.println(responce);
          // Test whether client would like to logout
          if (!customer.isLoggedIn()) break;
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        in.close();
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }

  private void requestLoginDetails() {
    try {
      // ask for user name
      out.println("Enter Username");
      userName = in.readLine();
      // ask for password
      out.println("Enter Password");
      password = in.readLine();
      out.println("Checking Details...");
    } catch (IOException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
    }
  }
}
