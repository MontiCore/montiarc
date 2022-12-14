/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import com.microsoft.z3.Context;
import com.microsoft.z3.Sort;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public interface IDeriveSMTSort {

  @NotNull Optional<Sort> toSort(@NotNull Context context, @NotNull ASTExpression expression);

  @NotNull Optional<Sort> toSort(@NotNull Context context, @NotNull SymTypeExpression typeExpression);

}
