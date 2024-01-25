public class MTP {
    private static final Fraction[] coefficients = {
//            new Fraction(1),
//            new Fraction(-1),
//            new Fraction(1),
//            new Fraction(-3, 2),
//            new Fraction(3, 32)
            new Fraction(1, 4),
            new Fraction(-1, 6),
            new Fraction(1, 2),
            new Fraction(-1, 15),
            new Fraction(-3, 25)
    };

    // row 1: degree(x)
    // row 2: degree(cos)
    // row 3: degree(sin)
    private static final int[][] exponents = {
//            {3, 1, 1, 3, 4},
//            {0, 3, 0, 0, 0},
//            {1, 0, 0, 0, 0},
            {3, 1, 3, 4, 6},
            {0, 1, 0, 0, 0},
            {2, 2, 0, 0, 0},
    };

    public static void main(String[] args) {
        Polynomial px;
        int k1, k2 = 0;
        double a = 0, b = 1.58; // interval: (0, pi/2)

        printFunctionTerms();

        // 0 0 // 0 1 // 0 2 // 0 3 // 0 4
        // - - // 1 0 // 2 0 // 3 0 // 4 0
        // - - // 1 1 // 1 2 // 1 3 // 1 4
        // - - // - - // 2 1 // 3 1 // 4 1
        // - - // - - // 2 2 // 2 3 // 2 4
        // - - // - - // - - // 3 2 // 4 2
        // - - // - - // - - // 3 3 // 3 4
        // - - // - - // - - // - - // 4 3
        // - - // - - // - - // - - // 4 4
        while (true) {
            k1 = 0;

            px = getPolynomial(k1, k2);
            int sgnPx = RealPolynomial.findPolynomialSgn(a, b, px, false);
            printPolynomialDetails(px, sgnPx, k1, k2, a, b);
            if (sgnPx == 1) break;

            if (k2 != 0) {
                px = getPolynomial(k2, k1);
                sgnPx = RealPolynomial.findPolynomialSgn(a, b, px, false);
                printPolynomialDetails(px, sgnPx, k2, k1, a, b);
                if (sgnPx == 1) break;
            }
            if (++k1 <= k2) {
                px = getPolynomial(k1, k2);
                sgnPx = RealPolynomial.findPolynomialSgn(a, b, px, false);
                printPolynomialDetails(px, sgnPx, k1, k2, a, b);
                if (sgnPx == 1) break;

                if (k1 < k2) {
                    px = getPolynomial(k2, k1);
                    sgnPx = RealPolynomial.findPolynomialSgn(a, b, px, false);
                    printPolynomialDetails(px, sgnPx, k2, k1, a, b);
                    if (sgnPx == 1) break;
                }
            }
            k2++;
        }
        System.out.println("P[k1=" + k1 + ", k2=" + k2 + "](x) is the sought polynomial to prove the positivity of the MTP function f(x).");
    }

    private static Polynomial getPolynomial(int k1, int k2) {
        Polynomial px = Polynomial.ZERO;

        for (int j = 0; j < coefficients.length; j++) {
            Polynomial term = new Polynomial(Fraction.ONE, 0);

            // Multiply pure polynomial and expanded cos and sin terms
            Fraction coefficient = coefficients[j];
            int coefficientSgn = coefficient.compareTo(Fraction.ZERO);
            for (int i = 0; i <= 2; i++) {
                Polynomial taylorPolynomial;
                int exponent = exponents[i][j];

                switch (i) {
                    case 0 -> { // a * x^n
                        term = term.multiply(new Polynomial(coefficient, exponent));
                    }
                    case 1 -> { // cosx
                        if (coefficientSgn > 0 && exponent != 0) {
                            taylorPolynomial = cosTaylorSeries(4 * k2 + 2);
                            while (exponent-- > 0) {
                                term = term.multiply(taylorPolynomial);
                            }
                        } else if (coefficientSgn < 0 && exponent != 0) {
                            taylorPolynomial = cosTaylorSeries(4 * k2 + 0);
                            while (exponent-- > 0) {
                                term = term.multiply(taylorPolynomial);
                            }
                        }
                    }
                    case 2 -> { // sinx
                        if (coefficientSgn > 0 && exponent != 0) {
                            taylorPolynomial = sinTaylorSeries(4 * k1 + 3);
                            while (exponent-- > 0) {
                                term = term.multiply(taylorPolynomial);
                            }
                        } else if (coefficientSgn < 0 && exponent != 0) {
                            taylorPolynomial = sinTaylorSeries(4 * k1 + 1);
                            while (exponent-- > 0) {
                                term = term.multiply(taylorPolynomial);
                            }
                        }
                    }
                }
            }
            px = px.add(term);
        }
        return px;
    }

    // Method for calculating cos(x) using Taylor series expansion
    public static Polynomial cosTaylorSeries(int maxExponent) {
        Polynomial result = Polynomial.ZERO;

        for (int i = 0; i <= (int) ((maxExponent) / 2.0); i++) {
            int exponent = 2 * i;
            Fraction coefficient = new Fraction((int) Math.pow(-1, i), factorial(exponent));

            result = result.add(new Polynomial(coefficient, exponent));
        }

        return result;
    }

    // Method for calculating sin(x) using Taylor series expansion
    public static Polynomial sinTaylorSeries(int maxExponent) {
        Polynomial result = Polynomial.ZERO;

        for (int i = 0; i <= (int) ((maxExponent - 1) / 2.0); i++) {
            int exponent = 2 * i + 1;
            Fraction coefficient = new Fraction((int) Math.pow(-1, i), factorial(exponent));

            result = result.add(new Polynomial(coefficient, exponent));
        }

        return result;
    }

    private static int factorial(int n) {
        if (n == 0 || n == 1) return 1;
        return n * factorial(n - 1);
    }

    private static void printPolynomialDetails(Polynomial px, int sgnPx, int k1, int k2, double a, double b) {
        System.out.println("\nP[k1=" + k1 + ", k2=" + k2 + "](x) = " + px);
        System.out.println("The given polynomial "
                + (sgnPx == -1 ? "is negative" : (sgnPx == 1 ? "is positive" : (sgnPx == 2 ? "changes sign" : "is of undetermined sign")))
                + " in the interval (" + a + ", " + b + ").");
    }

    private static void printFunctionTerms() {
        System.out.print("f(x) = ");
        for (int j = 0; j < coefficients.length; j++) {
            Fraction coefficient = coefficients[j];
            int coefficientSgn = coefficient.compareTo(Fraction.ZERO);

            for (int i = 0; i <= 2; i++) {
                int exponent = exponents[i][j];

                if (j != 0 && i == 0) {
                    if (coefficientSgn < 0) System.out.print(" - ");
                    else System.out.print(" + ");
                }

                switch (i) {
                    case 0 -> { // a * x^n
                        System.out.print(new Polynomial(coefficient.abs(), exponent));
                    }
                    case 1 -> { // cosx
                        if (exponent != 0) {
                            System.out.print("*cos");
                            if (exponent != 1) System.out.print("^" + exponent);
                            System.out.print("(x)");
                        }
                    }
                    case 2 -> { // sinx
                        if (exponent != 0) {
                            System.out.print("*sin");
                            if (exponent != 1) System.out.print("^" + exponent);
                            System.out.print("(x)");
                        }
                    }
                }
            }
        }
        System.out.println();
    }
}
