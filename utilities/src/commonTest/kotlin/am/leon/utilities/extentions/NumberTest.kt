package am.leon.utilities.extentions

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberTest {

    @Test
    fun smallFractions_areNotScientific() {
        assertEquals("0.0001", 0.0001.toPlainString())
        assertEquals("0.00001", 0.00001.toPlainString())
        assertEquals("0", 0.000001.toPlainString())
        assertEquals("-0.0001", (-0.0001).toPlainString())
    }

    @Test
    fun plainFractions_passThrough() {
        assertEquals("0.5", 0.5.toPlainString())
        assertEquals("1.25", 1.25.toPlainString())
        assertEquals("123.456", 123.456.toPlainString())
        assertEquals("-123.456", (-123.456).toPlainString())
    }

    @Test
    fun trailingZeros_areTrimmed() {
        assertEquals("1.5", 1.50.toPlainString())
        assertEquals("100.25", 100.250.toPlainString())
    }

    @Test
    fun wholeNumbers_haveNoDecimalPart() {
        assertEquals("0", 0.toPlainString())
        assertEquals("10", 10.toPlainString())
        assertEquals("-42", (-42).toPlainString())
        assertEquals("10", 10.0.toPlainString())
        assertEquals("10000000", 1.0E7.toPlainString())
        assertEquals("9007199254740992", 9007199254740992.0.toPlainString())
    }

    @Test
    fun largeValues_areNotScientific() {
        assertEquals("100000000000000000000", 1.0E20.toPlainString())
        assertEquals("-100000000000000000000", (-1.0E20).toPlainString())
    }

    @Test
    fun nonFiniteValues_doNotCrash() {
        assertEquals("NaN", Double.NaN.toPlainString())
        assertEquals("Infinity", Double.POSITIVE_INFINITY.toPlainString())
        assertEquals("-Infinity", Double.NEGATIVE_INFINITY.toPlainString())
    }

    @Test
    fun otherNumberTypes_work() {
        assertEquals("5", 5L.toPlainString())
        assertEquals("3", 3.toShort().toPlainString())
        assertEquals("2.5", 2.5f.toPlainString())
    }

    @Test
    fun toPlainString_roundsHalfUpToRequestedDecimals() {
        assertEquals("0.12346", 0.123456789.toPlainString())
        assertEquals("2.68", 2.675.toPlainString(decimals = 2))
        assertEquals("2", 1.5.toPlainString(decimals = 0))
        assertEquals("-2", (-1.5).toPlainString(decimals = 0))
    }

    @Test
    fun toPlainString_stripsTrailingZerosAndDecimalPoint() {
        assertEquals("1.1", 1.10.toPlainString())
        assertEquals("2", 2.0.toPlainString())
        assertEquals("0.0001", 0.0001.toPlainString())
        assertEquals("0", 0.000004.toPlainString())
    }

    @Test
    fun toPlainString_doesNotCrashOnNonFinite() {
        assertEquals("NaN", Double.NaN.toPlainString())
        assertEquals("Infinity", Double.POSITIVE_INFINITY.toPlainString())
    }
}