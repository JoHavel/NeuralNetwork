package core

import koma.create
import koma.extensions.map
import koma.matrix.Matrix
import koma.rand
import koma.zeros
//import kotlin.math.abs
import kotlin.math.sqrt

class BasicNeuralNetwork(
    private val numberOfHiddenLayers: Int,
    val activationFunction: ActivationFunctions = ActivationFunctions.Sigmoid,
    val sizes: (Int) -> Int = { numberOfHiddenLayers },
    private val inputLayerSize: Int = numberOfHiddenLayers,
    private val outputLayerSize: Int = numberOfHiddenLayers,
    private val weights: MutableList<Matrix<Double>> = MutableList(numberOfHiddenLayers + 1) {
        when (it) {
            0 -> rand(sizes(it), inputLayerSize) * (sqrt(2.0 / (sizes(it) + inputLayerSize)))
            numberOfHiddenLayers -> rand(
                outputLayerSize,
                sizes(it - 1)
            ) * (sqrt(2.0 / (outputLayerSize + sizes(it - 1))))
            else -> rand(sizes(it), sizes(it - 1)) * (sqrt(2.0 / (sizes(it) + sizes(it - 1))))
        }
    },
    private val biases: MutableList<Matrix<Double>> = MutableList(numberOfHiddenLayers + 1) {
        //rand(if (it == numberOfHiddenLayers) { outputLayerSize } else { sizes(it) }, 1)
        zeros(
            if (it == numberOfHiddenLayers) {
                outputLayerSize
            } else {
                sizes(it)
            }, 1
        )
    }
) : INeuralNetwork {

    private val values = MutableList(numberOfHiddenLayers + 2) {
        zeros(
            when (it) {
                0 -> inputLayerSize
                numberOfHiddenLayers + 1 -> outputLayerSize
                else -> sizes(it)
            }, 1
        )
    }

    var learningRate = 0.01

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
        //println(error.map { abs(it) }.elementSum())
        for (i in numberOfHiddenLayers downTo 0) {
            val derivations = values[i + 1].map { activationFunction.yD(it) }.elementTimes(error)
            biases[i] += derivations * learningRate
            error = weights[i].T * derivations
            weights[i] += derivations * values[i].T * learningRate
        }

    }

    fun save() =
        "$numberOfHiddenLayers;$activationFunction;${(0..numberOfHiddenLayers + 1).map(sizes)};$inputLayerSize;$outputLayerSize;${weights.map { it.toList() }};${biases.map { it.toList() }}"

    companion object {
        fun load(data: String): BasicNeuralNetwork {

            val dataList = data.split(";")
            val numberOfHiddenLayers = dataList[0].toInt()
            val sizes: (Int) -> Int = { dataList[2].removePrefix("[").removeSuffix("]").split(", ")[it].toInt() }
            val inputLayerSize = dataList[3].toInt()
            val outputLayerSize = dataList[4].toInt()
            return BasicNeuralNetwork(
                numberOfHiddenLayers,
                ActivationFunctions.valueOf(dataList[1]),
                sizes,
                inputLayerSize,
                outputLayerSize,
                dataList[5].removePrefix("[[").removeSuffix("]]").split("], [").mapIndexed
                { index, it ->
                    when (index) {
                        0 -> create(
                            it.split(", ").map { str -> str.toDouble() }.toDoubleArray(),
                            sizes(index),
                            inputLayerSize
                        )
                        numberOfHiddenLayers -> create(
                            it.split(", ").map { str -> str.toDouble() }.toDoubleArray(),
                            outputLayerSize,
                            sizes(index - 1)
                        )
                        else -> create(
                            it.split(", ").map { str -> str.toDouble() }.toDoubleArray(),
                            sizes(index),
                            sizes(index - 1)
                        )
                    }
                }.toMutableList(),
                dataList[6].removePrefix("[[").removeSuffix("]]").split("], [").mapIndexed
                { index, it ->
                    create(
                        it.split(", ").map { str -> str.toDouble() }.toDoubleArray(),
                        if (index == numberOfHiddenLayers) {
                            outputLayerSize
                        } else {
                            sizes(index)
                        },
                        1
                    )
                }.toMutableList()
            )
        }
    }
}