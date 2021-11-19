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
   * Allows instantiating a given Subcomponent for the duration of the
   * configuration.
   * Deleted after.
   */
  fun temp(comp: ISubcomponent)

  /**
   * Allows permanently creating and deleting Subcomponents.
   */
  fun create(comp: ISubcomponent)
  fun delete(comp: ISubcomponent)
  fun deleteAll()

  // Changes to Connectors====================================================

  /** Establishes a connector if allowed */
  fun connect(base: IDataSource?, target: IDataSink?)
  fun disconnect(base: IDataSource?, target: IDataSink?)
  fun connect(conn: Connector)
  fun disconnect(conn: Connector)

  fun disconnectAll()
}
