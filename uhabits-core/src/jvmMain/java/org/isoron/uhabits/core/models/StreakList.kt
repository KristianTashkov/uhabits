/*
 * Copyright (C) 2016-2021 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isoron.uhabits.core.models

import javax.annotation.concurrent.*
import kotlin.math.*

@ThreadSafe
class StreakList {
    private val list = ArrayList<Streak>()

    @Synchronized
    fun getBest(limit: Int): List<Streak> {
        list.sortWith { s1: Streak, s2: Streak -> s2.compareLonger(s1) }
        return list.subList(0, min(list.size, limit)).apply {
            sortWith { s1: Streak, s2: Streak -> s2.compareNewer(s1) }
        }.toList()
    }

    @Synchronized
    fun recompute(
        computedEntries: EntryList,
        from: Timestamp,
        to: Timestamp,
    ) {
        list.clear()
        val entries = computedEntries.getByInterval(from, to)
        var current: Timestamp = from
        var lastChecked: Timestamp = from
        var streakStart: Timestamp = from
        var isInStreak = false
        var skipDaysInStreak = 0

        for (i in entries.size - 1 downTo 0) {
            val value = entries[i].value
            current = entries[i].timestamp
            val isCurrentChecked = value == Entry.YES_AUTO || value == Entry.YES_MANUAL
            val isCurrentUnchecked = value == Entry.NO || value == Entry.UNKNOWN

            if (isCurrentChecked) lastChecked = current
            if (isInStreak && value == Entry.SKIP) skipDaysInStreak += 1
            else if (isInStreak && isCurrentUnchecked) {
                skipDaysInStreak -= lastChecked.daysUntil(current) - 1
                list.add(Streak(streakStart, lastChecked, skipDaysInStreak))
                isInStreak = false
            } else if (!isInStreak && isCurrentChecked) {
                streakStart = current
                isInStreak = true
                skipDaysInStreak = 0
            }
        }

        if (isInStreak) {
            skipDaysInStreak -= lastChecked.daysUntil(current)
            list.add(Streak(streakStart, lastChecked, skipDaysInStreak))
        }
    }
}
