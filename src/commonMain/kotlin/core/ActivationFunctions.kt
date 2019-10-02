package core

import kotlin.math.*

enum class ActivationFunctions(
    private val function: (Double) -> Double,
    val xD: (Double) -> Double,
    val yD: (Double) -> Double
) {
    @Deprecated("This function isn't smooth", level = DeprecationLevel.WARNING)
    BinaryStep({
        if (it < 0) {
            0.0
        } else {
            1.0
        }
    }, {
        if (it == 0.0) {
            INFTY
        } else {
            0.0
        }
    }, { 0.0 }),

    @Deprecated("This function isn't smooth", level = DeprecationLevel.WARNING)
    HardHyperbolicFunction({
        when {
            it < -1 -> -1.0
            it > 1 -> 1.0
            else -> it
        }
    }, {
        if (it < -1 || it > 1) {
            0.0
        } else {
            1.0
        }
    }, {
        if (it == -1.0 || it == 1.0) {
            0.0
        } else {
            1.0
        }
    }),

    @Deprecated("This function isn't smooth", level = DeprecationLevel.WARNING)
    RectifiedLinearUnit({ max(0.0, it) }, {
        if (it < 0) {
            0.0
        } else {
            1.0
        }
    }, {
        if (it == 0.0) {
            0.0
        } else {
            1.0
        }
    }),

    @Deprecated("This function isn't smooth", level = DeprecationLevel.WARNING)
    LeakyRectifiedLinearUnit({
        if (it < 0) {
            ALPHA * it
        } else {
            it
        }
    }, {
        if (it < 0) {
            ALPHA
        } else {
            1.0
        }
    }, {
        if (it < 0.0) {
            ALPHA
        } else {
            1.0
        }
    }),

    Identity({
        it
    }, {
        1.0
    }, {
        1.0
    }),

    Sigmoid({
        1 / (1 + exp(-it))
    }, {
        val expIt = exp(-it)
        expIt / (1 + expIt).pow(2)
    }, {
        it * (1 - it)
    }),

    Tanh({
        tanh(it)
    }, {
        1 / cosh(it).pow(2)
    }, {
        1 - it.pow(2)
    }),

    Softsign({
        it / (abs(it) + 1)
    }, {
        1 / (1 + abs(it)).pow(2)
    }, {
        (1 - abs(it)).pow(2)
    }),

    Softplus({
        ln(1 + exp(it))
    }, {
        1 / (1 + exp(-it))
    }, {
        1 / (2 - exp(it))
    }),

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

    Swift({
        it / (1 + exp(-it))   //it * Sigmoid(it)
    }, {
        val expIt = exp(-it)
        1 / (1 + expIt) + it * expIt / (1 + expIt).pow(2)
    }, {
        TODO("WTF?")
    }),

    ExponentialLinearSquashing({
        if (it < 0) (Sigmoid(it)) else (ExponentialLinearUnit(it))
    }, {
        if (it < 0) (Sigmoid.xD(it)) else (ExponentialLinearUnit.xD(it))
    }, {
        if (it < 0) (Sigmoid.yD(it)) else (ExponentialLinearUnit.yD(it))
    }),

    HardExponentialLinearSquashing({
        if (it < 0) ((exp(it) - 1) * max(0.0, min(1.0, (it + 1) / 2))) else (ExponentialLinearUnit(it))
    }, {
        if (it < 0) (Sigmoid.xD(it)) else (ExponentialLinearUnit.xD(it))
    }, {
        TODO("WTF^2")
    }),

    Sinus({
        sin(it)
    }, {
        cos(it)
    }, {
        sqrt(1 - it.pow(2))
    })
    ;

    operator fun invoke(double: Double) = function(double)
}
