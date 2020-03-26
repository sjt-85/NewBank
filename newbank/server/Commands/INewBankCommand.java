package newbank.server.Commands;

public interface INewBankCommand {
  /**
   * this method should be overridden by a concrete NewBankCommand class and simply return the name
   * of the command
   */
  String getCommandName();

  String getDescription();

  NewBankCommandResponse run(NewBankCommandParameter param);
}
