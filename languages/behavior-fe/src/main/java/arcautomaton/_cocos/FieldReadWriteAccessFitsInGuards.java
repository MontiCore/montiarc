/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._cocos.util.FieldReadWriteAccessFitsInExpressions;
import arcautomaton._visitor.NamesInExpressionsVisitor;
import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionBodyCoCo;
import de.monticore.visitor.ITraverser;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks the direction of assignment and read access to variables and ports in expressions that occur in guards
 */
public class FieldReadWriteAccessFitsInGuards implements SCTransitions4CodeASTTransitionBodyCoCo {

  /**
   * Used to check variable and port access (read and write) in expressions.
   */
  protected final FieldReadWriteAccessFitsInExpressions accessChecker;

  /**
   * default constructor that is sufficient for basic expression language components
   */
  public FieldReadWriteAccessFitsInGuards(){
    this(new NamesInExpressionsVisitor(), ArcAutomatonMill.inheritanceTraverser());
  }

  /**
   * This is a fully parameterized constructor that facilitates registering new expression types by passing another
   * visitor and traverser
   * @param visitor The visitor that is used to find variable names and the kind of access (read / write / both, e.g. in
   *               x++) on them in expressions.
   * @param traverser The traverser for the visitor.
   */
  public FieldReadWriteAccessFitsInGuards(@NotNull NamesInExpressionsVisitor visitor, @NotNull ITraverser traverser) {
    Preconditions.checkNotNull(visitor);
    Preconditions.checkNotNull(traverser);

    this.accessChecker = new FieldReadWriteAccessFitsInExpressions(visitor, traverser);
  }

  @Override
  public void check(@NotNull ASTTransitionBody node) {
    Preconditions.checkNotNull(node);
    if(node.isPresentPre()) {
      accessChecker.checkVarAccessIn(node.getPre());
    }
  }


}