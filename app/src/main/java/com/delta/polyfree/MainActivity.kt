package com.delta.polyfree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delta.polyfree.ui.theme.PolyFreeTheme
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.jsoup.select.Elements

internal class IndexArray(val arrayIndex: Int, val elementIndex: Int)

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pollSwasData();
        setContent {
            PolyFreeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                   TimeSelector();
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }
}

val OpenSansFamily =
    FontFamily(
        Font(R.font.opensans_bold, FontWeight.Bold),
        Font(R.font.opensans_semibold, FontWeight.SemiBold),
        Font(R.font.opensans_light, FontWeight.Light),
        Font(R.font.opensans_regular, FontWeight.Normal)
    )

val TimeStrings: Array<String> =
    arrayOf(
        "8:30 - 10:00",
        "10:00 - 11:30",
        "11:30 - 13:00",
        "13:00 - 14:30",
        "14:30 - 16:00",
        "16:00 - 17:30 ",
        "17:30 - 19:00",
        "19:00 - 20:30"
    )

val ClassStamps = ArrayList<ArrayList<String>>()

fun checkEmptyAndAdd(request: Elements, array: ArrayList<ArrayList<String>>) {
    if (request.isNullOrEmpty()) {
        var empty = ArrayList<String>()
        empty.add("OOB")
        array.add(empty)
    } else {
        var raw_data = request.text()
        raw_data = raw_data.replace(",", " ")

        var temp = ArrayList<String>()
        var split = raw_data.split("\\s".toRegex())
        split.forEach { temp.add(it) }
        array.add(temp)
    }
}

fun pollSwasData() =
    runBlocking(newSingleThreadContext("NetworkThread")) {
        val website_url =
            "https://www.swas.polito.it/dotnet/orari_lezione_pub/RicercaAuleLiberePerFasceOrarie.aspx"
        try {
            val doc = Jsoup.connect(website_url).get()

            checkEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_0"), ClassStamps)
            checkEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_1"), ClassStamps)
            checkEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_2"), ClassStamps)
            checkEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_3"), ClassStamps)
            checkEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_4"), ClassStamps)
            checkEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_5"), ClassStamps)
            checkEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_6"), ClassStamps)
            checkEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_7"), ClassStamps)
        } catch (e: Exception) {
            println("NETWORK FETCH FAILED")
            println(e.cause)
            println(e.message)
        }
    }

fun getCommonElements(arrayList: ArrayList<ArrayList<String>>): SnapshotStateList<String> {

    val commonElements = SnapshotStateList<String>()
    var isContain = true
    val firstArray = arrayList[0]

    val indexArray = ArrayList<IndexArray>()

    // for loop for firstArray
    for (e in firstArray) {

        var elementIndex: Int
        var arrayIndex: Int
        for (i in 1 until arrayList.size) {
            if (!arrayList[i].contains(e)) {
                isContain = false
                break
            } else {
                elementIndex = arrayList[i].indexOf(e)
                arrayIndex = i

                indexArray.add(IndexArray(arrayIndex, elementIndex))
            }
        }
        if (isContain) {
            commonElements.add(e)
            for (i in 0 until indexArray.size) {
                arrayList[indexArray[i].arrayIndex].removeAt(indexArray[i].elementIndex)
            }
            indexArray.clear()
        } else {
            indexArray.clear()
            isContain = true
        }
    }
    return commonElements
}

@Composable
fun ClassListVis(name: String){
    ElevatedCard(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
            )){
        Box(contentAlignment = Alignment.Center){
                Text(
                    text = name,
                    fontFamily = OpenSansFamily,
                    fontWeight = FontWeight.SemiBold,
                 fontSize = 50.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                );
        }
    }
}


