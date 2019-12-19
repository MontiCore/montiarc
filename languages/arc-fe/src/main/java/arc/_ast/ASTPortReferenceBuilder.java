/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

import com.google.common.base.Preconditions;

import java.util.Arrays;

/**
 * Extends the {@link ASTPortReferenceBuilderTOP} with utility functions for easy constructor of
 * {@link ASTPortReference} nodes.
 */
public class ASTPortReferenceBuilder extends ASTPortReferenceBuilderTOP {

  protected ASTPortReferenceBuilder() {
    super();
  }

  /**
   * Sets the name to be used by this builder. The provided {@code String} argument is expected
   * to be not null and a qualified name (parts separated by dots ".").
   *
   * @param qualifiedName qualified name of the referenced port
   * @return the current builder
   */
  public ASTPortReferenceBuilder setQualifiedName(String qualifiedName) {
    Preconditions.checkNotNull(qualifiedName);
    this.setQualifiedName(ArcMill.mCQualifiedNameBuilder()
        .setPartList(Arrays.asList(qualifiedName.split("\\."))).build());
    return this.realBuilder;
  }
}