/* (c) https://github.com/MontiCore/monticore */
package dsim.conf

import dsim.comp.IReconfigurable

class FunctionalReconfiguration(override val name: String, override val changeScript: IReconfigurable.() -> Unit) : IReconfiguration
