import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    val index = IntArray(scanner.nextInt()) { scanner.nextInt() }.run { indexOfFirst { it == max() } }
    println(index)
}