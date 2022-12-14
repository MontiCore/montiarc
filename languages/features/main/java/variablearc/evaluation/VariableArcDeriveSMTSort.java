/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import com.microsoft.z3.Sort;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.TypeCheckResult;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.evaluation.exp2smt.IDeriveSMTSort;

import java.util.Optional;

public final class VariableArcDeriveSMTSort implements IDeriveSMTSort {

  private final IArcTypeCalculator typeCalculator;

  public VariableArcDeriveSMTSort(@NotNull IArcTypeCalculator typeCalculator) {
    this.typeCalculator = typeCalculator;
  }

  @Override
  public Optional<Sort> toSort(@NotNull Context context, @NotNull ASTExpression nameExpression) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(nameExpression);
    Preconditions.checkNotNull(nameExpression.getEnclosingScope());
    TypeCheckResult typeCheckResult = typeCalculator.deriveType(nameExpression);

    if (typeCheckResult.isPresentResult()) {
      return toSort(context, typeCheckResult.getResult());
    }
    return Optional.empty();
  }

  @Override
  public Optional<Sort> toSort(@NotNull Context context, @NotNull SymTypeExpression typeExpression) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(typeExpression);
    if (typeExpression.isPrimitive()) {
      switch (((SymTypePrimitive) typeExpression).getPrimitiveName()) {
        case BasicSymbolsMill.BOOLEAN:
          return Optional.of(context.getBoolSort());
        case BasicSymbolsMill.BYTE:
        case BasicSymbolsMill.CHAR:
        case BasicSymbolsMill.SHORT:
        case BasicSymbolsMill.INT:
        case BasicSymbolsMill.LONG:
          return Optional.of(context.getIntSort());
        case BasicSymbolsMill.FLOAT:
          return Optional.of(context.mkFPSortSingle());
        case BasicSymbolsMill.DOUBLE:
          return Optional.of(context.mkFPSortDouble());
      }
    }

    return Optional.empty();
  }
}
