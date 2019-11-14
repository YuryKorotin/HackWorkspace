import java.util.*
import java.io.File
import java.io.BufferedReader
import java.io.StringReader
import com.beust.klaxon.*

//DEPS com.beust:klaxon:3.0.1
//INCLUDE rule.kt
//INCLUDE expression.kt

fun readFromFile(directory: String = "./inputs/", fileName: String = "rules.txt") : List<String> {
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

fun readFromFileToString(directory: String = "./inputs/", fileName: String = "rules.json") : String {
  var resultString = ""
  val bufferedReader = File(directory.plus(fileName)).bufferedReader()
  var c = bufferedReader.read()
  while(c != -1) {
    resultString = resultString.plus(c.toChar())
    c = bufferedReader.read()
  }
  return resultString
}

fun writeToFile(directory: String = "./results/", fileName: String = "packages.json", result : List<String>) {
  File(directory.plus(fileName)).printWriter().use { out ->
    result.forEachIndexed{ index, item ->
      out.println(item)
    }
  }
}

fun writeStringToFile(directory: String = "./results/", fileName: String = "texts.json", result : String) {
  File(directory.plus(fileName)).printWriter().use { out ->
    out.println(result)
  }
}

fun createJsonForTexts(locale: String = "en", 
                       lastId: Long = 4234L,
                       packId: String = "24") {  
  val img = "customs (4).svg"
  val categoryId = "8"
  val level = "0"

  val texts = readFromFileToString(fileName = "texts.txt")
  val textsCollection = texts.split("\n")

  val results : MutableList<String> = mutableListOf()

  val expressions = textsCollection.mapIndexed { index, item ->
    Expression(categoryId, "${index + lastId}", img, level, locale, packId, item, "") 
  }

  val expressionJson = Klaxon().toJsonString(expressions)
  
  writeStringToFile(result = expressionJson, fileName = "${packId}.json")
}

createJsonForTexts(packId = "24", locale = "en", lastId = 4234L)