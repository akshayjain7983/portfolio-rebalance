package io.github.funofprograming.pr.configuration

import DEFAULT_FNV_PRECISION
import java.math.BigDecimal
import java.math.MathContext

typealias FNV = FinancialNumericalValue

class FinancialNumericalValue : Number {

    val valueNominal: BigDecimal
    val inflationAdjusted:Boolean?
    val valueReal: BigDecimal?
        get() = if (this.inflationAdjusted == true) field else this.valueNominal

    private constructor(inflationAdjusted:Boolean?, valueNominal: BigDecimal, valueReal: BigDecimal?) {
        this.valueNominal = valueNominal
        this.inflationAdjusted = inflationAdjusted
        this.valueReal = valueReal
    }

    companion object{

        fun of(inflationAdjusted:Boolean?, valueNominal: BigDecimal, valueReal: BigDecimal?): FinancialNumericalValue = FinancialNumericalValue(inflationAdjusted, valueNominal, valueReal)

        fun of(valueNominal: BigDecimal) : FinancialNumericalValue = FinancialNumericalValue(false, valueNominal, null)

        fun of(other: FinancialNumericalValue):FinancialNumericalValue = FinancialNumericalValue(other.inflationAdjusted, other.valueNominal, other.valueReal)
    }

    operator fun plus(augend: FinancialNumericalValue): FinancialNumericalValue {

        val valNominal:BigDecimal = valueNominal.add(augend.valueNominal)
        val inflationAdjusted:Boolean? = inflationAdjusted?.or(augend.inflationAdjusted ?: false)
        val valReal:BigDecimal? = if (inflationAdjusted == true && valueReal != null && valNominal != null) valueReal?.add(augend.valueReal) else null
        return of(inflationAdjusted, valNominal, valReal)
    }

    operator fun minus(subtrahend: FinancialNumericalValue): FinancialNumericalValue {

        val valNominal:BigDecimal = valueNominal.subtract(subtrahend.valueNominal)
        val inflationAdjusted:Boolean? = inflationAdjusted?.or(subtrahend.inflationAdjusted ?: false)
        val valReal:BigDecimal? = if (inflationAdjusted == true && valueReal != null && valNominal != null) valueReal?.subtract(subtrahend.valueReal) else null
        return of(inflationAdjusted, valNominal, valReal)
    }

    operator fun times(multiplicand: FinancialNumericalValue): FinancialNumericalValue = times(multiplicand, DEFAULT_FNV_PRECISION)

    fun times(multiplicand: FinancialNumericalValue, precision: MathContext): FinancialNumericalValue {

        val valNominal:BigDecimal = valueNominal.multiply(multiplicand.valueNominal, precision)
        val inflationAdjusted:Boolean? = inflationAdjusted?.or(multiplicand.inflationAdjusted ?: false)
        val valReal:BigDecimal? = if (inflationAdjusted == true && valueReal != null && valNominal != null) valueReal?.multiply(multiplicand.valueReal, precision) else null
        return of(inflationAdjusted, valNominal, valReal)
    }

    operator fun div(divisor: FinancialNumericalValue): FinancialNumericalValue = div(divisor, DEFAULT_FNV_PRECISION)

    fun div(divisor: FinancialNumericalValue, precision: MathContext): FinancialNumericalValue {
        val valNominal:BigDecimal = valueNominal.divide(divisor.valueNominal, precision)
        val inflationAdjusted:Boolean? = inflationAdjusted?.or(divisor.inflationAdjusted ?: false)
        val valReal:BigDecimal? = if (inflationAdjusted == true && valueReal != null && valNominal != null) valueReal?.divide(divisor.valueReal, precision) else null
        return of(inflationAdjusted, valNominal, valReal)
    }

    /**
     * Returns the value of this number as a [Byte], which may involve rounding or truncation.
     */
    override fun toByte(): Byte = valueNominal.toByte()

    /**
     * Returns the value of this number as a [Double], which may involve rounding.
     */
    override fun toDouble(): Double = valueNominal.toDouble()

    /**
     * Returns the value of this number as a [Float], which may involve rounding.
     */
    override fun toFloat(): Float = valueNominal.toFloat()

    /**
     * Returns the value of this number as an [Int], which may involve rounding or truncation.
     */
    override fun toInt(): Int = valueNominal.toInt()

    /**
     * Returns the value of this number as a [Long], which may involve rounding or truncation.
     */
    override fun toLong(): Long = valueNominal.toLong()

    /**
     * Returns the value of this number as a [Short], which may involve rounding or truncation.
     */
    override fun toShort(): Short = valueNominal.toShort()
}