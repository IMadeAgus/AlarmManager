package com.example.myalarmmanager

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.*
import java.text.ParseException
import java.text.SimpleDateFormat

class AlarmReceiver : BroadcastReceiver() {

    //Kemudian kita tambahkan kode di dalam metode onReceive() dan kita tambahkan metode showToast di dalam kelas AlarmReceiver.
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_TYPE)
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        val title =
            if (type.equals(TYPE_ONE_TIME, ignoreCase = true)) TYPE_ONE_TIME else TYPE_REPEATING
        val notifId =
            if (type.equals(TYPE_ONE_TIME, ignoreCase = true)) ID_ONETIME else ID_REPEATING

        if (message != null) {
            showAlarmNotification(context, title, message, notifId)
        }
    }
    //Maka akan ada sebuah notifikasi sederhana yang tampil di panel notifikasi pengguna dengan bunyi notifikasi umum dan getaran.
    // Di sini metode showAlarmNotification bekerja.

    //tambahkan metode untuk mengatur alarm manager
    fun setOneTimeAlarm(
        context: Context,
        type: String,
        date: String,
        time: String,
        message: String
    ) {
        // Validasi inputan date dan time terlebih dahulu
        if (isDateInvalid(date, DATE_FORMAT) || isDateInvalid(time, TIME_FORMAT)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //Mendapatkan instance dari AlarmManager, yang digunakan untuk mengatur waktu alarm. AlarmManager adalah service yang mengelola semua fungsi alarm di Android.
        val intent = Intent(context, AlarmReceiver::class.java)
        //Membuat Intent yang akan dikirimkan ke AlarmReceiver ketika alarm aktif. AlarmReceiver adalah broadcast receiver yang akan mengeksekusi kode ketika alarm berbunyi.

        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)
        //Menambahkan data message dan type ke Intent untuk mengirim pesan yang akan diproses oleh AlarmReceiver.

        Log.e("ONE TIME", "$date $time")
        //Mencatat log untuk debugging. Log ini akan menampilkan tanggal dan waktu alarm yang telah ditetapkan untuk memastikan bahwa inputnya sudah benar.

        val dateArray = date.split("-").toTypedArray()
        //Memecah string date berdasarkan tanda pemisah - untuk memisahkan tahun, bulan, dan hari. Hasilnya disimpan dalam dateArray sebagai array.
        val timeArray = time.split(":").toTypedArray()
        //Memecah string time berdasarkan tanda pemisah : untuk memisahkan jam dan menit. Hasilnya disimpan dalam timeArray sebagai array.
        val calendar = Calendar.getInstance()
        //Membuat objek Calendar untuk mengatur waktu alarm.

        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[0]))
        //engatur bulan pada objek Calendar. Bulan pada Calendar dimulai dari 0 (Januari = 0), sehingga dikurangi 1 dari nilai bulan yang diberikan.
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1)
        //Mengatur bulan pada objek Calendar. Bulan pada Calendar dimulai dari 0 (Januari = 0), sehingga dikurangi 1 dari nilai bulan yang diberikan.
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]))
        //Mengatur hari pada objek Calendar berdasarkan data dari dateArray[2] (bagian hari dari tanggal)
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        //Mengatur jam (24-jam format) pada objek Calendar berdasarkan data dari timeArray[0] (bagian jam dari waktu yang di-input).
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        //Mengatur menit pada objek Calendar berdasarkan data dari timeArray[1] (bagian menit dari waktu yang di-input).
        calendar.set(Calendar.SECOND, 0)
        //Mengatur detik pada objek Calendar menjadi 0, sehingga alarm akan tepat berbunyi pada menit yang telah ditetapkan tanpa penundaan detik.

        val pendingIntent =
            PendingIntent.getBroadcast(context, ID_ONETIME, intent, PendingIntent.FLAG_IMMUTABLE)
        //Membuat PendingIntent dengan tipe Broadcast. PendingIntent ini digunakan untuk mengeksekusi Intent di masa depan
        // (pada waktu yang ditetapkan oleh alarm). ID_ONETIME digunakan untuk membedakan PendingIntent ini dari alarm lainnya.
        // FLAG_IMMUTABLE memastikan bahwa PendingIntent tidak dapat diubah setelah dibuat.
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        //Mengatur alarm pada AlarmManager menggunakan metode set() dengan tipe RTC_WAKEUP.
        // Tipe ini memastikan bahwa jika perangkat dalam keadaan tidur, maka akan dibangunkan saat alarm berbunyi.
        // Alarm akan diatur pada waktu yang dihitung dari calendar.timeInMillis (waktu dalam milidetik dari epoch).
        Toast.makeText(context, "One time alarm set up", Toast.LENGTH_SHORT).show()
        //Menampilkan pesan Toast kepada pengguna, yang memberi tahu bahwa alarm satu kali telah berhasil diatur.
    }
    //Ketika kondisi sesuai, maka akan BroadcastReceiver akan running dengan semua proses yang terdapat di dalam metode onReceive().


    fun setRepeatingAlarm(context: Context, type: String, time: String, message: String) {
        if (isDateInvalid(time, TIME_FORMAT)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)

        val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent =
            PendingIntent.getBroadcast(context, ID_REPEATING, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Toast.makeText(context, "Repeating alarm set up", Toast.LENGTH_SHORT).show()

    //Tidak ada perubahan yang signifikan ketika kita membuat metode setRepeatingAlarm() dengan method setOneTimeAlarm().
    // Yang membedakan adalah pada pemanggilan metode terkait.
    // Pada setOneTimeAlarm() kita menggunakan metode set() sedangkan pada setRepeatingAlarm() kita menggunakan setInexactRepeating()

    //Baris di atas akan menjalankan obyek PendingIntent pada setiap waktu yang ditentukan dalam millissecond dengan interval per hari.
    // Penggunaan metode setInexactRepeating() adalah pilihan lebih tepat karena Android akan menjalankan alarm ini secara bersamaan dengan alarm lain. Meskipun waktu antara tiap alarm tersebut tidak sama persis. Ini penting agar baterai peranti user jadi lebih hemat dan tidak cepat habis.
    // Ingat! Penggunaan alarm manager yang tidak tepat akan memakan daya baterai yang cukup besar.
    }


    //Mrmbatalkan alarm
    fun cancelAlarm(context: Context, type: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = if (type.equals(TYPE_ONE_TIME, ignoreCase = true)) ID_ONETIME else ID_REPEATING
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
        if (pendingIntent != null) {
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
            Toast.makeText(context, "Repeating alarm dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }





    private fun isDateInvalid(date: String, format: String): Boolean {
        return try {
            val df = SimpleDateFormat(format, Locale.getDefault())
            df.isLenient = false
            df.parse(date)
            false
        } catch (e: ParseException) {
            true
        }
    }

    //Fungsi pembuatan notifikasi untuk melihat hasil dari alarm manager yang sudah dibuat sebelumnya.
    private fun showAlarmNotification(
        context: Context,
        title: String,
        message: String,
        notifId: Int
    ) {
        val channelId = "Channel_1"
        //Mendefinisikan channelId, yaitu ID dari kanal notifikasi yang akan digunakan untuk notifikasi ini.
        val channelName = "AlarmManager channel"
        //Mendefinisikan channelName, yaitu nama dari kanal notifikasi yang akan tampil di pengaturan notifikasi perangkat.
        val notificationManagerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //Mengambil NotificationManager dari sistem yang digunakan untuk menampilkan dan mengelola notifikasi.
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        //Mengambil URI suara notifikasi default dari perangkat, yang akan digunakan sebagai suara alarm ketika notifikasi muncul.
        val builder = NotificationCompat.Builder(context, channelId)
            //Membuat objek NotificationCompat.Builder untuk membangun notifikasi. context digunakan untuk pengaturan notifikasi,
            //dan channelId digunakan untuk menetapkan kanal notifikasi di Android versi Oreo ke atas.
            .setSmallIcon(R.drawable.baseline_access_time_24)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            //Mengaktifkan getaran untuk kanal notifikasi ini.
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            //Menetapkan pola getaran yang sama seperti pada notifikasi, yaitu bergetar selama 1 detik dan berhenti selama 1 detik secara berulang.
            builder.setChannelId(channelId)
            notificationManagerCompat.createNotificationChannel(channel)
            //Membuat kanal notifikasi di sistem, jika kanal belum ada (hanya diperlukan untuk Android Oreo ke atas).
        }
        val notification = builder.build()
        //Membangun objek Notification dari builder, yang siap untuk ditampilkan oleh sistem.
        notificationManagerCompat.notify(notifId, notification)
        //Menampilkan notifikasi menggunakan NotificationManager dengan notifId sebagai ID unik notifikasi.
        // ID ini berguna untuk mengupdate atau menghapus notifikasi tertentu di kemudian hari.
    }


    companion object {
        //Selanjutnya pada AlarmReceiver kita membuat beberapa variabel konstanta penanda (flag) yang akan digunakan di keseluruhan bagian kode,
        const val TYPE_ONE_TIME = "OneTimeAlarm"
        const val TYPE_REPEATING = "RepeatingAlarm"

        //Dua baris di atas adalah konstanta untuk menentukan tipe alarm. Dan selanjutnya, dua baris di bawah ini adalah konstanta untuk intent key.
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_TYPE = "type"

        // Siapkan 2 id untuk 2 macam alarm, onetime dan repeating
        //Di sini kita menggunakan dua konstanta bertipe data integer untuk menentukan notif ID sebagai ID untuk menampilkan notifikasi kepada pengguna.
        private const val ID_ONETIME = 100
        private const val ID_REPEATING = 101

        private const val DATE_FORMAT = "yyyy-MM-dd"
        private const val TIME_FORMAT = "HH:mm"
    }
}