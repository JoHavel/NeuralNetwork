package core

/**
 * Interface for activation functions for neural networks
 */

interface IActivationFunctions {
    /**
     * Derivation (f'(x)) when we have x value
     */
    val xD: (Double) -> Double

    /**
     * Derivation (f'(x)) when we have y = f(x) value
     */
    val yD: (Double) -> Double

    /**
     * Returns functional value (f([double]))
     */
    operator fun invoke(double: Double): Double
}