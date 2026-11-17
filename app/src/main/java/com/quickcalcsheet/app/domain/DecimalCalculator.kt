package com.quickcalcsheet.app.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object DecimalCalculator {
    private const val MAX_DECIMAL_SCALE = 10

    private val displaySymbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }

    /**
     * Parses a user-entered string (supporting '.' or ',' as decimal separator) into BigDecimal.
     * Returns null if empty or incomplete/invalid.
     */
    fun parseOrNull(raw: String): BigDecimal? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty() || trimmed == "." || trimmed == ",") return null
        val normalized = trimmed.replace(',', '.')
        val sanitized = if (normalized.startsWith(".")) "0$normalized" else normalized
        return runCatching { BigDecimal(sanitized) }.getOrNull()
    }

    /**
     * Computes exact product: Col 1 * Col 2 using BigDecimal (no floating-point drift).
     */
    fun multiply(col1Raw: String, col2Raw: String): BigDecimal? {
        val a = parseOrNull(col1Raw) ?: return null
        val b = parseOrNull(col2Raw) ?: return null
        return a.multiply(b)
    }

    /**
     * Sums a sequence of raw cell strings using exact BigDecimal addition.
     */
    fun sumRaw(values: Iterable<String>): BigDecimal {
        var acc = BigDecimal.ZERO
        for (raw in values) {
            val parsed = parseOrNull(raw) ?: continue
            acc = acc.add(parsed)
        }
        return acc
    }

    /**
     * Sums a sequence of computed BigDecimal values.
     */
    fun sumDecimals(values: Iterable<BigDecimal?>): BigDecimal {
        var acc = BigDecimal.ZERO
        for (value in values) {
            if (value != null) {
                acc = acc.add(value)
            }
        }
        return acc
    }

    /**
     * Formats BigDecimal cleanly in US English style without trailing zero artifacts.
     */
    fun formatResult(value: BigDecimal?): String {
        if (value == null) return ""
        if (value.compareTo(BigDecimal.ZERO) == 0) return "0"

        val rounded = value.setScale(MAX_DECIMAL_SCALE, RoundingMode.HALF_UP)
            .stripTrailingZeros()
        val scale = rounded.scale().coerceAtLeast(0)
        val pattern = buildString {
            append("#,##0")
            if (scale > 0) {
                append('.')
                repeat(scale) { append('#') }
            }
        }
        val formatter = DecimalFormat(pattern, displaySymbols)
        return formatter.format(rounded)
    }
}
