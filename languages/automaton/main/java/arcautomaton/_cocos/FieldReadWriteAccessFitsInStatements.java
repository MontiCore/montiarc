/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._cocos.util.FieldReadWriteAccessFitsInExpressions;
import arcautomaton._visitor.ExpressionRootFinder;
import arcautomaton._visitor.NamesInExpressionsVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.mcstatementsbasis._cocos.MCStatementsBasisASTMCBlockStatementCoCo;
import de.monticore.visitor.ITraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Set;

/**
 * analog to {@link FieldReadWriteAccessFitsInGuards} but for {@link de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement} and actions
 */
public class FieldReadWriteAccessFitsInStatements implements MCStatementsBasisASTMCBlockStatementCoCo {

  /**
   * Used to check variable and port access (read and write) in expressions.
   */
  protected final FieldReadWriteAccessFitsInExpressions accessChecker;

  protected final ExpressionRootFinder exprRootFinder;
  protected final ExpressionsBasisTraverser exprRootTraverser;

  /**
   * analog to {@link FieldReadWriteAccessFitsInGuards#FieldReadWriteAccessFitsInGuards()
   * the other coco's default constructor}
   */
  public FieldReadWriteAccessFitsInStatements(){
    this(new NamesInExpressionsVisitor(), ArcAutomatonMill.inheritanceTraverser());
  }

  /**
   * analog to {@link FieldReadWriteAccessFitsInGuards#FieldReadWriteAccessFitsInGuards(NamesInExpressionsVisitor, ITraverser)
   * the other coco's parameterized constructor}
   */
  public FieldReadWriteAccessFitsInStatements(@NotNull NamesInExpressionsVisitor visitor, @NotNull ITraverser traverser) {
    Preconditions.checkNotNull(visitor);
    Preconditions.checkNotNull(traverser);

    this.accessChecker = new FieldReadWriteAccessFitsInExpressions(visitor, traverser);
    this.exprRootFinder = new ExpressionRootFinder();
    this.exprRootTraverser = ArcAutomatonMill.inheritanceTraverser();
    this.exprRootTraverser.add4ExpressionsBasis(exprRootFinder);
  }

  @Override
  public void check(@NotNull ASTMCBlockStatement block) {
    Preconditions.checkNotNull(block);

    if(block instanceof ASTMCJavaBlock) {
      return;
      // We exit early because a JavaBlock itself contains MCBlockStatements which will also be checked by other runs of
      // this coco. By exiting early we avoid printing duplicated outputs.
    }

    // Extract every top level expression from the transition action
    exprRootFinder.reset();
    block.accept(exprRootTraverser);
    Set<ASTExpression> rootExprs = exprRootFinder.getExpressionRoots();

    rootExprs.forEach(accessChecker::checkVarAccessIn);
  }
}