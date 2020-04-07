package newbank.server;

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

  public double getMaxInterestRate() {
    return 0.2;
  }
}