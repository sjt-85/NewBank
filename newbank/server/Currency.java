package newbank.server;

public enum Currency {

  GBP, EUR, USD;

  /**
   * Creates a {@linkplain Currency} if the given string matches an accepted currency or
   * null if the given currency string is invalid
   * @param currencyString 3-letter string of currency
   * @return null if string invalid, else the requested currency
   */
  public static Currency createCurrency(String currencyString) {
    for (Currency allowedCurrency : Currency.values()) {
      if (allowedCurrency.name().equals(currencyString.toUpperCase())) {
        return Currency.valueOf(allowedCurrency.name());
      }
    }
    return null;
  }

  /**
   * @return String listing all allowed currencies
   */
  public static String listAllCurrencies() {
    StringBuilder sb = new StringBuilder();
    for (Currency currency : Currency.values()) {
      sb.append(currency.name());
      sb.append(", ");
    }
    sb.deleteCharAt(sb.length() - 2); // delete last comma
    return sb.toString();
  }
}
