/*
 * file:               ConstantsJS.kt
 * original author:    Jonáš Havelka <jonas.havelka@volny.cz>
 * project:            NeuralNetwork - Kotlin library for NNs
 * content:            constant defaultDoubleMatrixFactory
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package sample

import koma.internal.default.generated.matrix.DefaultDoubleMatrixFactory
import koma.matrix.Matrix
import koma.matrix.MatrixFactory

actual val defaultDoubleMatrixFactory: MatrixFactory<Matrix<Double>> = DefaultDoubleMatrixFactory()