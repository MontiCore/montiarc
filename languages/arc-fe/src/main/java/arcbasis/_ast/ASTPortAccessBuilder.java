/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import com.google.common.base.Preconditions;

/**
 * Extends the {@link ASTPortAccessBuilderTOP} with utility functions for easy constructor of
 * {@link ASTPortAccess} nodes.
 */
public class ASTPortAccessBuilder extends ASTPortAccessBuilderTOP {

  public ASTPortAccessBuilder() {
    super();
  }

  /**
   * Sets the name to be used by this builder. The provided {@code String} argument is expected
   * to be not null and a qualified name (parts separated by dots ".").
   *
   * @param portAccess qualified name of the referenced port
   * @return the current builder
   */
  public ASTPortAccessBuilder setQualifiedName(String portAccess) {
    Preconditions.checkArgument(portAccess != null);
    Preconditions.checkArgument(portAccess.split("\\.").length < 3);
    if (portAccess.split("\\.").length == 1) {
      this.setPort(portAccess);
    } else if (portAccess.split("\\.").length == 2) {
      this.setComponent(portAccess.split("\\.")[0]);
      this.setPort(portAccess.split("\\.")[1]);
    }
    return this.realBuilder;
  }

}