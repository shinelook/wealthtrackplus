package com.liangyu.wealthtrackplus.ui.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

/**
 * ViewModel for the Asset Dashboard screen. For now, it exposes a fake data set
 * so we can design and implement the UI independently from the database layer.
 */
class AssetDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(buildFakeUiState())
    val uiState: StateFlow<AssetDashboardUiState> = _uiState.asStateFlow()

    /**
     * A lightweight refresh that just randomizes the day change to keep the UI lively
     * while we are working with static data.
     */
    fun refreshSnapshot() {
        val randomMultiplier = Random.nextDouble(from = 0.85, until = 1.15)
        _uiState.value = _uiState.value.copy(
            totalDayChangePercent = (uiState.value.totalDayChangePercent * randomMultiplier)
                .asPercent(),
            assetCards = uiState.value.assetCards.map { card ->
                card.copy(changePercent = (card.changePercent * randomMultiplier).asPercent())
            }
        )
    }

    private fun Double.asPercent(): Double {
        return BigDecimal(this).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    private fun buildFakeUiState(): AssetDashboardUiState = AssetDashboardUiState(
        userName = "Liang",
        totalNetWorth = 128_450_000.0,
        totalDayChangePercent = 1.28,
        totalDayChangeAmount = 1_630_000.0,
        assetCards = listOf(
            AssetCardUiState(
                label = "投資組合",
                amount = 92_300_000.0,
                changePercent = 1.6
            ),
            AssetCardUiState(
                label = "現金與活存",
                amount = 18_200_000.0,
                changePercent = 0.8
            ),
            AssetCardUiState(
                label = "房地產",
                amount = 17_950_000.0,
                changePercent = 0.35
            )
        ),
        assetAllocation = listOf(
            AssetAllocationUiState("股票", 52f, 0.8f),
            AssetAllocationUiState("債券", 23f, 0.35f),
            AssetAllocationUiState("不動產", 15f, 0.2f),
            AssetAllocationUiState("現金", 10f, 0.1f)
        ),
        insights = listOf(
            InsightUiState(
                title = "本週亮點",
                description = "科技與能源類股帶動組合上漲 1.6%",
                accent = InsightAccent.Positive
            ),
            InsightUiState(
                title = "風險提醒",
                description = "美元指數回落，建議調整 5% 匯率避險部位",
                accent = InsightAccent.Warning
            )
        ),
        recentActivities = listOf(
            AssetActivityUiState(
                title = "買進 NVIDIA",
                subtitle = "45 股 @ 1,125 USD",
                amount = -1_500_000.0,
                timestamp = "昨天 14:32"
            ),
            AssetActivityUiState(
                title = "配息入帳",
                subtitle = "台積電 (2330) Q1",
                amount = 420_000.0,
                timestamp = "3 天前"
            ),
            AssetActivityUiState(
                title = "活存轉入",
                subtitle = "平衡基金",
                amount = 800_000.0,
                timestamp = "4 天前"
            )
        )
    )
}

data class AssetDashboardUiState(
    val userName: String,
    val totalNetWorth: Double,
    val totalDayChangePercent: Double,
    val totalDayChangeAmount: Double,
    val assetCards: List<AssetCardUiState>,
    val assetAllocation: List<AssetAllocationUiState>,
    val insights: List<InsightUiState>,
    val recentActivities: List<AssetActivityUiState>
)

data class AssetCardUiState(
    val label: String,
    val amount: Double,
    val changePercent: Double
)

data class AssetAllocationUiState(
    val label: String,
    val percent: Float,
    val targetPercent: Float
)

data class AssetActivityUiState(
    val title: String,
    val subtitle: String,
    val amount: Double,
    val timestamp: String
)

data class InsightUiState(
    val title: String,
    val description: String,
    val accent: InsightAccent
)

enum class InsightAccent { Positive, Neutral, Warning }
