package newbank.server;

import java.math.BigDecimal;

public class Account {
  
  private String accountName;
  private BigDecimal openingBalance;

  // allowing to enter the balance in double to make it easier to use but save for calculations
  public Account(String accountName, double openingBalance) {
    this.accountName = accountName;
    this.openingBalance = BigDecimal.valueOf(openingBalance);
  }
  
  public String toString() {
    return (accountName + ": " + openingBalance);
  }
  
  public String getAccountName() {
    return this.accountName;
  }

}
