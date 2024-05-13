package tarc.edu.my.learniverse.ui.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel

class ProfileViewModel(application: Application) : AndroidViewModel(application)  {
    var imageUrl: Uri? = null
    var selectedProfile: Profile? = null
}