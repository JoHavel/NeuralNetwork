/*
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package core

import koma.create
import koma.extensions.toDoubleArray
import koma.matrix.Matrix

/**
 * Neural Network Interface
 *
 * Basic usage is train it by [train] and then use it by [run]
 */
interface INeuralNetwork {

    /**
     * Takes input, process it throw neural network and returns Matrix vector of [Double] outputs
     */
    fun run(input: Matrix<Double>): Matrix<Double>

    fun run(input: DoubleArray, numCols: Int = 1) = run(create(input, input.size / numCols, numCols))

    /**
     * Takes input and desired output, compute estimated output and apply backpropagation
     */
    fun train(input: Matrix<Double>, output: Matrix<Double>): Matrix<Double>

    fun train(input: DoubleArray, output: DoubleArray, inNumCols: Int = 1, outNumCols: Int = 1) =
        train(
            create(input, input.size / inNumCols, inNumCols),
            create(output, output.size / outNumCols, outNumCols)
        ).toDoubleArray()

    fun train(input: Array<DoubleArray>, output: Array<DoubleArray>) {
        require(input.size == output.size) { "Wrong training sets! Size of input is ${input.size}, size of output is ${output.size}." }
        for (i in input.indices) {
            train(input[i], output[i])
        }
    }

    /**
     * Returns save of NN in [String]
     */
    fun save(): String
}