package newbank.server.Commands;

import static newbank.server.NewBank.createDecimal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import newbank.server.MicroLoanMarketPlace;
import newbank.server.Offer;

public class ViewOffersCommand extends NewBankCommand {

  @Override
  public String getCommandName() {
    return "VIEWOFFERS";
  }

  @Override
  public String getDescription() {
    return "<optional: Minimum Loan Amount>"
        + System.lineSeparator()
        + "-> view available microloan offers that match selected criteria."
        + System.lineSeparator()
        + "   e.g. VIEWOFFERS 250";
  }
  
  protected static class OfferParams {
    public final BigDecimal maxInterestRate;
    public final BigDecimal minAmount;
    public final BigDecimal maxAmount;
    public final int minTerm;
    public final int maxTerm;
    public final int offerNumber;
    
    public static final int INVALID = -1;
    
    protected OfferParams(
        BigDecimal maxInterestRate,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        int minTerm,
        int maxTerm,
        int offerNumber) {
      this.maxInterestRate = maxInterestRate;
      this.minAmount = minAmount;
      this.maxAmount = maxAmount;
      this.minTerm = minTerm;
      this.maxTerm = maxTerm;
      this.offerNumber = offerNumber;
    }
  }

  @Override
  public void run(NewBankCommandRequest request, NewBankCommandResponse response) {

    OfferParams params = getOfferParams(request);
    Map<Integer, Offer> matchingOffers = MicroLoanMarketPlace.getInstance().getOffers();

    Predicate<Offer> filterTests = o ->
           ((params.offerNumber == OfferParams.INVALID) || (o.getOfferNumber() == params.offerNumber))
        && ((params.minTerm == OfferParams.INVALID) || (o.getBorrowingLengthInMonth() >= params.minTerm))
        && ((params.maxTerm == OfferParams.INVALID) || (o.getBorrowingLengthInMonth() <= params.maxTerm))
        && ((params.minAmount.intValue() == OfferParams.INVALID) || (o.getAmount().compareTo(params.minAmount) != -1))
        && ((params.maxAmount.intValue() == OfferParams.INVALID) || (o.getAmount().compareTo(params.maxAmount) != 1))
        && ((params.maxInterestRate.intValue() == OfferParams.INVALID) || (o.getInterestRate().compareTo(params.maxInterestRate) != 1));
    
    // filter based on params
    matchingOffers = filterOffersByValue(matchingOffers, filterTests);
    
    //sort based on rate (low->high), then amount (low->high), then max term (high->low)
    List<Offer> sortedOffers = sortOffers(matchingOffers);
    
    if (matchingOffers.size() > 0) {
      response.succeeded(printOffers(sortedOffers));
    } else {
      response.failed("FAIL: No matching offers found.");
    }
  }
  
  private static Map<Integer, Offer> filterOffersByValue(Map<Integer, Offer> offers, Predicate<Offer> predicate) {
    return offers.entrySet()
        .stream()
        .filter(entry -> predicate.test(entry.getValue()))
        .collect(Collectors.toMap(o -> o.getKey(), o -> o.getValue()));
  }

  private static OfferParams getOfferParams(NewBankCommandRequest request) {
    
    BigDecimal maxInterestRate = BigDecimal.valueOf(OfferParams.INVALID);
    BigDecimal minAmount = BigDecimal.valueOf(OfferParams.INVALID);
    BigDecimal maxAmount = BigDecimal.valueOf(OfferParams.INVALID);
    int minTerm = OfferParams.INVALID;
    int maxTerm = OfferParams.INVALID;
    int offerNumber = OfferParams.INVALID;
    
    Matcher m = request.matchCommandArgument("(?<amount>-?[0-9]+|[0-9]+\\.[0-9][0-9])$");
    
    if (m.matches() && validAmount(m.group("amount"))) {
      minAmount = createDecimal(Double.parseDouble(m.group("amount")));
    }
    
    // Activate more filters here

    return new OfferParams(
        maxInterestRate,
        minAmount,
        maxAmount,
        minTerm,
        maxTerm,
        offerNumber);
  }
  
  private static String printOffers(List<Offer> offers) {
    final int maxCount = 10;
    StringBuilder sb = new StringBuilder(
        offers.size() + " result(s) found, max " + maxCount + " results printed, apply filters if necessary.");
    sb.append(System.lineSeparator());
    
    offers.stream()
        .limit(maxCount)
        .forEach(o -> sb.append(System.lineSeparator() + o.toString()));
    
    return sb.toString();
  }
  
  private static boolean validAmount(String amountInput) {
    double amount;
    try {
      amount = Double.parseDouble(amountInput);
    } catch (NumberFormatException e) {
      return false;
    }
    return amount > 0;
  }
  
  private static List<Offer> sortOffers(Map<Integer, Offer> offers) { 
    return (new ArrayList<Offer>(offers.values()))
        .stream()
        .sorted((o1, o2) -> o1.compareTo(o2))
        .collect(Collectors.toList());
  }
}
