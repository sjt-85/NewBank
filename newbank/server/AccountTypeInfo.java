package newbank.server;

import newbank.server.Account.AccountType;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static newbank.server.NewBank.createDecimal;

public final class AccountTypeInfo {
  private final AccountType type;
  private final BigDecimal interestRate;
  private final BigDecimal overdraftLimit;
  private final BigDecimal monthlyFee;
  private final int term;
  private final ArrayList<String> otherFeatures;

  private static final Map<AccountType, AccountTypeInfo> accountInfo;

  static {
    accountInfo =
        Map.of(
            AccountType.CURRENT,
            new AccountTypeInfo(
                AccountType.CURRENT,
                createDecimal("0.25"),
                createDecimal("250"),
                createDecimal("0"),
                0,
                new ArrayList<String>(Arrays.asList("No additional features"))),
            AccountType.SAVINGS,
            new AccountTypeInfo(
                AccountType.SAVINGS,
                createDecimal("1.5"),
                createDecimal("0"),
                createDecimal("0"),
                0,
                new ArrayList<String>(Arrays.asList("No additional features"))),
            AccountType.LENDING,
            new AccountTypeInfo(
                AccountType.LENDING,
                createDecimal("0.0"),
                createDecimal("0.0"),
                createDecimal("0"),
                0,
                new ArrayList<String>(
                    Arrays.asList("Money can be borrowed by other New Bank customers"))),
            AccountType.CASHISA,
            new AccountTypeInfo(
                AccountType.CASHISA,
                createDecimal("2.25"),
                createDecimal("0"),
                createDecimal("0"),
                0,
                new ArrayList<String>(
                    Arrays.asList(
                        "No withdrawal limit, but may affect tax-free allowance",
                        "Tax-free up to £20,000 allowance"))));
  }

  private AccountTypeInfo(
      AccountType type,
      BigDecimal interestRate,
      BigDecimal overdraftLimit,
      BigDecimal monthlyFee,
      int term,
      ArrayList<String> otherFeatures) {

    this.type = type;
    this.interestRate = interestRate;
    this.overdraftLimit = overdraftLimit;
    this.monthlyFee = monthlyFee;
    this.term = term;
    this.otherFeatures = otherFeatures;
  }

  public static String getAllAccountTypeDescriptions() {
    StringBuilder sb = new StringBuilder();
    Map<AccountType, AccountTypeInfo> sortedAccountInfo =
        new TreeMap<AccountType, AccountTypeInfo>(accountInfo);
    for (AccountType accType : sortedAccountInfo.keySet()) {
      sb.append(accountInfo.get(accType));
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());
    }
    String descriptions = sb.toString().trim();
    return descriptions;
  }

  public static AccountTypeInfo getAccountTypeInfo(AccountType type) {
    return accountInfo.get(type);
  }

  @Override
  public String toString() {
    String result =
        String.format(
            "%s: %s"
                + System.lineSeparator()
                + "%s: %s"
                + System.lineSeparator()
                + "%s: %s"
                + System.lineSeparator()
                + "%s: %s"
                + System.lineSeparator()
                + "%s: %d",
            "Account Type",
            this.type.toString(),
            "Interest Rate (%)",
            new DecimalFormat("0.00").format(this.interestRate),
            "Overdraft Limit (GBP)",
            new DecimalFormat("0.00").format(this.overdraftLimit),
            "Fee (GBP/month)",
            new DecimalFormat("0.00").format(this.monthlyFee),
            "Term (months)",
            this.term);

    for (String s : this.otherFeatures) {
      result += (System.lineSeparator() + "> " + s);
    }

    return result;
  }

  /** @return String listing all allowed account types */
  public static String listAllAccountTypesCommaDelimited() {
    StringBuilder sb = new StringBuilder();
    for (AccountType type : AccountType.values()) {
      if (type == AccountType.NONE) continue;
      sb.append(type.toString());
      sb.append(", ");
    }
    sb.deleteCharAt(sb.length() - 2); // delete last comma
    return sb.toString().trim();
  }
}
