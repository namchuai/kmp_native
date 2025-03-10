import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import repositories.RecorderRepository
import repositories.RecordInfoRepository
import repositories.MediaPlayerRepository

actual val repositoryModule = module {
    singleOf(::RecorderRepository)
    singleOf(::RecordInfoRepository)
    singleOf(::MediaPlayerRepository)
}