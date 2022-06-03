/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

/**
 * Input Ports of the enclosing component and outgoing ports of subcomponents
 * may be connected as source-ports of connectors.
 * This interface may not provide methods similar to {@link TargetPort}.
 */
public interface SourcePort extends UndirectedPort {}