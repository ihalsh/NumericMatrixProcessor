data class City(val name: String) {
    var degrees: Int = 0
        set(value) {
            val predicate = (value < -92) xor (value > 57)
            field = when (name) {
                "Moscow" -> if (predicate) 5 else value
                "Hanoi" -> if (predicate) 20 else value
                "Dubai" -> if (predicate) 30 else value
                else -> value
            }
        }
}

fun main() {
    val first = readLine()!!.toInt()
    val second = readLine()!!.toInt()
    val third = readLine()!!.toInt()
    val firstCity = City("Dubai")
    firstCity.degrees = first
    val secondCity = City("Moscow")
    secondCity.degrees = second
    val thirdCity = City("Hanoi")
    thirdCity.degrees = third

    listOf(firstCity, secondCity, thirdCity).sortedBy(City::degrees)
            .let { cities ->
                print(if (cities[0].degrees == cities[1].degrees) "neither" else cities[0].name)
            }
}