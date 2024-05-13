package tarc.edu.my.learniverse.ui.tutor


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tarc.edu.my.learniverse.R
import tarc.edu.my.learniverse.databinding.FragmentTutorDetailsBinding


class TutorDetailsFragment: Fragment(), MenuProvider {
    private var _binding: FragmentTutorDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val tutorViewModel: TutorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorDetailsBinding.inflate(inflater, container, false)
        database = Firebase.database(getString(R.string.database_url)).reference
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        tutorViewModel.selectedTutor?.let {
            // Get element from the dataset at this position
            // and replace the contents of the view with that element
            val storageRef =
                Firebase.storage(requireContext().getString(R.string.storage_url)).reference
            val tutorPicRef =
                storageRef.child("tutors/" + tutorViewModel.selectedTutor!!.id)

            tutorPicRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(requireContext()).load(uri.toString()).centerCrop()
                    .into(binding.tutorImageDetails)
            }

            binding.tutorNameDetails.text = it.name
            binding.tutorEmailDetails.text = it.email
            binding.tutorHighestEducationDetails.text = it.education
            binding.tutorRatingDetails.text = it.rating
            binding.tutorStrengthDetails.text = it.strength
            binding.btnEmail.setOnClickListener {_ ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("mailto:" + it.email))
                startActivity(intent)
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            findNavController().navigateUp()
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}