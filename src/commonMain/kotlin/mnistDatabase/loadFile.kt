package mnistDatabase

import core.INeuralNetwork

expect fun loadFile(file: String): ByteArray
expect fun loadFileString(file: String): String
expect fun saveFile(file: String, text: String)

private fun Byte.toUInt() = (this.toInt() + 256) % 256
private fun Byte.toUNDouble() = ((this.toDouble() + 256.0) % 256.0) / 256

private fun List<Byte>.toIntArray(): IntArray {
    require(size % 4 == 0)
    val result = IntArray(size / 4)
    for (i in indices) {
        result[i / 4] += (this[i].toUInt() shl 8 * (when (i % 4) {
            0 -> 3
            1 -> 2
            2 -> 1
            3 -> 0
            else -> 4
        }))
    }
    return result
}

typealias TrainingData = Sequence<Pair<DoubleArray, DoubleArray>>

class MnistTrainingData(imageFile: String, numberFile: String, val inverse: Boolean) : TrainingData {

    private val imageBytes = loadFile(imageFile.removeSuffix(".idx3-ubyte") + ".idx3-ubyte")
    private val imageFirstInts = imageBytes.slice(4 until 16).toIntArray()
    private val numberOfImages = imageFirstInts[0]
    private val numberOfRows = imageFirstInts[1]
    private val numberOfColumns = imageFirstInts[2]
    private val sizeOfImage = numberOfColumns * numberOfRows

    private val numberBytes = loadFile(numberFile.removeSuffix(".idx1-ubyte") + ".idx1-ubyte")

    init {
        require(numberOfImages == numberBytes.slice(4 until 8).toIntArray().first()) { "Error" }
    }

    override fun iterator(): Iterator<Pair<DoubleArray, DoubleArray>> {
        return object : Iterator<Pair<DoubleArray, DoubleArray>> {
            val data = this@MnistTrainingData
            val indexes = (0 until numberOfImages).shuffled()
            var index = 0
            override fun hasNext() = index < numberOfImages

            override fun next(): Pair<DoubleArray, DoubleArray> {

                var image =
                    data.imageBytes.slice(16 + indexes[index] * sizeOfImage until 16 + (indexes[index] + 1) * sizeOfImage)
                        .map { byte -> byte.toUNDouble() }.toDoubleArray()

                if (inverse) {
                    val newimage = DoubleArray(sizeOfImage)
                    for (i in 0 until numberOfRows) {
                        for (j in 0 until numberOfColumns) {
                            newimage[i * 28 + j] = image[j * 28 + i]
                        }
                    }
                    image = newimage
                }

                val position = numberBytes[8 + indexes[index]].toUInt()
                val number = DoubleArray(10) {
                    if (it == position) {
                        1.0
                    } else {
                        0.0
                    }
                }

                index++
                return image to number
            }
        }
    }
}

fun INeuralNetwork.train(data: TrainingData, numCols: Int = 1) {
    for ((input, output) in data) {
        train(input, output, numCols)
    }
}