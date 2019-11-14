import java.util.*
import java.io.File
import java.io.BufferedReader
import java.io.StringReader
import com.beust.klaxon.*

//DEPS com.beust:klaxon:3.0.1
//INCLUDE task.kt


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

fun readFromFileToString(directory: String = "./inputs/", fileName: String = "tasks.json") : String {
  var resultString = ""
  val bufferedReader = File(directory.plus(fileName)).bufferedReader()
  var c = bufferedReader.read()
  while(c != -1) {
    resultString = resultString.plus(c.toChar())
    c = bufferedReader.read()
  }
  return resultString
}

fun writeToFile(directory: String = "./results/", fileName: String = "tasks.txt", result : List<String>) {
  File(directory.plus(fileName)).printWriter().use { out ->
    result.forEachIndexed{ index, item ->
      out.println(item)
    }
  }
}

fun transformTexts(texts: MutableList<String>) {
    val templatesToSearch = mapOf("<F>" to "", "<D>" to "").toSortedMap(compareByDescending<String> { it.length }) 

    texts.forEachIndexed { index, text ->
        var tempText = text
        templatesToSearch.keys.forEach{ template ->
            if (text.contains(template)) {
                texts.remove(text)
            }
        }
    }
}

fun areNoTags(task: Task) {
    val tags = listOf("<M>", "<F>", "<N>", "<D>", "<S>", "<A>", "<X>", "<P>")

    return tags.find { task.text_en.contains(t) } == null
}

fun extractFromJson() {
  var objectString = readFromFileToString()

  val texts : MutableList<String> = mutableListOf()

  val tasks = Klaxon().parseArray<Task>(objectString)
  

  tasks?.filter {it.text_en.orEmpty().isNotEmpty()}.forEach { 
    if (areNoTags(it)) {
      texts.add(it.text_en.orEmpty())
    } 
  }

  writeToFile(result = texts)
}

extractFromJson()