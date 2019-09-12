package sample

import core.BasicNeuralNetwork
import koma.create
import koma.matrix.Matrix
import mnistDatabase.TrainingData
import mnistDatabase.train
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

    @Test
    fun xor() {
        val dataset = setOf(
                DoubleArray(2) { listOf(0.0, 0.0)[it] } to DoubleArray(1) { 0.0 },
                DoubleArray(2) { listOf(1.0, 0.0)[it] } to DoubleArray(1) { 1.0 },
                DoubleArray(2) { listOf(0.0, 1.0)[it] } to DoubleArray(1) { 1.0 },
                DoubleArray(2) { listOf(1.0, 1.0)[it] } to DoubleArray(1) { 0.0 }
        )
        val nn = BasicNeuralNetwork(1, inputLayerSize = 2, outputLayerSize = 1, sizes = {2})
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

    //@Test
    fun mnist() {
        val nn = BasicNeuralNetwork(1, inputLayerSize = 28 * 28, outputLayerSize = 10, sizes = { 300 })
        //nn.train(TrainingData("t10k-images", "t10k-labels"))
        val data = TrainingData("train-images", "train-labels")//.iterator()
//        val dataset = setOf(data.next(), data.next())
//        repeat(1000000) {
//            val (input, output) = dataset.random()
//            nn.train(input, output)
//        }
//        dataset.forEach {
//            println(it.first.toList())
//            println(it.second.toList())
//            println(nn.run(it.first).toList())
//        }

        nn.train(data)
        nn.learningRate = 0.01
        nn.train(data)
        nn.train(TrainingData("t10k-images", "t10k-labels"))

        println(nn.run(TrainingData("train-images", "train-labels").iterator().next().first).toList())
        println(TrainingData("train-images", "train-labels").iterator().next().second.toList())

    }
}