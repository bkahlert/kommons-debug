package com.bkahlert.kommons.debug

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.jvm.reflect

public actual fun Function<*>.renderFunctionTypeTo(out: StringBuilder, simplified: Boolean) {
    when (val kFunction = if (this is KFunction<*>) this else reflect()) {
        is KFunction<*> -> {
            val parametersByKind = kFunction.parameters.groupBy { it.kind }
            parametersByKind.getOrDefault(KParameter.Kind.INSTANCE, emptyList()).forEach { it: KParameter ->
                out.append(renderType(it.type, simplified))
                out.append(".")
            }
            parametersByKind.getOrDefault(KParameter.Kind.EXTENSION_RECEIVER, emptyList()).forEach { it: KParameter ->
                out.append(renderType(it.type, simplified))
                out.append(".")
            }
            kFunction.name.takeUnless { it == "<anonymous>" }?.also { out.append(it) }
            out.append("(")
            parametersByKind.getOrDefault(KParameter.Kind.VALUE, emptyList()).forEachIndexed { index: Int, param: KParameter ->
                if (index > 0) out.append(", ")
                out.append(renderType(param.type, simplified))
            }
            out.append(")")
            out.append(" -> ")
            out.append(renderType(kFunction.returnType, simplified))
        }
        else -> out.append("Function")
    }
}

private fun renderType(type: KType, simplified: Boolean): String =
    if (simplified) type.toString().split('.').last() else type.toString()
