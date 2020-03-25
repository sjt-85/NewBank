package newbank.server.Commands;

/**
 * This class fundamental implements fundamental functionalities that are shared by all the concrete
 * NewBankCommand classes.
 */
public abstract class NewBankCommand implements newbank.server.Commands.INewBankCommand {

  @Override
  public abstract String getCommandName();
}
