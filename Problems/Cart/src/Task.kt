import java.math.BigDecimal
import java.text.DecimalFormat

fun main() {
    val productType = readLine()!!
    val price = readLine()!!.convertToValidPrice()
    val product = Product(productType, price)
    println(DecimalFormat("#.##").format(product.totalPrice))
}

val productTax: Map<String, Double> = mapOf("headphones" to 1.11, "smartphone" to 1.15, "tv" to 1.17, "laptop" to 1.19)

fun String.convertToValidPrice(): BigDecimal = when (this.toDouble()) {
    in Double.MIN_VALUE..0.0 -> 0.toBigDecimal()
    in 1_000_000.0..Double.MAX_VALUE -> 1_000_000.toBigDecimal()
    else -> this.toBigDecimal()
}

val Product.totalPrice: BigDecimal get() = productTax.getOrElse(type) { 1.0 }.toBigDecimal() * price

data class Product(val type: String, val price: BigDecimal)