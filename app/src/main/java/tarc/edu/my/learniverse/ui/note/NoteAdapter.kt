package tarc.edu.my.learniverse.ui.note

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


class NoteAdapter(
    private val recordClickListener: NoteRecordClickListener,
    private val noteList: ArrayList<Note>,
    private val context: Context,
    b: Boolean,
) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNoteTitle: TextView = view.findViewById(R.id.tv_note_item_title)
        val tvNoteAuthor: TextView = view.findViewById(R.id.tv_note_item_author)
        val tvNoteDescription: TextView = view.findViewById(R.id.tv_note_description)
        val ivNote: ImageView = view.findViewById(R.id.iv_note_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which define the UI of the list item
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.record_note,
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

        val notePicRef = storageRef.child("note/" + noteList[position].id)

        notePicRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context).load(uri.toString()).centerCrop()
                .into(holder.ivNote)
        }.addOnFailureListener {
            Toast.makeText(
                holder.itemView.context,
                "Failed to load images",
                Toast.LENGTH_SHORT
            ).show()
        }

        holder.tvNoteTitle.text = noteList[position].title
        holder.tvNoteAuthor.text = noteList[position].author
        holder.tvNoteDescription.text = noteList[position].description

        holder.itemView.setOnClickListener {
            // Item click event handler
            recordClickListener.onRecordClickListener(noteList[position])
        }
    }

    override fun getItemCount(): Int {
        return noteList.size
    }


    interface NoteRecordClickListener {
        fun onRecordClickListener(note: Note)
    }
}