package sample

import koma.matrix.Matrix
import koma.matrix.MatrixFactory

const val inputLayerSize = 1
const val numberOfHiddenLayers = 3
expect val defaultDoubleMatrixFactory: MatrixFactory<Matrix<Double>>

val input: DoubleArray = DoubleArray(2) { 1.0 }
//get() = DoubleArray(2) { 1.0 }
val output: DoubleArray = input
//get() = input

val inputTest = input.copyOf()
val outputTest = output.copyOf()