import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Class to represent fractions
public class Fraction implements Comparable<Fraction> {
    public final static Fraction ZERO = new Fraction(0);
    public final static Fraction ONE = new Fraction(1);

    private final BigInteger numerator;
    private final BigInteger denominator;

    public Fraction(int numerator) {
        this(BigInteger.valueOf(numerator));
    }

    public Fraction(double number) {
        int numberOfDecimals = numberOfDecimals(number);
        numerator = BigInteger.valueOf((long) (number * Math.pow(10, numberOfDecimals)));
        denominator = BigInteger.valueOf((long) Math.pow(10, numberOfDecimals));
    }

    public Fraction(int numerator, int denominator) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public Fraction(BigInteger numerator) {
        this(numerator, BigInteger.ONE);
    }

    public Fraction(BigInteger numerator, BigInteger denominator) {
        if (denominator.equals(BigInteger.ZERO)) throw new ArithmeticException("Denominator iz zero");

        // Reduce fraction if needed
        BigInteger gcdValue;
        while (!(gcdValue = gcd(numerator, denominator)).equals(BigInteger.ONE)) {
            numerator = numerator.divide(gcdValue);
            denominator = denominator.divide(gcdValue);
        }

        this.numerator = denominator.compareTo(BigInteger.ZERO) < 0 ? numerator.negate() : numerator;
        this.denominator = denominator.compareTo(BigInteger.ZERO) < 0 ? denominator.negate() : denominator;
    }

    public static Fraction[] getReversedFractionsList(Fraction[] values) {
        // Invert this array to be suitable for Polynomial objects
        List<Fraction> list = Arrays.asList(values);
        Collections.reverse(list);
        list.toArray(values);
        return values;
    }

    public static Fraction[] getReversedFractionsList(int[] values) {
        // Create an array of fractions
        Fraction[] fractions = new Fraction[values.length];
        for (int i = 0; i < fractions.length; i++) {
            fractions[i] = new Fraction(values[i]);
        }
        return getReversedFractionsList(fractions);
    }

    public Fraction add(Fraction other) {
        BigInteger newNumerator = numerator.multiply(other.denominator).add(other.numerator.multiply(denominator));
        BigInteger newDenominator = denominator.multiply(other.denominator);
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction subtract(Fraction other) {
        return this.add(new Fraction(other.numerator.negate(), other.denominator));
    }

    public Fraction multiply(Fraction other) {
        return new Fraction(numerator.multiply(other.numerator), denominator.multiply(other.denominator));
    }

    public Fraction divide(Fraction other) {
        Fraction reciprocal = new Fraction(other.denominator, other.numerator);
        return this.multiply(reciprocal);
    }

    public BigInteger getNumerator() {
        return numerator;
    }

    public BigInteger getDenominator() {
        return denominator;
    }

    public Fraction abs() {
        return new Fraction(this.numerator.abs(), denominator);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Fraction fraction = (Fraction) obj;
        return this.compareTo(fraction) == 0;
    }

    @Override
    public String toString() {
        return denominator.equals(BigInteger.ONE) ? "" + numerator : numerator + "/" + denominator;
    }

    @Override
    public int compareTo(Fraction fraction) {
        BigInteger expression = numerator.multiply(fraction.denominator).subtract(denominator.multiply(fraction.numerator));
        return expression.compareTo(BigInteger.ZERO);
    }

    private BigInteger gcd(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) return a;
        return gcd(b, a.remainder(b));
    }

    private int numberOfDecimals(double number) {
        // Convert the double to BigDecimal to preserve precision
        BigDecimal decimal = BigDecimal.valueOf(number);

        // Use stripTrailingZeros to remove any trailing zeros after the decimal point
        String decimalString = decimal.stripTrailingZeros().toPlainString();

        // Check if the decimal point exists in the string
        int decimalPointIndex = decimalString.indexOf('.');
        if (decimalPointIndex < 0) return 0;

        // Calculate the number of decimals by subtracting the position of the decimal point
        // from the length of the string
        return decimalString.length() - decimalPointIndex - 1;
    }
}
