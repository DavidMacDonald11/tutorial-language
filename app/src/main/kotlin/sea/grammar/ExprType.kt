package sea.grammar

import kotlin.math.max

class ExprType(
    val base: Base = Base.Unit,
    val dynamic: Boolean = false,
    val optional: Boolean = false
) {
    enum class Base {
        Any, Unit, Bool, Char,
        Nat8, Nat16, Nat32, Nat64, NatMax,
        Int8, Int16, Int32, Int64, IntMax,
        Real32, Real64, RealMax,
        Cplex32, Cplex64, CplexMax,
        Str;

        val size = when {
            "8" in name -> 8u
            "16" in name -> 16u
            "32" in name -> 32u
            "64" in name -> 64u
            "Max" in name -> UInt.MAX_VALUE
            else -> null
        }
    }

    val isNatural = "Nat" in base.name
    val isInteger = isNatural || "Int" in base.name
    val isReal = isInteger || "Real" in base.name
    val isNumber = isReal || "Cplex" in base.name
    val canOperateOn = !optional && base !in listOf(Base.Any, Base.Unit)

    override fun toString(): String {
        var result = "${base.name}"

        if(dynamic) result = "#$result"
        if(optional) result = "$result?"

        return result
    }

    fun coerceTo(type: ExprType): ExprType? {
        if(optional && !type.optional) return null
        if(base == type.base) return type
        if(!isNumber || !type.isNumber) return null

        val smaller = base.size!! <= type.base.size!!

        if(type.isInteger) return if(isInteger && smaller) type else null
        if(type.isReal) return if(isInteger || isReal && smaller) type else null
        return if(isReal || smaller) type else null
    }

    companion object {
        fun operateOn(t1: ExprType, t2: ExprType? = null): ExprType? {
            if(t2 == null) return if(t1.canOperateOn) t1 else null
            if(!t1.canOperateOn || !t2.canOperateOn) return null
            if(t1.isInteger && t2.isInteger) return mergeIntegers(t1, t2)
            return t1.coerceTo(t2)?:t2.coerceTo(t1)
        }

        fun mergeIntegers(t1: ExprType, t2: ExprType): ExprType {
            val size = max(t1.base.size!!, t2.base.size!!)

            val bases = when(size) {
                8u -> Pair(Base.Nat8, Base.Int8)
                16u -> Pair(Base.Nat16, Base.Int16)
                32u -> Pair(Base.Nat32, Base.Int32)
                64u -> Pair(Base.Nat64, Base.Int64)
                else -> Pair(Base.NatMax, Base.IntMax)
            }

            val isNat = t1.isNatural && t2.isNatural
            return ExprType(if(isNat) bases.first else bases.second)
        }
    }
}
