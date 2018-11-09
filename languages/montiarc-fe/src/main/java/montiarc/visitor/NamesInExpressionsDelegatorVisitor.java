/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTConstantExpressionSwitchLabel;
import de.monticore.java.javadsl._ast.ASTVariableDeclarator;
import de.monticore.java.javadsl._visitor.JavaDSLDelegatorVisitor;
import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.monticore.mcexpressions._ast.ASTAssignmentExpression;
import de.monticore.mcexpressions._ast.ASTCallExpression;
import de.monticore.mcexpressions._ast.ASTComparisonExpression;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.mcexpressions._ast.ASTIdentityExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.mcexpressions._ast.ASTPrefixExpression;
import de.monticore.mcexpressions._ast.ASTSuffixExpression;
import de.monticore.mcexpressions._visitor.MCExpressionsVisitor;

/**
 * Delegator Visitor to check names used in expressions. Combines
 * {@link NamesInJavaExpressionsVisitor} and {@link NamesInMCExpressionsVisitor}.
 *
 * @author Pfeiffer
 * @version $Revision$, $Date$
 */
public class NamesInExpressionsDelegatorVisitor extends JavaDSLDelegatorVisitor {
  
  /**
   * Enum used by {@link NamesInMCExpressionsVisitor}
   * {@link NamesInJavaExpressionsVisitor} and {@link NamesInExpressionsDelegatorVisitor}.
   * 
   * Expressions are mapped to these Kinds. A expression is of type DEFAULT when it is of no other kind. 
   *
   * @author Pfeiffer
   * @version $Revision$, $Date$
   */
  public enum ExpressionKind {
    CONSTANT_EXPRESSION_SWITCHLABEL, COMPARISON, CALL, ASSIGNMENT_RIGHT, ASSIGNMENT_LEFT, DEFAULT, PREFIX_EXPR, POSTFIX_EXPR;
  }

  
  private Optional<ExpressionKind> currentExpressionKind = Optional.empty();
  
