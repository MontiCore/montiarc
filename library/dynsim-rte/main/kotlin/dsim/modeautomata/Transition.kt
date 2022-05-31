package dsim.modeautomata

import dsim.conf.ChangeScript
import dsim.port.IDataSource
import dsim.sched.util.IEvent

data class Transition<T:IGuardInterface>(
        val source: String?,
        val target:String,
        val guard:(T) -> Boolean,
        val reaction:T.() -> ChangeScript,
        val ports: Collection<IDataSource>
        )