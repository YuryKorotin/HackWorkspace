import java.util.*
import java.io.File
import java.io.BufferedReader
import java.io.StringReader
import com.beust.klaxon.*
import kotlinx.coroutines.*

@file:MavenRepository("central", "https://repo.maven.apache.org/maven2/")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.2")
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

suspend fun readFromFileToString(directory: String = "./inputs/", fileName: String = "tasks.json") : String {
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

fun extractFromJson() {
  val tagsArray = listOf("<M>", "<F>", "<N>", "<D>", "<S>", "<A>", "<X>", "<P>")

  val imagesMappingsString = GlobalScope.async {readFromFileToString(fileName = "avatars.txt")} 

  var objectString = GlobalScope.async { readFromFileToString()}

  val texts : MutableList<String> = mutableListOf()

  runBlocking {
    val tasks = Klaxon().parseArray<Task>(objectString.await())
  
    val mappings = imagesMappingsString.await()

    tasks?.forEach { it ->
      if (!it.text_en.orEmpty().contains("<D>") &&
         !it.text_en.orEmpty().contains("<F>")) {
        texts.add(it.text_en.orEmpty())
      } 
    }

    writeToFile(result = texts)
  }
}

extractFromJson()