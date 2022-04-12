package dsim.conf

import dsim.comp.IReconfigurable

class EmptyReconfiguration(override val name: String) : IReconfiguration {
    override val changeScript: IReconfigurable.() -> Unit = {}
}