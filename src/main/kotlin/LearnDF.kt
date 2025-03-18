import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.io.read

fun main() {

    var df = DataFrame.read("/home/akshayjain/Downloads/movies.csv")
    val predicate = getStringPredicate("genres")
    df = df.filter(predicate)
    df = df.add (getStringAccessor("new_name_column") ) { getStringAccessor("title")() }
    df = df.remove(getStringAccessor("abradabra"))
    df = df.remove(getStringAccessor("new_name_column"))
    df.print()
    println("B" > "Vehicles")
    println(true xor true xor true xor true)

}

fun getStringPredicate(colName:String): DataRow<*>.(DataRow<*>) -> kotlin.Boolean {
    return { getStringAccessor(colName)() <= "F" }
}

fun getStringAccessor(colName:String):ColumnAccessor<java.lang.String> {
    return column<java.lang.String>(colName)
}