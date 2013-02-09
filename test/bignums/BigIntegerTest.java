/*
 * Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 4181191 4161971 4227146 4194389 4823171 4624738 4812225
 * @summary tests methods in BigInteger
 * @run main/timeout=400 BigIntegerTest
 * @author madbot
 */

package bignums;

import java.io.*;
import java.util.Random;

/**
 * This is a simple test class created to ensure that the results
 * generated by BigInteger adhere to certain identities. Passing
 * this test is a strong assurance that the BigInteger operations
 * are working correctly.
 *
 * Three arguments may be specified which give the number of
 * decimal digits you desire in the three batches of test numbers.
 *
 * The tests are performed on arrays of random numbers which are
 * generated by a Random class as well as special cases which
 * throw in boundary numbers such as 0, 1, maximum sized, etc.
 *
 */
public class BigIntegerTest {
    static Random rnd = new Random();
    static int size = 1000; // numbers per batch
    static boolean failure = false;

    public static void pow(int order) {
        int failCount1 = 0;

        for (int i=0; i<size; i++) {
            int power = rnd.nextInt(6) +2;
            BigInteger x = fetchNumber(order);
            BigInteger y = x.pow(power);
            BigInteger z = x;

            for (int j=1; j<power; j++)
                z = z.multiply(x);

            if (!y.equals(z))
                failCount1++;
        }
        report("pow", failCount1);
    }

    public static void arithmetic(int order) {
        int failCount = 0;

        for (int i=0; i<size; i++) {
            BigInteger x = fetchNumber(order);
            while(x.compareTo(BigInteger.ZERO) != 1)
                x = fetchNumber(order);
            BigInteger y = fetchNumber(order/2);
            while(x.compareTo(y) == -1)
                y = fetchNumber(order/2);
            if (y.equals(BigInteger.ZERO))
                y = y.add(BigInteger.ONE);

            BigInteger baz = x.divide(y);
            baz = baz.multiply(y);
            baz = baz.add(x.remainder(y));
            baz = baz.subtract(x);
            if (!baz.equals(BigInteger.ZERO))
                failCount++;
        }
        report("Arithmetic I for " + order + " bits", failCount);

        failCount = 0;
        for (int i=0; i<100; i++) {
            BigInteger x = fetchNumber(order);
            while(x.compareTo(BigInteger.ZERO) != 1)
                x = fetchNumber(order);
            BigInteger y = fetchNumber(order/2);
            while(x.compareTo(y) == -1)
                y = fetchNumber(order/2);
            if (y.equals(BigInteger.ZERO))
                y = y.add(BigInteger.ONE);

            BigInteger baz[] = x.divideAndRemainder(y);
            baz[0] = baz[0].multiply(y);
            baz[0] = baz[0].add(baz[1]);
            baz[0] = baz[0].subtract(x);
            if (!baz[0].equals(BigInteger.ZERO))
                failCount++;
        }
        report("Arithmetic II for " + order + " bits", failCount);
    }

    public static void bitCount() {
        int failCount = 0;

        for (int i=0; i<size*10; i++) {
            int x = rnd.nextInt();
            BigInteger bigX = BigInteger.valueOf((long)x);
            int bit = (x < 0 ? 0 : 1);
            int tmp = x, bitCount = 0;
            for (int j=0; j<32; j++) {
                bitCount += ((tmp & 1) == bit ? 1 : 0);
                tmp >>= 1;
            }

            if (bigX.bitCount() != bitCount) {
                //System.err.println(x+": "+bitCount+", "+bigX.bitCount());
                failCount++;
            }
        }
        report("Bit Count", failCount);
    }

    public static void bitLength() {
        int failCount = 0;

        for (int i=0; i<size*10; i++) {
            int x = rnd.nextInt();
            BigInteger bigX = BigInteger.valueOf((long)x);
            int signBit = (x < 0 ? 0x80000000 : 0);
            int tmp = x, bitLength, j;
            for (j=0; j<32 && (tmp & 0x80000000)==signBit; j++)
                tmp <<= 1;
            bitLength = 32 - j;

            if (bigX.bitLength() != bitLength) {
                //System.err.println(x+": "+bitLength+", "+bigX.bitLength());
                failCount++;
            }
        }

        report("BitLength", failCount);
    }