  private Map<ASTNameExpression, ExpressionKind> foundNames = new HashMap<>();
  
  
  /**
   * Constructor for montiarc.visitor.MyJavaDSLDelegatorVisitor
   */
  public NamesInExpressionsDelegatorVisitor() {
    setJavaDSLVisitor(new NamesInJavaExpressionsVisitor());
    setMCExpressionsVisitor(new NamesInMCExpressionsVisitor());
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLDelegatorVisitor#visit(de.monticore.java.javadsl._ast.ASTVariableDeclarator)
   */
  @Override
  public void visit(ASTVariableDeclarator node) {
    super.visit(node);
    currentExpressionKind = Optional.of(ExpressionKind.ASSIGNMENT_RIGHT);
  }
  
  
  /**
   * @return foundNames
   */
  public Map<ASTNameExpression, ExpressionKind> getFoundNames() {
    return this.foundNames;
  }
  
  
  /**
   * @see de.monticore.mcexpressions._visitor.MCExpressionsVisitor#visit(de.monticore.mcexpressions._ast.ASTAssignmentExpression)
   */
  @Override
  public void visit(ASTAssignmentExpression node) {
    currentExpressionKind = Optional.of(ExpressionKind.ASSIGNMENT_LEFT);
    node.getLeftExpression().accept(getRealThis());
    
    currentExpressionKind = Optional.of(ExpressionKind.ASSIGNMENT_RIGHT);
    node.getRightExpression().accept(getRealThis());
    ;
    currentExpressionKind = Optional.empty();
  }
  
  /**
   * @see de.monticore.mcexpressions._visitor.MCExpressionsVisitor#visit(de.monticore.mcexpressions._ast.ASTIdentityExpression)
   */
  @Override
  public void visit(ASTIdentityExpression node) {
    currentExpressionKind = Optional.of(ExpressionKind.COMPARISON);
    node.getLeftExpression().accept(getRealThis());
    node.getRightExpression().accept(getRealThis());
    currentExpressionKind = Optional.empty();
  }
  
  /**
   * @see de.monticore.mcexpressions._visitor.MCExpressionsVisitor#visit(de.monticore.mcexpressions._ast.ASTCallExpression)
   */
  @Override
  public void visit(ASTCallExpression node) {
    currentExpressionKind = Optional.of(ExpressionKind.CALL);
    node.getExpression().accept(getRealThis());
    for (ASTExpression arg : node.getArguments().getExpressionList()) {
      arg.accept(this);
    }
    currentExpressionKind = Optional.empty();
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLDelegatorVisitor#visit(de.monticore.mcexpressions._ast.ASTPrefixExpression)
   */
  @Override
  public void visit(ASTPrefixExpression node) {
    currentExpressionKind = Optional.of(ExpressionKind.PREFIX_EXPR);
    node.getExpression().accept(getRealThis());
    currentExpressionKind = Optional.empty();
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLDelegatorVisitor#visit(de.monticore.mcexpressions._ast.ASTSuffixExpression)
   */
  @Override
  public void visit(ASTSuffixExpression node) {
    currentExpressionKind = Optional.of(ExpressionKind.POSTFIX_EXPR);
    node.getExpression().accept(getRealThis());
    currentExpressionKind = Optional.empty();
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLDelegatorVisitor#visit(de.monticore.java.javadsl._ast.ASTConstantExpressionSwitchLabel)
   */
  @Override
  public void visit(ASTConstantExpressionSwitchLabel node) {
    currentExpressionKind = Optional.of(ExpressionKind.CONSTANT_EXPRESSION_SWITCHLABEL);
    node.getConstantExpression().accept(getRealThis());
    currentExpressionKind = Optional.empty();
  }
  
  /**
   * @see de.monticore.mcexpressions._visitor.MCExpressionsVisitor#visit(de.monticore.mcexpressions._ast.ASTComparisonExpression)
   */
  @Override
  public void visit(ASTComparisonExpression node) {
    currentExpressionKind = Optional.of(ExpressionKind.COMPARISON);
    node.getLeftExpression().accept(getRealThis());
    node.getRightExpression().accept(getRealThis());
    currentExpressionKind = Optional.empty();
  }
  
  /**
   * @see de.monticore.mcexpressions._visitor.MCExpressionsVisitor#visit(de.monticore.mcexpressions._ast.ASTNameExpression)
   */
  @Override
  public void visit(ASTNameExpression node) {
    if (currentExpressionKind.isPresent()) {
      foundNames.put(node, currentExpressionKind.get());
    }
    else {
      if (!foundNames.containsKey(node)) {
        foundNames.put(node, ExpressionKind.DEFAULT);
      }
    }
  }
  
  
  /**
   * Visits {@link ASTExpression} and stores used names {@link ASTNameExpression}
   * and the kind of expression they are used in.
   *
   * @author Pfeiffer
   * @version $Revision$, $Date$
   */
  private class NamesInMCExpressionsVisitor implements MCExpressionsVisitor {
    
    MCExpressionsVisitor realThis = this;
    
    
    /**
     * @see de.monticore.mcexpressions._visitor.MCExpressionsVisitor#setRealThis(de.monticore.mcexpressions._visitor.MCExpressionsVisitor)
     */
    @Override
    public void setRealThis(MCExpressionsVisitor realThis) {
      this.realThis = realThis;
    }
    
    /**
     * @see de.monticore.mcexpressions._visitor.MCExpressionsVisitor#getRealThis()
     */
    @Override
    public MCExpressionsVisitor getRealThis() {
      return this.realThis;
    };
    
  }
  
  /**
   * Used for checking names in AJava blocks which uses the JavaDSL.
   *
   * @author (last commit) $Author$
   * @version $Revision$, $Date$
   */
  private class NamesInJavaExpressionsVisitor implements JavaDSLVisitor {
    
    private JavaDSLVisitor realThis = this;
    
    /**
     * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#setRealThis(de.monticore.java.javadsl._visitor.JavaDSLVisitor)
     */
    @Override
    public void setRealThis(JavaDSLVisitor realThis) {
      this.realThis = realThis;
    }
    
    /**
     * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#getRealThis()
     */
    @Override
    public JavaDSLVisitor getRealThis() {
      return this.realThis;
    }
      
  }


}
