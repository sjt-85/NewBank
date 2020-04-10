package newbank.server;

import newbank.server.Commands.INewBankCommand;
import newbank.server.Commands.MoveCommand;
import newbank.server.Commands.NewAccountCommand;
import newbank.server.Commands.OfferCommand;
import newbank.server.Commands.PayCommand;
import newbank.server.Commands.ShowMyAccountsCommand;
import newbank.server.Commands.ViewAccountTypeCommand;
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
        new PayCommand(),
        new MoveCommand(),
        new OfferCommand()
      };

  private ServerSocket server;
  private static final AccountNumberGenerator accountNumberGenerator = new AccountNumberGenerator();

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

  public static synchronized int getNextAccountNumber() {
    return accountNumberGenerator.getNextAccountNumber();
  }
}
