import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class RationalApproximation {
    private static BigDecimal alpha = null;

    private static void showUsage() {
        System.out.println("""
                Available options:
                1. Set floor 'n' (minimum denominator value). Must be grater than 0.
                2. Set limit 'm' (maximum denominator value). Must be grater than 0.
                3. Set target 'alpha' (real number to be approximated)."""
        );
    }

    private static void atExit(String message) {
        if (message != null && !message.isEmpty()) {
            System.err.println("Error occurred: " + message);
        }
        showUsage();
        System.exit(-1);
    }

    public static class Fraction implements Comparable<Fraction> {
        private final int numerator;
        private final int denominator;
        private int kind; // 0 - 'not first-kind', 1 - 'first-kind', 2 - 'second-kind'

        public Fraction(int numerator, int denominator) {
            // Denominator cannot be zero
            if (denominator == 0) atExit("Denominator cannot be zero.");

            // Numerator and denominator have to be coprime numbers
            int gcdValue;
            while ((gcdValue = gcd(numerator, denominator)) != 1) {
                numerator /= gcdValue;
                denominator /= gcdValue;
            }

            this.numerator = numerator;
            this.denominator = denominator;
        }

        public BigDecimal error(BigDecimal alpha) {
            BigDecimal fractionValue = getFractionBigDecimal();

            return fractionValue.subtract(alpha).setScale(18, RoundingMode.HALF_UP);
        }

        public BigDecimal absoluteError(BigDecimal alpha) {
            return error(alpha).abs();
        }

        /**
         * Finds the rational approximation for a given limit and target fraction.
         *
         * @param q     The value of the denominator.
         * @param alpha The target fraction to approximate.
         * @return A {@code Fraction} object representing the rational approximation of 'alpha'.
         */
        private static Fraction findRationalApproximation(int q, BigDecimal alpha) {
            if (q <= 0 || alpha.compareTo(BigDecimal.ZERO) <= 0) {
                atExit("Denominator ('q') and target ('alpha') values should be positive.");
            }

            // Create and return the fraction
            int p = alpha.multiply(BigDecimal.valueOf(q)).setScale(0, RoundingMode.HALF_UP).intValue();
            return new Fraction(p, q);
        }

        public void setKind(BigDecimal alpha) {
            kind = isSecondKind(alpha) ? 2 : (isFirstKind(alpha) ? 1 : 0);
        }

        public int getKind() {
            return kind;
        }

        public int getNumerator() {
            return numerator;
        }

        private BigDecimal getNumeratorBigDecimal() {
            return BigDecimal.valueOf(numerator);
        }

        public int getDenominator() {
            return denominator;
        }

        private BigDecimal getDenominatorBigDecimal() {
            return BigDecimal.valueOf(denominator);
        }

        private BigDecimal getFractionBigDecimal() {
            return getNumeratorBigDecimal().divide(getDenominatorBigDecimal(), 18, RoundingMode.HALF_UP);
        }

        /**
         * This method calculates the continued fraction representation of a rational number.
         *
         * @return A string representation of the continued fraction.
         */
        public String getContinuedFraction() {
            List<Integer> a = new ArrayList<>();
            int numerator = this.numerator, denominator = this.denominator;

            while (denominator != 0) {
                a.add(numerator / denominator);
                int temp = numerator;
                numerator = denominator;
                denominator = temp % denominator;
            }
            return a.toString();
        }

        @Override
        public String toString() {
            return numerator + "/" + denominator;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Fraction fraction = (Fraction) obj;
            BigDecimal thisValue = getFractionBigDecimal();
            BigDecimal fractionValue = fraction.getFractionBigDecimal();

            return thisValue.compareTo(fractionValue) == 0;
        }

        @Override
        public int compareTo(Fraction fraction) {
            return absoluteError(alpha).compareTo(fraction.absoluteError(alpha));
        }

        private int gcd(int a, int b) {
            if (b == 0) return a;
            return gcd(b, a % b);
        }

        private boolean isFirstKind(BigDecimal alpha) {
            Fraction fraction;
            boolean isFirstKind = true;

            for (int s = 1; !this.equals(fraction = findRationalApproximation(s, alpha)); s++) {
                if (this.compareTo(fraction) >= 0) {
                    isFirstKind = false;
                    break;
                }
            }
            return isFirstKind;
        }

        private boolean isSecondKind(BigDecimal alpha) {
            Fraction fraction;
            BigDecimal numerator = getNumeratorBigDecimal(), fractionNumerator;
            BigDecimal denominator = getDenominatorBigDecimal(), fractionDenominator;
            boolean isSecondKind = true;

            for (int s = 1; !this.equals(fraction = findRationalApproximation(s, alpha)); s++) {
                fractionNumerator = fraction.getNumeratorBigDecimal();
                fractionDenominator = fraction.getDenominatorBigDecimal();

                BigDecimal expression1 = denominator.multiply(alpha).subtract(numerator).setScale(18, RoundingMode.HALF_UP).abs();
                BigDecimal expression2 = fractionDenominator.multiply(alpha).subtract(fractionNumerator).setScale(18, RoundingMode.HALF_UP).abs();

                if (expression1.compareTo(expression2) >= 0) {
                    isSecondKind = false;
                    break;
                }
            }
            return isSecondKind;
        }
    }

    public static void main(String[] args) {
        // Get user input for n, m, and alpha
        int n, m;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter n, m and alpha respectively:");

        System.out.print("Enter floor 'n': ");
        n = Integer.parseInt(scanner.next().trim());
        if (n <= 0) atExit("Invalid value for 'n'.");

        System.out.print("Enter limit 'm': ");
        m = Integer.parseInt(scanner.next().trim());
        if (m <= 0) atExit("Invalid value for 'm'.");

        System.out.print("Enter target 'alpha': ");
        alpha = new BigDecimal(scanner.next().trim()).setScale(18, RoundingMode.HALF_UP);
        if (alpha.compareTo(BigDecimal.ZERO) <= 0) atExit("Invalid value for 'alpha'.");

        // Find the rational approximations p / q
        List<Fraction> fractions = getFractionsList(n, m);

        // Print the results
        printSortedFractionsList(fractions);
    }

    /**
     * Generates a list of fractions with the denominator within the range [n, m].
     *
     * @param n The lower bound of the range.
     * @param m The upper bound of the range.
     * @return A list of fractions within the specified range.
     */
    private static List<Fraction> getFractionsList(int n, int m) {
        List<Fraction> fractions = new ArrayList<>();
        int q = n;

        // Loop through the range [n, m] to find fractions
        outerLoop:
        while (q <= m) {
            Fraction fraction = Fraction.findRationalApproximation(q++, alpha);

            // Check if 'fraction' should be added to the list
            if (fraction.getDenominator() < n) continue;

            // Check if 'fraction' is already in the list
            if (!fractions.isEmpty()) {
                for (Fraction otherFraction : fractions) {
                    if (fraction.equals(otherFraction))
                        continue outerLoop;
                }
            }

            // Add fraction to the list
            fraction.setKind(alpha);
            fractions.add(fraction);
        }
        return fractions;
    }

    /**
     * Prints the sorted list of fractions along with other details about them.
     *
     * @param fractions The list of fractions to be printed.
     */
    private static void printSortedFractionsList(List<Fraction> fractions) {
        // Define ANSI escape codes for color
        String resetColor = "\u001B[0m";
        String blueColor = "\u001B[34m";
        String redColor = "\u001B[31m";

        // Sort the fractions
        Collections.sort(fractions);

        // Print the header
        System.out.printf("%-15s | %-28s | %-16s | %-28s%n", "Fraction p/q", "Continued fraction", "Kind", "Error");

        // Iterate through the sorted fractions and print each one
        for (Fraction f : fractions) {
            String errorSign = (f.error(alpha).compareTo(BigDecimal.ZERO) >= 0) ? "+" : "-";
            String lineColor = f.getKind() == 1
                    ? blueColor
                    : (f.getKind() == 2 ? redColor : "");

            // Print the formatted fraction information
            System.out.printf(lineColor + "%-15s | %-28s | %-16s | %s%-30s%n",
                    f,
                    f.getContinuedFraction(),
                    f.getKind() == 0 ? "N" : f.getKind(),
                    errorSign,
                    f.absoluteError(alpha) + resetColor);
        }
    }
}
