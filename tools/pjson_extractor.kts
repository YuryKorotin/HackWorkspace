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

fun writeStringToFile(directory: String = "./results/", fileName: String = "tasks.txt", result : String) {
  File(directory.plus(fileName)).printWriter().use { out ->
    out.println(result)
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

    return tags.find { task.text_en.contains(t) || task.text_de.contains(t) || task.text_ru.contains(t) } == null
}

suspend fun createPunishTask(task: Task, index: Int, locale: String) : PunishTask{
  val punishTaskOffset = 3670

  var content = task.text_en

  if (locale.equals("en")) {
    content = task.text_en
  } else if (locale.equals("de")) {
    content = task.text_de
  } else if (locale.equals("ru")) {
    content - task.text_ru
  }

  return PunishTask(task.category_old_id, 
        "${index + punishTaskOffset}", "customs (4).svg", task.level, locale, task.pack_id, content)
} 

fun extractFromJson() {
  val packageOffset = 19

  val imagesMappingsString = GlobalScope.async { readFromFileToString(fileName = "avatars.txt") } 

  var objectString = GlobalScope.async { readFromFileToString() }

  val texts : Array<PunishTask> = mutableListOf()

  runBlocking {
    val tasks = Klaxon().parseArray<Task>(objectString.await())

    val mappings = imagesMappingsString.await()

    val punishTasks = mutableListOf<PunishTask>()
    val filteredTasks = tasks?.filter{ areNoTags(it) }

    var i = 0
    var j = 0
    while (i < filtetedTasks.size) {
      i++
      
      val item = filteredTasks[i]

      if (item.text_en.isNullOrEmpty) {
        filtetedTasks.add(createPunishTask(filteredTasks[i], j, "en"))
        j++;
      }
      if (item.text_de.isNullOrEmpty) {
        filtetedTasks.add(createPunishTask(filteredTasks[i], j, "de"))
        j++;
      }

      if (item.text_ru.isNullOrEmpty) {
        filtetedTasks.add(createPunishTask(filteredTasks[i], j, "ru"))
        j++;
      }
    }
    
    val punishJson = Klaxon().toJsonString(punishTasks.toList())

    writeStringToFile(result = texts, fileName = "punish_tasks.json")
  }
}

extractFromJson()