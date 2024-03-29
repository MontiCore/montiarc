/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._visitor;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsHandler;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.visitor.ITraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Visitor to search name usage in expressions.
 * This class can be extended to use react to more different kinds of expressions. To do so, the extending class should
 * <ul>
 *   <li> implement the additional interfaces </li>
 *   <li> extend the {@link #registerTo(ITraverser) registration} </li>
 *   <li> set the variable {@link #current} to the respective {@link VarAccessKind value} in the <code>visit</code>-methods </li>
 *   <li> set {@link #current} back to <code>null</code> in the corresponding <code>endVisit</code>-methods </li>
 *   <li> expressions that imply a {@link VarAccessKind#DEFAULT_READ read-only-access} do not need explicit treatment, as that is the default </li>
 * </ul>
 */
public class NamesInExpressionsVisitor implements AssignmentExpressionsVisitor2, AssignmentExpressionsHandler, CommonExpressionsVisitor2, ExpressionsBasisVisitor2 {

  /**
   * Qualifies the type of access to a variable in a certain context.
   * {@link #OVERWRITE}: new values are assigned to the variable, ignoring the variable's previous values.
   * {@link #UPDATE}: the content of a variable is read and then used to evaluate a new value that is then assigned to the variable.
   * {@link #DEFAULT_READ}: the values of a variable are just read, without reassigning a new value to the variable.
   */
  public enum VarAccessKind {
    OVERWRITE, DEFAULT_READ, UPDATE;

    /**
     * Whether the access performs a mutation. Is true for {@link #OVERWRITE} and {@link #UPDATE}
     */
    public boolean performsMutation() {
      switch (this) {
        case OVERWRITE:
        case UPDATE:
          return true;
        case DEFAULT_READ: return false;
        default: throw new IllegalStateException();
      }
    }

    /**
     * Whether the access includes reading a value. Is true for {@link #DEFAULT_READ} and {@link #UPDATE}
     */
    public boolean includesRead() {
      switch (this) {
        case DEFAULT_READ:
        case UPDATE:
          return true;
        case OVERWRITE: return false;
        default: throw new IllegalStateException();
      }
    }
  }

  /**
   * Traverser used for handling {@link ASTAssignmentExpression assignment-expressions}.
   * This is only needed to fulfill the interface {@link AssignmentExpressionsHandler}.
   * The {@link #setTraverser(AssignmentExpressionsTraverser) setter} does not have to be called explicitly when using mills
   */
  protected AssignmentExpressionsTraverser traverser;

  /**
   * The type of access, the last visited expression implies.
   * If this has the value <code>null</code>, it means that there was no expression yet that implies write access.
   */
  protected VarAccessKind current = null;

  /**
   * Lists all names that are found in the expression, and how the variables are accessed
   */
  protected HashMap<ASTNameExpression, VarAccessKind> foundNames = new HashMap<>();

  /**
   * @return {@link #foundNames all variables} that were found.
   */
  public Map<ASTNameExpression, VarAccessKind> getFoundNames() {
    return this.foundNames;
  }

  /**
   * Resets the internal state, so this element behaves like new.
   * This method does not use {@link Map#clear()},
   * because that would also affect any previous {@link #getFoundNames() output}
   */
  public void reset() {
    this.foundNames = new HashMap<>();
  }

  /**
   * Registers this visitor to the given traverser, so that running the traverser will fill the {@link #foundNames map}.
   * This method is independent from {@link #getTraverser()} and {@link #setTraverser(AssignmentExpressionsTraverser)},
   * as they refer to another {@link #traverser}.
   * If you want to extend this method you can do so by simply calling <code>super.registerTo(...)</code>
   * and then adding more <code>if</code>-clauses.
   * @param traverser traverser that should walk this visitor trough an expression ast
   */
  public void registerTo(@NotNull ITraverser traverser){
    Preconditions.checkNotNull(traverser);
    if(traverser instanceof AssignmentExpressionsTraverser){
      ((AssignmentExpressionsTraverser) traverser).setAssignmentExpressionsHandler(this);
      ((AssignmentExpressionsTraverser) traverser).add4AssignmentExpressions(this);
    }
    if(traverser instanceof CommonExpressionsTraverser){
      ((CommonExpressionsTraverser) traverser).add4CommonExpressions(this);
    }
    if(traverser instanceof ExpressionsBasisTraverser) {
      ((ExpressionsBasisTraverser) traverser).add4ExpressionsBasis(this);
    }
  }

  @Override
  public AssignmentExpressionsTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(@NotNull AssignmentExpressionsTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }
  
  @Override
  public void visit(@NotNull ASTNameExpression node) {
    Preconditions.checkNotNull(node);
    if (current != null) {
      foundNames.put(node, current);
    } else {
      foundNames.put(node, VarAccessKind.DEFAULT_READ);
    }
  }

  @Override
  public void traverse(@NotNull ASTAssignmentExpression node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(getTraverser() != null);
    current = node.getOperator() == ASTConstantsAssignmentExpressions.EQUALS?
                                VarAccessKind.OVERWRITE:
                                VarAccessKind.UPDATE;
    node.getLeft().accept(getTraverser());

    current = VarAccessKind.DEFAULT_READ;
    node.getRight().accept(getTraverser());
    
    current = null;
  }
  
  @Override
  public void visit(@NotNull ASTIncPrefixExpression node) {
    current = VarAccessKind.UPDATE;
  }
  
  @Override
  public void endVisit(@NotNull ASTIncPrefixExpression node) {
    current = null;
  }
  
  @Override
  public void visit(@NotNull ASTDecPrefixExpression node) {
    current = VarAccessKind.UPDATE;
  }
  
  @Override
  public void endVisit(@NotNull ASTDecPrefixExpression node) {
    current = null;
  }
  
  @Override
  public void visit(@NotNull ASTIncSuffixExpression node) {
    current = VarAccessKind.UPDATE;
  }
  
  @Override
  public void endVisit(@NotNull ASTIncSuffixExpression node) {
    current = null;
  }
  
  @Override
  public void visit(@NotNull ASTDecSuffixExpression node) {
    current = VarAccessKind.UPDATE;
  }
  
  @Override
  public void endVisit(@NotNull ASTDecSuffixExpression node) {
    current = null;
  }

  @Override
  public void visit(@NotNull ISymbol node) {
  
  }
  
  @Override
  public void endVisit(ISymbol node) {
  
  }
  
  @Override
  public void visit(IScope node) {
  
  }
  
  @Override
  public void endVisit(IScope node) {
  
  }
  
  @Override
  public void visit(ASTNode node) {
  
  }
  
  @Override
  public void endVisit(ASTNode node) {
  
  }
}