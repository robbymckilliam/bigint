package bignums;

import static bignums.BigInteger.ONE;
import static bignums.BigInteger.TEN;

/**
 * Benchmark for {@link BigInteger#divide(BigInteger)} using different input sizes.
 */
public class DivBenchmark {
    private static int POW10_MIN = 1;   // start with 10^1-digit numbers
    private static int POW10_MAX = 7;   // go up to 10^7 digits
    private static long MIN_BENCH_DURATION = 5000000000L;   // in nanoseconds
    
    /**
     * @param args ignored
     */
    public static void main(String[] args) {
        for (int i=POW10_MIN; i<=POW10_MAX; i++) {
            doBench(10, i);
            doBenchJava(10, i);
            doBench(25, i);
            doBenchJava(25, i);
            doBench(50, i);
            doBenchJava(50, i);
            doBench(75, i);
            doBenchJava(75, i);
        }
    }
    
    /**
     * Divides numbers of length <code>mag/10 * 10<sup>2*pow10</sup></code> by numbers of length
     * <code>mag/10 * 10<sup>pow10</sup></code>.
     * @param mag 25 for <code>2.5*10<sup>pow10</sup></code>, 50 for <code>5*10<sup>pow10</sup></code>, etc.
     * @param pow10
     */
    private static void doBench(int mag, int pow10) {
        int numDecimalDigits = 2 * TEN.pow(pow10).intValue() * mag / 10;
        BigInteger a = BigInteger.valueOf(5).pow(numDecimalDigits-1).shiftLeft(numDecimalDigits-1).add(ONE);   // 10^(numDecimalDigits-1)
        numDecimalDigits /= 2;
        BigInteger b = BigInteger.valueOf(5).pow(numDecimalDigits-1).shiftLeft(numDecimalDigits-1).add(ONE);

        System.out.print("bignums.BigInteger  ... Warming up... ");
        int numIterations = 0;
        long tStart = System.nanoTime();
        do {
            a.divide(b);
            numIterations++;
        } while (System.nanoTime()-tStart < MIN_BENCH_DURATION);
        
        System.out.print("Benchmarking " + mag/10.0 + "E" + pow10 + " digits... ");
        a = new BigInteger(a.toByteArray());
        b = new BigInteger(b.toByteArray());
        tStart = System.nanoTime();
        for (int i=0; i<numIterations; i++)
            a.divide(b);
        long tEnd = System.nanoTime();
        long tNano = (tEnd-tStart+(numIterations+1)/2) / numIterations;   // in nanoseconds
        double tMilli = tNano / 1000000.0;   // in milliseconds
        System.out.printf("Time per div: %12.5fms", tMilli);
        System.out.println();
    }
    
    /**
     * Divides numbers of length <code>mag/10 * 10<sup>2*pow10</sup></code> by numbers of length
     * <code>mag/10 * 10<sup>pow10</sup></code>.  Uses the standard java.math.BigInteger
     * @param mag 25 for <code>2.5*10<sup>pow10</sup></code>, 50 for <code>5*10<sup>pow10</sup></code>, etc.
     * @param pow10
     */
    private static void doBenchJava(int mag, int pow10) {
        int numDecimalDigits = 2 * java.math.BigInteger.TEN.pow(pow10).intValue() * mag / 10;
        java.math.BigInteger a = java.math.BigInteger.valueOf(5).pow(numDecimalDigits-1).shiftLeft(numDecimalDigits-1).add(java.math.BigInteger.ONE);   // 10^(numDecimalDigits-1)
        numDecimalDigits /= 2;
        java.math.BigInteger b = java.math.BigInteger.valueOf(5).pow(numDecimalDigits-1).shiftLeft(numDecimalDigits-1).add(java.math.BigInteger.ONE);

        System.out.print("java.math.BigInteger... Warming up... ");
        int numIterations = 0;
        long tStart = System.nanoTime();
        do {
            a.divide(b);
            numIterations++;
        } while (System.nanoTime()-tStart < MIN_BENCH_DURATION);
        
        System.out.print("Benchmarking " + mag/10.0 + "E" + pow10 + " digits... ");
        a = new java.math.BigInteger(a.toByteArray());
        b = new java.math.BigInteger(b.toByteArray());
        tStart = System.nanoTime();
        for (int i=0; i<numIterations; i++)
            a.divide(b);
        long tEnd = System.nanoTime();
        long tNano = (tEnd-tStart+(numIterations+1)/2) / numIterations;   // in nanoseconds
        double tMilli = tNano / 1000000.0;   // in milliseconds
        System.out.printf("Time per div: %12.5fms", tMilli);
        System.out.println();
    }
    
}