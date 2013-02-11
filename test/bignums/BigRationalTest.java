/*
 */
package bignums;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Robby McKilliam
 */
public class BigRationalTest {

    public BigRationalTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    public static boolean testEquals(String testName, Object obj, String str) {
        return testEquals(testName, obj, null, str);
    }

    public static boolean testEquals(String testName, Object obj1, Object obj2, String str) {
        // obj2 is optional
        boolean objEquality = (obj2 == null) || obj1.equals(obj2);
        String objStr = (obj1 instanceof BigDecimal) ? ((BigDecimal) obj1).toPlainString() : obj1.toString();
        boolean strEquality = objStr.equals(str);
        if (objEquality && strEquality) {
            System.out.println("PASS: " + testName);
            return true;
        }
        String err = "FAIL: " + testName + ": " + obj1.toString() + " not equal to";
        if (!objEquality) {
            err += " object " + obj2.toString();
        }
        if (!strEquality) {
            if (!objEquality) {
                err += " and";
            }
            err += " string " + str;
        }
        System.out.println(err);
        return false;
    }

    public static boolean testTrue(String testName, boolean b) {
        System.out.println((b ? "PASS: " : "FAIL: ") + testName);
        return b;
    }

    @Test
    public void testBigRational() {
        boolean passedAll = true;
        passedAll &= testEquals("BaseConstructor-1", new BigRational(BigInteger.valueOf(30), BigInteger.valueOf(72)), new BigRational(BigInteger.valueOf(5), BigInteger.valueOf(12)), "5/12");
        passedAll &= testEquals("BaseConstructor-2", new BigRational(BigInteger.valueOf(-30), BigInteger.valueOf(72)), new BigRational(BigInteger.valueOf(5), BigInteger.valueOf(-12)), "-5/12");
        passedAll &= testEquals("BaseConstructor-3", new BigRational(BigInteger.valueOf(-30), BigInteger.valueOf(-72)), new BigRational(BigInteger.valueOf(-5), BigInteger.valueOf(-12)), "5/12");
        passedAll &= testEquals("BaseConstructor-4", new BigRational(BigInteger.valueOf(0), BigInteger.valueOf(5)), new BigRational(BigInteger.valueOf(0), BigInteger.valueOf(-10)), "0");
        passedAll &= testTrue("Inequality-1", !new BigRational(BigInteger.valueOf(5), BigInteger.valueOf(12)).equals(new BigRational(BigInteger.valueOf(-5), BigInteger.valueOf(12))));
        passedAll &= testTrue("Inequality-2", !new BigRational(BigInteger.valueOf(5), BigInteger.valueOf(12)).equals(new BigRational(BigInteger.valueOf(4), BigInteger.valueOf(12))));
        passedAll &= testEquals("IntegerConstructor-1", BigRational.valueOf(5), "5");
        passedAll &= testEquals("IntegerConstructor-2", BigRational.valueOf(5, 12), new BigRational(BigInteger.valueOf(30), BigInteger.valueOf(72)), "5/12");
        passedAll &= testEquals("LongConstructor-1", BigRational.valueOf(10000000000L), "10000000000");
        passedAll &= testEquals("LongConstructor-2", BigRational.valueOf(50000000000L, 120000000000L), new BigRational(BigInteger.valueOf(300000000000L), BigInteger.valueOf(720000000000L)), "5/12");
        passedAll &= testEquals("BigDecimalConstructor-1", BigRational.valueOf(new BigDecimal("7.3412359")), "73412359/10000000");
        passedAll &= testEquals("BigDecimalConstructor-2", BigRational.valueOf(new BigDecimal("7.3412359"), new BigDecimal("2.6876980111")), BigRational.valueOf(15791000, 5781239), "15791000/5781239");
        // Derived from BigDecimal documentation, 0.1 is exactly 0.1000000000000000055511151231257827021181583404541015625. Simplified via WolframAlpha
        passedAll &= testEquals("DoubleConstructor-1", BigRational.valueOf(0.1), BigRational.valueOf(new BigDecimal(0.1)), "3602879701896397/36028797018963968");
        passedAll &= testEquals("DoubleConstructor-2", BigRational.valueOf(0.1, 0.2), BigRational.valueOf(1, 2), "1/2");
        passedAll &= testEquals("StringConstructor-1", BigRational.valueOf("18"), BigRational.valueOf(18), "18");
        passedAll &= testEquals("StringConstructor-2", BigRational.valueOf("18/-4"), BigRational.valueOf(9, -2), "-9/2");
        passedAll &= testEquals("StringConstructor-3", BigRational.valueOf("18", "-4"), BigRational.valueOf(9, -2), "-9/2");
        passedAll &= testEquals("AbsoluteValue", BigRational.valueOf(-1, 2).abs(), BigRational.valueOf(1, 2), "1/2");
        passedAll &= testEquals("Addition-1", BigRational.valueOf("2/5").add(BigRational.valueOf("3/7")), "29/35");
        passedAll &= testEquals("Addition-2", BigRational.valueOf("2/5").add(BigRational.valueOf("-3/7")), "-1/35");
        passedAll &= testEquals("Addition-3", BigRational.valueOf("-2/5").add(BigRational.valueOf("3/7")), "1/35");
        passedAll &= testEquals("Addition-4", BigRational.valueOf("-2/5").add(BigRational.valueOf("-3/7")), "-29/35");
        passedAll &= testEquals("Addition-5", BigRational.valueOf("2/5").add(BigRational.valueOf("4/5")), "6/5");
        passedAll &= testEquals("Subtraction-1", BigRational.valueOf("2/5").subtract(BigRational.valueOf("3/7")), "-1/35");
        passedAll &= testEquals("Subtraction-2", BigRational.valueOf("2/5").subtract(BigRational.valueOf("-3/7")), "29/35");
        passedAll &= testEquals("Subtraction-3", BigRational.valueOf("-2/5").subtract(BigRational.valueOf("3/7")), "-29/35");
        passedAll &= testEquals("Subtraction-4", BigRational.valueOf("-2/5").subtract(BigRational.valueOf("-3/7")), "1/35");
        passedAll &= testEquals("Subtraction-5", BigRational.valueOf("2/7").subtract(BigRational.valueOf("3/7")), "-1/7");
        passedAll &= testTrue("Comparison-1", BigRational.valueOf("2/5").compareTo(BigRational.valueOf("-2/5")) > 0);
        passedAll &= testTrue("Comparison-2", BigRational.valueOf("-2/5").compareTo(BigRational.valueOf("2/5")) < 0);
        passedAll &= testTrue("Comparison-3", BigRational.valueOf("2/5").compareTo(BigRational.valueOf("3/7")) < 0);
        passedAll &= testTrue("Comparison-4", BigRational.valueOf("-2/5").compareTo(BigRational.valueOf("-3/7")) > 0);
        passedAll &= testEquals("Increment-1", BigRational.valueOf("2/5").increment(), "7/5");
        passedAll &= testEquals("Increment-2", BigRational.valueOf("-2/5").increment(), "3/5");
        passedAll &= testEquals("Increment-3", BigRational.valueOf("-7/5").increment(), "-2/5");
        passedAll &= testEquals("Decrement-1", BigRational.valueOf("7/5").decrement(), "2/5");
        passedAll &= testEquals("Decrement-2", BigRational.valueOf("2/5").decrement(), "-3/5");
        passedAll &= testEquals("Decrement-3", BigRational.valueOf("-2/5").decrement(), "-7/5");
        passedAll &= testEquals("Divide-1", BigRational.valueOf("2/5").divide(BigRational.valueOf("3/5")), "2/3");
        passedAll &= testEquals("Divide-2", BigRational.valueOf("-2/5").divide(BigRational.valueOf("3/5")), "-2/3");
        passedAll &= testEquals("Divide-3", BigRational.valueOf("-2/5").divide(BigRational.valueOf("-3/5")), "2/3");
        passedAll &= testTrue("DoubleValue-1", BigRational.valueOf("0.1").doubleValue() == 0.1);
        passedAll &= testTrue("DoubleValue-2", BigRational.valueOf("1.1").doubleValue() == 1.1);
        passedAll &= testTrue("IntValue-1", BigRational.valueOf("7/3").intValue() == 2);
        passedAll &= testTrue("IntValue-2", BigRational.valueOf("8/3").intValue() == 2);
        passedAll &= testTrue("IntValue-2", BigRational.valueOf("9/3").intValue() == 3);
        passedAll &= testEquals("ToBigDecimal-1", BigRational.valueOf("0/3").toBigDecimal(10, RoundingMode.HALF_EVEN), "0.0000000000");
        passedAll &= testEquals("ToBigDecimal-2", BigRational.valueOf("1/3").toBigDecimal(10, RoundingMode.HALF_EVEN), "0.3333333333");
        passedAll &= testEquals("ToBigDecimal-3", BigRational.valueOf("2/3").toBigDecimal(10, RoundingMode.HALF_EVEN), "0.6666666667");
        passedAll &= testEquals("ToBigDecimal-4", BigRational.valueOf("3/3").toBigDecimal(10, RoundingMode.HALF_EVEN), "1.000000000");
        passedAll &= testEquals("ToBigDecimal-5", BigRational.valueOf("4/3").toBigDecimal(10, RoundingMode.HALF_EVEN), "1.333333333");
        passedAll &= testEquals("ToBigDecimal-6", BigRational.valueOf("5/3").toBigDecimal(10, RoundingMode.HALF_EVEN), "1.666666667");
        passedAll &= testEquals("ToBigDecimal-7", BigRational.valueOf("-1/3").toBigDecimal(10, RoundingMode.HALF_EVEN), "-0.3333333333");
        passedAll &= testEquals("ToBigDecimal-8", BigRational.valueOf("-2/3").toBigDecimal(10, RoundingMode.HALF_EVEN), "-0.6666666667");
        passedAll &= testEquals("ToBigDecimal-9", BigRational.valueOf("-3/3").toBigDecimal(10, RoundingMode.HALF_EVEN), "-1.000000000");
        passedAll &= testEquals("Max-1", BigRational.valueOf("2/5").max(BigRational.valueOf("3/7")), "3/7");
        passedAll &= testEquals("Max-2", BigRational.valueOf("2/5").max(BigRational.valueOf("-3/7")), "2/5");
        passedAll &= testEquals("Max-3", BigRational.valueOf("-2/5").max(BigRational.valueOf("-3/7")), "-2/5");
        passedAll &= testEquals("Min-1", BigRational.valueOf("2/5").min(BigRational.valueOf("3/7")), "2/5");
        passedAll &= testEquals("Min-2", BigRational.valueOf("2/5").min(BigRational.valueOf("-3/7")), "-3/7");
        passedAll &= testEquals("Min-3", BigRational.valueOf("-2/5").min(BigRational.valueOf("-3/7")), "-3/7");
        Object[] mixedFraction1 = BigRational.valueOf("7/3").mixedFraction();
        passedAll &= testEquals("MixedFraction-1", mixedFraction1[0], "2");
        passedAll &= testEquals("MixedFraction-2", mixedFraction1[1], "1/3");
        Object[] mixedFraction2 = BigRational.valueOf("-7/3").mixedFraction();
        passedAll &= testEquals("MixedFraction-3", mixedFraction2[0], "-2");
        passedAll &= testEquals("MixedFraction-4", mixedFraction2[1], "-1/3");
        passedAll &= testEquals("Multiply-1", BigRational.valueOf("2/3").multiply(BigRational.valueOf("3/5")), "2/5");
        passedAll &= testEquals("Multiply-2", BigRational.valueOf("-2/3").multiply(BigRational.valueOf("3/5")), "-2/5");
        passedAll &= testEquals("Multiply-3", BigRational.valueOf("2/3").multiply(BigRational.valueOf("-3/5")), "-2/5");
        passedAll &= testEquals("Multiply-4", BigRational.valueOf("-2/3").multiply(BigRational.valueOf("-3/5")), "2/5");
        passedAll &= testEquals("Multiply-5", BigRational.valueOf("2/3").multiply(BigInteger.valueOf(4)), "8/3");
        passedAll &= testEquals("Multiply-6", BigRational.valueOf("-2/3").multiply(BigInteger.valueOf(4)), "-8/3");
        passedAll &= testEquals("Multiply-7", BigRational.valueOf("2/3").multiply(BigInteger.valueOf(-4)), "-8/3");
        passedAll &= testEquals("Multiply-8", BigRational.valueOf("-2/3").multiply(BigInteger.valueOf(-4)), "8/3");
        passedAll &= testEquals("Negate-1", BigRational.valueOf("2/3").negate(), "-2/3");
        passedAll &= testEquals("Negate-2", BigRational.valueOf("-2/3").negate(), "2/3");
        passedAll &= testEquals("Power-1", BigRational.valueOf("2/3").pow(3), "8/27");
        passedAll &= testEquals("Power-2", BigRational.valueOf("-2/3").pow(3), "-8/27");
        passedAll &= testEquals("Power-3", BigRational.valueOf("-2/3").pow(4), "16/81");
        passedAll &= testEquals("Reciprocal-1", BigRational.valueOf("2/3").reciprocal(), "3/2");
        passedAll &= testEquals("Reciprocal-2", BigRational.valueOf("-2/3").reciprocal(), "-3/2");
        passedAll &= testEquals("Reciprocal-3", BigRational.valueOf("1/7").reciprocal(), "7");
        passedAll &= testEquals("Reciprocal-4", BigRational.valueOf("-1/7").reciprocal(), "-7");
        passedAll &= testTrue("Signum-1", BigRational.valueOf("1/7").signum() == 1);
        passedAll &= testTrue("Signum-2", BigRational.valueOf("0/7").signum() == 0);
        passedAll &= testTrue("Signum-3", BigRational.valueOf("-1/7").signum() == -1);
        passedAll &= testEquals("Numerator-1", BigRational.valueOf("2/7").numerator(), "2");
        passedAll &= testEquals("Numerator-2", BigRational.valueOf("-2/7").numerator(), "2");
        passedAll &= testEquals("Denominator-1", BigRational.valueOf("2/7").denominator(), "7");
        passedAll &= testEquals("Denominator-2", BigRational.valueOf("-2/7").denominator(), "7");
        passedAll &= testEquals("round-1", BigRational.valueOf("-2/7").round(), "0");
        passedAll &= testEquals("round-2", BigRational.valueOf("10/7").round(), "1");
        passedAll &= testEquals("round-3", BigRational.valueOf("-1000/8").round(), "-125");
        passedAll &= testEquals("round-3", BigRational.valueOf("1000/7").round(), "143");
        passedAll &= testEquals("round-3", BigRational.valueOf("1234567891/7").round(), "176366842");
        passedAll &= testEquals("round-3", BigRational.valueOf("-1234567891/7").round(), "-176366842");

        assertTrue(passedAll);
    }
}
