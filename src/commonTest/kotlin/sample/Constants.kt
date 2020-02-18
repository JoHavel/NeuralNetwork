/*
 * file:               Constants.kt
 * original author:    Jonáš Havelka <jonas.havelka@volny.cz>
 * project:            NeuralNetwork - Kotlin library for NNs
 * content:            constants for tests and defaultDoubleMatrixFactory
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package sample

import koma.matrix.Matrix
import koma.matrix.MatrixFactory

const val wrongInputLayerSize = 1
const val numberOfHiddenLayers = 1
const val numberOfDigits = 10
const val imageWidth = 28
const val imageHeight = 28
const val blackFrom = 0.5
const val learningRateEpochDecrease = 1.5

val input: DoubleArray = DoubleArray(2) { 1.0 }
//get() = DoubleArray(2) { 1.0 }
val output: DoubleArray = input
//get() = input
val inputTest = input.copyOf()
val outputTest = output.copyOf()

expect val defaultDoubleMatrixFactory: MatrixFactory<Matrix<Double>>