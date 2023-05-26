/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.expressions;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

import java.util.Optional;

public class NegatedExpression extends Expression {

  public NegatedExpression(ASTExpression astExpression) {
    super(astExpression);
  }

  public NegatedExpression(ASTExpression astExpression, String prefix) {
    super(astExpression, prefix);
  }

  @Override
  public Expression copyWithPrefix(String prefix) {
    return new NegatedExpression(astExpression, prefix);
  }

  public Optional<BoolExpr> convert(Context context, IDeriveSMTExpr converter) {
    converter.setPrefix(getPrefix().orElse(""));
    return converter.toBool(getAstExpression()).map(context::mkNot);
  }
}
