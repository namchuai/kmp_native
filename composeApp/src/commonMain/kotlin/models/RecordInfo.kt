package models

data class RecordInfo(
    val id: String,
    val name: String,
    val path: String,
    val size: Long,
    val duration: Long, // in milliseconds
//    val createdAt: Long, // in milliseconds // TODO: import Instant
)