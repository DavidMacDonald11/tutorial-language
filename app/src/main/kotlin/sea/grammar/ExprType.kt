package sea.grammar

class ExprType(
    val bType: BasicType = BasicType.Unit,
    val dynamic: Boolean = false,
    val optional: Boolean = false
) {
    override fun toString(): String {
        var result = "$bType"

        if(dynamic) result = "#$result"
        if(optional) result = "$result?"

        return result
    }

    val canOperateOn = !optional && bType.canOperateOn

    fun copy(
        bType: BasicType? = null,
        dynamic: Boolean? = null,
        optional: Boolean? = null
    ) = ExprType(
        bType?:this.bType,
        dynamic?:this.dynamic,
        optional?:this.optional
    )

    fun implicitlyCastTo(type: ExprType): ExprType? {
        if(optional && !type.optional) return null
        val bType = bType.implicitlyCastTo(type.bType)
        return bType?.let{copy(bType)}
    }

    fun implicitlyCastUpTo(type: ExprType): ExprType? {
        if(optional && !type.optional) return null
        val bType = bType.implicitlyCastUpTo(type.bType)
        return bType?.let{copy(bType)}
    }

    fun implicityCastUpTo(type: BasicType) = implicitlyCastUpTo(copy(type))
    fun makeIntSigned() = copy(bType.makeIntSigned())

    companion object {
        fun operateOn(t1: ExprType, t2: ExprType? = null): ExprType? {
            if(t2 == null) return if(t1.canOperateOn) t1 else null
            if(!t1.canOperateOn || !t2.canOperateOn) return null
            return t1.implicitlyCastTo(t2)?:t2.implicitlyCastTo(t1)
        }
    }
}
