package tarc.edu.my.learniverse.ui.question


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


class QuestionAdapter(
    private val recordClickListener: QuestionRecordClickListener,
    private val questionList: ArrayList<Question>,
    private val context: Context,
    b: Boolean,
) :
    RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvQuestionTitle: TextView = view.findViewById(R.id.textViewTitleQuestion)
        val ivQuestion: ImageView = view.findViewById(R.id.imageViewQuestion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which define the UI of the list item
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.record_question,
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

        val questionPicRef = storageRef.child("questions/" + questionList[position].id)

        questionPicRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context).load(uri.toString()).centerCrop()
                .into(holder.ivQuestion)
        }.addOnFailureListener {
            Toast.makeText(
                holder.itemView.context,
                "Failed to load images",
                Toast.LENGTH_SHORT
            ).show()
        }

        holder.tvQuestionTitle.text = questionList[position].title


        holder.itemView.setOnClickListener {
            // Item click event handler
            recordClickListener.onRecordClickListener(questionList[position])
        }
    }

    override fun getItemCount(): Int {
        return questionList.size
    }


    interface QuestionRecordClickListener {
        fun onRecordClickListener(question: Question)
    }
}