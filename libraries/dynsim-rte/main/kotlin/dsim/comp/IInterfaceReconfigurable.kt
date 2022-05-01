/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.port.IDataSink
import dsim.port.IDataSource
import dsim.port.IPort

interface IInterfaceReconfigurable {
  val inputPorts: Set<IDataSource>
  val outputPorts: Set<IDataSink>

  fun getInputPort(name: String): IDataSource
  fun getOutputPort(name: String): IDataSink

  fun addInPort(port: IPort)
  fun addOutPort(port: IPort)

  fun removeInPort(port: IDataSource)
  fun removeOutPort(port: IDataSink)
}
