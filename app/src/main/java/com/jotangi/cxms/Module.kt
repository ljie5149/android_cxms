package com.jotangi.cxms

import com.jotangi.cxms.Api.book.BookApiRepository
import com.jotangi.cxms.Api.book.BookViewModel
import com.jotangi.cxms.Api.book.QrCodeApiRepository
import com.jotangi.cxms.Api.qrcode.QrCodeViewModel
import com.jotangi.cxms.utils.smartwatch.WatchApiRepository
import com.jotangi.cxms.utils.smartwatch.WatchViewModel
import org.koin.dsl.module


val viewModule = module {
    single {
        WatchViewModel(get())
    }
}

val repoModule = module {
    single {
        WatchApiRepository()
    }
}

val appModule = listOf(
    viewModule,
    repoModule
)


val viewModule2 = module {
    single {
        BookViewModel(get())
    }
}

val repoModule2 = module {
    single {
        BookApiRepository()
    }
}

val appModule2 = listOf(
    viewModule2,
    repoModule2
)

val viewModule3 = module {
    single {
        QrCodeViewModel(get())
    }
}

val repoModule3 = module {
    single {
        QrCodeApiRepository()
    }
}

val appModule3 = listOf(
    viewModule3,
    repoModule3
)