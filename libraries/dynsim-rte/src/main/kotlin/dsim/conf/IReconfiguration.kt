/* (c) https://github.com/MontiCore/monticore */
package dsim.conf

import dsim.comp.IReconfigurable

/**
 * Holds information about a reconfiguration via a change script
 */
interface IReconfiguration {
  val name: String

  val changeScript: IReconfigurable.() -> Unit
}
