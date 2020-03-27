package newbank.server;

import java.math.BigDecimal;

public class Account {

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
  private AccountType accountType;
  private BigDecimal balance;
  private Currency currency;

  // allowing to enter the balance in double to make it easier to use but save for calculations
  public Account(AccountType accountType, String accountName, double openingBalance) {
    this.accountType = accountType;
    this.accountName = accountName;
    this.balance = convertDoubleToBigDecimal(openingBalance);
    this.currency = Currency.GBP;
  }

  public Account(
      AccountType accountType, String accountName, double openingBalance, Currency currency) {
    this.accountType = accountType;
    this.accountName = accountName;
    this.balance = convertDoubleToBigDecimal(openingBalance);
    this.currency = currency;
  }

  public String toString() {
    return (accountType.toString() + ": " + accountName + ": " + balance + " " + currency.name());
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

  public void moneyIn(double amount) {
    BigDecimal in = convertDoubleToBigDecimal(amount);
    this.balance = balance.add(in);
  }

  public void moneyOut(double amount) {
    BigDecimal out = convertDoubleToBigDecimal(-amount);
    this.balance = balance.add(out);
  }

  private BigDecimal convertDoubleToBigDecimal(double amount) {
    BigDecimal bd = BigDecimal.valueOf(amount);
    return bd.setScale(2);
  }
}
