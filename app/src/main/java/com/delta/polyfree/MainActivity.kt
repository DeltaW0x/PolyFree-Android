package com.delta.polyfree

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.delta.polyfree.ui.theme.PolyFreeTheme
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pollSwasData()
        setContent {
            PolyFreeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    TimeSelector()
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

internal class IndexArray(val arrayIndex: Int, val elementIndex: Int)

var lastNonNull: Int = -1

fun getCommonElements(arrayList: ArrayList<ArrayList<String>>): ArrayList<String> {

    val commonElements = ArrayList<String>()
    var isContain = true
    val firstArray = arrayList[0]

    val indexArray = ArrayList<IndexArray>()

    // for loop for firstArray
    for (e in firstArray) {

        var elementIndex: Int
        var arrayIndex: Int

        // for loop for next ArrayList
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

            // remove element
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
        lastNonNull++
        var empty = ArrayList<String>()
        empty.add("OOB")
        array.add(empty)
    } else {
        var raw_data = request.text()
        raw_data = raw_data.replace(",", " ")

        var temp = ArrayList<String>()
        var split = raw_data.split("\\s".toRegex())
        split.forEach {
            if (!it.isNullOrEmpty()) {
                temp.add(it)
            }
        }
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

            lastNonNull = 7 - lastNonNull
        } catch (e: Exception) {
            println("NETWORK FETCH FAILED")
            println(e.cause)
            println(e.message)
        }
    }

@ExperimentalMaterial3Api
@Composable
fun TimeSelector(modifier: Modifier = Modifier) {
    val toggles = remember {
        mutableStateListOf<Boolean>(false, false, false, false, false, false, false, false)
    }
    var creditToggle by remember { mutableStateOf(false) }

    var classHasmap = remember { mutableMapOf<String, ArrayList<String>>() }
    var finalClassList = remember { mutableListOf<String>() }
    val updateClassList = {run{
        if(classHasmap.isNotEmpty()) {
            finalClassList.clear();
            finalClassList = classHasmap.values.toList().reduce { acc, it -> acc.apply { retainAll(it) } }
            if(finalClassList.isNullOrEmpty()){
                finalClassList = arrayListOf("No empty rooms available all throughout the selected range!");
            }
            println(finalClassList.toList());
        }
    }}
    val updateHasMap = { index: Int ->
        run {
            val moduledIndex = ((index + lastNonNull) % 8)
            if (toggles[index]) {
                classHasmap.remove("SECTOR_$moduledIndex")
                toggles[index] = !toggles[index]
                updateClassList();
                return@run
            } else {
                classHasmap["SECTOR_$moduledIndex"] = ClassStamps[moduledIndex]
                toggles[index] = !toggles[index]
                updateClassList();
                return@run
            }
        }
    }
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Top) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ElevatedFilterChip(
                    selected = toggles[0],
                    onClick = { updateHasMap(0) },
                    label = { Text(text = TimeStrings[0])}
                )
                ElevatedFilterChip(
                    selected = toggles[1],
                    onClick = { updateHasMap(1) },
                    label = { Text(text = TimeStrings[1]) }
                )
                ElevatedFilterChip(
                    selected = toggles[2],
                    onClick = { updateHasMap(2) },
                    label = { Text(text = TimeStrings[2]) }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ElevatedFilterChip(
                    selected = toggles[3],
                    onClick = { updateHasMap(3) },
                    label = { Text(text = TimeStrings[3]) }
                )
                ElevatedFilterChip(
                    selected = toggles[4],
                    onClick = { updateHasMap(4) },
                    label = { Text(text = TimeStrings[4]) }
                )
                ElevatedFilterChip(
                    selected = toggles[5],
                    onClick = { updateHasMap(5) },
                    label = { Text(text = TimeStrings[5]) }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ElevatedFilterChip(
                    selected = toggles[6],
                    onClick = { updateHasMap(6) },
                    label = { Text(text = TimeStrings[6]) }
                )
                ElevatedFilterChip(
                    selected = toggles[7],
                    onClick = { updateHasMap(7) },
                    label = { Text(text = TimeStrings[7]) }
                )
            }
        }
    }
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(10.dp)){
        Button(onClick = { creditToggle = !creditToggle } )
        {
            Text(text = "Credits")
        }
    }
    if(creditToggle){
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)){
            val imageLoader = ImageLoader.Builder(LocalContext.current)
                .components {
                    if (SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
            Image(
                painter = rememberAsyncImagePainter(R.drawable.red, imageLoader),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Row (horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Top){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ){
                    Text(text = "Original code by Simone Albano, UI port by DeltaWave0x\n \n" + "\"Negro sei gay\" - Riley Freeman,2005", textAlign = TextAlign.Center)
                }
            }
        }
    }
}
