import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.io.read
import java.util.UUID

fun main() {

    var rebalanceId = UUID.randomUUID()
    rebalanceId.let{
        checkScopeContext(it)
    }

    var df = DataFrame.read("/home/akshayjain/Downloads/movies.csv")
    val predicate = getStringPredicate("genres")
    df = df.filter(predicate)
    df = df.add (getStringAccessor("new_name_column") ) { getStringAccessor("title")() }
    df = df.remove(getStringAccessor("abradabra"))
    df = df.remove(getStringAccessor("new_name_column"))

//    df = df["security_id", "rebalance_price", "rebalance_units", "market_value", "rebalance_weight", "min_run_locked_since"]

    df = df.select("title", "genres")
    df.print()
    println("B" > "Vehicles")
    println(true xor true xor true xor true)

}

fun checkScopeContext(uuid: UUID):Unit {
    println(uuid)
}

fun getStringPredicate(colName:String): DataRow<*>.(DataRow<*>) -> kotlin.Boolean {
    return { getStringAccessor(colName)() <= "F" }
}

fun getStringAccessor(colName:String):ColumnAccessor<java.lang.String> {
    return column<java.lang.String>(colName)
}