/* (c) https://github.com/MontiCore/monticore */
package dsim.reactions

import dsim.comp.ISubcomponent
import openmodeautomata.runtime.*

abstract class AComponentForReactions(name: String) : AComponentWithConnectors(name) {

  override fun getInputPorts(): Collection<SourcePort> = inputPorts.map { SourceInReaction(it) }
  override fun getOutputPorts(): Collection<TargetPort> = outputPorts.map { TargetInReaction(it) }
  override fun getSourcePorts(): Collection<SourcePort> = getInputPorts() + subcomponents.flatMap { it.outputPorts }.map { SourceInReaction(it) }
  override fun getTargetPorts(): Collection<TargetPort> = getOutputPorts() + subcomponents.flatMap { it.inputPorts }.map { TargetInReaction(it) }
  override fun getAllPorts(): Collection<PortElement> = sourcePorts + targetPorts
  override fun getUnconnectedPorts(): Collection<PortElement> = allPorts.filterNot { it.isConnected }
  override fun getSubcomponents(): Collection<SubcomponentInstance> = subcomponents.map { SubcomponentInReaction(it) }
  override fun decorate(subcomponent: ISubcomponent) = SubcomponentInReaction(subcomponent)

  /**
   * implements [SubcomponentBuilder.build]
   */
  fun ISubcomponent.build(): SubcomponentInstance {
    create(this)
    return decorate(this)
  }

  /**
   * implements [SubcomponentInstance]
   */
  inner class SubcomponentInReaction(private val instance: ISubcomponent) : SubcomponentInstance {
    override fun delete() = delete(instance)
    override fun getName(): String = instance.name
    override fun getPorts(): Collection<PortElement> = inputPorts + outputPorts
    override fun getInputPorts(): Collection<TargetPort> = instance.inputPorts.map { TargetInReaction(it) }
    override fun getOutputPorts(): Collection<SourcePort> = instance.outputPorts.map { SourceInReaction(it) }
    override fun deactivate() = deactivate(instance)
    override fun activate() = activate(instance)
    override fun isActive() = this@AComponentForReactions.isActive(instance)
  }

}