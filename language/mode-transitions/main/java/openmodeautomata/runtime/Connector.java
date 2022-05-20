/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

public interface Connector {

  /**
   * @return the connected port
   */
  SourcePort getSource();

  /**
   * @return the connected port
   */
  TargetPort getTarget();

  /**
   * @return the former Source
   */
  SourcePort setSource(SourcePort newSource);

  /**
   * @param newTarget a port which is not connected yet
   * @return the former Target
   */
  TargetPort setTarget(TargetPort newTarget);

  /**
   * Disassembles both connectors and reconnects them crosswise
   * Switches the start of both connectors, while leaving their ends untouched
   */
  void crossWith(Connector other);

  /**
   * @return the connected component, if it is not the containing component type
   */
  SubcomponentInstance getSourceComponent();

  /**
   * @return the connected component, if it is not the containing component type
   */
  SubcomponentInstance getTargetComponent();

  /**
   * @return true, if the connector starts at a port of the component type's interface
   */
  boolean isInput();

  /**
   * @return true, if the connector ends at a port of the component type's interface
   */
  boolean isOutput();

  /**
   * @return true, if the connector only has ports of subcomponents of the component type
   */
  boolean isHidden();

  /**
   * deletes this element
   */
  void delete();
}