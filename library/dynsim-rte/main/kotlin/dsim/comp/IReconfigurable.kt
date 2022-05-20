/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.port.*
import dsim.port.util.Connector

/**
 * Exposes all actions that can be taken to reconfigure a given component.
 * Inspiration for what should be able to be done from Heim 16: Retrofitting...
 */
interface IReconfigurable : IInterfaceReconfigurable {
  /** Allows getting subcomponents including base- and targetPorts. */
  val subcomponents: Set<ISubcomponent>
  fun getSubcomponent(name: String): ISubcomponent

  // Changes to Subcomponents=================================================

  /** Allows activating the  given subcomponent, if already present */
  fun activate(comp: ISubcomponent?)
  fun deactivate(comp: ISubcomponent?)
  fun deactivateAll()

  /**
   * adds and activates the given subcomponent
   * @param comp component to add
   * @param permanent temporary components are removed implicitly at certain times, permanent ones are not
   */
  fun create(comp: ISubcomponent, permanent:Boolean = true)
  fun delete(comp: ISubcomponent)
  fun deleteAll()

  // Changes to Connectors====================================================

  /** Establishes a connector if allowed */
  fun connect(base: IDataSource?, target: IDataSink?, permanent:Boolean = true)
  fun disconnect(base: IDataSource?, target: IDataSink?)
  fun connect(conn: Connector, permanent:Boolean = true)
  fun disconnect(conn: Connector)

  fun disconnectAll()
}
