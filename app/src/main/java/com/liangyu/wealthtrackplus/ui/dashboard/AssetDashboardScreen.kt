package com.liangyu.wealthtrackplus.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AssetDashboardRoute(
    modifier: Modifier = Modifier,
    viewModel: AssetDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AssetDashboardScreen(
        uiState = uiState,
        onRefresh = viewModel::refreshSnapshot,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDashboardScreen(
    uiState: AssetDashboardUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val lazyState = rememberLazyListState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "資產儀表板", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Hi, ${uiState.userName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            state = lazyState
        ) {
            item {
                DashboardSummaryCard(uiState)
            }
            item {
                AssetHighlightsSection(uiState.assetCards)
            }
            item {
                AllocationSection(uiState.assetAllocation)
            }
            item {
                InsightsSection(uiState.insights)
            }
            item {
                RecentActivitySection(uiState.recentActivities)
            }
        }
    }
}

@Composable
private fun DashboardSummaryCard(uiState: AssetDashboardUiState) {
    val gradient = remember {
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
            )
        )
    }
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Column {
                        Text(
                            text = "總資產",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = uiState.totalNetWorth.formatAsCurrency(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val changeColor = if (uiState.totalDayChangePercent >= 0) {
                        Color(0xFFFFD166)
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                    val dayChangePercentText = uiState.totalDayChangePercent
                        .formatAsPercent(withSign = true)
                    val dayChangeAmountText = uiState.totalDayChangeAmount
                        .formatAsCurrency(withSign = true)
                    Icon(
                        imageVector = Icons.Rounded.ArrowOutward,
                        contentDescription = null,
                        tint = changeColor,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = dayChangePercentText,
                        color = changeColor,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = dayChangeAmountText,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetHighlightsSection(cards: List<AssetCardUiState>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(title = "資產亮點", iconTint = MaterialTheme.colorScheme.primary)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            cards.forEach { card ->
                AssetHighlightCard(card, modifier = Modifier.weight(1f, fill = true))
            }
        }
    }
}

@Composable
private fun AssetHighlightCard(card: AssetCardUiState, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = card.label, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = card.amount.formatAsCurrency(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${card.changePercent.formatAsPercent(withSign = true)} 今日",
                style = MaterialTheme.typography.bodySmall,
                color = if (card.changePercent >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun AllocationSection(allocation: List<AssetAllocationUiState>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(title = "資產配置", iconTint = Color(0xFFFFD166))
        allocation.forEach { item ->
            AllocationRow(item)
        }
    }
}

@Composable
private fun AllocationRow(item: AssetAllocationUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.label, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${item.percent.toInt()}% 配置 · 目標 ${(item.targetPercent * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${item.percent.toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = item.percent / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun InsightsSection(insights: List<InsightUiState>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(title = "洞察", iconTint = MaterialTheme.colorScheme.secondary)
        insights.forEach { insight ->
            InsightCard(insight)
        }
    }
}

@Composable
private fun InsightCard(insight: InsightUiState) {
    val accentColor = when (insight.accent) {
        InsightAccent.Positive -> Color(0xFF00C853)
        InsightAccent.Warning -> Color(0xFFFFA726)
        InsightAccent.Neutral -> MaterialTheme.colorScheme.primary
    }
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.BarChart,
                    contentDescription = null,
                    tint = accentColor
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = insight.title, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = insight.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecentActivitySection(activities: List<AssetActivityUiState>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(title = "最近動態", iconTint = MaterialTheme.colorScheme.primary)
        ElevatedCard {
            Column(modifier = Modifier.fillMaxWidth()) {
                activities.forEachIndexed { index, activity ->
                    ActivityRow(activity)
                    if (index < activities.lastIndex) {
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(activity: AssetActivityUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = activity.title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = activity.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            val amountColor = if (activity.amount >= 0) Color(0xFF00C853) else MaterialTheme.colorScheme.error
            Text(
                text = activity.amount.formatAsCurrency(withSign = true),
                style = MaterialTheme.typography.titleSmall,
                color = amountColor,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = activity.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, iconTint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.BarChart,
                contentDescription = null,
                tint = iconTint
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium)
    }
}

private fun Double.formatAsCurrency(withSign: Boolean = false): String {
    val format = NumberFormat.getCurrencyInstance(Locale.TAIWAN)
    val value = format.format(kotlin.math.abs(this))
    return when {
        withSign && this > 0 -> "+$value"
        withSign && this < 0 -> "-$value"
        else -> value
    }
}

private fun Double.formatAsPercent(withSign: Boolean = false, fractionDigits: Int = 2): String {
    val formatter = NumberFormat.getNumberInstance(Locale.TAIWAN).apply {
        maximumFractionDigits = fractionDigits
        minimumFractionDigits = 0
    }
    val value = formatter.format(kotlin.math.abs(this))
    val percentText = "$value%"
    return when {
        withSign && this > 0 -> "+$percentText"
        withSign && this < 0 -> "-$percentText"
        else -> percentText
    }
}

@Preview(showBackground = true)
@Composable
private fun AssetDashboardPreview() {
    val previewState = AssetDashboardViewModel().uiState.value
    MaterialTheme {
        Surface {
            AssetDashboardScreen(uiState = previewState, onRefresh = {})
        }
    }
}
