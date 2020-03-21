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
  private BigDecimal openingBalance;

  // allowing to enter the balance in double to make it easier to use but save for calculations
  public Account(AccountType accountType, String accountName, double openingBalance) {
    this.accountType = accountType;
    this.accountName = accountName;
    this.openingBalance = convertDoubleToBigDecimal(openingBalance);
  }
  
  public String toString() {
    return (accountType.toString() + ": " + accountName + ": " + openingBalance);
  }
  
  public String getAccountName() {
    return this.accountName;
  }
  
  public AccountType getAccountType() {
    return this.accountType;
  }

  private BigDecimal convertDoubleToBigDecimal(double amount) {
    BigDecimal bd = BigDecimal.valueOf(amount);
    return bd.setScale(2);
  }

}
