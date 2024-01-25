import java.util.*;

public class PolynomialRootFinder {
    //private static final int[] coefficients = {1, 8,-6,-52,-23, 84, 100, 32};
    private static final int[] coefficients = {1, 0, -3, -1, 3, 3, -1, -3, 0, 1};

    public static void main(String[] args) {
        // Define interval and coefficients of the polynomial P(x)
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the bounds of the interval (a, b]: ");

        double a = Double.parseDouble(scanner.next().trim()), b = Double.parseDouble(scanner.next().trim());
        Fraction[] coefficients = Fraction.getReversedFractionsList(PolynomialRootFinder.coefficients);

        // Sturm theorem
        Polynomial px = new Polynomial(coefficients);

        int realRoots = sturmTheorem(px, a, b, true);
        System.out.println("Number of roots of the polynomial in the interval (" + a + ", " + b + "]: " + realRoots);
    }

    public static int sturmTheorem(Polynomial px, double a, double b, boolean printResults) {
        // Determine the GCD of P(x) and P'(x)
        Polynomial pxPrime = px.differentiate();
        Polynomial gcd = Polynomial.gcd(px, pxPrime);
        if (printResults) {
            System.out.println("Determining the GCD(P(x), P'(x)):\nP(x) = " + px);
            System.out.println("P'(x) = " + pxPrime);
            System.out.println("G(x) = GCD(P(x), P'(x)) = " + gcd);
        }

        // Determine polynomial P0(x)
        Polynomial p0 = px.divide(gcd);

        // Sturm algorithm
        List<Polynomial> sturmSequence = new ArrayList<>();
        sturmSequence.add(p0);
        sturmSequence.add(p0.differentiate());

        if (printResults) {
            System.out.println("\nSturm sequence of polynomials:");
            System.out.println("P0(x) = " + sturmSequence.get(0));
            System.out.println("P1(x) = " + sturmSequence.get(1));
        }
        while (sturmSequence.get(sturmSequence.size() - 2).getDegree() > 1) {
            Polynomial pi_1 = sturmSequence.get(sturmSequence.size() - 2);
            Polynomial pi = sturmSequence.get(sturmSequence.size() - 1);

            Polynomial remainder = pi_1.remainder(pi).negate();
            sturmSequence.add(remainder);
            if (printResults) System.out.println("P" + (sturmSequence.size() - 1) + "(x) = " + remainder);
        }
        return findNumberOfRoots(sturmSequence, a, b, printResults);
    }

    private static int findNumberOfRoots(List<Polynomial> sturmSequence, double a, double b, boolean printResults) {
        int signChangesA = countSignChanges(sturmSequence, a, printResults);
        int signChangesB = countSignChanges(sturmSequence, b, printResults);

        if (printResults) {
            System.out.println("\nNumber of sign changes at point " + a + ": " + signChangesA);
            System.out.println("Number of sign changes at point " + b + ": " + signChangesB);
        }
        return signChangesA - signChangesB;
    }

    // Method to calculate number of sign changes on the interval [0, 3]
    private static int countSignChanges(List<Polynomial> sequence, double x, boolean printResults) {
        int signChanges = 0, i;
        Fraction current = null, previous = null;
        Fraction xFraction = new Fraction(x);

        if (printResults) System.out.println("\nValues of the polynomial sequence at point x = " + x + ":");
        for (i = 1; i < sequence.size(); i++) {
            if (i == 1 || (current != null && !current.equals(Fraction.ZERO)))
                previous = sequence.get(i - 1).evaluate(xFraction);
            current = sequence.get(i).evaluate(xFraction);
            if (printResults) System.out.print("P" + (i - 1) + "(x = " + xFraction + ") = " + previous + " ");

            if ((previous.compareTo(Fraction.ZERO) > 0 && current.compareTo(Fraction.ZERO) < 0)
                    || (previous.compareTo(Fraction.ZERO) < 0 && current.compareTo(Fraction.ZERO) > 0)) {
                signChanges++;
            }
        }
        if (printResults) System.out.println(" P" + (i - 1) + "(x = " + xFraction + ") = " + current);

        return signChanges;
    }
}
