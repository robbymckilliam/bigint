/*
 * Ration arithmetic class taken from the Rosetta stone website.
 */
package bignums;

import java.io.Serializable;


public class BigRational extends Number implements Comparable<BigRational>, Serializable
{
 
  public final static BigRational ZERO = new BigRational(false, BigInteger.ZERO, BigInteger.ONE);
  public final static BigRational ONE = new BigRational(false, BigInteger.ONE, BigInteger.ONE);
 
  private final boolean isNegative;
  private final BigInteger numerator;
  private final BigInteger denominator;
  private final int hashCode;
 
  private BigRational(boolean isNegative, BigInteger nonNegativeNumerator, BigInteger nonNegativeDenominator)
  {
    this.isNegative = isNegative;
    this.numerator = nonNegativeNumerator;
    this.denominator = nonNegativeDenominator;
    this.hashCode = computeHashCode(isNegative, nonNegativeNumerator, nonNegativeDenominator);
  }
 
  private BigRational(BigInteger numerator, BigInteger denominator, boolean ignoreComponentSigns, boolean forcedSign)
  {
    if (denominator.equals(BigInteger.ZERO))
      throw new ArithmeticException("Denominator is zero");
    boolean isNegative = ignoreComponentSigns ? forcedSign : false;
    if (numerator.equals(BigInteger.ZERO))
    {
      denominator = BigInteger.ONE;
      isNegative = false;
    }
    else
    {
      if (numerator.signum() < 0)
      {
        if (!ignoreComponentSigns)
          isNegative = true;
        numerator = numerator.negate();
      }
      if (denominator.signum() < 0)
      {
        if (!ignoreComponentSigns)
          isNegative = !isNegative;
        denominator = denominator.negate();
      }
      BigInteger gcd = numerator.gcd(denominator);
      if (!gcd.equals(BigInteger.ONE))
      {
        numerator = numerator.divide(gcd);
        denominator = denominator.divide(gcd);
      }
    }
    this.isNegative = isNegative;
    this.numerator = numerator;
    this.denominator = denominator;
    this.hashCode = computeHashCode(isNegative, numerator, denominator);
  }
  
  /** Integer.  Assumes denominator is 1. */
  public BigRational(BigInteger numerator) {
      this(numerator, BigInteger.ONE);
  } 
  
  public BigRational(int numerator, int denominator){  
      this(new BigInteger(Integer.toString(numerator,2),2), 
            new BigInteger(Integer.toString(denominator,2),2), false, false);  
  }
  
  /** 
   * Construct a big rational that is an approximation to the double d, accurate to p decimal places.
   */ 
  public BigRational(double d, int p){  
      this(new BigDecimal(d).multiply(BigDecimal.TEN.pow(p)).toBigInteger(),
              BigDecimal.TEN.pow(p).toBigInteger());  
  }

  
  public BigRational(BigInteger numerator, BigInteger denominator)
  {  this(numerator, denominator, false, false);  }
 
  public BigRational abs()
  {  return isNegative ? new BigRational(false, numerator, denominator) : this;  }
 
  public BigRational add(BigRational br)
  {
    if (isNegative == br.isNegative)
      return addIgnoreNegatives(isNegative, this, br);
    if (isNegative)
      return subtractIgnoreNegatives(br, this);
    return subtractIgnoreNegatives(this, br);
  }
 
  public int compareTo(BigRational br)
  {
    if (isNegative != br.isNegative)
      return isNegative ? -1 : 1;
    return subtract(br).signum();
  }
 
  public BigRational decrement()
  {
    if (isNegative)
      return new BigRational(numerator.add(denominator), denominator, true, true);
    return new BigRational(numerator.subtract(denominator), denominator, true, (numerator.compareTo(denominator) < 0));
  }
 
  public BigInteger denominator()
  {  return denominator;  }
 
  public BigRational divide(BigInteger bi)
  {
    boolean isNegative = (bi.signum() < 0);
    if (isNegative)
      bi = bi.negate();
    isNegative = (isNegative != this.isNegative);
    return new BigRational(numerator, denominator.multiply(bi), true, isNegative);
  }
 
  public BigRational divide(BigRational divisor)
  {  return multiply(divisor.reciprocal());  }
 
