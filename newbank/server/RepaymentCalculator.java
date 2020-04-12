package newbank.server;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RepaymentCalculator {
  public BigDecimal calculateRepayments(BigDecimal amount, BigDecimal rate, int length) {
    BigDecimal periodRate = rate.divide(BigDecimal.valueOf(length), 4, RoundingMode.HALF_EVEN);
    BigDecimal addOneToPRate = periodRate.add(BigDecimal.valueOf(1));
    BigDecimal toPowerN = addOneToPRate.pow(length);
    BigDecimal denominator = toPowerN.subtract(BigDecimal.valueOf(1));
    BigDecimal fraction = periodRate.divide(denominator, 4, RoundingMode.HALF_EVEN);
    BigDecimal fractionPlusPR = periodRate.add(fraction);
    return fractionPlusPR.multiply(amount).setScale(2, RoundingMode.HALF_EVEN);
  }
}
