package com.bkahlert.kommons

/** The running program. */
public expect object Program {

    /** The name of running program. */
    public val name: String?

    /** The group name of running program. */
    public val group: String?

    /** The version of the running program, or `null` if it can't be determined. */
    public val version: SemanticVersion?

    /** Whether this program is running in debug mode. */
    public val isDebugging: Boolean

    /** Registers the specified [handler] as to be called when this program is about to stop. */
    public fun onExit(handler: () -> Unit)
}
