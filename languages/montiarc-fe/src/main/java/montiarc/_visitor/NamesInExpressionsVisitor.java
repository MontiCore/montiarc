/* (c) https://github.com/MontiCore/monticore */
package montiarc._visitor;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsHandler;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Visitor to search name usage in expressions.
 */
public class NamesInExpressionsVisitor implements AssignmentExpressionsVisitor2, AssignmentExpressionsHandler, CommonExpressionsVisitor2, ExpressionsBasisVisitor2 {
  
  protected AssignmentExpressionsTraverser traverser;
  
  @Override
  public AssignmentExpressionsTraverser getTraverser() {
    return this.traverser;
  }
  
  public void setTraverser(AssignmentExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
  
  public enum ExpressionKind {
    COMPARISON, CALL, ASSIGNMENT_RIGHT, ASSIGNMENT_LEFT, DEFAULT, PREFIX_EXPR, SUFFIX_EXPR
  }
  
  private ExpressionKind currentExpressionKind = null;
  
  private HashMap<ASTNameExpression, ExpressionKind> foundNames = new HashMap<>();
  
  public Map<ASTNameExpression, ExpressionKind> getFoundNames() {
    return this.foundNames;
  }
  
  public void reset() {
    this.foundNames = new HashMap<>();
  }
  
  @Override
  public void visit(@NotNull ASTNameExpression node) {
    if (currentExpressionKind != null) {
      foundNames.put(node, currentExpressionKind);
    } else {
      foundNames.put(node, ExpressionKind.DEFAULT);
    }
  }
  
  @Override
  public void visit(@NotNull ASTCallExpression node) {
    currentExpressionKind = ExpressionKind.CALL;
  }
  
  @Override
  public void endVisit(@NotNull ASTCallExpression node) {
    currentExpressionKind = null;
  }
  
  @Override
  public void traverse(@NotNull ASTAssignmentExpression node) {
    currentExpressionKind = ExpressionKind.ASSIGNMENT_LEFT;
    node.getLeft().accept(getTraverser());
    
    currentExpressionKind = ExpressionKind.ASSIGNMENT_RIGHT;
    node.getRight().accept(getTraverser());
    
    currentExpressionKind = null;
  }
  
  @Override
  public void visit(@NotNull ASTLessEqualExpression node) {
    currentExpressionKind = ExpressionKind.COMPARISON;
  }
  
  @Override
  public void endVisit(@NotNull ASTLessEqualExpression node) {
    currentExpressionKind = null;
  }
  
  @Override
  public void visit(@NotNull ASTGreaterEqualExpression node) {
    currentExpressionKind = ExpressionKind.COMPARISON;
  }
  
  @Override
  public void endVisit(@NotNull ASTGreaterEqualExpression node) {
    currentExpressionKind = null;
  }
  
  @Override
  public void visit(@NotNull ASTLessThanExpression node) {
    currentExpressionKind = ExpressionKind.COMPARISON;
  }
  
  @Override
  public void endVisit(@NotNull ASTLessThanExpression node) {
    currentExpressionKind = null;
  }
  
  @Override
  public void visit(@NotNull ASTGreaterThanExpression node) {
    currentExpressionKind = ExpressionKind.COMPARISON;
  }
  
  @Override
  public void endVisit(@NotNull ASTGreaterThanExpression node) {
    currentExpressionKind = null;
  }
  
  @Override
  public void visit(@NotNull ASTEqualsExpression node) {
    currentExpressionKind = ExpressionKind.COMPARISON;
  }
  
  @Override
  public void endVisit(@NotNull ASTEqualsExpression node) {
    currentExpressionKind = null;
  }
  
  @Override
  public void visit(@NotNull ASTNotEqualsExpression node) {
    currentExpressionKind = ExpressionKind.COMPARISON;
  }
  
  @Override
  public void endVisit(@NotNull ASTNotEqualsExpression node) {
    currentExpressionKind = null;
  }
  
  @Override
  public void visit(@NotNull ASTIncPrefixExpression node) {
    currentExpressionKind = ExpressionKind.PREFIX_EXPR;
  }
  
  @Override
  public void endVisit(@NotNull ASTIncPrefixExpression node) {
    currentExpressionKind = null;
  }
  
  @Override
  public void visit(@NotNull ASTDecPrefixExpression node) {
    currentExpressionKind = ExpressionKind.PREFIX_EXPR;
  }
  
  @Override
  public void endVisit(@NotNull ASTDecPrefixExpression node) {
    currentExpressionKind = null;
  }
  
  @Override
  public void visit(@NotNull ASTIncSuffixExpression node) {
    currentExpressionKind = ExpressionKind.SUFFIX_EXPR;
  }
  
  @Override
  public void endVisit(@NotNull ASTIncSuffixExpression node) {
    currentExpressionKind = null;
  }
  
  @Override
  public void visit(@NotNull ASTDecSuffixExpression node) {
    currentExpressionKind = ExpressionKind.SUFFIX_EXPR;
  }
  
  @Override
  public void endVisit(@NotNull ASTDecSuffixExpression node) {
    currentExpressionKind = null;
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