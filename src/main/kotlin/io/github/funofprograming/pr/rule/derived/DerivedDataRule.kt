package io.github.funofprograming.pr.rule.derived

import io.github.funofprograming.pr.rule.RegistrableRule
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.util.*

interface DerivedDataRule: RegistrableRule {

    override fun execute(rebalanceId: UUID, securities: DataFrame<*>?): DataFrame<*>? {

        val derivations = getDerivations()
        var securitiesResult = securities
        for(derivation in derivations) {
            securitiesResult = derivation.invoke(rebalanceId, securitiesResult)
        }

        return securitiesResult
    }

    fun getDerivations(): List<(UUID, DataFrame<*>?)->DataFrame<*>?>
}