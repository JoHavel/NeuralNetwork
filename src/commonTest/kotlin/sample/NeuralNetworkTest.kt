/*
 * file:               NeuralNetworkTest.kt
 * original author:    Jonáš Havelka <jonas.havelka@volny.cz>
 * project:            NeuralNetwork - Kotlin library for NNs
 * content:            tests
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package sample

import core.BasicNeuralNetwork
import koma.create
import koma.matrix.Matrix
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class NeuralNetworkTest {

    init {
        Matrix.doubleFactory = defaultDoubleMatrixFactory
    }

    //@Test
    fun inputs() {
        assertFailsWith<IllegalArgumentException>("Wrong size of input! This NN has input size $wrongInputLayerSize, but you offer it input with size ${input.size}.") {
            val nn = BasicNeuralNetwork(numberOfHiddenLayers, inputLayerSize = wrongInputLayerSize)
            nn.run(input)
        }
    }

    //@Test
    fun learning() {
        val nn = BasicNeuralNetwork(numberOfHiddenLayers, inputLayerSize = input.size, outputLayerSize = output.size)
        repeat(1000) {
            nn.train(input, output)
        }
        assertTrue("Error of simple memory is bigger than 0.1") {
            (nn.run(input) - create(
                output,
                output.size,
                1
            )).elementSum() <= 0.1
        }
        assertTrue("Input changed (from $inputTest to $input)") { input.contentEquals(inputTest) }
        assertTrue("Output changed (from $outputTest to $output)") { output.contentEquals(outputTest) }
    }

    //@Test
    fun xor() {
        val dataset = setOf(
            DoubleArray(2) { listOf(0.0, 0.0)[it] } to DoubleArray(1) { 0.0 },
            DoubleArray(2) { listOf(1.0, 0.0)[it] } to DoubleArray(1) { 1.0 },
            DoubleArray(2) { listOf(0.0, 1.0)[it] } to DoubleArray(1) { 1.0 },
            DoubleArray(2) { listOf(1.0, 1.0)[it] } to DoubleArray(1) { 0.0 }
        )
        val nn = BasicNeuralNetwork(
            numberOfHiddenLayers,
            inputLayerSize = dataset.random().first.size,
            outputLayerSize = dataset.random().second.size,
            sizes = { 2 })
        repeat(50000) {
            val (input, output) = dataset.random()
            nn.train(input, output)
        }
        dataset.forEach {
            println(it.first.toList())
            println(it.second.toList())
            println(nn.run(it.first).toList())
        }
    }
}