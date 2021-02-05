package top.rainysummer.openhappyhackingcalendar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xhinliang.lunarcalendar.LunarCalendar
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class DayActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day)

        val c: Calendar = Calendar.getInstance()
        var today = Date()
        c.time = today
        val year = c.get(Calendar.YEAR).toString()
        val month = (c.get(Calendar.MONTH) + 1).toString()
        val date = c.get(Calendar.DATE).toString()
        val dateText = findViewById<TextView>(R.id.date)
        dateText.text = year + "-" + month + "-" + date

        val weekdayName = arrayOf(
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
        )
        //c.firstDayOfWeek = Calendar.MONDAY;
        var weekday = c.get(Calendar.DAY_OF_WEEK)
        val weekdayText = findViewById<TextView>(R.id.weekday)
        weekdayText.text = weekdayName[weekday - 1]

        weekday -= 2
        if (weekday == -1) {
            weekday = 6
        }
        val cc: Calendar = Calendar.getInstance()
        today = Date()
        cc.time = today
        cc.add(Calendar.DATE, -1 * weekday)

        val monthNum = cc.get(Calendar.MONTH)
        val yearNum = cc.get(Calendar.YEAR)

        for (i in 0..6) {
            val lunarCalender = LunarCalendar.obtainCalendar(yearNum, monthNum + 1, i + 1)

            val id: Int =
                resources.getIdentifier("weekdayText$i", "id", packageName)
            val weekdayText1 = findViewById<TextView>(id)
            val month1 = (monthNum + 1).toString()
            val date1 = cc.get(Calendar.DATE).toString()
            weekdayText1.text = month1 + "-" + date1
            weekdayText1.width = 130

            cc.add(Calendar.DATE, 1)

            val idChn: Int =
                resources.getIdentifier("weekdayTextLunar$i", "id", packageName)
            val chnWeekday = findViewById<TextView>(idChn)
            val festivals = lunarCalender.festivals
            var festivalStr = ""
            for (festival in festivals.set) {
                festivalStr += festival
            }
            if (festivalStr != "") {
                chnWeekday.text = festivalStr
                chnWeekday.setTextColor(resources.getColor(android.R.color.holo_red_dark, theme))
            } else {
                chnWeekday.text = lunarCalender.lunarDay
            }
            chnWeekday.width = 130
        }

        val jsonfile: String =
            applicationContext.assets.open("wiki.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonfile)

        val langNum = jsonArray.length() - 1
        val randomLang = (0..langNum).random()
        val langJson = JSONObject(jsonArray[randomLang].toString())

        val langName = findViewById<TextView>(R.id.langName)
        langName.text = langJson.getString("lang") // + "-" + langJson.getString("desc")

        val langExt = langJson.getString("code")
        val langCode: String =
            applicationContext.assets.open("HackingDate.$langExt").bufferedReader()
                .use { it.readText() }
        val langCodeText = findViewById<TextView>(R.id.langCode)
        langCodeText.text = langCode

        val langDesc = findViewById<TextView>(R.id.langWiki)
        langDesc.text = langJson.getString("desc")

        val detector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val intent = Intent()
                intent.setClass(this@DayActivity, MonthActivity::class.java)
                startActivity(intent)
                this@DayActivity.finish()
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }
        })

        val layout = findViewById<LinearLayout>(R.id.layout2)
        layout.setOnTouchListener { _, event -> detector.onTouchEvent(event) }
    }
}