package repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import models.RecordInfo
import kotlin.coroutines.CoroutineContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class RecordInfoRepository {

    suspend fun getAllRecords(dispatcher: CoroutineContext = Dispatchers.IO): List<RecordInfo>

    suspend fun getRecord(id: String, dispatcher: CoroutineContext = Dispatchers.IO): RecordInfo?

    suspend fun deleteRecord(id: String, dispatcher: CoroutineContext = Dispatchers.IO): Boolean
}