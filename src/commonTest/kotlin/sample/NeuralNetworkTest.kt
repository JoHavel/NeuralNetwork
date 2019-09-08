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

    @Test
    fun inputs() {
        assertFailsWith<IllegalArgumentException>("Wrong size of input! This NN has input size $inputLayerSize, but you offer it input with size ${input.size}.") {
            val nn = BasicNeuralNetwork(numberOfHiddenLayers, inputLayerSize = inputLayerSize)
            nn.run(input)
        }
    }

    @Test
    fun learning() {
        val nn = BasicNeuralNetwork(numberOfHiddenLayers, inputLayerSize = 2, outputLayerSize = 2)
        repeat(1000) {
            nn.train(input, output)
        }
        assertTrue("Error of simple memory is bigger than 0.1") { (nn.run(input) - create(output, output.size, 1)).elementSum() <= 0.1 }
        assertTrue("Input changed (from $inputTest to $input)") { input.contentEquals(inputTest) }
        assertTrue("Output changed (from $outputTest to $output)") { output.contentEquals(outputTest) }
    }
}