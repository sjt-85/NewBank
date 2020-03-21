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
    target.run();
  }

  public static class ClientThreadTarget {

    private String userName;
    private String password;

    public BufferedReader in;
    public PrintWriter out;

    public ClientThreadTarget(BufferedReader in, PrintWriter out) {
      this.in = in;
      this.out = out;
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

    public void run() {
      // keep getting requests from the client and processing them
      try {
        // Ask for Username and password
        requestLoginDetails();
        // record how many login attempts
        int loginAttempts = 1;
        // authenticate user and get customer ID token from bank for use in subsequent requests
        CustomerID customer = NewBank.getBank().checkLogInDetails(userName, password);
        // Loop continues until user gets correct password or has 3 login attempts
        while (customer == null && loginAttempts < 3) {
          out.println("Log In Failed");
          requestLoginDetails();
          customer = NewBank.getBank().checkLogInDetails(userName, password);
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
            if (request == null) break;
            System.out.println("Request from " + customer.getKey());
            String responce = NewBank.getBank().processRequest(customer, request);
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
  }
}
