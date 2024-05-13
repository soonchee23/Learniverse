package tarc.edu.my.learniverse.ui.tutor

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tarc.edu.my.learniverse.R
import tarc.edu.my.learniverse.databinding.FragmentAddTutorBinding
import java.io.FileNotFoundException

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddTutorFragment : Fragment(), MenuProvider {

    private var _binding: FragmentAddTutorBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private var imageUri: Uri? = null
    private val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.tutorAddImage.setImageURI(uri)
        }
    }
    // This property is only valid between onCreateView and
    // onDestroyView.

    private val tutorViewModel: TutorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddTutorBinding.inflate(inflater, container, false)
        database = Firebase.database(getString(R.string.database_url)).reference
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tutorAddImage.setOnClickListener {
            getPhoto.launch("image/*")
        }


        binding.buttonSaveTutor.setOnClickListener {

            val newTutorRef = database
                .child("tutors")
                .push()


            val updates: MutableMap<String, Any> = HashMap()
            updates["id"] = newTutorRef.key.toString()
            updates["name"] = binding.tutorNameSave2.text.toString()
            updates["email"] = binding.tutorEmailSave2.text.toString()
            updates["highest education"] = binding.tutorHighestEducationSave2.text.toString()
            updates["rating"] = binding.tutorRatingSave2.text.toString()
            updates["strength"] = binding.tutorStrengthSave2.text.toString()


            if (imageUri == null || imageUri.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Image is null or empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                tutorViewModel.imageUrl = imageUri
            }

            try {
                val storageRef = Firebase.storage(getString(R.string.storage_url)).reference
                val productPicRef =
                    storageRef.child("tutors").child(newTutorRef.key.toString())

                val fileUri = Uri.parse(tutorViewModel.imageUrl.toString())
                productPicRef.putFile(fileUri).addOnFailureListener {
                    // Handle unsuccessful uploads
                    Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener { _ ->
                    // Continue adding data to RT DB
                    newTutorRef.updateChildren(updates)
                        .addOnSuccessListener {
                            // Write was successful
                            Snackbar.make(
                                binding.root,
                                "Tutor is added successfully.",
                                Snackbar.LENGTH_SHORT
                            ).setAction("DISMISS") {}.show()
                            tutorViewModel.imageUrl = null
                            findNavController().navigateUp()
                        }
                        .addOnFailureListener {
                            // Write failed
                            Snackbar.make(
                                binding.root,
                                "Error adding product.",
                                Snackbar.LENGTH_SHORT
                            ).setAction("DISMISS") {}.show()
                        }
                }
            } catch (ex: FileNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    "Tutor image not found, please retry.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(
                    "SellFragmentStep2",
                    "Tutor image not found"
                )
            }

        }

        //Add support for menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        tutorViewModel.selectedTutor = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == android.R.id.home){
            findNavController().navigateUp()
        }

        return true
    }
}