package newbank.server;

import java.math.BigDecimal;

public class CurrencyConverter {
  private static final double GBPTOUSD = 1.24;
  private static final double GBPTOEUR = 1.14;
  private static final double EURTOUSD = 1.09;
  private static final double SELFTOSELF = 1.00;

  public BigDecimal convertToGBP(Currency currency, BigDecimal amount) {
    double exchangeRate = 0;
    switch (currency) {
      case GBP:
        exchangeRate = SELFTOSELF;
        break;
      case EUR:
        exchangeRate = GBPTOEUR;
        break;
      case USD:
        exchangeRate = GBPTOUSD;
    }
    return BigDecimal.valueOf(exchangeRate).multiply(amount);
  }

  public BigDecimal convertToEur(Currency currency, BigDecimal amount) {
    double exchangeRate = 0;
    switch (currency) {
      case GBP:
        exchangeRate = 1 / GBPTOEUR;
        break;
      case EUR:
        exchangeRate = SELFTOSELF;
        break;
      case USD:
        exchangeRate = EURTOUSD;
    }
    return BigDecimal.valueOf(exchangeRate).multiply(amount);
  }

  public BigDecimal convertToUsd(Currency currency, BigDecimal amount) {
    double exchangeRate = 0;
    switch (currency) {
      case GBP:
        exchangeRate = 1 / GBPTOUSD;
        break;
      case EUR:
        exchangeRate = 1 / EURTOUSD;
        break;
      case USD:
        exchangeRate = SELFTOSELF;
    }
    return BigDecimal.valueOf(exchangeRate).multiply(amount);
  }
}
