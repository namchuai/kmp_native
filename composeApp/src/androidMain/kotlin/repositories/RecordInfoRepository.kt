package repositories

import android.content.Context
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.withContext
import models.RecordInfo
import kotlin.coroutines.CoroutineContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class RecordInfoRepository(
    private val context: Context,
) {

    actual suspend fun getAllRecords(dispatcher: CoroutineContext): List<RecordInfo> =
        withContext(dispatcher) {
            val directory = context.getExternalFilesDir(null) ?: return@withContext emptyList()
            val metadataRetriever = MediaMetadataRetriever()
            directory.listFiles()?.map {
                metadataRetriever.setDataSource(it.path)
                val durationStr =
                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val duration = durationStr?.toLongOrNull() ?: 0L

                RecordInfo(
                    id = it.name,
                    name = it.name,
                    path = it.absolutePath,
                    size = it.length(),
                    duration = duration,
                )
            } ?: emptyList()
        }

    actual suspend fun deleteRecord(id: String, dispatcher: CoroutineContext) =
        withContext(dispatcher) {
            val directory = context.getExternalFilesDir(null) ?: return@withContext false
            return@withContext directory.listFiles()?.find { it.name == id }?.delete() ?: false
        }

    actual suspend fun getRecord(
        id: String,
        dispatcher: CoroutineContext
    ): RecordInfo? {
        return withContext(dispatcher) {
            val directory = context.getExternalFilesDir(null) ?: return@withContext null
            val metadataRetriever = MediaMetadataRetriever()
            directory.listFiles()?.find {
                it.name == id
            }?.let {
                metadataRetriever.setDataSource(it.path)
                val durationStr =
                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val duration = durationStr?.toLongOrNull() ?: 0L

                RecordInfo(
                    id = it.name,
                    name = it.name,
                    path = it.absolutePath,
                    size = it.length(),
                    duration = duration,
                )
            }
        }
    }
}