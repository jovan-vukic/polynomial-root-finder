package math.analysis;

import math.util.Fraction;
import math.util.Polynomial;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class RealPolynomial {
    private static final BigDecimal[] realCoefficients;
    private static final Fraction[] rationalCoefficients;

    static {
//        realCoefficients = new BigDecimal[9];
//        Arrays.fill(realCoefficients, BigDecimal.ZERO);
//        realCoefficients[0] = BigDecimal.valueOf(Math.PI / 1260 - 1.0 / 420);
//        realCoefficients[1] = BigDecimal.valueOf(-1 * Math.pow(Math.PI, 2) / 1680 + Math.PI / 840);
//        realCoefficients[2] = BigDecimal.valueOf(-1 * Math.PI / 30 + 1.0 / 10);
//        realCoefficients[3] = BigDecimal.valueOf(-1 * Math.pow(Math.PI, 2) / 60 + Math.PI / 30);
//        realCoefficients[4] = BigDecimal.valueOf(2 * Math.PI / 3 - 2);

        realCoefficients = new BigDecimal[9];
        Arrays.fill(realCoefficients, BigDecimal.ZERO);
        realCoefficients[0] = BigDecimal.valueOf(-1 * Math.pow(Math.E, 3) / 10);
        realCoefficients[3] = BigDecimal.valueOf(-1 * (Math.E / 4 - 1));
        realCoefficients[4] = BigDecimal.valueOf(Math.pow(Math.E, 3) / 4 - 2);
        realCoefficients[5] = BigDecimal.valueOf(Math.E / 2);
        realCoefficients[7] = BigDecimal.valueOf(Math.E / 5);

        rationalCoefficients = new Fraction[realCoefficients.length];
        Arrays.fill(rationalCoefficients, Fraction.ZERO);
    }

    public static void main(String[] args) {
        // Enter the value of 'k'
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the value for k: ");
        int k = Integer.parseInt(scanner.next().trim());

        // Enter values of [a, b]
        System.out.print("Enter the bounds of the segment [a, b]: ");
        double a = Double.parseDouble(scanner.next().trim()), b = Double.parseDouble(scanner.next().trim());

        // Create rational coefficients
        createRationalCoefficients(k);

        // Evaluate P(x) and Q(x)
        Polynomial qx = new Polynomial((Fraction[]) invertCoefficientsOrder(rationalCoefficients));
        System.out.println("\nP(x) = " + printRealPolynomial((BigDecimal[]) invertCoefficientsOrder(realCoefficients)));
        System.out.println("Q(x) = " + qx);

        findPolynomialSgn(a, b, qx, true);
    }

    private static void createRationalCoefficients(int k) {
        for (int i = 0; i < realCoefficients.length; i++) {
            int degree = countLeadingZeros(realCoefficients[i]);
            rationalCoefficients[i] = getRationalCoefficient(realCoefficients[i], k, degree);
        }
    }

    private static int countLeadingZeros(BigDecimal coefficient) {
        if (coefficient.equals(BigDecimal.ZERO)) return -1;

        BigDecimal divisor = new BigDecimal(10);
        BigDecimal quotient = new BigDecimal(String.valueOf(coefficient));
        int leadingZeros = 0;

        while (quotient.abs().compareTo(BigDecimal.ONE) < 0) {
            leadingZeros++;
            quotient = quotient.multiply(divisor);
        }

        return leadingZeros;
    }

    private static Fraction getRationalCoefficient(BigDecimal coefficient, int k, int degree) {
        if (coefficient.equals(BigDecimal.ZERO)) return Fraction.ZERO;

        int numerator, denominator;
        BigDecimal multiplier1 = BigDecimal.valueOf(Math.pow(10, degree));
        BigDecimal multiplier2 = BigDecimal.valueOf(Math.pow(10, k));

        numerator = coefficient
                .multiply(multiplier1).setScale(k, RoundingMode.FLOOR)
                .multiply(multiplier2).intValue();
        denominator = multiplier1.multiply(multiplier2).intValue();

        return new Fraction(numerator, denominator);
    }

    private static Object[] invertCoefficientsOrder(Object[] coefficients) {
        // Invert this array to be suitable for Polynomial objects
        List<Object> list = Arrays.asList(coefficients);
        Collections.reverse(list);
        return list.toArray(coefficients);
    }

    private static String printRealPolynomial(BigDecimal[] coefficients) {
        StringBuilder sb = new StringBuilder();
        for (int i = coefficients.length - 1; i >= 0; i--) {
            if (coefficients[i].equals(BigDecimal.ZERO)) continue;

            if (i == 0 || !((coefficients[i].equals(BigDecimal.ONE)
                    || coefficients[i].equals(BigDecimal.ONE.negate()))
                    && coefficients[i].equals(BigDecimal.ONE))) {
                if (i != coefficients.length - 1) {
                    sb.append(coefficients[i].abs());
                } else sb.append(coefficients[i]);
                if (i != 0) sb.append("*");
            }

            if (i != 0) {
                sb.append("x");
                if (i != 1) sb.append("^").append(i);
            }
            if (i != 0) {
                for (int j = i; j >= 1; j--) {
                    if (coefficients[j - 1].compareTo(BigDecimal.ZERO) < 0) {
                        sb.append(" - ");
                        break;
                    } else if (coefficients[j - 1].compareTo(BigDecimal.ZERO) > 0) {
                        sb.append(" + ");
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }

    public static int findPolynomialSgn(double a, double b, Polynomial qx, boolean printResults) {
        // Sturm's theorem
        int nOfRoots = PolynomialRootFinder.sturmTheorem(qx, a - 0.1, b, false);
        if (printResults)
            System.out.println("\nNumber of roots of the polynomial Q(x) in the interval (" + (a - 0.1) + ", " + b + "]: " + nOfRoots);

        // Print sgn[Q(x)]
        int sgnQx = getSgn(a, b, qx, nOfRoots, printResults);
        if (printResults) printSgn(a, b, sgnQx);
        return sgnQx; // -1: negative, 1: positive, 2: alternates sign, 3: undetermined
    }

    private static void printSgn(double a, double b, int sgnQx) {
        switch (sgnQx) {
            case -1 -> { // Negative
                System.out.println("\nThe polynomial Q(x) is negative on the segment [" + a + ", " + b + "].");
                System.out.println("Since P(x) > Q(x) on the segment [" + a + ", " + b + "], then P(x) has the same sign on that segment.");
            }
            case 1 -> { // Positive
                System.out.println("\nThe polynomial Q(x) is positive on the segment [" + a + ", " + b + "].");
                System.out.println("Since P(x) > Q(x) on the segment [" + a + ", " + b + "], then P(x) has the same sign on that segment.");
            }
            case 2 -> // Alternates sign
                    System.out.println(" The polynomial Q(x) changes sign on the segment [" + a + ", " + b + "].");
            case 3 -> { // Undetermined
                System.out.println("The polynomial Q(x) has more roots in the interval (" + (a - 1) + ", " + b + "].");
                System.out.println("It's not easy to determine the sign on the segment [" + a + ", " + b + "].");
            }
        }
    }

    private static int getSgn(double a, double b, Polynomial qx, int nOfRoots, boolean printResults) {
        int sgnQx; // -1: negative, 1: positive, 2: alternates sign, 3: undetermined

        // Evaluate polynomial at 'a' and 'b'
        Fraction pValueA = qx.evaluate(new Fraction(a));
        Fraction pValueB = qx.evaluate(new Fraction(b));

        // Evaluate the sgn of Q(x)
        int sgnA = pValueA.compareTo(Fraction.ZERO);
        int sgnB = pValueB.compareTo(Fraction.ZERO);
        switch (nOfRoots) {
            case 0, 1, 2 -> {
                if ((sgnA == 0 || sgnB == 0)) {
                    Fraction value = sgnA == 0 ? pValueB : pValueA;
                    int sgn = sgnA == 0 ? sgnB : sgnA;
                    if (printResults) {
                        System.out.println("The polynomial Q(x) has a root at point x = " + (sgnA == 0 ? a : b) + ".");
                        System.out.print("Q(x = " + (sgnA == 0 ? b : a) + ") = " + value + ((sgn > 0) ? " > 0" : " < 0"));
                    }
                    sgnQx = nOfRoots == 2 ? 2 : ((sgn > 0) ? 1 : -1); // Let's assume the other root is inside [a, b] for nOfRoots == 2
                } else if ((sgnA > 0 && sgnB < 0) || (sgnA < 0 && sgnB > 0) && nOfRoots != 2) {
                    if (printResults) {
                        System.out.println("Q(x = " + a + ") = " + pValueA + ((sgnA > 0) ? " > 0" : " < 0"));
                        System.out.println("Q(x = " + b + ") = " + pValueB + ((sgnB > 0) ? " > 0" : " < 0"));
                        System.out.println("\nThe polynomial Q(x) has a root on the segment [" + a + ", " + b + "].");
                    }
                    sgnQx = 2;
                } else if (nOfRoots != 2) { // No roots inside [a, b]
                    if (printResults) {
                        System.out.println("Q(x = " + a + ") = " + pValueA + ((sgnA > 0) ? " > 0" : " < 0"));
                        System.out.println("Q(x = " + b + ") = " + pValueB + ((sgnA > 0) ? " > 0" : " < 0"));
                        System.out.println("\nThe polynomial Q(x) has a root in the interval (" + (a - 0.1) + ", " + a + "].");
                    }
                    sgnQx = (sgnA > 0) ? 1 : -1;
                } else sgnQx = 3;
            }
            default -> sgnQx = 3;
        }
        return sgnQx;
    }
}
