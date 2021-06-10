/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton._visitor.ExpressionRootFinder;
import arcautomaton._visitor.NamesInExpressionsVisitor;
import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionActionCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * analog to {@link FieldReadWriteAccessFitsInGuards} but for reactions and actions
 */
public class FieldReadWriteAccessFitsInReactions implements SCTransitions4CodeASTTransitionActionCoCo {

  /**
   * analog to {@link FieldReadWriteAccessFitsInGuards#visitor}
   */
  protected final NamesInExpressionsVisitor visitor;

  /**
   * analog to {@link FieldReadWriteAccessFitsInGuards#FieldReadWriteAccessFitsInGuards()
   * the other coco's default constructor}
   */
  public FieldReadWriteAccessFitsInReactions(){
    this(new NamesInExpressionsVisitor());
  }

  /**
   * analog to {@link FieldReadWriteAccessFitsInGuards#FieldReadWriteAccessFitsInGuards(NamesInExpressionsVisitor)
   * the other coco's parameterized constructor}
   */
  public FieldReadWriteAccessFitsInReactions(@NotNull NamesInExpressionsVisitor visitor) {
    Preconditions.checkNotNull(visitor);
    this.visitor = Preconditions.checkNotNull(visitor);
  }

  @Override
  public void check(@NotNull ASTTransitionAction action) {
    Preconditions.checkNotNull(action);
    // use the other coco because that already has some infrastructure to check expressions
    FieldReadWriteAccessFitsInGuards checker = new FieldReadWriteAccessFitsInGuards(visitor);
    // and apply that coco to every top-level-expression in this block statement
    action.accept(new ExpressionRootFinder(checker::checkReferencesIn).createTraverser());
  }
}