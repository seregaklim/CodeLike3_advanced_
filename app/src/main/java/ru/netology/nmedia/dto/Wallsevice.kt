import java.util.*
import android.os.Parcel
import android.os.Parcelable
import ru.netology.nmedia.dto.Post
import java.text.SimpleDateFormat


class Wallsevice() {

    fun notZeroShare(share: Long) = when (share) {
        in 0..999 -> share
        in 1_000..9_999 -> share / 10_000.toDouble()
        in 10_000..99_999 -> share / 10_000
        in 100_000..999_999 -> share / 1000_000.toDouble()
        in 1_000_000..100_000_000 -> share / 1000_000
        else -> share
    }

    fun zeroingOutShare(share: Long) = when (share) {
        in 0..999 -> share
        in 1_000..9_999 -> "${this.notZeroShare(share)} К"
        in 10_000..99_999 -> "${this.notZeroShare(share)} К"
        in 100_000..999_999 -> "${this.notZeroShare(share)} М"
        in 1_000_000..100_000_000 -> "${this.notZeroShare(share)} М"
        else -> share
    }

    fun notZeroLikes(likes: Int) = when (likes) {
        in 0..999 -> likes
        in 1_000..9_999 -> likes / 10_000.toDouble()
        in 10_000..99_999 -> likes / 10_000
        in 100_000..999_999 -> likes / 1000_000.toDouble()
        in 1_000_000..100_000_000 -> likes / 1000_000
        else -> likes
    }

    fun zeroingOutLikes(likes: Long) = when (likes) {
        in 0..999 -> likes
        in 1_000..9_999 -> "${this.notZeroShare(likes)} К"
        in 10_000..99_999 -> "${this.notZeroShare(likes)} К"
        in 100_000..999_999 -> "${this.notZeroShare(likes)} М"
        in 1_000_000..100_000_000 -> "${this.notZeroShare(likes)} М"
        else -> likes
    }

    val hours = fun(timing: Long): Long {

        val currentDate = Date()
        //текущщее время минус дата создания поста
        val time = currentDate.time  - timing

        //переводим в дни
       // val days = time / 1000 / 3600 / 24
        //переводим в часы
        val hours = time/ 1000/3600

        val strHours = hours.toLong()
        return strHours
    }

    //конвектор часовой
    fun timeСonverter(timing: Long): String = when (this.hours(timing)) {
        in 0..24 -> "Сегодня!"
        in 24..48 -> "Вчера!"
        in 48..168 -> "На прошлой неделе!"
        else -> "давно"
    }
}


//    val date= Date();
//    val  nowTime:Long  = date.getTime()
//    val nowHours= fun(post: Post): Long {
//        val h = nowTime - post.timing
//        val hours = h /60/60
//        return hours
//
//    }
//
//
//    fun timeСonverter (): String =   when (nowHours){
//    in 0..24 -> "Сегодня!"
//    in 24..48 -> "Вчера!"
//    in 48..168 -> "На прошлой неделе!"
//    else -> "давно"
//}


//
//    fun timeСonverter () {
//
//        val date = Date();
//        val nowTime: Long = date.getTime()
//        val nowHours = fun(post: Post): Int {
//            val h = nowTime - post.timing
//            val hours = h / 60 / 60
//            return hours.toInt()
//
//        }
//
//
//
//
//
//        val s = when (nowHours) {
//            in 0..24 -> "Сегодня!"
//            in 24..48 -> "Вчера!"
//            in 48..168 -> "На прошлой неделе!"
//            else -> "давно"
//        }
//
//    }













//    val date= Date();
//    val  nowTime:Long  = date.getTime()
//    val nowHours= fun(post: Post): Long {
//        val h = nowTime - post.timing
//        val hours = h /60/60
//        return hours
//
//    }
//
//
//    fun timeСonverter (): String =   when (nowHours){
//    in 0..24 -> "Сегодня!"
//    in 24..48 -> "Вчера!"
//    in 48..168 -> "На прошлой неделе!"
//    else -> "давно"
//}








//@RequiresApi(Build.VERSION_CODES.O)
//val agoToText =  timeСonverter(post)
    // преобразователь времени в часах

//Сегодня: элементы, опубликованные в пределах от текущего времени до текущее время - 24 часа.
//Вчера: элементы, опубликованные в пределах от текущего времени - 24 часа до текущее время - 48 часов.
//На прошлой неделе: всё, что старше текущее время - 48 часов.


//fun timeСonverter ( hours: Long): String =   when (){
//    in 0..24 -> "Сегодня!"
//    in 24..48 -> "Вчера!"
//    in 48..168 -> "На прошлой неделе!"
//    else -> "давно"
//}
//val agoToText =  timeСonverter( hours)








//
//    fun daysString(dataend: String):String{
//        // val dateFormat = SimpleDateFormat("dd.MM.yyyy")
//
//        val dateFormat = SimpleDateFormat("")
//        val endDate = dateFormat.parse(dataend)
//        val currentDate = Date()
//        val time = endDate.time - currentDate.time
//        val days = time / 1000 / 3600 / 24
//        val strtoday = days.toString()
//        return strtoday
//    }



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










