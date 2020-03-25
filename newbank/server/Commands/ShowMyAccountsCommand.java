package newbank.server.Commands;

public class ShowMyAccountsCommand extends newbank.server.Commands.NewBankCommand {
    @Override
    public String getCommandName() {
        return "SHOWMYACCOUNTS";
    }

    @Override
    public String getDescription() {
        return "-> Lists all of your active accounts.";
    }

    @Override
    public newbank.server.Commands.NewBankCommandResponse run(newbank.server.Commands.NewBankCommandParameter parameter) {
        return newbank.server.Commands.NewBankCommandResponse.failed("not implemented");
    }
}
