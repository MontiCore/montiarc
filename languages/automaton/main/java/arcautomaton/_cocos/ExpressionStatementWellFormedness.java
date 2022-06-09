/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.ExpressionRootFinder;
import montiarc.util.ArcAutomataError;
import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.mcstatementsbasis._cocos.MCStatementsBasisASTMCBlockStatementCoCo;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks whether expression statements are evaluable expressions, e.g. {@code 4 + 8}. An example for a violation is
 * {@code "Hello" / 2}, as strings must not be divided by integers
 */
public class ExpressionStatementWellFormedness implements MCStatementsBasisASTMCBlockStatementCoCo {

  /**
   * Used to traverse expressions, finding malformed ones.
   */
  protected final IArcTypeCalculator typeCalculator;
  protected final ExpressionRootFinder exprRootFinder;
  protected final ExpressionsBasisTraverser exprRootTraverser;

  public ExpressionStatementWellFormedness(@NotNull IArcTypeCalculator typeCalculator) {
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);

    this.exprRootFinder = new ExpressionRootFinder();
    this.exprRootTraverser = ArcAutomatonMill.inheritanceTraverser();
    this.exprRootTraverser.add4ExpressionsBasis(exprRootFinder);
  }

  protected IArcTypeCalculator getTypeCalculator() {
    return typeCalculator;
  }

  @Override
  public void check(@NotNull ASTMCBlockStatement block) {
    Preconditions.checkNotNull(block);

    if (block instanceof ASTMCJavaBlock) {
      return;
      // We exit early because a JavaBlock itself contains MCBlockStatements which will also be checked by other runs of
      // this coco. By exiting early we avoid printing duplicated outputs.
    }

    exprRootFinder.reset();
    block.accept(exprRootTraverser);
    exprRootFinder.getExpressionRoots().forEach(this::checkWellFormedness);
  }

  protected void checkWellFormedness(@NotNull ASTExpression expression) {
    Preconditions.checkNotNull(expression);
    TypeCheckResult result = this.getTypeCalculator().deriveType(expression);

    if (!result.isPresentResult()) {
      Log.error(ArcAutomataError.MALFORMED_EXPRESSION.format(expression.get_SourcePositionStart()),
        expression.get_SourcePositionStart(), expression.get_SourcePositionEnd()
      );
    }
  }
}