    public static void bitOps(int order) {
        int failCount1 = 0, failCount2 = 0, failCount3 = 0;

        for (int i=0; i<size*5; i++) {
            BigInteger x = fetchNumber(order);
            BigInteger y;

            /* Test setBit and clearBit (and testBit) */
            if (x.signum() < 0) {
                y = BigInteger.valueOf(-1);
                for (int j=0; j<x.bitLength(); j++)
                    if (!x.testBit(j))
                        y = y.clearBit(j);
            } else {
                y = BigInteger.ZERO;
                for (int j=0; j<x.bitLength(); j++)
                    if (x.testBit(j))
                        y = y.setBit(j);
            }
            if (!x.equals(y))
                failCount1++;

            /* Test flipBit (and testBit) */
            y = BigInteger.valueOf(x.signum()<0 ? -1 : 0);
            for (int j=0; j<x.bitLength(); j++)
                if (x.signum()<0  ^  x.testBit(j))
                    y = y.flipBit(j);
            if (!x.equals(y))
                failCount2++;
        }
        report("clearBit/testBit", failCount1);
        report("flipBit/testBit", failCount2);

        for (int i=0; i<size*5; i++) {
            BigInteger x = fetchNumber(order);

            /* Test getLowestSetBit() */
            int k = x.getLowestSetBit();
            if (x.signum() == 0) {
                if (k != -1)
                    failCount3++;
            } else {
                BigInteger z = x.and(x.negate());
                int j;
                for (j=0; j<z.bitLength() && !z.testBit(j); j++)
                    ;
                if (k != j)
                    failCount3++;
            }
        }
        report("getLowestSetBit", failCount3);
    }

    public static void bitwise(int order) {

        /* Test identity x^y == x|y &~ x&y */
        int failCount = 0;
        for (int i=0; i<size; i++) {
            BigInteger x = fetchNumber(order);
            BigInteger y = fetchNumber(order);
            BigInteger z = x.xor(y);
            BigInteger w = x.or(y).andNot(x.and(y));
            if (!z.equals(w))
                failCount++;
        }
        report("Logic (^ | & ~)", failCount);

        /* Test identity x &~ y == ~(~x | y) */
        failCount = 0;
        for (int i=0; i<size; i++) {
            BigInteger x = fetchNumber(order);
            BigInteger y = fetchNumber(order);
            BigInteger z = x.andNot(y);
            BigInteger w = x.not().or(y).not();
            if (!z.equals(w))
                failCount++;
        }
        report("Logic (&~ | ~)", failCount);
    }

    public static void shift(int order) {
        int failCount1 = 0;
        int failCount2 = 0;
        int failCount3 = 0;

        for (int i=0; i<100; i++) {
            BigInteger x = fetchNumber(order);
            int n = Math.abs(rnd.nextInt()%200);

            if (!x.shiftLeft(n).equals
                (x.multiply(BigInteger.valueOf(2L).pow(n))))
                failCount1++;

            BigInteger y[] =x.divideAndRemainder(BigInteger.valueOf(2L).pow(n));
            BigInteger z = (x.signum()<0 && y[1].signum()!=0
                            ? y[0].subtract(BigInteger.ONE)
                            : y[0]);

            BigInteger b = x.shiftRight(n);

            if (!b.equals(z)) {
                System.err.println("Input is "+x.toString(2));
                System.err.println("shift is "+n);

                System.err.println("Divided "+z.toString(2));
                System.err.println("Shifted is "+b.toString(2));
                if (b.toString().equals(z.toString()))
                    System.err.println("Houston, we have a problem.");
                failCount2++;
            }

            if (!x.shiftLeft(n).shiftRight(n).equals(x))
                failCount3++;
        }
        report("baz shiftLeft", failCount1);
        report("baz shiftRight", failCount2);
        report("baz shiftLeft/Right", failCount3);
    }

    public static void divideAndRemainder(int order) {
        int failCount1 = 0;

        for (int i=0; i<size; i++) {
            BigInteger x = fetchNumber(order).abs();
            while(x.compareTo(BigInteger.valueOf(3L)) != 1)
                x = fetchNumber(order).abs();
            BigInteger z = x.divide(BigInteger.valueOf(2L));
            BigInteger y[] = x.divideAndRemainder(x);
            if (!y[0].equals(BigInteger.ONE)) {
                failCount1++;
                System.err.println("fail1 x :"+x);
                System.err.println("      y :"+y);
            }
            else if (!y[1].equals(BigInteger.ZERO)) {
                failCount1++;
                System.err.println("fail2 x :"+x);
                System.err.println("      y :"+y);
            }

            y = x.divideAndRemainder(z);
            if (!y[0].equals(BigInteger.valueOf(2))) {
                failCount1++;
                System.err.println("fail3 x :"+x);
                System.err.println("      y :"+y);
            }
        }
        report("divideAndRemainder for " + order + " bits", failCount1);
    }

