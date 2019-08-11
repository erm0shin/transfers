package ru.banking.utils

class ExchangeRates {
    val matrix = Array2D<Double>(3, 3)

    init {
        // currency relations
        // RUB - 0, USD - 1, EUR - 2
        matrix[0, 0] = 1.0
        matrix[0, 1] = 0.0153
        matrix[0, 2] = 0.0137
        matrix[1, 0] = 65.2825
        matrix[1, 1] = 1.0
        matrix[1, 2] = 0.8931
        matrix[2, 0] = 73.1
        matrix[2, 1] = 0.0
        matrix[2, 2] = 1.1197
    }
}

class Array2D<T>(val xSize: Int, val ySize: Int, val array: Array<Array<T>>) {

    companion object {

        inline operator fun <reified T> invoke() = Array2D(0, 0, Array(0) { emptyArray<T>() })

        inline operator fun <reified T> invoke(xWidth: Int, yWidth: Int) =
            Array2D(xWidth, yWidth, Array(xWidth) { arrayOfNulls<T>(yWidth) })

        inline operator fun <reified T> invoke(xWidth: Int, yWidth: Int, operator: (Int, Int) -> (T)): Array2D<T> {
            val array = Array(xWidth) {
                val x = it
                Array(yWidth) { it1 -> operator(x, it1) }
            }
            return Array2D(xWidth, yWidth, array)
        }
    }

    operator fun get(x: Int, y: Int): T {
        return array[x][y]
    }

    operator fun set(x: Int, y: Int, t: T) {
        array[x][y] = t
    }

    inline fun forEach(operation: (T) -> Unit) {
        array.forEach { it.forEach { operation.invoke(it) } }
    }

    inline fun forEachIndexed(operation: (x: Int, y: Int, T) -> Unit) {
        array.forEachIndexed { x, p -> p.forEachIndexed { y, t -> operation.invoke(x, y, t) } }
    }
}