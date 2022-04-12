package dsim.modeautomata

import dsim.conf.ChangeScript
import dsim.port.IDataSource

data class Transition<T:IGuardInterface>(
        val source: String?,
        val target:String,
        val guard:(T) -> Boolean,
        val reaction:ChangeScript,
        val ports: Collection<IDataSource>
        )