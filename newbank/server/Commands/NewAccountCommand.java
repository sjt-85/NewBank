package newbank.server.Commands;

import newbank.test.NBUnit;

public  class NewAccountCommand extends newbank.server.Commands.NewBankCommand
{
    @Override
    public String getCommandName() {
        return "NEWACCOUNT";
    }

    @Override
    public newbank.server.Commands.NewBankCommandResponse run(newbank.server.Commands.NewBankCommandParameter parameter) {
        String command = "NEWACCOUNT";
        this.getCommandName().equals(command);
        NBUnit.AssertEqual(command, parameter.getCommandName());
        return null;
    }

}