    public static void stringConv() {
        int failCount = 0;

        for (int i=0; i<100; i++) {
            byte xBytes[] = new byte[Math.abs(rnd.nextInt())%100+1];
            rnd.nextBytes(xBytes);
            BigInteger x = new BigInteger(xBytes);

            for (int radix=2; radix < 37; radix++) {
                String result = x.toString(radix);
                BigInteger test = new BigInteger(result, radix);
                if (!test.equals(x)) {
                    failCount++;
                    System.err.println("BigInteger toString: "+x);
                    System.err.println("Test: "+test);
                    System.err.println(radix);
                }
            }
        }
        report("String Conversion", failCount);
    }

    public static void byteArrayConv(int order) {
        int failCount = 0;

        for (int i=0; i<size; i++) {
            BigInteger x = fetchNumber(order);
            while (x.equals(BigInteger.ZERO))
                x = fetchNumber(order);
            BigInteger y = new BigInteger(x.toByteArray());
            if (!x.equals(y)) {
                failCount++;
                System.err.println("orig is "+x);
                System.err.println("new is "+y);
            }
        }
        report("Array Conversion", failCount);
    }

    public static void modInv(int order) {
        int failCount = 0, successCount = 0, nonInvCount = 0;

        for (int i=0; i<size; i++) {
            BigInteger x = fetchNumber(order);
            while(x.equals(BigInteger.ZERO))
                x = fetchNumber(order);
            BigInteger m = fetchNumber(order).abs();
            while(m.compareTo(BigInteger.ONE) != 1)
                m = fetchNumber(order).abs();

            try {
                BigInteger inv = x.modInverse(m);
                BigInteger prod = inv.multiply(x).remainder(m);

                if (prod.signum() == -1)
                    prod = prod.add(m);

                if (prod.equals(BigInteger.ONE))
                    successCount++;
                else
                    failCount++;
            } catch(ArithmeticException e) {
                nonInvCount++;
            }
        }
        report("Modular Inverse for " + order + " bits", failCount);
    }

    public static void modExp(int order1, int order2) {
        int failCount = 0;

        for (int i=0; i<size/10; i++) {
            BigInteger m = fetchNumber(order1).abs();
            while(m.compareTo(BigInteger.ONE) != 1)
                m = fetchNumber(order1).abs();
            BigInteger base = fetchNumber(order2);
            BigInteger exp = fetchNumber(8).abs();

            BigInteger z = base.modPow(exp, m);
            BigInteger w = base.pow(exp.intValue()).mod(m);
            if (!z.equals(w)) {
                System.err.println("z is "+z);
                System.err.println("w is "+w);
                System.err.println("mod is "+m);
                System.err.println("base is "+base);
                System.err.println("exp is "+exp);
                failCount++;
            }
        }
        report("Exponentiation I", failCount);
    }

    // This test is based on Fermat's theorem
    // which is not ideal because base must not be multiple of modulus
    // and modulus must be a prime or pseudoprime (Carmichael number)
    public static void modExp2(int order) {
        int failCount = 0;

        for (int i=0; i<10; i++) {
            BigInteger m = new BigInteger(100, 5, rnd);
            while(m.compareTo(BigInteger.ONE) != 1)
                m = new BigInteger(100, 5, rnd);
            BigInteger exp = m.subtract(BigInteger.ONE);
            BigInteger base = fetchNumber(order).abs();
            while(base.compareTo(m) != -1)
                base = fetchNumber(order).abs();
            while(base.equals(BigInteger.ZERO))
                base = fetchNumber(order).abs();

            BigInteger one = base.modPow(exp, m);
            if (!one.equals(BigInteger.ONE)) {
                System.err.println("m is "+m);
                System.err.println("base is "+base);
                System.err.println("exp is "+exp);
                failCount++;
            }
        }
        report("Exponentiation II", failCount);
    }

    private static final int[] mersenne_powers = {
        521, 607, 1279, 2203, 2281, 3217, 4253, 4423, 9689, 9941, 11213, 19937,
        21701, 23209, 44497, 86243, 110503, 132049, 216091, 756839, 859433,
        1257787, 1398269, 2976221, 3021377, 6972593, 13466917 };

