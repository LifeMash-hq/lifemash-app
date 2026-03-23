package org.bmsk.lifemash.home.ui

import org.bmsk.lifemash.home.api.MarketplaceBlockInfo

sealed interface MarketplaceUiState {
    data object Loading : MarketplaceUiState
    data class Loaded(
        val blocks: List<MarketplaceBlockInfo>,
        val installing: String? = null,
    ) : MarketplaceUiState
    data class Error(val message: String) : MarketplaceUiState
}
