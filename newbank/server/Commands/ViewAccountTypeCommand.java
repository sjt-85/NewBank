package newbank.server.Commands;

public class ViewAccountTypeCommand extends newbank.server.Commands.NewBankCommand {
    @Override
    public String getCommandName() {
        return "VIEWACCOUNTTYPE";
    }

    @Override
    public String getDescription() {
        return "<account type> -> Prints details of specified account type e.g. VIEWACCOUNTTYPE \"Cash ISA\"";
    }

    @Override
    public newbank.server.Commands.NewBankCommandResponse run(newbank.server.Commands.NewBankCommandParameter parameter) {
        return newbank.server.Commands.NewBankCommandResponse.failed("not implemented");
    }
}