  public double doubleValue()
  {  return toBigDecimal(18, RoundingMode.HALF_EVEN).doubleValue();  }
 
  public boolean equals(Object o)
  {
    if ((o == null) || !(o instanceof BigRational))
      return false;
    BigRational br = (BigRational)o;
    return (isNegative == br.isNegative) && numerator.equals(br.numerator) && denominator.equals(br.denominator);
  }
 
  public float floatValue()
  {  return toBigDecimal(9, RoundingMode.HALF_EVEN).floatValue();  }
 
  public int hashCode()
  {  return hashCode;  }
 
  public BigRational increment()
  {
    if (!isNegative)
      return new BigRational(numerator.add(denominator), denominator, true, false);
    return new BigRational(numerator.subtract(denominator), denominator, true, (numerator.compareTo(denominator) > 0));
  }
 
  public int intValue()
  {  return toBigDecimal(12, RoundingMode.HALF_EVEN).intValue();  }
 
  public boolean isWholeNumber()
  {  return denominator.equals(BigInteger.ONE);  }
 
  public boolean isZero()
  {  return numerator.equals(BigInteger.ZERO);  }
 
  public long longValue()
  {  return toBigDecimal(21, RoundingMode.HALF_EVEN).longValue();  }
 
  public BigRational max(BigRational br)
  {  return (compareTo(br) >= 0) ? this : br;  }
 
  public BigRational min(BigRational br)
  {  return (compareTo(br) <= 0) ? this : br;  }
 
  public Object[] mixedFraction()
  {
    BigInteger[] dar = numerator.divideAndRemainder(denominator);
    BigInteger whole = dar[0];
    if (isNegative)
      whole = whole.negate();
    BigRational fraction = new BigRational(dar[1], denominator, true, isNegative);
    return new Object[] { whole, fraction };
  }
 
  public BigRational multiply(BigInteger bi)
  {
    boolean isNegative = (bi.signum() < 0);
    if (isNegative)
      bi = bi.negate();
    isNegative = (isNegative != this.isNegative);
    return new BigRational(numerator.multiply(bi), denominator, true, isNegative);
  }
 
  public BigRational multiply(BigRational br)
  {
    BigInteger numerator = this.numerator.multiply(br.numerator);
    BigInteger denominator = this.denominator.multiply(br.denominator);
    return new BigRational(numerator, denominator, true, isNegative != br.isNegative);
  }
 
  public BigRational negate()
  {
    if (isZero())
      return this;
    return new BigRational(!isNegative, numerator, denominator);
  }
 
  public BigInteger numerator()
  {  return numerator;  }
 
  public BigRational pow(int n)
  {
    BigInteger numerator = this.numerator.pow(n);
    BigInteger denominator = this.denominator.pow(n);
    return new BigRational(numerator, denominator, true, isNegative ? ((n & 1) != 0) : false);
  }
 
  public BigRational reciprocal()
  {
    if (isZero())
      throw new ArithmeticException("Can not calculate reciprocal of zero");
    return new BigRational(isNegative, denominator, numerator);
  }
 
  public int signum()
  {
    if (isZero())
      return 0;
    return isNegative ? -1 : 1;
  }
 
  public BigRational subtract(BigRational br)
  {
    if (isNegative != br.isNegative)
      return addIgnoreNegatives(isNegative, this, br);
    if (isNegative)
      return subtractIgnoreNegatives(br, this);
    return subtractIgnoreNegatives(this, br);
  }
  
  /** Rounds to nearest BigInteger */
  public BigInteger round() {
    int prec = Math.max(1, (numerator.bitLength() - denominator.bitLength()));
    BigDecimal bigdec = this.toBigDecimal(prec,RoundingMode.FLOOR).setScale(0, BigDecimal.ROUND_HALF_UP);
    return bigdec.toBigInteger();
  }
 
