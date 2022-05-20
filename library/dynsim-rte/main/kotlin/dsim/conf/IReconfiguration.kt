/* (c) https://github.com/MontiCore/monticore */
package dsim.conf

import dsim.comp.IReconfigurable

/**
 * Holds information about a reconfiguration via a change script
 */
interface IReconfiguration {

  /**
   * name of the mode
   */
  val name: String

  /**
   * actions to take to enter the mode
   */
  val changeScript: IReconfigurable.() -> Unit
}
