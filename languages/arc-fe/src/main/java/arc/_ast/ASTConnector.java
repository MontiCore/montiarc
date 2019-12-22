/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a connector. Extends {@Link ASTConnectorTOP} with utility functionality
 * for easy access.
 */
public class ASTConnector extends ASTConnectorTOP {

  protected ASTConnector() {
    super();
  }

  /**
   * Returns the name of the source of this connector.
   *
   * @return the name of the source of this connector
   * @see this#getSource()
   */
  public String getSourceName() {
    return this.getSource().getQualifiedName().getQName();
  }

  /**
   * Returns a list of names of all targets of this connector. The list is empty if the connector
   * contains no targets.
   *
   * @return a list of names of all targets of this connector
   * @see this#getTargetList()
   */
  public List<String> getTargetsNames() {
    return this.getTargetList().stream()
        .map(astPortReference -> astPortReference.getQualifiedName().getQName())
        .collect(Collectors.toList());
  }
}