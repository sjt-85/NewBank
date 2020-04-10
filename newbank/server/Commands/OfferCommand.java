package newbank.server.Commands;

import newbank.server.Account;
import newbank.server.Customer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class OfferCommand extends NewBankCommand {

  private final double MAXOFFER = 2500;
  private final double MAXRATE = 0.2;
  private final int MAXLENGTH = 24;

  @Override
  public String getCommandName() {
    return "OFFER";
  }

  @Override
  public String getDescription() {
    return "<amount> <interest rate as percentage> <max borrowing length in months> "
        + System.lineSeparator()
        + "-> Offer a loan to borrowers eg OFFER 500 10% 6. "
        + System.lineSeparator()
        + "   Standard currency is GBP, please specify an account name and currency to create an account with a different currency.";
  }

  @Override
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {
    String[] input = request.getCommandArgument().split(" ");

    if (input.length != 3) {
      response.invalidRequest("Incorrect number of arguments. Please try again");
      return;
    }

    if (!input[1].contains("%")) {
      response.invalidRequest(
          "Please enter correct format for amounts. Please see help and try again.");
      return;
    }

    String percentInput[] = input[1].split("%");

    if (!validDouble(input[0]) || !validDouble(percentInput[0]) || !validInt(input[2])) {
      response.invalidRequest(
          "Please enter correct format for amounts. Please see help and try again.");
      return;
    }

    BigDecimal amount =
        BigDecimal.valueOf(Double.parseDouble(input[0])).setScale(2, RoundingMode.HALF_EVEN);

    BigDecimal percent = BigDecimal.valueOf(Double.parseDouble(percentInput[0]));
    BigDecimal rate = percent.divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_EVEN);

    BigInteger length = BigInteger.valueOf(Integer.parseInt(input[2]));

    if (amount.compareTo(BigDecimal.valueOf(MAXOFFER)) > 0) {
      response.failed("Amount offered greater than " + MAXOFFER + "GBP. Please try again.");
      return;
    }

    if (amount.compareTo(BigDecimal.valueOf(0)) == 0) {
      response.failed("Amount cannot be zero. Please try again.");
      return;
    }

    if (rate.compareTo(BigDecimal.valueOf(MAXRATE)) > 0) {
      response.failed("Rate offered greater than " + MAXRATE * 100 + "%. Please try again.");
      return;
    }

    if (length.compareTo(BigInteger.valueOf(MAXLENGTH)) > 0) {
      response.failed("Length offered greater than " + MAXLENGTH + " months. Please try again.");
      return;
    }

    Customer customer = request.getCustomer();

    List<Account> lendingAccounts = customer.collectAccountsByType(Account.AccountType.LENDING);
    if (lendingAccounts.size() == 0) {
      response.failed("Lending account required to make offer. Please try again.");
      return;
    }

    List<Account> validAccounts =
        lendingAccounts.stream()
            .filter(account -> account.getBalance().compareTo(amount) >= 0)
            .collect(Collectors.toList());

    if (validAccounts.size() == 0) {
      response.failed("Not enough funds in lending accounts. Please try again.");
      return;
    }

    Account lendingAccount = chooseLendingAccount(validAccounts, response, customer);

    
  }

  private Account chooseLendingAccount(List<Account> lendingAccounts, NewBankCommandResponse response, Customer customer){
    return lendingAccounts.size()==1
            ? lendingAccounts.get(0)
            : queryAccounts(lendingAccounts, response, customer);

  }

  private Account queryAccounts(
      List<Account> lendingAccounts, NewBankCommandResponse response, Customer customer) {
    Account account;
    do {
      account =
          customer.getAccountFromName(
              response.query(
                  String.format(
                      "Please input the account name:%s",
                      lendingAccounts.stream()
                          .map(accountName -> "\"" + accountName + "\"")
                          .reduce((name1, name2) -> name1 + "," + name2)
                          .orElse(""))));

    } while (account == null);

    return account;
  }

  private boolean validDouble(String amountInput) {
    double amount;
    try {
      amount = Double.parseDouble(amountInput);
    } catch (NumberFormatException e) {
      return false;
    }
    return amount >= 0;
  }

  private boolean validInt(String amountInput) {
    double amount;
    try {
      amount = Integer.parseInt(amountInput);
    } catch (NumberFormatException e) {
      return false;
    }
    return amount > 0;
  }
}
