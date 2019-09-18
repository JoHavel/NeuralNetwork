package sample

import koma.internal.default.generated.matrix.DefaultDoubleMatrixFactory
import koma.matrix.Matrix
import koma.matrix.MatrixFactory
import mnistDatabase.TrainingData

actual val defaultDoubleMatrixFactory: MatrixFactory<Matrix<Double>> = DefaultDoubleMatrixFactory()

val mnistDigitTrainingDataset = TrainingData("train-images", "train-labels", false)
val emnistDigitTrainingDataset = TrainingData("emnist-digits-train-images", "emnist-digits-train-labels", true)