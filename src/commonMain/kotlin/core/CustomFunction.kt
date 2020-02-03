package core

class CustomFunction(
    private val function: (Double) -> Double,
    override val xD: (Double) -> Double,
    override val yD: (Double) -> Double
) : IActivationFunctions {
    override fun invoke(double: Double): Double = function(double)
}