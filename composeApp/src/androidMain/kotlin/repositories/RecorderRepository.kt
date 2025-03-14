package repositories

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.CoroutineContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class RecorderRepository(
    private val context: Context
) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    actual fun startRecording() {
        try {
            val outputDir = context.getExternalFilesDir(null)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            outputFile = File(outputDir, "RECORDING_$timeStamp.3gp")

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(outputFile?.absolutePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                prepare()
                start()

                Log.d(TAG, "Recording started: ${outputFile?.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            throw e
        }
    }

    actual fun stopRecording(): String? {
        try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null

            val filePath = outputFile?.absolutePath
            Log.d(TAG, "Recording stopped: $filePath")
            return filePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording", e)
            return null
        }
    }

    actual suspend fun updateRecordName(
        path: String,
        newName: String,
        dispatcher: CoroutineContext,
    ) = withContext(dispatcher) {
        val originalFile = File(path)
        val extension = path.substringAfterLast(".", "")
        val directory = originalFile.parentFile
        val newFile = File(directory, "$newName.$extension")

        try {
            if (originalFile.renameTo(newFile)) {
                Log.d(TAG, "Successfully renamed recording to: ${newFile.absolutePath}")
                newFile.absolutePath
            } else {
                Log.e(TAG, "Failed to rename recording, keeping original: $path")
                path
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error renaming recording: ${e.message}")
            path
        }
    }

    actual fun release() {
        try {
            recorder?.release()
            recorder = null
            outputFile = null
            Log.d(TAG, "Recorder released")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to release recorder", e)
        }
    }

    companion object {
        private const val TAG = "RecorderRepository"
    }
}
