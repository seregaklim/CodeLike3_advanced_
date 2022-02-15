import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class Wallsevice {

    fun notZeroShare(share: Long) = when (share) {
        in 0..999 -> share
        in 1_000..9_999 -> share/10_000.toDouble()
        in 10_000..99_999 -> share/10_000
        in 100_000..999_999 -> share/1000_000.toDouble()
        in 1_000_000..100_000_000 ->share/1000_000
        else ->share
    }

    fun zeroingOutShare(share: Long) = when (share) {
        in 0..999 -> share
        in 1_000..9_999 -> "${this.notZeroShare(share)} К"
        in 10_000..99_999 -> "${this.notZeroShare(share)} К"
        in 100_000..999_999 -> "${this.notZeroShare(share)} М"
        in 1_000_000..100_000_000 ->"${this.notZeroShare(share)} М"
        else ->share
    }

    fun notZeroLikes(likes: Int) = when (likes) {
        in 0..999 -> likes
        in 1_000..9_999 -> likes/10_000.toDouble()
        in 10_000..99_999 -> likes/10_000
        in 100_000..999_999 -> likes/1000_000.toDouble()
        in 1_000_000..100_000_000 ->likes/1000_000
        else ->likes
    }

    fun zeroingOutLikes(likes: Long) = when (likes) {
        in 0..999 -> likes
        in 1_000..9_999 -> "${this.notZeroShare(likes)} К"
        in 10_000..99_999 -> "${this.notZeroShare(likes)} К"
        in 100_000..999_999 -> "${this.notZeroShare(likes)} М"
        in 1_000_000..100_000_000 ->"${this.notZeroShare(likes)} М"
        else ->likes
    }


    //Текущее время
    @RequiresApi(Build.VERSION_CODES.O)
    val then = run {
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("H:m")
        val date = LocalDate.parse("Feb 13 2022", dateFormatter)
        val time = LocalTime.parse("22:13", timeFormatter)
        date.atTime(time)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    val now = LocalDateTime.now()
    // конвектор времени
    @RequiresApi(Build.VERSION_CODES.O)
    val hours = then.until(now, ChronoUnit.HOURS)
    @RequiresApi(Build.VERSION_CODES.O)
    val days = then.until(now, ChronoUnit.DAYS)
    @RequiresApi(Build.VERSION_CODES.O)
    val secunds = then.until(now, ChronoUnit.SECONDS)

    // преобразователь времени в часах

//Сегодня: элементы, опубликованные в пределах от текущего времени до текущее время - 24 часа.
//Вчера: элементы, опубликованные в пределах от текущего времени - 24 часа до текущее время - 48 часов.
//На прошлой неделе: всё, что старше текущее время - 48 часов.

    fun timeСonverter ( hours: Long): String =   when (hours){
        in 0..24 -> "Сегодня!"
        in 24..48 -> "Вчера!"
        in 48..168 -> "На прошлой неделе!"
        else -> "давно"
    }
    val agoToText =  timeСonverter( hours)


}




//преобразователь в секундах
//fun tutext (sec: Int, minute: String, hours: String): String =   when (sec){
//    in 0..60 -> "только что"
//    in 61..3600 -> "$minute назад"
//    in 3601..86400 -> "$hours назад"
//    in 86401..172800 -> "сегодня"
//    in 172801..259200 -> "вчера"
//    259200.toInt() -> "давно"
//    else -> "давно"
//}
//
//val agoToText = tutext(sec.toInt(), minute, hours)
//
//fun minuteText (timeMinute: Int,) : String = when {
//    timeMinute % 10 == 1 && timeMinute % 100 !== 11 -> "$timeMinute минуту"
//    timeMinute % 10 in 2..4 && timeMinute % 100 !in 11..19 -> "$timeMinute минуты"
//    else -> "$timeMinute минут"
//}
//
//fun textHour(timeHours:Int,) : String = when {
//    timeHours % 10 == 1 && timeHours % 100 !== 11 ->"$timeHours час"
//    timeHours % 10 in 2..4 && timeHours % 100 !in 11..19 -> "$timeHours часа"
//    else -> "$timeHours часов"
//}
//}









