package newbank.server;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/** Singleton object for MarketPlace. */
public class MicroLoanMarketPlace {

  private static final MicroLoanMarketPlace instance = new MicroLoanMarketPlace();

  public static MicroLoanMarketPlace getInstance() {
    return instance;
  }

  protected MicroLoanMarketPlace() {}

  private Map<Integer, Offer> offers = new HashMap<>();

  public Map<Integer, Offer> getOffers() {
    return offers;
  }

  public BigDecimal getMaxInterestRate() {
    return new BigDecimal("0.25");
  }
}
