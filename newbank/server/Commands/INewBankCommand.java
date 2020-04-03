package newbank.server.Commands;

public interface INewBankCommand {
  /**
   * this method should be overridden by a concrete NewBankCommand class and simply return the name
   * of the command
   */
  String getCommandName();

  String getDescription();

  void run(NewBankCommandRequest request, NewBankCommandResponse response);
}
