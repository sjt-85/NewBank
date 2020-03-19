package newbank.server;

import java.math.BigDecimal;

public class Account {
  
  private String accountName;
  private String accountType;
  private BigDecimal openingBalance;

  // allowing to enter the balance in double to make it easier to use but save for calculations
  public Account(String accountType, String accountName, double openingBalance) {
    this.accountType = accountType;
    this.accountName = accountName;
    this.openingBalance = convertDoubleToBigDecimal(openingBalance);
  }
  
  public String toString() {
    return (accountName + ": " + openingBalance);
  }
  
  public String getAccountName() {
    return this.accountName;
  }
  
  public String getAccountType() {
    return this.accountType;
  }

  private BigDecimal convertDoubleToBigDecimal(double amount) {
    BigDecimal bd = BigDecimal.valueOf(amount);
    return bd.setScale(2);
  }

}
