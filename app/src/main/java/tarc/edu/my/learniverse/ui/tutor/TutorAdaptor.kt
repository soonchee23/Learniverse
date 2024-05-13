package tarc.edu.my.learniverse.ui.tutor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tarc.edu.my.learniverse.R

class TutorAdaptor (
    private val recordClickListener: TutorRecordClickListener,
    private val tutorList: ArrayList<Tutor>,
    private val context: Context,
    b: Boolean,
):
    RecyclerView.Adapter<TutorAdaptor.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tutorName: TextView = view.findViewById(R.id.tutor_item_name)
        val tutorEmail: TextView = view.findViewById(R.id.tutor_item_email)
        val tutorEducation: TextView = view.findViewById(R.id.tutor_item_he)
        val tutorRating: TextView = view.findViewById(R.id.tutor_item_rate)
        val tutorStrength: TextView = view.findViewById(R.id.tutor_item_strength)
        val ivTutor: ImageView = view.findViewById(R.id.tutor_item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which define the UI of the list item
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.record_tutor,
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from the dataset at this position
        // and replace the contents of the view with that element
        val storageRef =
            Firebase.storage(holder.itemView.context.getString(R.string.storage_url)).reference

        val tutorPicRef = storageRef.child("tutors/" + tutorList[position].id)

        tutorPicRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context).load(uri.toString()).centerCrop()
                .into(holder.ivTutor)
        }.addOnFailureListener {
            Toast.makeText(
                holder.itemView.context,
                "Failed to load images",
                Toast.LENGTH_SHORT
            ).show()
        }

        holder.tutorName.text = tutorList[position].name
        holder.tutorEmail.text = tutorList[position].email
        holder.tutorEducation.text = tutorList[position].education
        holder.tutorRating.text = tutorList[position].rating
        holder.tutorStrength.text = tutorList[position].strength


        holder.itemView.setOnClickListener {
            // Item click event handler
            recordClickListener.onRecordClickListener(tutorList[position])
        }
    }

    override fun getItemCount(): Int {
        return tutorList.size
    }


    interface TutorRecordClickListener {
        fun onRecordClickListener(tutor: Tutor)
    }
}