package repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class RecorderRepository {
    fun startRecording()

    fun stopRecording(): String?

    suspend fun updateRecordName(
        path: String,
        newName: String,
        dispatcher: CoroutineContext = Dispatchers.IO
    ): String

    fun release()
}