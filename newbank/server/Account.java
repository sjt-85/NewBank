package newbank.server;

import java.math.BigDecimal;
import java.util.Locale;

public class Account {

  private static final Locale accountNameLocale = Locale.ENGLISH;

  public static boolean compareAccountName(String accountName1, String accountName2) {
    return normalizeAccountName(accountName1).equals(normalizeAccountName(accountName2));
  }

  public enum AccountType {
    NONE {
      @Override
      public String toString() {
        return "";
      }
    },
    CURRENT {
      @Override
      public String toString() {
        return "Current Account";
      }
    },
    SAVINGS {
      @Override
      public String toString() {
        return "Savings Account";
      }
    },
    CASHISA {
      @Override
      public String toString() {
        return "Cash ISA";
      }
    };

    public static AccountType getAccountTypeFromString(String s) {
      if (s != null) {
        for (AccountType a : AccountType.values()) {
          if (a.toString().equalsIgnoreCase(s)) {
            return a;
          }
        }
      }
      return AccountType.NONE;
    }
  }

  private String accountName;
  private int accountNumber;
  private AccountType accountType;
  private BigDecimal balance;
  private Currency currency;

  // 1. allowing to enter the balance in double to make it easier to use but save for calculations
  // 2. This constructor is used only for TestData setup.
  //    Specifying an account number explicitly makes it easier to test.
  public Account(
          AccountType accountType, String accountName, double openingBalance, int accountNumber) {
    this(accountType, accountName, openingBalance, Currency.GBP, accountNumber);
  }

  // an account number is automatically generated.
  public Account(
          AccountType accountType, String accountName, double openingBalance, Currency currency) {
    this(accountType, accountName, openingBalance, currency, getNextAccountNumber());
  }

  protected Account(
          AccountType accountType,
          String accountName,
          double openingBalance,
          Currency currency,
          int accountNumber) {

    this.accountType = accountType;
    this.accountName = accountName;
    this.balance = convertDoubleToBigDecimal(openingBalance);
    this.currency = currency;
    this.accountNumber = accountNumber;
  }

  public String toString() {
    return (accountType.toString()
            + ": "
            + accountName
            + " ("
            + String.format("%03d", accountNumber)
            + "): "
            + balance
            + " "
            + currency.name());
  }

  public String getAccountName() {
    return this.accountName;
  }

  public AccountType getAccountType() {
    return this.accountType;
  }

  public BigDecimal getBalance() {
    return this.balance;
  }

  public Currency getCurrency() {
    return this.currency;
  }

  public int getAccountNumber() {
    return accountNumber;
  }

  // As not called by commandline used bigdecimal
  public void moneyIn(BigDecimal in) {
    this.balance = this.balance.add(in);
  }

  public void moneyOut(BigDecimal out) {
    this.balance = this.balance.subtract(out);
  }

  private static BigDecimal convertDoubleToBigDecimal(double amount) {
    BigDecimal bd = BigDecimal.valueOf(amount);
    return bd.setScale(2);
  }

  private static int getNextAccountNumber() {
    return NewBankServer.getNextAccountNumber();
  }

  private static String normalizeAccountName(String accountName) {
    return accountName.toLowerCase(accountNameLocale);
  }
}
