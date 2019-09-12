package mnistDatabase

import java.io.File

actual fun loadFile(file: String) = File(file).readBytes()