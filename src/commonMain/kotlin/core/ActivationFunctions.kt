/*
 * file:               ActivationFunctions.kt
 * original author:    Jonáš Havelka <jonas.havelka@volny.cz>
 * project:            NeuralNetwork - Kotlin library for NNs
 * content:            enumerate class ActivationFunctions
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package core

import kotlin.math.*

/**
 * Enumerate of many common functions ([invoke] returns f(x)), with it's derivation (f'(x)) for 2 cases: when we have x - [xD] or when we have f(x) - [yD]
 *
 * @param[xD] Derivation (f'(x)) when we have x value
 * @param[yD] Derivation (f'(x)) when we have y = f(x) value
 */
enum class ActivationFunctions(
    private val function: (Double) -> Double,
    override val xD: (Double) -> Double,
    override val yD: (Double) -> Double
) : IActivationFunctions {
    /**
     * Zero for negative values, one for others.
     */
    @Deprecated("This function isn't smooth", level = DeprecationLevel.WARNING)
    BinaryStep({
        if (it < 0) {
            0.0
        } else {
            1.0
        }
    }, {
        if (it == 0.0) {
            Double.POSITIVE_INFINITY
        } else {
            0.0
        }
    }, { 0.0 }),

    /**
     * Identity for -1 <= x <= 1, -1 for x < -1 and 1 for x > 1
     */
    @Deprecated("This function isn't smooth", level = DeprecationLevel.WARNING)
    HardHyperbolicFunction({
        when {
            it < -1 -> -1.0
            it > 1 -> 1.0
            else -> it
        }
    }, {
        when {
            it < -1 || it > 1 -> {
                0.0
            }
            it == -1.0 || it == 1.0 -> {
                Double.NaN
            }
            else -> {
                1.0
            }
        }
    }, {
        if (it == -1.0 || it == 1.0) {
            0.0
        } else {
            1.0
        }
    }),

    /**
     * Zero for negative x, identity for positive x
     */
    @Deprecated("This function isn't smooth", level = DeprecationLevel.WARNING)
    RectifiedLinearUnit({ max(0.0, it) }, {
        when {
            it < 0 -> {
                0.0
            }
            it == 0.0 -> {
                Double.NaN
            }
            else -> {
                1.0
            }
        }
    }, {
        if (it == 0.0) {
            0.0
        } else {
            1.0
        }
    }),

    /**
     * Identity for positive x, scaled identity ([ALPHA] * x) for negative x
     */
    @Deprecated("This function isn't smooth", level = DeprecationLevel.WARNING)
    LeakyRectifiedLinearUnit({
        if (it < 0) {
            ALPHA * it
        } else {
            it
        }
    }, {
        when {
            it < 0 -> {
                ALPHA
            }
            it == 0.0 -> {
                Double.NaN
            }
            else -> {
                1.0
            }
        }
    }, {
        if (it < 0.0) {
            ALPHA
        } else {
            1.0
        }
    }),

    /**
     * f(x) = x
     */
    Identity({
        it
    }, {
        1.0
    }, {
        1.0
    }),

    /**
     * Smooth step: f(x) = 1 / (1 + e^-x)
     */
    Sigmoid({
        1 / (1 + exp(-it))
    }, {
        val expIt = exp(-it)
        expIt / (1 + expIt).pow(2)
    }, {
        it * (1 - it)
    }),

    /**
     * Hyperbolic tangents
     */
    Tanh({
        tanh(it)
    }, {
        1 / cosh(it).pow(2)
    }, {
        1 - it.pow(2)
    }),

    /**
     * Sign with smoothing (x / (|x| + 1))
     */
    Softsign({
        it / (abs(it) + 1)
    }, {
        1 / (1 + abs(it)).pow(2)
    }, {
        (1 - abs(it)).pow(2)
    }),

    /**
     * [RectifiedLinearUnit] with smoothing (ln(1 + exp(x)))
     */
    Softplus({
        ln(1 + exp(it))
    }, {
        1 / (1 + exp(-it))
    }, {
        1 / (2 - exp(it))
    }),

    /**
     * Identity for positive x, scaled exponential ([ALPHA] * exp(x) - 1) for negative x
     */
    ExponentialLinearUnit({
        if (it > 0) {
            it
        } else (ALPHA * exp(it) - 1)
    }, {
        if (it > 0) {
            1.0
        } else (ALPHA * (exp(it) + 1) - 1)
    }, {
        if (it > 0) {
            1.0
        } else (it + ALPHA)
    }),

    /**
     * x * [Sigmoid] (x / (1 + exp(-x)))
     */
    Swish({
        it / (1 + exp(-it))   //it * Sigmoid(it)
    }, {
        val expIt = exp(-it)
        1 / (1 + expIt) + it * expIt / (1 + expIt).pow(2)
    }, {
        TODO("WTF?")
    }),

    /**
     * [Sigmoid] for negative x, [ExponentialLinearUnit] for positive x and 0
     */
    ExponentialLinearSquashing({
        if (it < 0) (Sigmoid(it)) else (ExponentialLinearUnit(it))
    }, {
        if (it < 0) (Sigmoid.xD(it)) else (ExponentialLinearUnit.xD(it))
    }, {
        if (it < 0) (Sigmoid.yD(it)) else (ExponentialLinearUnit.yD(it))
    }),

    /**
     * ??? for negative x, [ExponentialLinearUnit] for positive x and 0
     */
    HardExponentialLinearSquashing({
        if (it < 0) ((exp(it) - 1) * max(0.0, min(1.0, (it + 1) / 2))) else (ExponentialLinearUnit(it))
    }, {
        if (it < 0) (Sigmoid.xD(it)) else (ExponentialLinearUnit.xD(it))
    }, {
        TODO("WTF^2")
    }),

    /**
     * Simply sinus
     */
    Sinus({
        sin(it)
    }, {
        cos(it)
    }, {
        sqrt(1 - it.pow(2))
    })
    ;

    override operator fun invoke(double: Double) = function(double)

    companion object {
        const val ALPHA = 1.0
    }
}
