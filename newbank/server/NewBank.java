package newbank.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import newbank.server.Account.AccountType;

public class NewBank {

  private static final NewBank bank = new NewBank();
  private HashMap<String, Customer> customers;

  private NewBank() {
    customers = new HashMap<>();
    addTestData();
  }

  private void addTestData() {
    // Password = 1
    Customer bhagy = new Customer();
    bhagy.addAccount(new Account(AccountType.CURRENT, "Main 1", 1000.0));
    bhagy.assignPassword("c4ca4238a0b923820dcc509a6f75849b");
    customers.put("Bhagy", bhagy);

    // Password = 2
    Customer christina = new Customer();
    christina.addAccount(new Account(AccountType.SAVINGS, "Savings 1", 1500.0));
    christina.assignPassword("c81e728d9d4c2f636f067f89cc14862c");
    customers.put("Christina", christina);

    // Password = 3
    newbank.server.Customer john = new newbank.server.Customer();
    john.addAccount(new newbank.server.Account(AccountType.CURRENT, "Checking 1", 250.0));
    john.assignPassword("eccbc87e4b5ce2fe28308fd9f2a7baf3");
    customers.put("John", john);
  }

  public static NewBank getBank() {
    return bank;
  }

  public synchronized newbank.server.CustomerID checkLogInDetails(String userName, String password) {
    if (customers.containsKey(userName)) {
      HashGenerator generator = new HashGenerator();
      String hashedPassword = generator.generateHash(password);
      String storedPassword = customers.get(userName).retrievePassword();
      if (hashedPassword.equals(storedPassword)) {
        return new CustomerID(userName);
      }
    }
    return null;
  }

  // commands from the NewBank customer are processed in this method
  public synchronized String processRequest(CustomerID customer, String request) {
    if (customers.containsKey(customer.getKey())) {

      List<String> tokens = Arrays.asList(request.split("\\s+"));

      if (tokens.size() > 0) {
        switch (tokens.get(0)) {
          case "SHOWMYACCOUNTS":
            return showMyAccounts(customer);
          case "NEWACCOUNT":
            return addNewAccount(customer, tokens);
          case "LOGOUT":
            return "Log out successful. Goodbye " + customer.getKey();
          case "VIEWACCOUNTTYPE":
            return viewAccountTypeInfo(tokens);
          default:
            return "FAIL";
        }
      }
    }
    return "FAIL";
  }

  private String showMyAccounts(CustomerID customer) {
    return (customers.get(customer.getKey())).accountsToString();
  }

  private String addNewAccount(CustomerID customerID, List<String> request) {
    String result = "FAIL";
    Customer customer = customers.get(customerID.getKey());

    if ((customer != null) && (request.size() > 1)) {

      String fullUserRequest = ""; // rebuild original user request
      for (String token : request) {
        fullUserRequest += (token + " ");
      }

      // use regex to obtain account type and name
      Pattern p =
          Pattern.compile(
              "NEWACCOUNT[\\s]+(?<accType>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)(?:[\\s]+|$)(?<accName>\"[a-zA-Z0-9 ]*\"|[a-zA-Z0-9]*)(?:[\\s]+|$)(?<currency>[a-zA-Z]*)$");
      Matcher m = p.matcher(fullUserRequest.trim());

      if (m.matches()) {
        String accountName = m.group("accName"); // get account name from regex result
        String accountTypeStr = m.group("accType"); // get account type from regex result
        String currencyStr = m.group("currency"); // get currency from regex result

        if (accountTypeStr != null) {
          accountTypeStr = accountTypeStr.replace("\"", ""); // remove enclosing "" if present
          AccountType accountType = AccountType.getAccountTypeFromString(accountTypeStr);

          if (accountType != AccountType.NONE) {
            if (accountName == null || accountName.isBlank()) {
              // no name provided so build our own
              int accountNameSuffix = 1;
              accountName = (accountType.toString() + " " + accountNameSuffix);
              while (customer.hasAccount(accountName)) {
                accountName = (accountType.toString() + " " + (++accountNameSuffix));
              }
            } else {
              // remove enclosing "" if present
              accountName = accountName.replace("\"", "");
            }

            if (!customer.hasAccount(accountName)) {
              if (currencyStr == null || currencyStr.isBlank()) {
                customer.addAccount(new Account(accountType, accountName, 0));
                result =
                    (customer.hasAccount(accountType, accountName))
                        ? createAccountDescriptionWhenSuccessful(
                            accountName, accountType, Currency.GBP)
                        : "FAIL";
              } else {
                Currency acceptedCurrency = Currency.createCurrency(currencyStr);
                if (acceptedCurrency != null) { // requested currency is allowed
                  customer.addAccount(new Account(accountType, accountName, 0, acceptedCurrency));
                  result =
                      (customer.hasAccount(accountType, accountName))
                          ? createAccountDescriptionWhenSuccessful(
                              accountName, accountType, acceptedCurrency)
                          : "FAIL";
                } else {
                  return "FAIL: Currency not allowed. Accepted currencies: "
                      + Currency.listAllCurrencies();
                }
              }
            }
          }
        }
      }
    }
    return result;
  }

  private String createAccountDescriptionWhenSuccessful(
      String accountName, AccountType accountType, Currency acceptedCurrency) {
    return "SUCCESS: Opened account TYPE:\""
        + accountType.toString()
        + "\" NAME:\""
        + accountName
        + "\""
        + " CURRENCY:"
        + acceptedCurrency.name();
  }

  private String viewAccountTypeInfo(List<String> request) {
    String result = "FAIL";

    if (request.size() > 1) {

      String fullUserRequest = ""; // rebuild original user request
      for (String token : request) {
        fullUserRequest += (token + " ");
      }

      // use regex to obtain account type and name
      Pattern p =
          Pattern.compile("VIEWACCOUNTTYPE[\\s]+(?<accType>\"[a-zA-Z0-9 ]+\"|[a-zA-Z0-9]+)$");
      Matcher m = p.matcher(fullUserRequest.trim());

      if (m.matches()) {
        String accountTypeStr = m.group("accType"); // get account type from regex result

        if (accountTypeStr != null) {
          accountTypeStr = accountTypeStr.replace("\"", ""); // remove enclosing "" if present
          AccountType accountType = AccountType.getAccountTypeFromString(accountTypeStr);

          if (accountType != AccountType.NONE) {
            AccountTypeInfo info = AccountTypeInfo.getAccountTypeInfo(accountType);
            if (info != null) {
              result = info.toString();
            }
          }
        }
      }
    }
    return result;
  }
}
