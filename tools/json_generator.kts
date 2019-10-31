import java.util.*
import java.io.File
import java.io.BufferedReader
import java.io.StringReader
import com.beust.klaxon.*

//DEPS com.beust:klaxon:3.0.1
//INCLUDE rule.kt


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

fun createJsonForTexts() {
  val localeToExtract = "en" 
  
  val lastId = 4234L 

  val expressions = readFromFileToString()

  val expressionCollection = expressions.split("\n")

  val texts : MutableList<String> = mutableListOf()

  val expressionCollection.map {Expression(it)}

  val expressionJson = Klaxon().toJsonString(Expression())
  
  rules?.filter { it.language.equals(localeToExtract) }?.forEach { texts.add(it.text) }

  transformTexts(texts)

  writeToFile(result = texts)
}

extractFromJson()

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