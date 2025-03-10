import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import org.nam.namnative.record.RecordViewModel
import org.nam.namnative.recordlist.RecordListViewModel
import org.nam.namnative.mediaplayer.MediaPlayerViewModel

actual val viewModelModule = module {
    viewModelOf(::RecordViewModel)
    viewModelOf(::RecordListViewModel)
    viewModelOf(::MediaPlayerViewModel)
}