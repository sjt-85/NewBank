package newbank.server;

import java.util.HashMap;
import java.util.Map;

/** Singleton object for MarketPlace. */
public class MicroLoanMarketPlace {

  private static final MicroLoanMarketPlace instance = new MicroLoanMarketPlace();
  private static final OfferNumberGenerator offerNumberGenerator = new OfferNumberGenerator();

  public static MicroLoanMarketPlace getInstance() {
    return instance;
  }

  protected MicroLoanMarketPlace() {}

  private Map<Integer, Offer> offers = new HashMap<>();
  private Map<Integer, Offer> takenOffers = new HashMap<>();

  public Map<Integer, Offer> getOffers() {
    return offers;
  }

  public Map<Integer, Offer> getTakenOffers() {
    return takenOffers;
  }

  public void addOffer(Offer offer) {
    offers.put(offer.getOfferNumber(), offer);
  }

  public static synchronized int getNextOfferNumber() {
    return offerNumberGenerator.getNextOfferNumber();
  }

  public void removeOffer(int offerNumber) {
    takenOffers.put(offerNumber, offers.get(offerNumber));
    offers.remove(offerNumber);
  }
}
