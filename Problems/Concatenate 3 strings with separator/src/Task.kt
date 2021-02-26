fun main() = println(mutableListOf<String>().apply { repeat(4) { add(readLine()!!) } }
        .run { subList(0, 3).joinToString(separator = if (this.last() == "NO SEPARATOR") " " else this.last()) })