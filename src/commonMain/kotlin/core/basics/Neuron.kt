package core.basics

import core.ActivationFunctions
import kotlin.random.Random

class Neuron(
    private val function: ActivationFunctions,
    private var bias: Double = Random.nextDouble(1.0),
    private val inputs: MutableList<Pair<Neuron, Double>> = mutableListOf()
) {
    var actualValue = 0.0
    var lastValue = 0.0
    var error = 0.0

    fun run() {
        actualValue = function(inputs.sumByDouble { it.first.lastValue * it.second } + bias)
    }

    fun train() {
        val error2 = function.yD(actualValue) * error
        bias -= error2
        inputs.forEach { it.first.error = error2 * it.second }
    }

    fun nextStep() {
        lastValue = actualValue
    }
}