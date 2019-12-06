package core

import koma.extensions.forEachIndexed
import koma.extensions.get
import koma.extensions.set
import koma.matrix.Matrix
import koma.sqrt

class ConvolutionalNeuralNetwork(
    private val filter: BasicNeuralNetwork,
    private val neuralNetwork: BasicNeuralNetwork,
    val trainBoth: Boolean = false
) :
    INeuralNetwork {

    private val filterSizeSqrt: Int

    init {
        val s = sqrt(filter.inputLayerSize)
        filterSizeSqrt = s.toInt()
        require(s != filterSizeSqrt.toDouble()) { "Filter is not square" }
        require(neuralNetwork.inputLayerSize % filter.outputLayerSize != 0) { "Filter is not for this neural network" }
    }

    private fun runFilter(input: Matrix<Double>): Matrix<Double> {
        require(input.numRows() % filterSizeSqrt != 0 || input.numCols() % filterSizeSqrt != 0) { "Invalid input matrix size for filter" }
        val output = Matrix(1, input.size) { _, _ -> 0.0 }
        val columnSize = input.numRows()
        for (i in 0 until input.numRows() step filterSizeSqrt) {
            for (j in 0 until input.numCols() step filterSizeSqrt) {
                val output1 = filter.run(input[i until i + filterSizeSqrt, j until j + filterSizeSqrt].asColVector())
                val offset = columnSize * j + i - 1
                output1.forEachIndexed { it, ele ->
                    output[offset + it] = ele
                }
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
        return filter.train(error)
    }

}