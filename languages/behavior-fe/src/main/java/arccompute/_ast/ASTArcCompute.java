/* (c) https://github.com/MontiCore/monticore */
package arccompute._ast;

import arcbehaviorbasis._symboltable.IArcBehaviorBasisScope;
import arccompute._symboltable.IArcComputeScope;
import com.google.common.base.Preconditions;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.mcstatementsbasis._symboltable.IMCStatementsBasisScope;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Represents a compute statement. Extends {@link ASTArcComputeTOP} with
 * improved precondition checks.
 */
public class ASTArcCompute extends ASTArcComputeTOP {

  protected ASTArcCompute() { super(); }

  protected ASTArcCompute(@NotNull ASTMCBlockStatement blockStatement) {
    Preconditions.checkArgument(blockStatement != null);
    this.mCBlockStatement = blockStatement;
  }

  /**
   * @throws IllegalArgumentException
   *         In case that the {@code enclosingScope} argument is not an instance
   *         of {@link IArcComputeScope}.
   *
   * @see ASTArcCompute#setEnclosingScope(IArcComputeScope)
   */
  @Override
  public void setEnclosingScope(@NotNull IArcBehaviorBasisScope enclosingScope) {
    Preconditions.checkArgument(enclosingScope instanceof IArcComputeScope);
    super.setEnclosingScope(enclosingScope);
  }

  /**
   * @throws IllegalArgumentException
   *         In case that the {@code enclosingScope} argument is not an instance
   *         of {@link IArcComputeScope}.
   *
   * @see ASTArcCompute#setEnclosingScope(IArcComputeScope)
   */
  @Override
  public void setEnclosingScope(@NotNull IMCStatementsBasisScope enclosingScope) {
    Preconditions.checkArgument(enclosingScope instanceof IArcComputeScope);
    super.setEnclosingScope(enclosingScope);
  }

  @Override
  public void setMCBlockStatement(@NotNull ASTMCBlockStatement blockStatement) {
    Preconditions.checkArgument(blockStatement != null);
    super.setMCBlockStatement(blockStatement);
  }
}