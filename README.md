<div id="top"></div>

<!-- PROJECT [jovan-vukic] SHIELDS -->

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <h2 align="center">Polynomial Root Counter</h2>

  <p align="center">
    Polynomial Root Counter is a Java program designed to analyze and count the roots of polynomials with rational coefficients within specified intervals.
    It provides tools for efficiently applying Sturm's theorem to determine the number of roots of polynomials over a given range.
    Additionally, it can offer simple analysis of the sign of polynomials with both rational and real coefficients, within defined range.
    <br />
    <a href="https://github.com/jovan-vukic/polynomial-root-finder"><strong>Explore the project »</strong></a>
    <br />
    <br />
    <a href="https://github.com/jovan-vukic/polynomial-root-finder/issues">Report Bug</a>
    ·
    <a href="https://github.com/jovan-vukic/polynomial-root-finder/issues">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
         <li><a href="#rational-approximation">Rational Approximation</a></li>
         <li><a href="#root-counter">Root Counter</a></li>
         <li><a href="#mixed-trigonometric-polynomial-functions">Mixed Trigonometric Polynomial Functions</a></li>
      </ul>
    </li>
    <li>
      <a href="#implementation">Implementation</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
         <li><a href="#installation">Installation</a></li>
         <li>
            <a href="#expected-output">Expected Output</a>
            <ul>
               <li><a href="#rational-approximation-1">Rational Approximation</a></li>
               <li><a href="#root-counter-1">Root Counter</a></li>
               <li><a href="#mixed-trigonometric-polynomial-functions-1">Mixed Trigonometric Polynomial Functions</a></li>
            </ul>
         </li>
      </ul>
    </li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

## About The Project

The project consists of three main parts.

### Rational Approximation

The first part involves determining the _best rational approximations_ of the first and second kind for given number
$\alpha$.
Convergents $\frac{p}{q}$ are checked, where $q$ is in a specified range $[n, m]$, and $p$ is obtained as the nearest
integer value to $\alpha \cdot q$.

**Definition 1.** A rational number $\frac{p}{q}$ is the best first-kind rational approximation of $\alpha$ if
$$|\alpha - \frac{p}{q}| < |\alpha - \frac{r}{s}|$$
for all fractions $\frac{r}{s} \neq \frac{p}{q}$ where $0 < s \leq q$.

**Definition 2.** A rational number $\frac{p}{q}$ is the best second-kind rational approximation of $\alpha$ if
$$|q \cdot \alpha - p| < |s \cdot \alpha - r|$$
for all fractions $\frac{r}{s} \neq \frac{p}{q}$ where $0 < s \leq q$.

It's worth noting that each best second-kind rational approximation is also the best first-kind rational approximation.

The results are sorted by the value of the absolute error $|\alpha - \frac{p}{q}|$. Convergents $\frac{p}{q}$ are
presented in continued fraction form $[a0; a1, ..., an]$ too, where $a0, a1, ..., an$ are continued decimals. This is
handled by the class `RationalApproximation`.

### Root Counter

