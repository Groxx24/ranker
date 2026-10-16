package com.ranker.app.ui.rankings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ranker.app.domain.model.Ranking
import com.ranker.app.ui.theme.OnAccent
import com.ranker.app.ui.theme.accentFor

/**
 * One pill per ranking. The selected one fills with its accent and opens up to show its title;
 * the others shrink back to their emoji, so all five fit on a phone without scrolling.
 */
@Composable
fun StatTabs(
    rankings: List<Ranking>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        rankings.forEachIndexed { index, ranking ->
            StatTab(
                emoji = ranking.stat.emoji,
                title = ranking.stat.title,
                index = index,
                isSelected = index == selected,
                onClick = { onSelect(index) },
            )
        }
    }
}

@Composable
private fun StatTab(emoji: String, title: String, index: Int, isSelected: Boolean, onClick: () -> Unit) {
    val accent = accentFor(index)
    val idle = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f)
    val fill by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tabFill",
    )
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .drawBehind {
                val corner = CornerRadius(size.height / 2)
                drawRoundRect(idle, cornerRadius = corner)
                drawRoundRect(
                    brush = Brush.horizontalGradient(listOf(accent.start, accent.end)),
                    cornerRadius = corner,
                    alpha = fill,
                )
            }
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(PaddingValues(horizontal = 14.dp, vertical = 10.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, fontSize = 18.sp)
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn() + expandHorizontally(spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow)),
            exit = fadeOut() + shrinkHorizontally(spring(stiffness = Spring.StiffnessMedium)),
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = OnAccent,
                maxLines = 1,
            )
        }
    }
}
