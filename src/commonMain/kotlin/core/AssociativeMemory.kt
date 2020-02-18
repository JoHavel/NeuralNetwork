/*
 * file:               AssociativeMemory.kt
 * original author:    Jonáš Havelka <jonas.havelka@volny.cz>
 * project:            NeuralNetwork - Kotlin library for NNs
 * content:            class AssociativeMemory
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package core

import kotlin.random.Random

/**
 * Associative memory (neural network) consists of [Neuron]s
 *
 * @constructor creates new [AssociativeMemory] with
 * * [neurons] map connect objects to [Neuron]s      OR:
 * * [ideas] objects which stores Associative memory
 * * [function] which of [IActivationFunctions] we want to use in [neurons]
 *
 * @param[neurons] map connect objects to [Neuron]s
 */
class AssociativeMemory(private val neurons: MutableMap<Any, Neuron> = mutableMapOf()) {

    constructor(ideas: Set<Any>, function: IActivationFunctions = ActivationFunctions.Sigmoid) : this() {
        ideas.forEach { idea ->
            neurons[idea] = Neuron(
                function,
                inputs = neurons.values.map { it to Random.nextDouble(1.0) }.toMutableList()
            )
        }
    }

    /**
     * gets state after [repeat] times computing values of neurons
     */
    fun run(repeat: Int) {
        repeat(repeat) {
            neurons.values.forEach { it.prepareForStep() }
            neurons.values.forEach { it.run() }
        }
    }

    /**
     * gets only next state of [AssociativeMemory]
     */
    fun nextStep() {
        neurons.values.forEach { it.prepareForStep() }
        neurons.values.forEach { it.run() }
    }
}