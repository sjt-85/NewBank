package newbank.server;

public class OfferNumberGenerator {

  private Integer seed = null;

  public synchronized int getNextOfferNumber() {
    if (seed == null)
      // only fall here the first time this method is called initialize the seed value
      if (MicroLoanMarketPlace.getInstance().getOffers().size() > 0) {
        seed = MicroLoanMarketPlace.getInstance().getOffers().keySet().stream().max(Integer::compareTo).get();
      } else {
        seed = 0;
      }

    return ++seed;
  }
}
