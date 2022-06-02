/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

/**
 * Input Ports of the enclosing component and outgoing ports of subcomponents
 * may be connected as source-ports of connectors.
 * This interface may not provide methods similar to {@link TargetPort},
 * because that would not be implementable, as it might cause clashes in {@link dsim.port.IPort}.
 */
public interface SourcePort extends UndirectedPort {}