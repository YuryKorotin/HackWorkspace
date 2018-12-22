import java.util.*
import java.io.File
import java.io.BufferedReader
import java.io.StringReader
import com.beust.klaxon.*

//DEPS com.beust:klaxon:3.0.1

fun readFromConsole() : List<String>{
  val lineList = mutableListOf<String>()
  val scan = Scanner(System.`in`)

  val t = scan.nextLine().trim().toInt()
  lineList.add(t.toString())

  for (tItr in 1..t) {
    val s = scan.nextLine()

    lineList.add(s)

    println(">  " + s)
  }

  return lineList
}

fun readFromFile(directory: String = "./inputs/", fileName: String = "input.json") : List<String> {
  val bufferedReader = File(directory.plus(fileName)).bufferedReader()
  val lineList = mutableListOf<String>()

  bufferedReader.useLines { lines ->
    lines.forEach {
      lineList.add(it)
    }
  }

  lineList.forEach {
    println(">  " + it)
  }

  return lineList
}

fun readFromFileToString(directory: String = "./inputs/", fileName: String = "input.json") : String {
  var resultString = ""
  val bufferedReader = File(directory.plus(fileName)).bufferedReader()
  var c = bufferedReader.read()
  while(c != -1) {
    resultString = resultString.plus(c.toChar())
    c = bufferedReader.read()
  }
  return resultString
}

fun writeToFile(directory: String = "./results/", fileName: String = "output.txt", result : List<String>) {
  File(directory.plus(fileName)).printWriter().use { out ->
    result.forEachIndexed{ index, item ->
      out.print(item)
      if (index != result.size - 1) {
        out.print(", ")
      }
    }
  }
}

fun parseJson() {
  var objectString = readFromFileToString()
  //objectString = objectString.substring(1, objectString.length - 2)
  val result : MutableList<String> = mutableListOf()

  println(objectString)
  JsonReader(StringReader(objectString)).use { reader -> 
    reader.beginObject() {
      var translations: JsonObject? = null
      while (reader.hasNext()) {
        val readName = reader.nextName()
        when (readName) {
          "translateArray" -> translations = reader.nextObject()
          else -> println("Unexpected name: $readName")
	}
	for ((key, value) in translations!!) {
          println("$key - $value")
	  result.add(value.toString())
        }
      }
    }
  }
  writeToFile(result = result)
}

parseJson()

