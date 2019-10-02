package core

import koma.create
import koma.matrix.Matrix

interface INeuralNetwork {
    fun run(input: Matrix<Double>): Matrix<Double>
    fun run(input: DoubleArray) = run(create(input, input.size, 1))

    fun train(input: Matrix<Double>, output: Matrix<Double>)
    fun train(input: DoubleArray, output: DoubleArray) =
        train(create(input, input.size, 1), create(output, output.size, 1))

    fun train(input: Array<DoubleArray>, output: Array<DoubleArray>) {
        require(input.size == output.size) { "Wrong training sets! Size of input is ${input.size}, size of output is ${output.size}." }
        for (i in input.indices) {
            train(input[i], output[i])
        }
    }
}