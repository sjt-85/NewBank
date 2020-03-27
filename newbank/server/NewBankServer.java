package newbank.server;

import newbank.server.Commands.*;
import newbank.test.NBUnit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NewBankServer extends Thread {

  /**
   * Register commands here when you create a new command. The order in the COMMANDS command
   * reflects the order of command objects on this collection.
   */
  public static final INewBankCommand[] DefaultCommandList =
      new INewBankCommand[] {
        new ShowMyAccountsCommand(),
        new NewAccountCommand(),
        new ViewAccountTypeCommand(),
        new TransferCommand()
      };

  private ServerSocket server;

  public NewBankServer(int port) throws IOException {
    server = new ServerSocket(port);
  }

  public void run() {
    // starts up a new client handler thread to receive incoming connections and process requests
    System.out.println("New Bank Server listening on " + server.getLocalPort());
    try {
      while (true) {
        Socket s = server.accept();
        NewBankClientHandler clientHandler = new NewBankClientHandler(s);
        clientHandler.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        server.close();
      } catch (IOException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 0 && args[0].equals("TEST")) {
      NBUnit.run();
      return;
    }
    // starts a new NewBankServer thread on a specified port number
    new NewBankServer(14002).start();
  }
}
