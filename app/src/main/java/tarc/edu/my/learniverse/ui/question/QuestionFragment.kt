package tarc.edu.my.learniverse.ui.question

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
import tarc.edu.my.learniverse.databinding.FragmentQuestionBinding
import tarc.edu.my.learniverse.decoration.GridSpacingItemDecoration
import kotlin.math.roundToInt


class QuestionFragment : Fragment(), QuestionAdapter.QuestionRecordClickListener{

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    private lateinit var questionList: ArrayList<Question>
    private val questionViewModel: QuestionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)


        questionList = ArrayList()
        val adapter = QuestionAdapter(this, questionList, requireContext(), false)
        adapter.setHasStableIds(true)
        ViewCompat.setNestedScrollingEnabled(binding.recyclerViewQuestion, false)

        val questionLayoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerViewQuestion.layoutManager = questionLayoutManager
        binding.recyclerViewQuestion.adapter = adapter

        val spanCount = 1 // 2 columns
        val spacing = (16 * resources.displayMetrics.density).roundToInt()
        val includeEdge = true
        binding.recyclerViewQuestion.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )

        val database = Firebase.database(
            getString(R.string.database_url)
        ).reference
        val questionListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                questionList.clear()
                for (questionSnapshot in dataSnapshot.children) {
                    // Get question object and use the values to update the UI

                    val description =
                        questionSnapshot.child("description").getValue(String::class.java)
                    val id = questionSnapshot.child("id").getValue(String::class.java)
                    val title = questionSnapshot.child("title").getValue(String::class.java)

                    val question = Question(
                        id,
                        title,
                        description,
                    )

                    questionList.add(question)
                }

                if (questionList.isEmpty()) {
                    binding.recyclerViewQuestion.visibility = View.GONE

                } else {
                    binding.recyclerViewQuestion.visibility = View.VISIBLE

                }

                questionList.reverse()

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("questions").addValueEventListener(questionListener)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fab.setOnClickListener { view ->
            findNavController().navigate(R.id.action_navigation_question_to_addQuestionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


    override fun onRecordClickListener(question: Question) {
        questionViewModel.selectedQuestion = question
        findNavController().navigate(R.id.action_navigation_question_to_questionDetailsFragment)
    }
}