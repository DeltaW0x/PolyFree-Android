package com.delta.polyfree

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PollSwassData();
        minimumViableTimeStamp();
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
        // PollSwassData();
    }

    override fun onStop() {
        super.onStop()
        // PollSwassData();
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
    arrayOf("8:30", "10:00", "11:30", "13:00", "14:30", "16:00", "17:30", "19:00")

val TimeStamps = ArrayList<String>()
val ClassStamps = ArrayList<String>()

fun CheckEmptyAndAdd(request: Elements, array: ArrayList<String>) {
    if (request.isNullOrEmpty()) {
        array.add("OOB")
    } else {
        array.add(request.text())
    }
}

fun PollSwassData() =
    runBlocking(newSingleThreadContext("NetworkThread")) {
        val website_url =
            "https://www.swas.polito.it/dotnet/orari_lezione_pub/RicercaAuleLiberePerFasceOrarie.aspx"
        try {
            val doc = Jsoup.connect(website_url).get()

            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_7"), TimeStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_6"), TimeStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_5"), TimeStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_4"), TimeStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_3"), TimeStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_2"), TimeStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_1"), TimeStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_0"), TimeStamps)

            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_7"), ClassStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_6"), ClassStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_5"), ClassStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_4"), ClassStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_3"), ClassStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_2"), ClassStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_1"), ClassStamps)
            CheckEmptyAndAdd(doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_0"), ClassStamps)

            ClassStamps.forEach { println(it) }
        } catch (e: Exception) {
            println("NETWORK FETCH FAILED")
            println(e.cause)
            println(e.message)
        }
    }

fun minimumViableTimeStamp(): Int {
    TimeStamps.withIndex().forEach{
        if(it.value != "OOB"){
            return it.index;
        }
    }
    return 0;
}

@Composable
fun ClassCard(name: String) {
    Button(onClick = {}, shape = RoundedCornerShape(20.dp)) {
        Text(
            text = name,
            fontFamily = OpenSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 50.sp,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelector(modifier: Modifier = Modifier) {
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Top) {
        var sliderPosition by remember { mutableStateOf(minimumViableTimeStamp().toFloat()..7f) }
        var min by remember { mutableStateOf(0) }
        var max by remember { mutableStateOf(7) }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Text(
                "%s - %s".format(TimeStrings[min], TimeStrings[max]),
                fontFamily = OpenSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            val view = LocalView.current
            RangeSlider(
                value = sliderPosition,
                steps = 6,
                onValueChange = { values ->
                    run {
                        sliderPosition = values;
                        min = (sliderPosition.start + 0.2).toInt()
                        max = (sliderPosition.endInclusive + 0.2).toInt()
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    }
                },

                valueRange = (minimumViableTimeStamp().toFloat()..7f),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(20) { ClassCard(name = "1B") }
            }
        }
    }
}