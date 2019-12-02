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
//INCLUDE punish_task.kt


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

fun areNoTags(task: Task): Boolean {
    val tags = listOf("<M>", "<F>", "<N>", "<D>", "<S>", "<A>", "<X>", "<P>")

    return tags.find { task.text_en?.contains(it) ?: false || task.text_de?.contains(it) ?: false || task.text_ru?.contains(it) ?: false } == null
}

suspend fun createPunishTask(task: Task, index: Int, locale: String, avatarMappings : Map<String, String>) : PunishTask{
  //val punishTaskOffset = 3670 843 3409
  //val punishTaskOffset = 3670 + 1411
  val punishTaskOffset = 9621

  val packageOffset = 19

  var imagePath = "customs (4).svg"

  for ((k, v) in avatarMappings) {
    if (task.text_ru?.contains(k)?: false) {      
      imagePath = v
      break            
    }
    if (task.text_en?.contains(k) ?: false) {      
      imagePath = v
      break            
    }
  }

  var content = task.text_en ?: ""

  if (locale.equals("en")) {
    content = task.text_en!!
  } else if (locale.equals("de")) {
    content = task.text_de!!
  } else if (locale.equals("ru")) {
    content = task.text_ru!!
  }

  return PunishTask("${task.category_old_id}", 
        "${index + punishTaskOffset}", imagePath, "${task.level}", locale, "${task.pack_id + packageOffset}", content)
} 

suspend fun createImageMap(imageString: String): Map<String, String> {
  val imageMap = mutableMapOf<String, String>()

  val imageVocabulary = imageString.split("\n")

  for (i in 0 until imageVocabulary.size) {
    val imageRule = imageVocabulary[i]

    val terms = imageRule.split("|")

    println(terms)

    val russianTerms = terms[0].split(",")

    val englishTerms = terms[1].split(",")

    val targetImage = terms[2] + terms[3] + ".png"

    for (j in 0 until russianTerms.size) {
      imageMap[russianTerms[j]] = targetImage      
    }

    for (j in 0 until englishTerms.size) {
      imageMap[englishTerms[j]] = targetImage      
    }
  }
  return imageMap.toMap()
}

fun extractFromJson() {
  val imagesMappingsString = GlobalScope.async { readFromFileToString(fileName = "avatars_meta.txt") } 

  var objectString = GlobalScope.async { readFromFileToString() }

  runBlocking {
    val tasks = Klaxon().parseArray<Task>(objectString.await())

    val mappingString = imagesMappingsString.await()

    val avatarMappings : Map<String, String> = createImageMap(mappingString)

    val punishTasks = mutableListOf<PunishTask>()

    val filteredTasks = tasks!!.filter{ areNoTags(it) }

    println("Tasks were filtered")

    var i = 0
    var j = 0

    while (i < filteredTasks.size) {
      println("${i} task is processing")
      val item = filteredTasks[i]

      if (!item.text_en.isNullOrEmpty()) {
        println("${j} task is created")
        punishTasks.add(createPunishTask(filteredTasks[i], j, "en", avatarMappings))
        j++;
      }
      if (!item.text_de.isNullOrEmpty()) {
        println("${j} task is created")
        punishTasks.add(createPunishTask(filteredTasks[i], j, "de", avatarMappings))
        j++;
      }

      if (!item.text_ru.isNullOrEmpty()) {
        println("${j} task is created")
        punishTasks.add(createPunishTask(filteredTasks[i], j, "ru", avatarMappings))
        j++;
      }
      i++
    }
    
    val punishJson = Klaxon().toJsonString(punishTasks.toList())

    writeStringToFile(result = punishJson, fileName = "punish_tasks.json")
  }
}

extractFromJson()