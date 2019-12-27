package core

import koma.create
import koma.extensions.*
import koma.matrix.Matrix
import koma.sqrt

class ConvolutionalNeuralNetwork(
    private val filter: BasicNeuralNetwork,
    private val neuralNetwork: BasicNeuralNetwork,
    private val trainBoth: Boolean = false
) :
    INeuralNetwork {

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

    fun save() = filter.save() + ";;" + neuralNetwork.save()

    companion object {
        fun load(data: String): ConvolutionalNeuralNetwork {
            val nns = data.split(";;")
            return ConvolutionalNeuralNetwork(BasicNeuralNetwork.load(nns[0]), BasicNeuralNetwork.load(nns[1]))
        }

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
        val edgeFilter: BasicNeuralNetwork
            get() = BasicNeuralNetwork(
                0, ActivationFunctions.RectifiedLinearUnit, { 0 }, 9, 8,
                mutableListOf(Matrix(8, 9) { row: Int, cols: Int ->
                    edgeFilterData[row][cols]
                })
            )
    }

}