package tarc.edu.my.learniverse.ui.note

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
import tarc.edu.my.learniverse.databinding.FragmentAddNoteBinding
import java.io.FileNotFoundException

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddNoteFragment : Fragment(), MenuProvider {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private var imageUri: Uri? = null
    private val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {

            imageUri = uri
            binding.ivAddImage.setImageURI(uri)

        }
    }
    // This property is only valid between onCreateView and
    // onDestroyView.

    private val noteViewModel: NoteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        database = Firebase.database(getString(R.string.database_url)).reference
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivAddImage.setOnClickListener {
            getPhoto.launch("image/*")
        }


        binding.buttonSave.setOnClickListener {

            val newNoteRef = database
                .child("notes")
                .push()


            val updates: MutableMap<String, Any> = HashMap()
            updates["id"] = newNoteRef.key.toString()
            updates["title"] = binding.tvNoteTitleSave2.text.toString()
            updates["author"] = binding.tvNoteAuthorSave2.text.toString()
            updates["description"] = binding.editTextDescription2.text.toString()
            updates["link"] = binding.tvNoteLinkSave2.text.toString()
            updates["phonenumber"] = binding.tvNotePhoneSave2.text.toString()


            if (imageUri == null || imageUri.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Image is null or empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                noteViewModel.imageUrl = imageUri
            }

            try {
                val storageRef = Firebase.storage(getString(R.string.storage_url)).reference
                val notePicRef =
                    storageRef.child("notes").child(newNoteRef.key.toString())

                val fileUri = Uri.parse(noteViewModel.imageUrl.toString())
                notePicRef.putFile(fileUri).addOnFailureListener {
                    // Handle unsuccessful uploads
                    Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener { _ ->
                    // Continue adding data to RT DB
                    newNoteRef.updateChildren(updates)
                        .addOnSuccessListener {
                            // Write was successful
                            Snackbar.make(
                                binding.root,
                                "Note is added successfully.",
                                Snackbar.LENGTH_SHORT
                            ).setAction("DISMISS") {}.show()
                            noteViewModel.imageUrl = null
                            findNavController().navigateUp()
                        }
                        .addOnFailureListener {
                            // Write failed
                            Snackbar.make(
                                binding.root,
                                "Error adding note.",
                                Snackbar.LENGTH_SHORT
                            ).setAction("DISMISS") {}.show()
                        }
                }
            } catch (ex: FileNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    "Note image not found, please retry.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(
                    "SellFragmentStep2",
                    "Note image not found"
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
        noteViewModel.selectedNote = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == android.R.id.home){
            findNavController().navigateUp()
        }

        return true
    }
}