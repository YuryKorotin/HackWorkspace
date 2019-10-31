import java.util.*
import java.io.File
import java.io.BufferedReader
import java.io.StringReader
import com.beust.klaxon.*

//DEPS com.beust:klaxon:3.0.1
//INCLUDE rule.kt


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

fun writeToFile(directory: String = "./results/", fileName: String = "rules.txt", result : List<String>) {
  File(directory.plus(fileName)).printWriter().use { out ->
    result.forEachIndexed{ index, item ->
      out.println(item)
    }
  }
}

fun transformTexts(texts: MutableList<String>) {
	//val templatesToSearch = mapOf("If you\'ve ever" to "Never have I ever", "your" to "my", "you" to "I").toSortedMap(compareByDescending<String> { it.length }) 

	val templatesToSearch = mapOf("If you\'ve ever" to "Never have I ever", "your" to "my").toSortedMap(compareByDescending<String> { it.length }) 

	texts.forEachIndexed { index, text ->
		var tempText = text
	    templatesToSearch.keys.forEach{ template ->
	    	tempText = tempText.replace(template, templatesToSearch[template]!!, true)
	    }
	    texts[index] = tempText
	}
}

fun extractFromJson() {
  var localeToExtract = "en" 

  var objectString = readFromFileToString()

  val texts : MutableList<String> = mutableListOf()

  val rules = Klaxon().parseArray<Rule>(objectString)
  
  rules?.filter { it.language.equals(localeToExtract) }?.forEach { texts.add(it.text) }

  transformTexts(texts)

  writeToFile(result = texts)
}

extractFromJson()