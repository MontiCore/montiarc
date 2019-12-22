/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

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
   * @see this#getInstance(int)
   */
  public String getInstanceName(int index) {
    return this.getInstance(index).getName();
  }

  /**
   * Returns a list of names of all instances declared in this component instantiation. The list is
   * empty if this component instantiation contains no instance declarations.
   *
   * @return a list of names of all instances declared in this component definition.
   * @see this#getInstanceList()
   */
  public List<String> getInstancesNames() {
    return this.getInstanceList().stream()
      .map(ASTComponentInstance::getName)
      .collect(Collectors.toList());
  }
}