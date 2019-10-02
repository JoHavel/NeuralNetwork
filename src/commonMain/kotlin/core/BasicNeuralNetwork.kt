package core

import koma.extensions.map
import koma.matrix.Matrix
import koma.rand
import koma.zeros
import kotlin.math.sqrt

class BasicNeuralNetwork(
    private val numberOfHiddenLayers: Int,
    val activationFunction: ActivationFunctions = ActivationFunctions.Sigmoid,
    val sizes: (Int) -> Int = { numberOfHiddenLayers },
    private val inputLayerSize: Int = numberOfHiddenLayers,
    private val outputLayerSize: Int = numberOfHiddenLayers
) : INeuralNetwork {

    private val weights = MutableList(numberOfHiddenLayers + 1) {
        when (it) {
            0 -> rand(sizes(it), inputLayerSize) * (sqrt(2.0 / (sizes(it) + inputLayerSize)))
            numberOfHiddenLayers -> rand(
                outputLayerSize,
                sizes(it - 1)
            ) * (sqrt(2.0 / (outputLayerSize + sizes(it - 1))))
            else -> rand(sizes(it), sizes(it - 1)) * (sqrt(2.0 / (sizes(it) + sizes(it - 1))))
//            0 ->                        zeros(sizes(it), inputLayerSize)
//            numberOfHiddenLayers ->     zeros(outputLayerSize, sizes(it - 1))
//            else ->                     zeros(sizes(it), sizes(it - 1))
        }
    }

    private val values = MutableList(numberOfHiddenLayers + 2) {
        zeros(
            when (it) {
                0 -> inputLayerSize
                numberOfHiddenLayers + 1 -> outputLayerSize
                else -> sizes(it)
            }, 1
        )
    }

    private val biases = MutableList(numberOfHiddenLayers + 1) {
        //rand(if (it == numberOfHiddenLayers) { outputLayerSize } else { sizes(it) }, 1)
        zeros(
            if (it == numberOfHiddenLayers) {
                outputLayerSize
            } else {
                sizes(it)
            }, 1
        )
    }

    var learningRate = 0.1

    override fun run(input: Matrix<Double>): Matrix<Double> {
        require(inputLayerSize == input.size) { "Wrong size of input! This NN has input size $inputLayerSize, but you offer it input with size ${input.size}." }
        values[0] = input
        for (index in weights.indices) {
            values[index + 1] = (weights[index] * values[index] + biases[index]).map { activationFunction(it) }
        }
        return values.last()
    }

    override fun train(input: Matrix<Double>, output: Matrix<Double>) {
        var error = output - run(input)
        for (i in numberOfHiddenLayers downTo 0) {
            val derivations = values[i + 1].map { activationFunction.yD(it) }.elementTimes(error)
            biases[i] += derivations * learningRate
            error = weights[i].T * derivations
            weights[i] += derivations * values[i].T * learningRate
        }

    }

}