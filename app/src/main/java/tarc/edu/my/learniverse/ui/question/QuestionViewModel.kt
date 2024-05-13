package tarc.edu.my.learniverse.ui.question

import android.net.Uri
import androidx.lifecycle.ViewModel


class QuestionViewModel : ViewModel() {
    var imageUrl: Uri? = null
    var selectedQuestion: Question? = null
}