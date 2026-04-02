package org.bmsk.lifemash.memo.ui.di

import org.bmsk.lifemash.memo.ui.MemoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val memoUiModule = module {
    viewModel { MemoViewModel(get(), get()) }
}
