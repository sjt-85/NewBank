package newbank.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NewBankServer extends Thread {

  /** register commands here */
  public static final newbank.server.Commands.INewBankCommand[] DefaultCommandList =
      new newbank.server.Commands.INewBankCommand[] {
        new newbank.server.Commands.NewAccountCommand(),
        new newbank.server.Commands.ShowMyAccountsCommand(),
        new newbank.server.Commands.ViewAccountTypeCommand()
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
      newbank.test.NBUnit.run();
      return;
    }
    // starts a new NewBankServer thread on a specified port number
    new NewBankServer(14002).start();
  }
}
