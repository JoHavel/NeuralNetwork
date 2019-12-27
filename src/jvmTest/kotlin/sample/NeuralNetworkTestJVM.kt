package sample

import core.BasicNeuralNetwork
import core.ConvolutionalNeuralNetwork
import mnistDatabase.loadFileString
import mnistDatabase.saveFile
import mnistDatabase.train
import org.junit.Test

class NeuralNetworkTestJVM {
    @Test
    fun serialization() {
        val nn = BasicNeuralNetwork(numberOfHiddenLayers, inputLayerSize = input.size, outputLayerSize = output.size)
        val saved = nn.save()
        println(nn.save())
        val nn2 = BasicNeuralNetwork.load(saved)
        println(nn2.save())
        nn2.run(input)
        nn2.train(input, output)
    }

    //@Test
    fun mnist() {
        val nn = BasicNeuralNetwork(
            numberOfHiddenLayers,
            inputLayerSize = imageWidth * imageHeight,
            outputLayerSize = numberOfDigits,
            sizes = { 100 })
        repeat(10) {
            nn.train(mnistDigitTrainingDataset)
            nn.train(emnistDigitTrainingDataset)
            nn.learningRate /= learningRateEpochDecrease

            val data = mnistDigitTrainingDataset.iterator().next()
            println(nn.run(data.first).toList())
            println(data.second.toList())
            saveFile("output.txt", nn.save())
        }
    }

    //@Test
    fun savedNN() {
        var error = 0
        repeat(100) {
            val data = mnistDigitTrainingDataset.iterator().next()
            val answer =
                BasicNeuralNetwork.load(loadFileString("output.txt")).run(data.first).toList()
            if (answer.indexOf(answer.maxBy { it }) != data.second.indexOf(1.0)) {
                error++
            }
        }
        println(error)
    }

    //@Test
    fun mnistC() {
        val nn = ConvolutionalNeuralNetwork(
            ConvolutionalNeuralNetwork.edgeFilter,
            BasicNeuralNetwork(
                1,
                inputLayerSize = (imageWidth - 2) * (imageHeight - 2) * 8,
                outputLayerSize = numberOfDigits,
                sizes = { 100 })
        )
        repeat(10) {
            nn.train(mnistDigitTrainingDataset, imageWidth)
            nn.train(emnistDigitTrainingDataset, imageWidth)
            nn.learningRate /= learningRateEpochDecrease

            saveFile("outputC.txt", nn.save())
            savedNNC()
        }
    }

    //@Test
    fun savedNNC() {
        var error = 0
        repeat(100) {
            val data = mnistDigitTrainingDataset.iterator().next()
            val answer =
                ConvolutionalNeuralNetwork.load(loadFileString("outputC.txt")).run(data.first, imageWidth).toList()
            if (answer.indexOf(answer.maxBy { it }) != data.second.indexOf(1.0)) {
                error++
            }
        }
        println(error)
    }

    //@Test
    fun print() {
        fun Pair<DoubleArray, DoubleArray>.print() {
            for (i in 0 until imageHeight) {
                for (j in 0 until imageWidth) {
                    print(
                        if (first[j + i * imageWidth] < blackFrom) {
                            "."
                        } else {
                            "#"
                        }
                    )
                }
                println()
            }
            println(second.indexOf(1.0))
        }

        val data = mnistDigitTrainingDataset.iterator().next()
        data.print()
    }
}