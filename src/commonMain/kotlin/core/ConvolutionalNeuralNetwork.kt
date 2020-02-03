package core

import koma.create
import koma.extensions.*
import koma.matrix.Matrix
import koma.sqrt

/**
 * Convolutional Neural Network consisted only of two [BasicNeuralNetwork].
 *
 * @constructor creates new [ConvolutionalNeuralNetwork] with
 * * [filter] as small [BasicNeuralNetwork] that applies on every part of image before [neuralNetwork]
 * * [neuralNetwork] as the main network
 *
 * @param[filter] small main network
 * @param[neuralNetwork] main neural network
 * @param[trainBoth] if filter should be trained
 */
class ConvolutionalNeuralNetwork(
    private val filter: BasicNeuralNetwork,
    private val neuralNetwork: BasicNeuralNetwork,
    private val trainBoth: Boolean = false
) :
    INeuralNetwork {

    /**
     * Size of one side of [filter]
     */
    private val filterSizeSqrt: Int

    /**
     * [Double] value which declares how quickly weights and biases are changing
     */
    var learningRate = 0.1
        set(value) {
            field = value
            filter.learningRate = value
            neuralNetwork.learningRate = value
        }

    init {
        val s = sqrt(filter.inputLayerSize)
        filterSizeSqrt = s.toInt()
        require(s == filterSizeSqrt.toDouble()) { "Filter is not square" }
        require(neuralNetwork.inputLayerSize % filter.outputLayerSize == 0) { "Filter is not for this neural network" }
    }

    /**
     * Applies filter on every square of [input]
     */
    private fun runFilter(input: Matrix<Double>): Matrix<Double> {
        val output = Matrix(
            (input.numRows() - filterSizeSqrt + 1) * (input.numCols() - filterSizeSqrt + 1) * filter.outputLayerSize,
            1
        ) { _, _ -> 0.0 }
        var offset = 0
        for (i in 0 until input.numRows() - filterSizeSqrt + 1) {
            for (j in 0 until input.numCols() - filterSizeSqrt + 1) {
                val output1 = filter.run(input[i until i + filterSizeSqrt, j until j + filterSizeSqrt].toDoubleArray())
                output1.forEachIndexed { it, ele ->
                    output[offset + it] = ele
                }
                offset += output1.size
            }
        }
        return output
    }

    override fun run(input: Matrix<Double>): Matrix<Double> {
        val input2 = runFilter(input)
        require(input2.size == neuralNetwork.inputLayerSize) { "Invalid input matrix size for neural network" }
        return neuralNetwork.run(input2)
    }

    override fun train(input: Matrix<Double>, output: Matrix<Double>): Matrix<Double> {
        val input2 = runFilter(input)
        val error = neuralNetwork.train(input2, output)
        return if (trainBoth) {
            val error2 = Matrix(filter.outputLayerSize, 1) { _, _ -> 0.0 }
            error.forEachIndexed { idx: Int, ele: Double -> error[idx % filter.outputLayerSize] += ele }
            filter.train(error2.map { it * filter.outputLayerSize / error.size })
        } else create(DoubleArray(0))
    }

    override fun save() = filter.save() + ";;" + neuralNetwork.save()

    companion object {
        /**
         * Load [ConvolutionalNeuralNetwork] from [data]
         */
        fun load(data: String): ConvolutionalNeuralNetwork {
            val nns = data.split(";;")
            return ConvolutionalNeuralNetwork(BasicNeuralNetwork.load(nns[0]), BasicNeuralNetwork.load(nns[1]))
        }

        /**
         * Data of [Matrix] for [edgeFilter]
         */
        private val edgeFilterData = mutableListOf(
            mutableListOf(1.0, 1.0, 1.0, 0.0, 0.0, 0.0, -1.0, -1.0, -1.0),
            mutableListOf(1.0, 0.0, -1.0, 1.0, 0.0, -1.0, 1.0, 0.0, -1.0),
            mutableListOf(-1.0, -1.0, -1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
            mutableListOf(-1.0, 0.0, 1.0, -1.0, 0.0, 1.0, -1.0, 0.0, 1.0),
            mutableListOf(1.0, 1.0, 0.0, 1.0, 0.0, -1.0, 0.0, -1.0, -1.0),
            mutableListOf(-1.0, -1.0, 0.0, -1.0, 0.0, 1.0, 0.0, 1.0, 1.0),
            mutableListOf(0.0, 1.0, 1.0, 1.0, 0.0, -1.0, -1.0, -1.0, 0.0),
            mutableListOf(0.0, -1.0, -1.0, -1.0, 0.0, 1.0, 1.0, 1.0, 0.0)
        )

        /**
         * Example filter, detects edges
         */
        val edgeFilter: BasicNeuralNetwork
            get() = BasicNeuralNetwork(
                0, ActivationFunctions.RectifiedLinearUnit, { 0 }, 9, 8,
                mutableListOf(Matrix(8, 9) { row: Int, cols: Int ->
                    edgeFilterData[row][cols]
                })
            )
    }

}