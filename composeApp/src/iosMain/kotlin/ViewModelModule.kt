import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.nam.namnative.record.RecordViewModel

actual val viewModelModule = module {
    singleOf(::RecordViewModel)
}