    private static final long[] carmichaels = {
      561,1105,1729,2465,2821,6601,8911,10585,15841,29341,41041,46657,52633,
      62745,63973,75361,101101,115921,126217,162401,172081,188461,252601,
      278545,294409,314821,334153,340561,399001,410041,449065,488881,512461,
      225593397919L };

    // Note: testing the larger ones takes too long.
    private static final int NUM_MERSENNES_TO_TEST = 7;
    // Note: this constant used for computed Carmichaels, not the array above
    private static final int NUM_CARMICHAELS_TO_TEST = 5;

    private static final String[] customer_primes = {
        "120000000000000000000000000000000019",
        "633825300114114700748351603131",
        "1461501637330902918203684832716283019651637554291",
        "779626057591079617852292862756047675913380626199",
        "857591696176672809403750477631580323575362410491",
        "910409242326391377348778281801166102059139832131",
        "929857869954035706722619989283358182285540127919",
        "961301750640481375785983980066592002055764391999",
        "1267617700951005189537696547196156120148404630231",
        "1326015641149969955786344600146607663033642528339" };

    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = new BigInteger("2");
    private static final BigInteger SIX = new BigInteger("6");
    private static final BigInteger TWELVE = new BigInteger("12");
    private static final BigInteger EIGHTEEN = new BigInteger("18");

    public static void prime() {
        BigInteger p1, p2, c1;
        int failCount = 0;

        // Test consistency
        for(int i=0; i<10; i++) {
            p1 = BigInteger.probablePrime(100, rnd);
            if (!p1.isProbablePrime(100)) {
                System.err.println("Consistency "+p1.toString(16));
                failCount++;
            }
        }

        // Test some known Mersenne primes (2^n)-1
        // The array holds the exponents, not the numbers being tested
        for (int i=0; i<NUM_MERSENNES_TO_TEST; i++) {
            p1 = new BigInteger("2");
            p1 = p1.pow(mersenne_powers[i]);
            p1 = p1.subtract(BigInteger.ONE);
            if (!p1.isProbablePrime(100)) {
                System.err.println("Mersenne prime "+i+ " failed.");
                failCount++;
            }
        }

        // Test some primes reported by customers as failing in the past
        for (int i=0; i<customer_primes.length; i++) {
            p1 = new BigInteger(customer_primes[i]);
            if (!p1.isProbablePrime(100)) {
                System.err.println("Customer prime "+i+ " failed.");
                failCount++;
            }
        }

        // Test some known Carmichael numbers.
        for (int i=0; i<carmichaels.length; i++) {
            c1 = BigInteger.valueOf(carmichaels[i]);
            if(c1.isProbablePrime(100)) {
                System.err.println("Carmichael "+i+ " reported as prime.");
                failCount++;
            }
        }

        // Test some computed Carmichael numbers.
        // Numbers of the form (6k+1)(12k+1)(18k+1) are Carmichael numbers if
        // each of the factors is prime
        int found = 0;
        BigInteger f1 = new BigInteger(40, 100, rnd);
        while (found < NUM_CARMICHAELS_TO_TEST) {
            BigInteger k = null;
            BigInteger f2, f3;
            f1 = f1.nextProbablePrime();
            BigInteger[] result = f1.subtract(ONE).divideAndRemainder(SIX);
            if (result[1].equals(ZERO)) {
                k = result[0];
                f2 = k.multiply(TWELVE).add(ONE);
                if (f2.isProbablePrime(100)) {
                    f3 = k.multiply(EIGHTEEN).add(ONE);
                    if (f3.isProbablePrime(100)) {
                        c1 = f1.multiply(f2).multiply(f3);
                        if (c1.isProbablePrime(100)) {
                            System.err.println("Computed Carmichael "
                                               +c1.toString(16));
                            failCount++;
                        }
                        found++;
                    }
                }
            }
            f1 = f1.add(TWO);
        }

        // Test some composites that are products of 2 primes
        for (int i=0; i<50; i++) {
            p1 = BigInteger.probablePrime(100, rnd);
            p2 = BigInteger.probablePrime(100, rnd);
            c1 = p1.multiply(p2);
            if (c1.isProbablePrime(100)) {
                System.err.println("Composite failed "+c1.toString(16));
                failCount++;
            }
        }

        for (int i=0; i<4; i++) {
            p1 = BigInteger.probablePrime(600, rnd);
            p2 = BigInteger.probablePrime(600, rnd);
            c1 = p1.multiply(p2);
            if (c1.isProbablePrime(100)) {
                System.err.println("Composite failed "+c1.toString(16));
                failCount++;
            }
        }

        report("Prime", failCount);
    }

