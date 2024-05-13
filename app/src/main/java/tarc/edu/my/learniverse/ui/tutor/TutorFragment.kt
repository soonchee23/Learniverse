package tarc.edu.my.learniverse.ui.tutor

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
import tarc.edu.my.learniverse.databinding.FragmentTutorBinding
import tarc.edu.my.learniverse.decoration.GridSpacingItemDecoration
import kotlin.math.roundToInt

class TutorFragment : Fragment(),TutorAdaptor.TutorRecordClickListener {

    private var _binding: FragmentTutorBinding? = null
    private val binding get() = _binding!!
    private lateinit var tutorList: ArrayList<Tutor>
    private val tutorViewModel: TutorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorBinding.inflate(inflater, container, false)


        tutorList = ArrayList()
        val adapter = TutorAdaptor(this, tutorList, requireContext(), false)
        adapter.setHasStableIds(true)
        ViewCompat.setNestedScrollingEnabled(binding.rvTutor, false)

        val tutorLayoutManager = GridLayoutManager(requireContext(), 1)
        binding.rvTutor.layoutManager = tutorLayoutManager
        binding.rvTutor.adapter = adapter

        val spanCount = 1 // 1 columns
        val spacing = (16 * resources.displayMetrics.density).roundToInt()
        val includeEdge = true
        binding.rvTutor.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )

        val database = Firebase.database(
            getString(R.string.database_url)
        ).reference
        val tutorListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tutorList.clear()
                for (tutorSnapshot in dataSnapshot.children) {
                    // Get tutor object and use the values to update the UI
                    val id =
                        tutorSnapshot.child("id").getValue(String::class.java)
                    val name =
                        tutorSnapshot.child("name").getValue(String::class.java)
                    val email =
                        tutorSnapshot.child("email").getValue(String::class.java)
                    val education = tutorSnapshot.child("highest education").getValue(String::class.java)
                    val rating = tutorSnapshot.child("rating").getValue(String::class.java)
                    val strength = tutorSnapshot.child("strength").getValue(String::class.java)

                    val tutor = Tutor(
                        id,
                        name,
                        email,
                        education,
                        rating,
                        strength,
                    )

                    tutorList.add(tutor)
                }

                if (tutorList.isEmpty()) {
                    binding.rvTutor.visibility = View.GONE

                } else {
                    binding.rvTutor.visibility = View.VISIBLE

                }

                tutorList.reverse()

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("tutors").addValueEventListener(tutorListener)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonAddTutor.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_tutor_to_addTutorFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


    override fun onRecordClickListener(tutor: Tutor) {
        tutorViewModel.selectedTutor = tutor
        findNavController().navigate(R.id.action_navigation_tutor_to_tutorDetails)
    }

}