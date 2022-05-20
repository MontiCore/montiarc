/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

public interface SubcomponentBuilder extends NamedArchitectureElement {

  /**
   * starts this subcomponent and adds it to the containing component type.
   */
  SubcomponentInstance build();

}