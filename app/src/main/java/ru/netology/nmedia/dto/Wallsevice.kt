import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZoneId

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



//Преобразователь времени

    @RequiresApi(Build.VERSION_CODES.O)
    val dateTime =  LocalDateTime.now()



    @RequiresApi(Build.VERSION_CODES.O)
    val zoneDateTime = dateTime.atZone(ZoneId.of("Europe/Moscow"));

    @RequiresApi(Build.VERSION_CODES.O)
    val sec = zoneDateTime.toInstant().toEpochMilli()/1000
    val timeHours = sec * 60 * 60
    val timeMinute = sec * 60


    val hours = textHour (timeHours.toInt())
    val minute = minuteText(timeMinute.toInt(),)


    fun tutext (sec: Int, minute: String, hours: String): String =   when (sec){
        in 0..60 -> "только что"
        in 61..3600 -> "$minute назад"
        in 3601..86400 -> "$hours назад"
        in 86401..172800 -> "сегодня"
        in 172801..259200 -> "вчера"
        259200.toInt() -> "давно"
        else -> "давно"
    }

    val agoToText = tutext(sec.toInt(), minute, hours)

    fun minuteText (timeMinute: Int,) : String = when {
        timeMinute % 10 == 1 && timeMinute % 100 !== 11 -> "$timeMinute минуту"
        timeMinute % 10 in 2..4 && timeMinute % 100 !in 11..19 -> "$timeMinute минуты"
        else -> "$timeMinute минут"
    }

    fun textHour(timeHours:Int,) : String = when {
        timeHours % 10 == 1 && timeHours % 100 !== 11 ->"$timeHours час"
        timeHours % 10 in 2..4 && timeHours % 100 !in 11..19 -> "$timeHours часа"
        else -> "$timeHours часов"
    }
}
