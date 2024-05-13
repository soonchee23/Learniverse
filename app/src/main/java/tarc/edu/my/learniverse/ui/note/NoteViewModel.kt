package tarc.edu.my.learniverse.ui.note

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel

class NoteViewModel(application: Application) : AndroidViewModel(application)  {
    var imageUrl: Uri? = null
    var selectedNote: Note? = null
}