    private static final long[] primesTo100 = {
        2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97
    };

    private static final long[] aPrimeSequence = {
        1999999003L, 1999999013L, 1999999049L, 1999999061L, 1999999081L,
        1999999087L, 1999999093L, 1999999097L, 1999999117L, 1999999121L,
        1999999151L, 1999999171L, 1999999207L, 1999999219L, 1999999271L,
        1999999321L, 1999999373L, 1999999423L, 1999999439L, 1999999499L,
        1999999553L, 1999999559L, 1999999571L, 1999999609L, 1999999613L,
        1999999621L, 1999999643L, 1999999649L, 1999999657L, 1999999747L,
        1999999763L, 1999999777L, 1999999811L, 1999999817L, 1999999829L,
        1999999853L, 1999999861L, 1999999871L, 1999999873
    };

    public static void nextProbablePrime() throws Exception {
        int failCount = 0;
        BigInteger p1, p2, p3;
        p1 = p2 = p3 = ZERO;

        // First test nextProbablePrime on the low range starting at zero
        for (int i=0; i<primesTo100.length; i++) {
            p1 = p1.nextProbablePrime();
            if (p1.longValue() != primesTo100[i]) {
                System.err.println("low range primes failed");
                System.err.println("p1 is "+p1);
                System.err.println("expected "+primesTo100[i]);
                failCount++;
            }
        }

        // Test nextProbablePrime on a relatively small, known prime sequence
        p1 = BigInteger.valueOf(aPrimeSequence[0]);
        for (int i=1; i<aPrimeSequence.length; i++) {
            p1 = p1.nextProbablePrime();
            if (p1.longValue() != aPrimeSequence[i]) {
                System.err.println("prime sequence failed");
                failCount++;
            }
        }

        // Next, pick some large primes, use nextProbablePrime to find the
        // next one, and make sure there are no primes in between
        for (int i=0; i<100; i+=10) {
            p1 = BigInteger.probablePrime(50 + i, rnd);
            p2 = p1.add(ONE);
            p3 = p1.nextProbablePrime();
            while(p2.compareTo(p3) < 0) {
                if (p2.isProbablePrime(100)){
                    System.err.println("nextProbablePrime failed");
                    System.err.println("along range "+p1.toString(16));
                    System.err.println("to "+p3.toString(16));
                    failCount++;
                    break;
                }
                p2 = p2.add(ONE);
            }
        }

        report("nextProbablePrime", failCount);
    }

    public static void serialize() throws Exception {
        int failCount = 0;

        String bitPatterns[] = {
             "ffffffff00000000ffffffff00000000ffffffff00000000",
             "ffffffffffffffffffffffff000000000000000000000000",
             "ffffffff0000000000000000000000000000000000000000",
             "10000000ffffffffffffffffffffffffffffffffffffffff",
             "100000000000000000000000000000000000000000000000",
             "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "-ffffffff00000000ffffffff00000000ffffffff00000000",
            "-ffffffffffffffffffffffff000000000000000000000000",
            "-ffffffff0000000000000000000000000000000000000000",
            "-10000000ffffffffffffffffffffffffffffffffffffffff",
            "-100000000000000000000000000000000000000000000000",
            "-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        };

        for(int i = 0; i < bitPatterns.length; i++) {
            BigInteger b1 = new BigInteger(bitPatterns[i], 16);
            BigInteger b2 = null;

            File f = new File("serialtest");

            try (FileOutputStream fos = new FileOutputStream(f)) {
                try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                    oos.writeObject(b1);
                    oos.flush();
                }

                try (FileInputStream fis = new FileInputStream(f);
                     ObjectInputStream ois = new ObjectInputStream(fis))
                {
                    b2 = (BigInteger)ois.readObject();
                }

                if (!b1.equals(b2) ||
                    !b1.equals(b1.or(b2))) {
                    failCount++;
                    System.err.println("Serialized failed for hex " +
                                       b1.toString(16));
                }
            }
            f.delete();
        }

        for(int i=0; i<10; i++) {
            BigInteger b1 = fetchNumber(rnd.nextInt(100));
            BigInteger b2 = null;
            File f = new File("serialtest");
            try (FileOutputStream fos = new FileOutputStream(f)) {
                try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                    oos.writeObject(b1);
                    oos.flush();
                }

                try (FileInputStream fis = new FileInputStream(f);
                     ObjectInputStream ois = new ObjectInputStream(fis))
                {
                    b2 = (BigInteger)ois.readObject();
                }
            }

            if (!b1.equals(b2) ||
                !b1.equals(b1.or(b2)))
                failCount++;
            f.delete();
        }

