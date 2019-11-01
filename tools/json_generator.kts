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

fun createJsonForTexts() {
  val locale = "en" 
  
  val lastId = 4234L 
  val img = "customs (4).svg"
  val level = "0"
  val categoryId = "8"
  val packId = "24"

  val texts = readFromFileToString(fileName = "texts.txt")
  val textsCollection = texts.split("\n")

  val results : MutableList<String> = mutableListOf()

  val expressions = textsCollection.mapIndexed { index, item ->
    Expression(categoryId, index + lastId, img, level, locale, packId, item, "") 
  }

  val expressionJson = Klaxon().toJsonString(expressions)
  
  writeStringToFile(result = expressionJson)
}

createJsonForTexts()

/*
{
        "category_id": "8",
        "id": "4",
        "img": "customs (4).svg",
        "level": "0",
        "locale": "en",
        "pack_id": "",
        "text": "Never have I ever Been to Spain",
        "type": ""
    },
*/