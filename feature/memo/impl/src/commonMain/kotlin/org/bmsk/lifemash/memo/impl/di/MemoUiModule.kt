package org.bmsk.lifemash.memo.impl.di

import org.bmsk.lifemash.domain.usecase.calendar.GetMyGroupsUseCase
import org.bmsk.lifemash.domain.usecase.memo.CreateMemoUseCase
import org.bmsk.lifemash.domain.usecase.memo.DeleteMemoUseCase
import org.bmsk.lifemash.domain.usecase.memo.GetGroupMemosUseCase
import org.bmsk.lifemash.domain.usecase.memo.SearchMemosUseCase
import org.bmsk.lifemash.domain.usecase.memo.SyncChecklistUseCase
import org.bmsk.lifemash.domain.usecase.memo.UpdateMemoUseCase
import org.bmsk.lifemash.memo.impl.MemoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val memoUiModule = module {
    viewModel {
        MemoViewModel(
            GetMyGroupsUseCase(get()),
            GetGroupMemosUseCase(get()),
            CreateMemoUseCase(get()),
            UpdateMemoUseCase(get()),
            DeleteMemoUseCase(get()),
            SearchMemosUseCase(get()),
            SyncChecklistUseCase(get()),
        )
    }
}
