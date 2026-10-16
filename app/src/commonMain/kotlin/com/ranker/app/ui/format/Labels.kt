package com.ranker.app.ui.format

import androidx.compose.runtime.Composable
import com.ranker.app.domain.model.Continent
import com.ranker.app.resources.Res
import com.ranker.app.resources.continent_africa
import com.ranker.app.resources.continent_asia
import com.ranker.app.resources.continent_europe
import com.ranker.app.resources.continent_north_america
import com.ranker.app.resources.continent_oceania
import com.ranker.app.resources.continent_south_america
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
fun continentName(continent: Continent): String = stringResource(
    when (continent) {
        Continent.AFRICA -> Res.string.continent_africa
        Continent.ASIA -> Res.string.continent_asia
        Continent.EUROPE -> Res.string.continent_europe
        Continent.NORTH_AMERICA -> Res.string.continent_north_america
        Continent.SOUTH_AMERICA -> Res.string.continent_south_america
        Continent.OCEANIA -> Res.string.continent_oceania
    },
)

/** `THU · 16 OCT`. */
fun shortDate(date: LocalDate): String =
    "${date.dayOfWeek.name.take(3)} · ${date.day} ${date.month.name.take(3)}"
