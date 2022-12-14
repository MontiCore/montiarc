/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public interface IDeriveSMTExpr {

  @NotNull Optional<Expr<?>> toExpr(@NotNull ASTExpression expr);

  @NotNull Optional<IntExpr> toInt(@NotNull ASTExpression expr);

  @NotNull Optional<BoolExpr> toBool(@NotNull ASTExpression expr);

  @NotNull Optional<ArithExpr<?>> toArith(@NotNull ASTExpression expr);
}
