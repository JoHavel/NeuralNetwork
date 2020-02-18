/*
 * file:               loadFileJVM.kt
 * original author:    Jonáš Havelka <jonas.havelka@volny.cz>
 * project:            NeuralNetwork - Kotlin library for NNs
 * content:            fun loadFile, saveFile, loadFileString
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package mnistDatabase

import java.io.*

actual fun loadFile(file: String) = File(file).readBytes()
actual fun saveFile(file: String, text: String) {
    val f = File(file)
    f.createNewFile()
    val bw = BufferedWriter(FileWriter(f))
    bw.append(text)
    bw.close()
}

actual fun loadFileString(file: String): String = BufferedReader(FileReader(File(file))).readLine()