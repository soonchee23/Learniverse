package tarc.edu.my.learniverse.ui.tutor

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel

class TutorViewModel(application: Application) : AndroidViewModel(application) {
    var imageUrl: Uri? = null
    var selectedTutor: Tutor? = null
}