  public BigDecimal toBigDecimal(int desiredPrecision, RoundingMode roundingMode)
  {
    BigDecimal bdNumerator = new BigDecimal(numerator);
    BigDecimal bdDenominator = new BigDecimal(denominator);
    int resultScale = bdNumerator.scale() - bdDenominator.scale() - bdNumerator.precision() + bdDenominator.precision() + desiredPrecision;
    BigDecimal bigDecimalValue = bdNumerator.divide(bdDenominator, resultScale, roundingMode);
    if (bigDecimalValue.precision() > desiredPrecision)
      bigDecimalValue = bigDecimalValue.setScale(bigDecimalValue.scale() - 1, roundingMode);
    if (isNegative)
      bigDecimalValue = bigDecimalValue.negate();
    return bigDecimalValue;
  }
 
  public BigDecimal toBigDecimalExact()
  {
    BigDecimal bigDecimalValue = new BigDecimal(numerator).divide(new BigDecimal(denominator));
    if (isNegative)
      bigDecimalValue = bigDecimalValue.negate();
    return bigDecimalValue;
  }
 
  public String toString()
  {
    if (isWholeNumber())
    {
      if (isNegative)
        return "-" + numerator.toString();
      return numerator.toString();
    }
    if (isNegative)
      return "-" + numerator.toString() + "/" + denominator.toString();
    return numerator.toString() + "/" + denominator.toString();
  }
 
  private static int computeHashCode(boolean isNegative, BigInteger numerator, BigInteger denominator)
  {
    int c = numerator.hashCode() + denominator.hashCode();
    if (isNegative)
      c = ~c;
    return c;
  }
 
  private static BigRational addIgnoreNegatives(boolean resultIsNegative, BigRational first, BigRational second)
  {
    first = first.abs();
    second = second.abs();
    BigInteger numerator = null;
    BigInteger denominator = null;
    if (first.denominator.equals(second.denominator))
    {
      numerator = first.numerator.add(second.numerator);
      denominator = first.denominator;
    }
    else
    {
      numerator = first.numerator.multiply(second.denominator).add(second.numerator.multiply(first.denominator));
      denominator = first.denominator.multiply(second.denominator);
    }
    return new BigRational(numerator, denominator, true, resultIsNegative);
  }
 
  private static BigRational subtractIgnoreNegatives(BigRational first, BigRational second)
  {
    first = first.abs();
    second = second.abs();
    BigInteger numerator = null;
    BigInteger denominator = null;
    if (first.denominator.equals(second.denominator))
    {
      numerator = first.numerator.subtract(second.numerator);
      denominator = first.denominator;
    }
    else
    {
      numerator = first.numerator.multiply(second.denominator).subtract(second.numerator.multiply(first.denominator));
      denominator = first.denominator.multiply(second.denominator);
    }
    return new BigRational(numerator, denominator);
  }
 
  public static BigRational valueOf(int integerValue)
  {  return valueOf(integerValue, 1);  }
 
  public static BigRational valueOf(int numerator, int denominator)
  {  return new BigRational(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));  }
 
  public static BigRational valueOf(long longValue)
  {  return valueOf(longValue, 1);  }
 
  public static BigRational valueOf(long numerator, long denominator)
  {  return new BigRational(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));  }
 
  public static BigRational valueOf(BigDecimal bigDecimalValue)
  {
    int scale = bigDecimalValue.scale();
    BigInteger numerator = bigDecimalValue.unscaledValue();
    if (scale <= 0)
    {
      if (scale < 0)
        numerator = numerator.multiply(BigInteger.TEN.pow(-scale));
      return new BigRational(numerator, BigInteger.ONE);
    }
    return new BigRational(numerator, BigInteger.TEN.pow(scale));
  }
 
  public static BigRational valueOf(BigDecimal numerator, BigDecimal denominator)
  {  return valueOf(numerator).divide(valueOf(denominator));  }
 
  public static BigRational valueOf(double doubleValue)
  {  return valueOf(new BigDecimal(doubleValue));  }
 
  public static BigRational valueOf(double numerator, double denominator)
  {  return valueOf(numerator).divide(valueOf(denominator));  }
 
  public static BigRational valueOf(String number)
  {
    int slashIndex = number.indexOf("/");
    if (slashIndex > 0)
      return valueOf(number.substring(0, slashIndex), number.substring(slashIndex + 1));
    return valueOf(new BigDecimal(number));
  }
 
  public static BigRational valueOf(String numerator, String denominator)
  {  return valueOf(numerator).divide(valueOf(denominator));  }
 

 

 
}

