package io.github.funofprograming.pr.vo

data class SecurityWeightCapperWithParams(
    var securityWeightCapperId:String,
    var capperParams:Map<String, Any>? = null
)
