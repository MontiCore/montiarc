/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import variablearc._visitor.VariableArcVisitor2;

import java.util.function.Consumer;

/**
 * A visitor that does something on {@link ASTNameExpression} in the AST.
 */
public class GenericASTNameExpressionVisitor implements VariableArcVisitor2, ExpressionsBasisVisitor2 {

  final Consumer<ASTNameExpression> consumer;

  public GenericASTNameExpressionVisitor(Consumer<ASTNameExpression> consumer) {
    this.consumer = consumer;
  }

  @Override
  public void visit(ASTNameExpression node) {
    consumer.accept(node);
  }
}
