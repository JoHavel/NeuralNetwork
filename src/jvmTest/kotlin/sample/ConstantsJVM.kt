package sample

import koma.internal.default.generated.matrix.DefaultDoubleMatrixFactory
import koma.matrix.Matrix
import koma.matrix.MatrixFactory

actual val defaultDoubleMatrixFactory: MatrixFactory<Matrix<Double>> = DefaultDoubleMatrixFactory()