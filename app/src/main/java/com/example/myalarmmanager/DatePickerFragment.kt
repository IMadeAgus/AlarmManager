package com.example.myalarmmanager
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

//Setelah membuat sebuah AlarmReceiver, kita tambahkan kelas untuk membantu kita mengambil waktu.
// Pertama, kita buat fragment untuk setup tanggal. Buatlah kelas dengan nama DatePickerFragment.
class DatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener {
    private var mListener: DialogDateListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as DialogDateListener?
    }

    override fun onDetach() {
        super.onDetach()
        if (mListener != null) {
            mListener = null
        }
    }
    //Fungsi onAttach() hanya sekali dipanggil dalam fragment dan berfungsi untuk mengkaitkan dengan activity pemanggil,
    // sedangkan onDetach() hanya dipanggil sebelum fragmen tidak lagi dikaitkan dengan activity pemanggil.


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        //Ketika mengimport komponen Calendar, pastikan Anda menggunakan java.util.calendar.

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DATE)
        return DatePickerDialog(activity as Context, this, year, month, date)
    }


    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        mListener?.onDialogDateSet(tag, year, month, dayOfMonth)
    }
    //Fungsi onDateSet akan dipanggil ketika kita memilih tanggal yang kita inginkan. Kemudian setelah tanggal dipilih maka variable tanggal,
    //bulan dan tahun akan dikirim ke MainActivity menggunakan bantuan interface DialogDateListener.

    interface DialogDateListener {
        fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int)
    }
}