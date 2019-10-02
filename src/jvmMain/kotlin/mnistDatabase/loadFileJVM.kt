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