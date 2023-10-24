package com.delta.polyfree

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.Console

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}

val OpenSansFamily = FontFamily(
    Font(R.font.opensans_bold,FontWeight.Bold),
    Font(R.font.opensans_semibold,FontWeight.SemiBold),
    Font(R.font.opensans_light,FontWeight.Light),
    Font(R.font.opensans_regular,FontWeight.Normal)
)

val TimeStrings : Array<String> = arrayOf("8:30", "10:00", "11:30", "13:00", "14:30", "16:00", "17:30", "19:00");
data class ClassRoomData(var id: String="", val time: String="");
data class ScrapingResult(val data: MutableList<ClassRoomData> = mutableListOf(), var count:Int = 0)

fun PollSwassData() {
    val website_url = "https://www.swas.polito.it/dotnet/orari_lezione_pub/RicercaAuleLiberePerFasceOrarie.aspx"

    var coroutineScope = CoroutineScope(newSingleThreadContext("HTMLFetchThread"));
    coroutineScope.launch {
        try {
            val doc = Jsoup.connect(website_url).get();
            var hour0: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_0");
            val hour1: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_1");
            val hour2: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_2");
            val hour3: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_3");
            val hour4: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_4");
            val hour5: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_5");
            val hour6: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_6");
            val hour7: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_FasciaOraria_7");

            var sector0: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_0");
            val sector1: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_1");
            val sector2: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_2");
            val sector3: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_3");
            val sector4: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_4");
            val sector5: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_5");
            val sector6: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_6");
            val sector7: Elements = doc.select("#Pagina_gv_AuleLibere_lbl_AuleLibere_7");

            println(hour0.text());
            println(hour1.text());
            println(hour2.text());
            println(hour3.text());
            println(hour4.text());
            println(hour5.text());
            println(hour6.text());
            println(hour7.text());
            println();
            println(sector0.text());
            println(sector1.text());
            println(sector2.text());
            println(sector3.text());
            println(sector4.text());
            println(sector5.text());
            println(sector6.text());
            println(sector7.text());

        }catch (e: Exception){
            println("Didn't work");
            println(e.cause);
            println(e.message);
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelector(modifier: Modifier = Modifier) {
    Row ( horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Top){
        var sliderPosition by remember { mutableStateOf(0f..7f) }
        var min by remember { mutableStateOf(0) }
        var max by remember { mutableStateOf(7) }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
             val view = LocalView.current;
             RangeSlider(
                 value = sliderPosition,
                 steps = 6,
                 onValueChange = { values ->
                     run {
                         sliderPosition = values;
                         min = (values.start+0.2).toInt() ;
                         max = (values.endInclusive+0.2).toInt();
                         view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                     }
                 },
                 valueRange = 0f..7f,
                 modifier = Modifier.padding(horizontal = 30.dp)
             )
                Text("%s - %s".format(TimeStrings[min], TimeStrings[max]), fontFamily = OpenSansFamily, fontWeight = FontWeight.SemiBold, fontSize = 40.sp, textAlign = TextAlign.Center);
                Button(onClick = {PollSwassData()}) {
                    Text("Poll Swass");
                }
        }
    }
}

