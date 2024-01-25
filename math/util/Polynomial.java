package math.util;

import java.math.BigInteger;
import java.util.Arrays;

// Class to represent polynomials
public class Polynomial {
    public final static Polynomial ZERO = new Polynomial(Fraction.ZERO, 0);

    private final Fraction[] coefficients;
    private int degree;

    public Polynomial(Fraction[] coefficients) {
        int numberOfLeadingZeros = 0;
        for (int i = coefficients.length - 1; i >= 0; i--) {
            if (coefficients[i].equals(Fraction.ZERO)) numberOfLeadingZeros++;
            else break;
        }

        this.coefficients = new Fraction[coefficients.length - numberOfLeadingZeros > 0 ? coefficients.length - numberOfLeadingZeros : 1];
        System.arraycopy(coefficients, 0, this.coefficients, 0, this.coefficients.length);
        calculateDegree();
    }

    // Creates a polynomial with a single term a * x^n
    public Polynomial(Fraction a, int n) {
        coefficients = new Fraction[n + 1];
        Arrays.fill(coefficients, Fraction.ZERO);
        coefficients[n] = a;
        calculateDegree();
    }

    public Polynomial differentiate() {
        if (degree == 0) return ZERO;

        Fraction[] derivativeCoefficients = new Fraction[degree];
        for (int i = 0; i < degree; i++) {
            derivativeCoefficients[i] = coefficients[i + 1].multiply(new Fraction(i + 1));
        }
        return new Polynomial(derivativeCoefficients);
    }

    public static Polynomial gcd(Polynomial a, Polynomial b) {
        while (!b.isZero()) {
            Polynomial temp = a;
            a = b;
            b = temp.remainder(b);
        }

        // Divide a = gcd with a non-zero constant = gcd(numerators)
        BigInteger constant = a.coefficients[0].getNumerator();
        for (int i = 1; i < a.coefficients.length; i++) {
            constant = gcd(constant, a.coefficients[i].getNumerator());

            if (constant.equals(BigInteger.ONE)) break;
        }

        for (int i = 0; i < a.coefficients.length; i++) {
            a.coefficients[i] = a.coefficients[i].divide(new Fraction(constant));
        }

        // Multiply a = gcd with a non-zero constant = max(denominators)
        constant = a.coefficients[0].getDenominator();
        for (int i = 1; i < a.coefficients.length; i++) {
            constant = constant.max(a.coefficients[i].getDenominator());
        }

        for (int i = 0; i < a.coefficients.length; i++) {
            a.coefficients[i] = a.coefficients[i].multiply(new Fraction(constant));
        }

        return a;
    }

    public Polynomial remainder(Polynomial other) {
        // The formulation "this = quotient * other + reminder" holds
        Polynomial quotient = this.divide(other);
        return this.subtract(quotient.multiply(other));
    }

    public Polynomial negate() {
        Polynomial multiplier = new Polynomial(new Fraction(-1), 0);
        return this.multiply(multiplier);
    }

    public Fraction evaluate(Fraction x) {
        Fraction result = Fraction.ZERO;
        for (int i = degree; i >= 0; i--) {
            result = coefficients[i].add(x.multiply(result));
        }
        return result;
    }

    public int getDegree() {
        return degree;
    }

    public boolean isZero() {
        return this == ZERO || (degree == 0 && coefficients.length == 1 && coefficients[0].equals(Fraction.ZERO));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = degree; i >= 0; i--) {
            if (coefficients[i].getNumerator().equals(BigInteger.ZERO)) continue;

            if (i == 0 || !((coefficients[i].getNumerator().equals(BigInteger.ONE)
                    || coefficients[i].getNumerator().equals(BigInteger.ONE.negate()))
                    && coefficients[i].getDenominator().equals(BigInteger.ONE))) {
                if (i != degree) {
                    sb.append(new Fraction(coefficients[i].getNumerator().abs(), coefficients[i].getDenominator()));
                } else sb.append(coefficients[i]);
                if (i != 0) sb.append("*");
            }

            if (i != 0) {
                sb.append("x");
                if (i != 1) sb.append("^").append(i);
            }
            if (i != 0) {
                for (int j = i; j >= 1; j--) {
                    if (coefficients[j - 1].compareTo(Fraction.ZERO) < 0) {
                        sb.append(" - ");
                        break;
                    } else if (coefficients[j - 1].compareTo(Fraction.ZERO) > 0) {
                        sb.append(" + ");
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }

    private void calculateDegree() {
        // Assuming that there are no coefficients equal to zero next to the highest degree terms,
        // which is handled in the constructor
        degree = coefficients.length - 1;
    }

    public Polynomial add(Polynomial other) {
        Fraction[] resultCoefficients = new Fraction[Math.max(degree, other.degree) + 1];
        Arrays.fill(resultCoefficients, Fraction.ZERO);
        for (int i = 0; i <= degree; i++)
            resultCoefficients[i] = resultCoefficients[i].add(coefficients[i]);
        for (int i = 0; i <= other.degree; i++)
            resultCoefficients[i] = resultCoefficients[i].add(other.coefficients[i]);

        return new Polynomial(resultCoefficients);
    }

    public Polynomial subtract(Polynomial other) {
        return this.add(other.negate());
    }

    public Polynomial multiply(Polynomial other) {
        Fraction[] resultCoefficients = new Fraction[degree + other.degree + 1];
        Arrays.fill(resultCoefficients, Fraction.ZERO);
        for (int i = 0; i <= degree; i++)
            for (int j = 0; j <= other.degree; j++) {
                resultCoefficients[i + j] = resultCoefficients[i + j].add(coefficients[i].multiply(other.coefficients[j]));
            }
        return new Polynomial(resultCoefficients);
    }

    public Polynomial divide(Polynomial other) {
        // Specific cases
        if (other.isZero())
            throw new RuntimeException("Cannot divide by zero polynomial.");

        if (degree < other.degree) return ZERO;

        // Division algorithm
        Fraction coefficient = coefficients[degree].divide(other.coefficients[other.degree]);
        int exponent = degree - other.degree;
        Polynomial result = new Polynomial(coefficient, exponent);

        Polynomial subtractionResult = this.subtract(other.multiply(result));
        Polynomial divisionResult = subtractionResult.isZero() ? ZERO : subtractionResult.divide(other);
        return result.add(divisionResult);
    }

    private static BigInteger gcd(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) return a;
        return gcd(b, a.remainder(b));
    }
}
