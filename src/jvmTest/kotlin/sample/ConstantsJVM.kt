/*
 * file:               ConstantsJVM.kt
 * original author:    Jonáš Havelka <jonas.havelka@volny.cz>
 * project:            NeuralNetwork - Kotlin library for NNs
 * content:            Constants for tests and defaultDoubleMatrixFactory
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package sample

import koma.internal.default.generated.matrix.DefaultDoubleMatrixFactory
import koma.matrix.Matrix
import koma.matrix.MatrixFactory
import mnistDatabase.MnistTrainingData

actual val defaultDoubleMatrixFactory: MatrixFactory<Matrix<Double>> = DefaultDoubleMatrixFactory()

val mnistDigitTrainingDataset = MnistTrainingData("train-images", "train-labels", false)
val emnistDigitTrainingDataset = MnistTrainingData("emnist-digits-train-images", "emnist-digits-train-labels", true)