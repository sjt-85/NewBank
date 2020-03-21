package newbank.server;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import newbank.server.Account.AccountType;

public final class AccountTypeInfo {
  private final AccountType type;
  private final BigDecimal interestRate;
  private final BigDecimal overdraftLimit;
  private final BigDecimal monthlyFee;
  private final int term;
  private final ArrayList<String> otherFeatures;
  
  private static final Map<AccountType, AccountTypeInfo> accountInfo = Map.of(

      AccountType.CURRENT,
      new AccountTypeInfo(
          AccountType.CURRENT,
          new BigDecimal("0.25"),
          new BigDecimal("250"),
          new BigDecimal("0"),
          0,
          new ArrayList<String>(Arrays.asList(
              "No additional features"))),
      
      AccountType.SAVINGS,
      new AccountTypeInfo(
          AccountType.SAVINGS,
          new BigDecimal("1.5"),
          new BigDecimal("0"),
          new BigDecimal("0"),
          0,
          new ArrayList<String>(Arrays.asList(
              "No additional features"))),
      
      AccountType.CASHISA,
      new AccountTypeInfo(
          AccountType.CASHISA,
          new BigDecimal("2.25"),
          new BigDecimal("0"),
          new BigDecimal("0"),
          0,
          new ArrayList<String>(Arrays.asList(
              "No withdrawal limit, but may affect tax-free allowance",
              "Tax-free up to £20,000 allowance")))
      );
  
  private AccountTypeInfo(
      AccountType type,
      BigDecimal interestRate,
      BigDecimal overdraftLimit,
      BigDecimal monthlyFee,
      int term,
      ArrayList<String> otherFeatures) {
    
    this.type = type;
    this.interestRate = interestRate;
    this.interestRate.setScale(2);
    this.overdraftLimit = overdraftLimit;
    this.overdraftLimit.setScale(2);
    this.monthlyFee = monthlyFee;
    this.monthlyFee.setScale(2);
    this.term = term;
    this.otherFeatures = otherFeatures;
  }
  
  public static AccountTypeInfo getAccountTypeInfo(AccountType type) {
    return accountInfo.get(type);
  }
  
  @Override
  public String toString() {
    String result = String.format(
        "%s: %s" + System.lineSeparator()
        + "%s: %s" + System.lineSeparator()
        + "%s: %s" + System.lineSeparator()
        + "%s: %s" + System.lineSeparator()
        + "%s: %d",
        "Account Type", this.type.toString(),
        "Interest Rate (%)", new DecimalFormat("0.00").format(this.interestRate),
        "Overdraft Limit (GBP)", new DecimalFormat("0.00").format(this.overdraftLimit),
        "Fee (GBP/month)", new DecimalFormat("0.00").format(this.monthlyFee),
        "Term (months)", this.term
        );
    
    for (String s : this.otherFeatures) {
      result += (System.lineSeparator() + "> " + s);
    }
    
    return result;
  }
}
