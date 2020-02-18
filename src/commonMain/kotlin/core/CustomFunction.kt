/*
 * file:               CustomFunction.kt
 * original author:    Jonáš Havelka <jonas.havelka@volny.cz>
 * project:            NeuralNetwork - Kotlin library for NNs
 * content:            class CustomFunction
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package core

class CustomFunction(
    private val function: (Double) -> Double,
    override val xD: (Double) -> Double,
    override val yD: (Double) -> Double
) : IActivationFunctions {
    override fun invoke(double: Double): Double = function(double)
}