@ExperimentalMaterial3Api
@Composable
fun TimeSelector(modifier: Modifier = Modifier) {
    var toggle1 by remember { mutableStateOf(false) }
    var toggle2 by remember { mutableStateOf(false) }
    var toggle3 by remember { mutableStateOf(false) }
    var toggle4 by remember { mutableStateOf(false) }
    var toggle5 by remember { mutableStateOf(false) }
    var toggle6 by remember { mutableStateOf(false) }
    var toggle7 by remember { mutableStateOf(false) }
    var toggle8 by remember { mutableStateOf(false) }

    var classList = remember { mutableMapOf<String, ArrayList<String>>() }
    var classFinal = remember { mutableStateListOf<String>() }

    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Top) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = toggle1,
                    onClick = {
                        run {
                            if (toggle1) {
                                classList.remove("SECTOR_0")
                                toggle1 = !toggle1
                                if (classFinal.isNotEmpty()) {
                                    classFinal = getCommonElements(ArrayList(classList.values))
                                }
                            } else {
                                classList["SECTOR_0"] = ClassStamps[0]
                                toggle1 = !toggle1
                                if (classFinal.isNotEmpty()) {
                                    classFinal = getCommonElements(ArrayList(classList.values))
                                } else {
                                    ClassStamps[0].forEach {
                                        classFinal.add(it)
                                        println(it)
                                    }
                                }
                            }
                        }
                    },
                    label = { Text(text = TimeStrings[0]) }
                )
                FilterChip(
                    selected = toggle2,
                    onClick = {
                        run {
                            if (toggle2) {
                                classList.remove("SECTOR_1")
                                toggle2 = !toggle2
                                classFinal = getCommonElements(ArrayList(classList.values))
                            } else {
                                classList["SECTOR_1"] = ClassStamps[1]
                                toggle2 = !toggle2
                                classFinal = getCommonElements(ArrayList(classList.values))
                            }
                        }
                    },
                    label = { Text(text = TimeStrings[1]) }
                )
                FilterChip(
                    selected = toggle3,
                    onClick = {
                        run {
                            if (toggle3) {
                                classList.remove("SECTOR_3")
                                toggle3 = !toggle3
                                classFinal = getCommonElements(ArrayList(classList.values))
                            } else {
                                classList["SECTOR_3"] = ClassStamps[2]
                                toggle3 = !toggle3
                                classFinal = getCommonElements(ArrayList(classList.values))
                            }
                        }
                    },
                    label = { Text(text = TimeStrings[2]) }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = toggle4,
                    onClick = {
                        run {
                            if (toggle4) {
                                classList.remove("SECTOR_4")
                                toggle4 = !toggle4
                                classFinal = getCommonElements(ArrayList(classList.values))
                            } else {
                                classList["SECTOR_4"] = ClassStamps[3]
                                toggle4 = !toggle4
                                classFinal = getCommonElements(ArrayList(classList.values))
                            }
                        }
                    },
                    label = { Text(text = TimeStrings[3]) }
                )
                FilterChip(
                    selected = toggle5,
                    onClick = {
                        run {
                            if (toggle5) {
                                classList.remove("SECTOR_5")
                                toggle5 = !toggle5
                                classFinal = getCommonElements(ArrayList(classList.values))
                            } else {
                                classList["SECTOR_5"] = ClassStamps[4]
                                toggle5 = !toggle5
                                classFinal = getCommonElements(ArrayList(classList.values))
                            }
                        }
                    },
                    label = { Text(text = TimeStrings[4]) }
                )
                FilterChip(
                    selected = toggle6,
                    onClick = {
                        run {
                            if (toggle6) {
                                classList.remove("SECTOR_6")
                                toggle6 = !toggle6
                                classFinal = getCommonElements(ArrayList(classList.values))
                            } else {
                                classList["SECTOR_6"] = ClassStamps[5]
                                toggle6 = !toggle6
                                classFinal = getCommonElements(ArrayList(classList.values))
                            }
                        }
                    },
                    label = { Text(text = TimeStrings[5]) }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = toggle7,
                    onClick = {
                        run {
                            if (toggle7) {
                                classList.remove("SECTOR_7")
                                toggle7 = !toggle7
                                classFinal = getCommonElements(ArrayList(classList.values))
                            } else {
                                classList["SECTOR_7"] = ClassStamps[6]
                                toggle7 = !toggle7
                                classFinal = getCommonElements(ArrayList(classList.values))
                            }
                        }
                    },
                    label = { Text(text = TimeStrings[6]) }
                )
                FilterChip(
                    selected = toggle8,
                    onClick = {
                        run {
                            if (toggle8) {
                                classList.remove("SECTOR_8")
                                toggle8 = !toggle8
                                if (classList.isNotEmpty()) {
                                    classFinal = getCommonElements(ArrayList(classList.values))
                                }
                            } else {
                                classList["SECTOR_8"] = ClassStamps[7]
                                toggle8 = !toggle8
                                if (classList.isNotEmpty()) {
                                    classFinal = getCommonElements(ArrayList(classList.values))
                                }
                            }
                        }
                    },
                    label = { Text(text = TimeStrings[7]) }
                )
            }
            if (classFinal.isNotEmpty()) {
                var a = classFinal.toList().filter { !it.isNullOrEmpty() }
                List(a.size) { ClassListVis(name = a[it]) }
            }
        }
    }
}
