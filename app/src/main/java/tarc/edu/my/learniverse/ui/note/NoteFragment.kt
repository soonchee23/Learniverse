package tarc.edu.my.learniverse.ui.note

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import tarc.edu.my.learniverse.R
import tarc.edu.my.learniverse.databinding.FragmentNoteBinding
import tarc.edu.my.learniverse.decoration.GridSpacingItemDecoration
import kotlin.math.roundToInt


class NoteFragment : Fragment(), NoteAdapter.NoteRecordClickListener{

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteList: ArrayList<Note>
    private val noteViewModel: NoteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)


        noteList = ArrayList()
        val adapter = NoteAdapter(this, noteList, requireContext(), false)
        adapter.setHasStableIds(true)
        ViewCompat.setNestedScrollingEnabled(binding.rvNote, false)

        val noteLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvNote.layoutManager = noteLayoutManager
        binding.rvNote.adapter = adapter

        val spanCount = 2 // 2 columns
        val spacing = (16 * resources.displayMetrics.density).roundToInt()
        val includeEdge = true
        binding.rvNote.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )

        val database = Firebase.database(
            getString(R.string.database_url)
        ).reference
        val noteListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                noteList.clear()
                for (noteSnapshot in dataSnapshot.children) {
                    // Get note object and use the values to update the UI

                    val author =
                        noteSnapshot.child("author").getValue(String::class.java)
                    val description =
                        noteSnapshot.child("description").getValue(String::class.java)
                    val id = noteSnapshot.child("id").getValue(String::class.java)
                    val title = noteSnapshot.child("title").getValue(String::class.java)
                    val link = noteSnapshot.child("link").getValue(String::class.java)

                    val note = Note(
                        id,
                        title,
                        author,
                        description,
                        link,
                    )

                    noteList.add(note)
                }

                if (noteList.isEmpty()) {
                    binding.rvNote.visibility = View.GONE

                } else {
                    binding.rvNote.visibility = View.VISIBLE

                }

                noteList.reverse()

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("notes").addValueEventListener(noteListener)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonAddNote.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_note_to_addNote)
        }
    }

   override fun onDestroyView() {
        super.onDestroyView()
    }


    override fun onRecordClickListener(note: Note) {
        noteViewModel.selectedNote = note
        findNavController().navigate(R.id.action_navigation_note_to_note_details)
    }
}