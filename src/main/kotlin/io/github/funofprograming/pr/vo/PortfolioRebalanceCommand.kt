package io.github.funofprograming.pr.vo

import com.fasterxml.jackson.databind.json.JsonMapper
import io.github.funofprograming.pr.configuration.PortfolioConfiguration
import io.github.funofprograming.pr.configuration.PortfolioRebalanceExecutor
import io.github.funofprograming.pr.util.JsonMapperProvider
import io.github.funofprograming.pr.util.fromJson
import io.github.funofprograming.pr.util.registerAllMarketValueCalculatorObjects
import io.github.funofprograming.pr.util.registerAllSecurityWeightCalculatorObjects
import kotlinx.datetime.LocalDate
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.io.*
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic
import java.io.InputStream
import java.nio.charset.Charset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KType

data class PortfolioRebalanceCommand private constructor (
    val portfolioConfiguration: PortfolioConfiguration,
    val rebalanceDate: LocalDate,
    val inputSecurities: DataFrame<*>,
    val lastRebalanceConstituents: DataFrame<*>?,
    val rebalanceId: UUID = UUID.randomUUID()
) {

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    fun execute(): PortfolioRebalance {

        registerAllMarketValueCalculatorObjects()
        registerAllSecurityWeightCalculatorObjects()

        return PortfolioRebalanceExecutor(this).execute()
    }

    public class Builder() {

        private var portfolioConfiguration: PortfolioConfiguration? = null
        private var rebalanceDate: LocalDate? = null
        private var inputSecurities: DataFrame<*>? = null
        private var lastRebalanceConstituents: DataFrame<*>? = null

        fun customizeJsonMapper(jsonMapperBuilder: JsonMapper.Builder): Builder{
            JsonMapperProvider.reset()
            JsonMapperProvider.getJsonMapper(jsonMapperBuilder)
            return this
        }

        fun setPortfolioConfiguration(portfolioConfiguration: PortfolioConfiguration?):Builder {
            this.portfolioConfiguration = portfolioConfiguration
            return this
        }

        fun setPortfolioConfigurationJson(portfolioConfigurationJson:InputStream):Builder {
            portfolioConfiguration = fromJson<PortfolioConfiguration>(portfolioConfigurationJson)
            return this
        }

        fun setRebalanceDate(rebalanceDate:LocalDate):Builder {
            this.rebalanceDate = rebalanceDate
            return this
        }

        fun setInputSecurities(inputSecurities: DataFrame<*>?):Builder {
            this.inputSecurities = inputSecurities
            return this
        }

        fun setInputSecuritiesCsv(inputSecuritiesCsv:InputStream,
                                  delimiter: Char = ',',
                                  header: List<String> = listOf(),
                                  isCompressed: Boolean = false,
                                  colTypes: Map<String, ColType> = mapOf(),
                                  skipLines: Int = 0,
                                  readLines: Int? = null,
                                  duplicate: Boolean = true,
                                  charset: Charset = Charsets.UTF_8,
                                  locale: Locale? = null,
                                  dateTimeFormatter: DateTimeFormatter? = null,
                                  dateTimePattern: String? = null,
                                  nullStrings: Set<String>? = null,
                                  skipTypes: Set<KType>? = null):Builder {

            inputSecurities =
                DataFrame.readCSV(
                    inputSecuritiesCsv,
                    delimiter,
                    header,
                    isCompressed,
                    colTypes,
                    skipLines,
                    readLines,
                    duplicate,
                    charset,
                    ParserOptions(
                        locale,
                        dateTimeFormatter,
                        dateTimePattern,
                        nullStrings,
                        skipTypes,
                    )
                )
            return this
        }

        fun setLastRebalanceConstituents(lastRebalanceConstituents: DataFrame<*>?):Builder {
            this.lastRebalanceConstituents = lastRebalanceConstituents
            return this
        }

        fun setLastRebalanceConstituentsCsv(lastRebalanceConstituentsCsv:InputStream,
                                  delimiter: Char = ',',
                                  header: List<String> = listOf(),
                                  isCompressed: Boolean = false,
                                  colTypes: Map<String, ColType> = mapOf(),
                                  skipLines: Int = 0,
                                  readLines: Int? = null,
                                  duplicate: Boolean = true,
                                  charset: Charset = Charsets.UTF_8,
                                  locale: Locale? = null,
                                  dateTimeFormatter: DateTimeFormatter? = null,
                                  dateTimePattern: String? = null,
                                  nullStrings: Set<String>? = null,
                                  skipTypes: Set<KType>? = null):Builder {

            lastRebalanceConstituents =
                DataFrame.readCSV(
                    lastRebalanceConstituentsCsv,
                    delimiter,
                    header,
                    isCompressed,
                    colTypes,
                    skipLines,
                    readLines,
                    duplicate,
                    charset,
                    ParserOptions(
                        locale,
                        dateTimeFormatter,
                        dateTimePattern,
                        nullStrings,
                        skipTypes,
                    )
                )
            return this
        }

        fun build(): PortfolioRebalanceCommand {

            if(portfolioConfiguration == null || rebalanceDate == null || inputSecurities == null)
                throw IllegalStateException("Cannot build command unless all required params are set")

            return PortfolioRebalanceCommand(portfolioConfiguration!!, rebalanceDate!!, inputSecurities!!, lastRebalanceConstituents)
        }

        /**
         *
         * Copying DataFrame TypeClashTactic here for Java compatibility
         */
        public enum class TypeClashTactic {
            ARRAY_AND_VALUE_COLUMNS,
            ANY_COLUMNS,
        }
    }


}