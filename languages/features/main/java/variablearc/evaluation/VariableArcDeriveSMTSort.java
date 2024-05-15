/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import com.microsoft.z3.Sort;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc.evaluation.exp2smt.IDeriveSMTSort;

import java.util.Optional;

public final class VariableArcDeriveSMTSort implements IDeriveSMTSort {

  private final IArcTypeCalculator tc;

  public VariableArcDeriveSMTSort(@NotNull IArcTypeCalculator tc) {
    this.tc = tc;
  }

  @Override
  public Optional<Sort> toSort(@NotNull Context context, @NotNull ASTExpression nameExpression) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(nameExpression);
    Preconditions.checkNotNull(nameExpression.getEnclosingScope());
    SymTypeExpression typeOfExpr = this.tc.typeOf(nameExpression);

    return toSort(context, typeOfExpr);
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
        case BasicSymbolsMill.DOUBLE:
          return Optional.of(context.getRealSort());
      }
    } else if (typeExpression.isObjectType() && typeExpression.asObjectType().hasTypeInfo() && VariableArcMill.typeDispatcher().isOOSymbolsOOType(
      typeExpression.asObjectType().getTypeInfo()) && VariableArcMill.typeDispatcher().asOOSymbolsOOType(typeExpression.asObjectType().getTypeInfo()).isIsEnum()) {
      // Case Enums
      String[] s = VariableArcMill.typeDispatcher().asOOSymbolsOOType(typeExpression.asObjectType().getTypeInfo()).getSpannedScope().getLocalFieldSymbols().stream().map(FieldSymbol::getName).toArray(String[]::new);
      return Optional.of(context.mkEnumSort(typeExpression.asObjectType().getTypeInfo().getFullName(), s));
    }

    return Optional.empty();
  }
}
