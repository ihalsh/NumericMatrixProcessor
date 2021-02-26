package processor

import processor.Matrix.Companion.buildMatrix
import processor.Matrix.Companion.readSingleNumber
import java.text.DecimalFormat
import java.util.*
import kotlin.math.pow

const val MAIN_MENU = """|
                        |1. Add matrices
                        |2. Multiply matrix to a constant
                        |3. Multiply matrices
                        |4. Transpose matrix
                        |5. Calculate a determinant
                        |6. Inverse matrix
                        |0. Exit
                        |Your choice:"""
const val TRANSPOSE_MENU = """1. Main diagonal
                        |2. Side diagonal
                        |3. Vertical line
                        |4. Horizontal line
                        |Your choice:"""

fun main() {
    val scanner = Scanner(System.`in`)
    while (true) {
        println(MAIN_MENU.trimMargin("|"))
        when (scanner.nextInt()) {
            0 -> return
            1 -> (buildMatrix(scanner, "first matrix:") to buildMatrix(scanner, "second matrix:"))
                    .run { (first + second)?.printResult("The additions result is:") }
            2 -> (buildMatrix(scanner, "matrix:") to readSingleNumber(scanner, "Enter constant:"))
                    .run { (first * second).printResult("The multiplication result is:") }
            3 -> (buildMatrix(scanner, "first matrix:") to buildMatrix(scanner, "second matrix:"))
                    .run { (first * second)?.printResult("The multiplication result is:") }
            4 -> {
                println(TRANSPOSE_MENU.trimMargin("|"))
                when (scanner.nextInt()) {
                    1 -> buildMatrix(scanner, "matrix:").run { transposeMain().printResult() }
                    2 -> buildMatrix(scanner, "matrix:").run { transposeSide().printResult() }
                    3 -> buildMatrix(scanner, "matrix:").run { transposeVertical().printResult() }
                    4 -> buildMatrix(scanner, "matrix:").run { transposeHorizontal().printResult() }
                }
            }
            5 -> buildMatrix(scanner, "matrix:").run { println("The result is:\n${getDeterminant()}") }
            6 -> buildMatrix(scanner, "matrix:").run { inverse(getDeterminant()).printResult() }
        }
    }
}

class Matrix(private val row: Int, private val col: Int,
             private val array2D: Array<DoubleArray> = Array(row) { DoubleArray(col) }) {

    companion object {

        fun readSingleNumber(scanner: Scanner, string: String): Double {
            println(string)
            return scanner.nextDouble()
        }

        fun readPairOfNumbers(scanner: Scanner, string: String): Pair<Int, Int> {
            println(string)
            return scanner.nextInt() to scanner.nextInt()
        }

        fun buildMatrix(scanner: Scanner, matrix: String): Matrix {
            val size = readPairOfNumbers(scanner, "Enter size of $matrix")
            println("Enter $matrix")
            return Matrix(size.first, size.second, Array(size.first) { DoubleArray(size.second) { scanner.nextDouble() } })
        }

        val traverseMatrix: (row: Int) -> (col: Int) -> (function: (Int, Int) -> Unit) -> Unit =
                { row: Int ->
                    { col: Int ->
                        { function: (Int, Int) -> Unit ->
                            for (r in 0 until row) {
                                for (c in 0 until col) {
                                    function(r, c)
                                }
                            }
                        }
                    }
                }
    }

    operator fun get(x: Int, y: Int): Double = array2D[x][y]

    operator fun set(x: Int, y: Int, t: Double) {
        array2D[x][y] = t
    }

    operator fun plus(other: Matrix): Matrix? {
        if (this.row != other.row || this.col != other.col) println("ERROR").also { return null }
        return Matrix(row, col)
                .apply { traverseMatrix(row)(col)() { r, c -> this[r, c] = this@Matrix[r, c] + other[r, c] } }
    }

    operator fun times(scalar: Double): Matrix = Matrix(row, col).apply {
        traverseMatrix(row)(col)() { r, c -> this[r, c] = this@Matrix[r, c] * scalar }
    }

    operator fun times(other: Matrix): Matrix? {

        fun calculateValue(r: Int, c: Int): Double =
                mutableListOf<Double>().apply { repeat(col) { n -> add(this@Matrix[r, n] * other[n, c]) } }.sum()

        if (col != other.row) println("ERROR").also { return null }
        return Matrix(row, other.col).apply {
            traverseMatrix(this.row)(this.col)() { r, c -> this[r, c] = calculateValue(r, c) }
        }
    }

    fun transposeMain(): Matrix = Matrix(row, col).apply {
        traverseMatrix(row)(col)() { r, c -> this[r, c] = this@Matrix[c, r] }
    }

    fun transposeSide(): Matrix = Matrix(row, col).apply {
        traverseMatrix(row)(col)() { r, c -> this[r, c] = this@Matrix[this.col - 1 - c, this.row - 1 - r] }
    }

    fun transposeVertical(): Matrix = Matrix(row, col).apply {
        traverseMatrix(row)(col)() { r, c -> this[r, c] = this@Matrix[r, this.col - 1 - c] }
    }

    fun transposeHorizontal(): Matrix = Matrix(row, col).apply {
        traverseMatrix(row)(col)() { r, c -> this[r, c] = this@Matrix[this.row - 1 - r, c] }
    }

    private fun subMatrix(aRow: Int, aCol: Int, matrix: Matrix): Matrix = arrayListOf<Double>()
            .apply {
                traverseMatrix(matrix.row)(matrix.col)() { r, c -> if (r != aRow && c != aCol) add(matrix[r, c]) }
            }.let { subList ->
                Matrix(matrix.row - 1, matrix.col - 1,
                        Array(matrix.row - 1) { DoubleArray(matrix.col - 1) }).apply {
                    traverseMatrix(this.row)(this.col)() { r, c -> this[r, c] = subList.removeAt(0) }
                }
            }

    fun getDeterminant(): Double {

        var determinant = 0.0

        if (col == 2) return this[0, 0] * this[1, 1] - this[0, 1] * this[1, 0]
        repeat(col) { n ->
            determinant += this[0, n] * (-1.0).pow((2 + n).toDouble()) * subMatrix(0, n, this).getDeterminant()
        }
        return determinant
    }

    fun inverse(determinant: Double): Matrix = Matrix(row, col, Array(row) { DoubleArray(col) })
            .run {
                traverseMatrix(row)(col)() { r, c ->
                    this[r, c] = (-1.0).pow((2 + r + c).toDouble()) * subMatrix(r, c, this@Matrix).getDeterminant()
                }
                transposeMain() * determinant.pow(-1.0)
            }

    fun printResult(comment: String = "The result is:") {

        println(comment)

        fun maxLengthsOfColumns() = MutableList(col) { 0 }.apply {
            traverseMatrix(row)(col)() { r, c ->
                val length = DecimalFormat("#.##").format(this@Matrix[r, c].toBigDecimal()).length
                if (length > this[c]) this[c] = length
            }
        }

        val columnsLength = maxLengthsOfColumns()

        traverseMatrix(row)(col)() { r, c ->
            val number = DecimalFormat("#.##")
                    .format(this[r, c].toBigDecimal())
                    .padStart(columnsLength[c], ' ')
            if (c < col - 1) print("$number ") else println(number)
        }
    }
}