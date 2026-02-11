import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jotangi.cxms.R

class CustomerServiceBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.service_bottom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvServiceLine).setOnClickListener {
            makePhoneCall("033330567")

        }

        view.findViewById<TextView>(R.id.tvLineAt).setOnClickListener {
            openUrl("https://line.me/R/ti/p/@588fmkjn")

        }

        view.findViewById<TextView>(R.id.tvCancel).setOnClickListener {
            dismiss()  // Close the bottom sheet
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }

        startActivity(intent)
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context?.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open the link.", Toast.LENGTH_SHORT).show()
        }
    }

}
