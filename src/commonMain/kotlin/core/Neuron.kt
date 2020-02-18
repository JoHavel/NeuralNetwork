/*
 * file:               Neuron.kt
 * original author:    Jonáš Havelka <jonas.havelka@volny.cz>
 * project:            NeuralNetwork - Kotlin library for NNs
 * content:            class Neuron
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package core

import kotlin.random.Random

/**
 * Neuron for [AssociativeMemory]
 *
 * @param [function] activation function for neuron
 * @param [bias] bias for neuron
 * @param [inputs] [Pair]s of neurons and weights from them
 */
class Neuron(
    private val function: IActivationFunctions = ActivationFunctions.Sigmoid,
    private var bias: Double = Random.nextDouble(1.0),
    private val inputs: MutableList<Pair<Neuron, Double>> = mutableListOf()
) {
    var actualValue = 0.0
        private set
    private var lastValue = 0.0
    var error = 0.0

    /**
     * computation of new value
     */
    fun run() {
        actualValue = function(inputs.sumByDouble { it.first.lastValue * it.second } + bias)
    }

    /**
     * trains neuron
     */
    fun train() {
        val error2 = function.yD(actualValue) * error
        bias -= error2
        inputs.forEach { it.first.error += error2 * it.second }
    }

    /**
     * prepares neuron for next computation
     */
    fun prepareForStep() {
        lastValue = actualValue
    }
}