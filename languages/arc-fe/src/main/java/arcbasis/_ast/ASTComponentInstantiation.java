/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents component instantiation. Extends {@link ASTComponentInstantiationTOP} with utility functions
 * for easy access.
 */
public class ASTComponentInstantiation extends ASTComponentInstantiationTOP {

  protected ASTComponentInstantiation() {
    super();
  }

  /**
   * Returns the name of the instance at the specified position in the instances declared in this
   * component instantiation.
   *
   * @param index index of the instance whose name to return
   * @return the name of the instance at the specified position
   * @see this#getComponentInstance(int)
   */
  public String getInstanceName(int index) {
    return this.getComponentInstance(index).getName();
  }

  /**
   * Returns a list of names of all instances declared in this component instantiation. The list is
   * empty if this component instantiation contains no instance declarations.
   *
   * @return a list of names of all instances declared in this component definition.
   * @see this#getComponentInstanceList()
   */
  public List<String> getInstancesNames() {
    return this.getComponentInstanceList().stream()
      .map(ASTComponentInstance::getName)
      .collect(Collectors.toList());
  }
}