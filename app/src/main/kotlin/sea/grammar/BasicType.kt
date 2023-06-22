package sea.grammar

private typealias KInt = Int

enum class BasicType(val priority: KInt) {
    Bool(1), Char(2),
    Nat8(10), Int8(11),
    Nat16(12), Int16(13),
    Nat(14), Int(15),
    Nat32(16), Int32(17),
    Nat64(18), Int64(19),
    Real32(20), Real(22), Real64(24),
    Cplex32(30), Cplex(32), Cplex64(34),
    Any(0), Unit(-1), Str(-1);

    val isNumber = priority in 1..34
    val isInteger = priority in 1..19
    val canOperateOn get() = this !in listOf(Any, Unit)

    override fun toString() = name

    fun implicitlyCastTo(type: BasicType): BasicType? {
        if(this == type) return type
        if(priority < 0) return null
        return if(priority < type.priority) type else null
    }

    fun implicitlyCastUpTo(type: BasicType): BasicType? {
        if(this == type) return type
        if(priority < 0) return null
        return if(priority < type.priority) type else this
    }

    fun makeIntSigned() = when(this) {
        Nat8 -> Int8
        Nat16 -> Int16
        Nat -> Int
        Nat32 -> Int32
        Nat64 -> Int64
        else -> null
    }

    companion object {
        fun operateOn(t1: BasicType, t2: BasicType): BasicType? {
            if(!t1.canOperateOn || !t2.canOperateOn) return null
            return t1.implicitlyCastTo(t2)?:t2.implicitlyCastTo(t1)
        }
    }
}
