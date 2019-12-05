package core.basics

import core.ActivationFunctions
import kotlin.random.Random

class StupidNeuralNetwork(private val neurons: MutableMap<Any, Neuron> = mutableMapOf()) {

    constructor(ideas: MutableSet<Any>, function: ActivationFunctions = ActivationFunctions.Sigmoid) : this() {
        ideas.forEach { idea ->
            neurons[idea] = Neuron(
                function,
                inputs = neurons.values.map { it to Random.nextDouble(1.0) }.toMutableList()
            )
        }
    }

    fun run(repeat: Int) {
        neurons.values.forEach { it.nextStep() }
        neurons.values.forEach { it.run() }
    }

    fun nextStep() = run(1)
}

typealias AssociativeMemory = StupidNeuralNetwork