The second part of the project applies Sturm's theorem to the polynomial $P(x)$ to determine the number of roots in the
interval $(a, b]$.
$GCD(P(x), P'(x))$ is calculated in case $P(x)$ has multiple roots. This is addressed by the
class `PolynomialRootFinder`.

Additionally, the `RealPolynomial` class performs the same process on the polynomial $P(x)$ with real (not necessarily
rational) coefficients.
Before applying Sturm's theorem, real coefficients are rounded down to $k$ decimals.
This yields a rational polynomial to which the Sturm theorem described in `PolynomialRootFinder` is then applied.
The sign of $P(x)$ is determined on the segment $\[ a, b \]$.

### Mixed Trigonometric Polynomial Functions

The third part of the project involves considering MTP (Mixed Trigonometric Polynomial) Functions:
$$f(x) = \sum_{i=1}^{n} \alpha_i x^{p_i} \cos^{q_i}(x) \sin^{r_i}(x)$$
in the context of determining the positivity of such functions on the standard interval $(0, \frac{\pi}{2})$.
This is done by expanding terms containing $\sin(x)$ and $\cos(x)$ with Maclaurin series up to a certain degree.
Thus, an approximate polynomial $P(x) < f(x)$ for the function $f(x)$ is obtained.
It is further verified that $P(x) > 0$ on the specified interval, and if so, it is certain that $f(x) > 0$.
The choice of polynomial $P(x)$ is made iteratively until a suitable polynomial is found. This is handled by the
class `MTP`.
</sh>

<p align="right">(<a href="#top">back to top</a>)</p>

## Implementation

The project is implemented using Java classes in the `math.analysis` package, which provide solutions to the described
problems using object-oriented programming concepts.
The `math.util` package contains classes implementing fraction concepts in the `Fraction` class and polynomials with
rational coefficients in the `Polynomial` class, utilizing `BigDecimal` and `BigInteger` types for high precision.

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- GETTING STARTED -->

## Getting Started

To get a local copy up and running follow these simple steps.

### Installation

Setup:

1. Clone the repository:
   ```sh
   git clone https://github.com/jovan-vukic/polynomial-root-finder.git
   ```
2. Build and compile the project using your preferred Java IDE.
3. Run the main methods of individual classes to test the implemented functionalities.

### Expected Output

#### Rational Approximation

This is the output of the program when the `RationalApproximation` class is executed. For a given number $\alpha$ and a
range $\[ n, m \]$, we observe the best rational approximations of the first and second kinds.

```shell-session
Enter n, m and alpha respectively:
Enter floor 'n': 1
Enter limit 'm': 10
Enter target 'alpha': 3.1415926
Fraction p/q    | Continued fraction           | Kind             | Error                       
22/7            | [3, 7]                       | 2                | +0.001264542857142857      
25/8            | [3, 8]                       | N                | -0.016592600000000000      
19/6            | [3, 6]                       | 1                | +0.025074066666666667      
28/9            | [3, 9]                       | N                | -0.030481488888888889      
31/10           | [3, 10]                      | N                | -0.041592600000000000      
16/5            | [3, 5]                       | 1                | +0.058407400000000000      
13/4            | [3, 4]                       | 1                | +0.108407400000000000      
3/1             | [3]                          | 2                | -0.141592600000000000      

Process finished with exit code 0
```

#### Root Counter

Then, in the `PolynomialRootFinder` class, for a given interval $\( a, b \]$ and a statically defined polynomial:
$$P(x) = x^9 - 3 \cdot x^7 - x^6 + 3 \cdot x^5 + 3 \cdot x^4 - x^3 - 3 \cdot x^2 + 1$$
the number of roots in the interval is determined using Sturm's theorem.

```shell-session
Enter the bounds of the interval (a, b]: 0 3
Determining the GCD(P(x), P'(x)):
P(x) = x^9 - 3*x^7 - x^6 + 3*x^5 + 3*x^4 - x^3 - 3*x^2 + 1
P'(x) = 9*x^8 - 21*x^6 - 6*x^5 + 15*x^4 + 12*x^3 - 3*x^2 - 6*x
G(x) = GCD(P(x), P'(x)) = x^5 - x^4 - 2*x^3 + 2*x^2 + x - 1

Sturm sequence of polynomials:
P0(x) = x^4 + x^3 - x - 1
P1(x) = 4*x^3 + 3*x^2 - 1
P2(x) = 3/16*x^2 + 3/4*x + 15/16
P3(x) = -32*x - 64
P4(x) = -3/16

Values of the polynomial sequence at point x = 0.0:
P0(x = 0) = -1 P1(x = 0) = -1 P2(x = 0) = 15/16 P3(x = 0) = -64  P4(x = 0) = -3/16

Values of the polynomial sequence at point x = 3.0:
P0(x = 3) = 104 P1(x = 3) = 134 P2(x = 3) = 39/8 P3(x = 3) = -160  P4(x = 3) = -3/16

Number of sign changes at point 0.0: 2
Number of sign changes at point 3.0: 1
Number of roots of the polynomial in the interval (0.0, 3.0]: 1

Process finished with exit code 0
```

Next, in the `RealPolynomial` class, the sign of the real polynomial:

$$P(x) = -\frac{e^3}{10} \cdot x^8 - \( \frac{e}{4} - 1 \) \cdot x^5 + (\frac{e^3}{4} - 2) \cdot x^4 + \frac{e}{2} \cdot x^3 +
\frac{e}{5} \cdot x$$

on the segment $\[ a, b \]$ is determined.  To achieve this, the Sturm theorem is applied to the approximate polynomial $Q \( x \)$ on the interval $\( a - 0.1, b \]$.

```shell-session
Enter the value for k: 4
Enter the bounds of the segment [a, b]: 0 1.2

P(x) = -2.0085536923187663*x^8 + 0.3204295428852387*x^5 + 3.021384230796916*x^4 + 1.3591409142295225*x^3 + 0.543656365691809*x
Q(x) = -10043/5000*x^8 + 16021/50000*x^5 + 30213/10000*x^4 + 13591/10000*x^3 + 10873/20000*x

Number of roots of the polynomial Q(x) in the interval (-0.1, 1.2]: 1
The polynomial Q(x) has a root at point x = 0.0.
Q(x = 1.2) = 5572529799/3906250000 > 0
The polynomial Q(x) is positive on the segment [0.0, 1.2].
Since P(x) > Q(x) on the segment [0.0, 1.2], then P(x) has the same sign on that segment.

Process finished with exit code 0
```

#### Mixed Trigonometric Polynomial Functions

By selecting a suitable Maclaurin series for $\sin \( x \)$ and $\cos \( x \)$, the approximate polynomial $P \( x \)$
to the function:
$$f(x) = \frac{1}{4} \cdot x^3\sin^2\(x\) - \frac{1}{6}\cdot x\cos\(x\)\sin^2\(x\) + \frac{1}{2}\cdot x^3 - \frac{1}{15}\cdot x^4 - \frac{3}{25} \cdot x^6$$
is created. Positivity of the suitable polynomial $P \( x \)$ is examined to prove the positivity of the function $f \( x \)$.

```shell-session
f(x) = 1/4*x^3*sin^2(x) - 1/6*x*cos(x)*sin^2(x) + 1/2*x^3 - 1/15*x^4 - 3/25*x^6

P[k1=0, k2=0](x) = 1/144*x^9 - 1/12*x^7 - 3/25*x^6 + 1/4*x^5 - 1/15*x^4 + 1/3*x^3
The given polynomial changes sign in the interval (0.0, 1.58).

P[k1=0, k2=1](x) = 1/144*x^9 - 13/144*x^7 - 3/25*x^6 + 1/3*x^5 - 1/15*x^4 + 1/3*x^3
The given polynomial is positive in the interval (0.0, 1.58).
P[k1=0, k2=1](x) is the sought polynomial to prove the positivity of the MTP function f(x).

Process finished with exit code 0
```

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTRIBUTING -->

## Contributing

Contributions are what makes the open source community such an amazing place to learn, inspire, and create. Any
contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also
simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- LICENSE -->

## License

Distributed under the MIT License. See `LICENSE` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTACT -->

## Contact

Jovan - [@jovan-vukic](https://github.com/jovan-vukic)

Project
Link: [https://github.com/jovan-vukic/polynomial-root-finder](https://github.com/jovan-vukic/polynomial-root-finder)

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- ACKNOWLEDGMENTS -->

## Acknowledgments

This project was done as part of the course 'Selected Topics in Numerical Analysis' (13M081OPNA) at the University of
Belgrade, Faculty of Electrical Engineering.

<p align="right">(<a href="#top">back to top</a>)</p>