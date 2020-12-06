/* (c) https://github.com/MontiCore/monticore */
package arccompute._ast;

import com.google.common.base.Preconditions;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Extends {@link ASTArcComputeBuilderTOP} with improved precondition checks and
 * utility functions for easy construction of {@link ASTArcCompute} nodes.
 */
public class ASTArcComputeBuilder extends ASTArcComputeBuilderTOP {

  @Override
  public ASTArcCompute build() {
    Preconditions.checkState(isValid());
    return super.build();
  }

  @Override
  public ASTArcComputeBuilder setMCBlockStatement(@NotNull ASTMCBlockStatement blockStatement) {
    Preconditions.checkArgument(blockStatement != null);
    this.mCBlockStatement = blockStatement;
    return this.realBuilder;
  }
}