package newbank.server;

public enum Currency {

  GBP, EUR, USD;

  /**
   * Creates a {@linkplain Currency} if the given string matches an accepted currency or
   * null if the given currency string is invalid
   * @param currencyString 3-letter string of currency
   * @return null if string invalid, else the requested currency
   */
  public Currency createCurrency(String currencyString) {
    for (Currency allowedCurrency : Currency.values()) {
      if (allowedCurrency.name().equals(currencyString.toUpperCase())) {
        return Currency.valueOf(allowedCurrency.name());
      }
    }
    return null;
  }

}
