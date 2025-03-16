import io.github.funofprograming.context.Key
import io.github.funofprograming.context.KeyType
import io.github.funofprograming.pr.rule.LoopPortfolioRule
import io.github.funofprograming.pr.rule.RelaxationCondition
import io.github.funofprograming.pr.vo.PortfolioRebalance
import io.github.funofprograming.pr.vo.PortfolioRebalanceCommand
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.math.MathContext
import java.util.concurrent.BlockingDeque

val DEFAULT_FNV_PRECISION:MathContext = MathContext.DECIMAL64
val REBAL_CMD_KEY: Key<PortfolioRebalanceCommand> = Key.of("PortfolioRebalanceCommand", PortfolioRebalanceCommand::class.java)
val REBAL_INPUT_SECURITIES_KEY: Key<DataFrame<*>> = Key.of("RebalanceInputSecurities", DataFrame::class.java)
val REBAL_OUTPUT_KEY: Key<PortfolioRebalance> = Key.of("RebalanceOutput", PortfolioRebalance::class.java)
val REBAL_LOOP_RULE_STATES_KEY: Key<BlockingDeque<LoopPortfolioRule.LoopState>> = Key.of("LoopRuleStates", KeyType.of(BlockingDeque::class.java))
val REBAL_RELAXATION_COND_KEY: Key<RelaxationCondition> = Key.of("RebalanceRelaxationCondition", RelaxationCondition::class.java)