        report("Serialize", failCount);
    }

    /**
     * Main to interpret arguments and run several tests.
     *
     * Up to three arguments may be given to specify the size of BigIntegers
     * used for call parameters 1, 2, and 3. The size is interpreted as
     * the maximum number of decimal digits that the parameters will have.
     *
     */
    public static void main(String[] args) throws Exception {

        // Some variables for sizing test numbers in bits
        int order1 = 100;
        int order2 = 60;
        int order3 = 1800;   // #bits for testing Karatsuba and Burnikel-Ziegler
        int order4 = 3000;   // #bits for testing Toom-Cook

        if (args.length >0)
            order1 = (int)((Integer.parseInt(args[0]))* 3.333);
        if (args.length >1)
            order2 = (int)((Integer.parseInt(args[1]))* 3.333);
        if (args.length >2)
            order3 = (int)((Integer.parseInt(args[2]))* 3.333);
        if (args.length >3)
            order4 = (int)((Integer.parseInt(args[3]))* 3.333);

        prime();
        nextProbablePrime();

        arithmetic(order1);   // small numbers
        arithmetic(order3);   // Karatsuba / Burnikel-Ziegler range
        arithmetic(order4);   // Toom-Cook range

        divideAndRemainder(order1);   // small numbers
        divideAndRemainder(order3);   // Karatsuba / Burnikel-Ziegler range
        divideAndRemainder(order4);   // Toom-Cook range

        pow(order1);

        bitCount();
        bitLength();
        bitOps(order1);
        bitwise(order1);

        shift(order1);

        byteArrayConv(order1);

        modInv(order1);   // small numbers
        modInv(order3);   // Karatsuba / Burnikel-Ziegler range
        modInv(order4);   // Toom-Cook range

        modExp(order1, order2);
        modExp2(order1);

        stringConv();
        serialize();

        if (failure)
            throw new RuntimeException("Failure in BigIntegerTest.");
    }

    /*
     * Get a random or boundary-case number. This is designed to provide
     * a lot of numbers that will find failure points, such as max sized
     * numbers, empty BigIntegers, etc.
     *
     * If order is less than 2, order is changed to 2.
     */
    private static BigInteger fetchNumber(int order) {
        boolean negative = rnd.nextBoolean();
        int numType = rnd.nextInt(7);
        BigInteger result = null;
        if (order < 2) order = 2;

        switch (numType) {
            case 0: // Empty
                result = BigInteger.ZERO;
                break;

            case 1: // One
                result = BigInteger.ONE;
                break;

            case 2: // All bits set in number
                int numBytes = (order+7)/8;
                byte[] fullBits = new byte[numBytes];
                for(int i=0; i<numBytes; i++)
                    fullBits[i] = (byte)0xff;
                int excessBits = 8*numBytes - order;
                fullBits[0] &= (1 << (8-excessBits)) - 1;
                result = new BigInteger(1, fullBits);
                break;

            case 3: // One bit in number
                result = BigInteger.ONE.shiftLeft(rnd.nextInt(order));
                break;

            case 4: // Random bit density
                int iterations = rnd.nextInt(order-1);
                result = BigInteger.ONE.shiftLeft(rnd.nextInt(order));
                for(int i=0; i<iterations; i++) {
                    BigInteger temp = BigInteger.ONE.shiftLeft(
                                                rnd.nextInt(order));
                    result = result.or(temp);
                }
                break;
            case 5: // Runs of consecutive ones and zeros
                result = ZERO;
                int remaining = order;
                int bit = rnd.nextInt(2);
                while (remaining > 0) {
                    int runLength = Math.min(remaining, rnd.nextInt(order));
                    result = result.shiftLeft(runLength);
                    if (bit > 0)
                        result = result.add(ONE.shiftLeft(runLength).subtract(ONE));
                    remaining -= runLength;
                    bit = 1 - bit;
                }
                break;

            default: // random bits
                result = new BigInteger(order, rnd);
        }

        if (negative)
            result = result.negate();

        return result;
    }

    static void report(String testName, int failCount) {
        System.err.println(testName+": " +
                           (failCount==0 ? "Passed":"Failed("+failCount+")"));
        if (failCount > 0)
            failure = true;
    }
}