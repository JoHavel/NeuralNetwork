package mnistDatabase

import core.INeuralNetwork

expect fun loadFile(file: String): ByteArray

private fun Byte.toUInt() = (this.toInt() + 256) % 256
private fun Byte.toUNDouble() = ((this.toDouble() + 256.0) % 256.0) / 256

private fun List<Byte>.toIntArray(): IntArray {
    require(size % 4 == 0)
    val result = IntArray(size / 4)
    for (i in indices) {
        result[i / 4] += (this[i].toUInt() shl 8 * (when(i % 4) {
            0 -> 3
            1 -> 2
            2 -> 1
            3 -> 0
            else -> 4
        }))
    }
    return result
}

class TrainingData(imageFile: String, numberFile: String): Sequence<Pair<DoubleArray, DoubleArray>> {

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
        return object: Iterator<Pair<DoubleArray, DoubleArray>> {
            val data = this@TrainingData
            var index = 0
            override fun hasNext() = index < numberOfImages

            override fun next(): Pair<DoubleArray, DoubleArray> {

                val image = data.imageBytes.slice(16 + index * sizeOfImage until 16 + (index + 1) * sizeOfImage).map { byte -> byte.toUNDouble() }.toDoubleArray()

                val position = numberBytes[8 + index].toUInt()
                val number = DoubleArray(10) { if (it == position) {1.0} else {0.0} }

                index++
                return image to number
            }
        }
    }
}

fun INeuralNetwork.train(data: TrainingData) {
    for((input, output) in data) {
        train(input, output)
    }
}