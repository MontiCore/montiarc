/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import com.microsoft.z3.Sort;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types3.TypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc.evaluation.exp2smt.IDeriveSMTSort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Derives a Z3 sort from an SymTypeExpression.
 * Caches the result of complex types and reuses them for subsequent conversions.
 */
public final class VariableArcDeriveSMTSort implements IDeriveSMTSort {

  private final Context context;
  private final Map<String, Sort> sortMap;

  public VariableArcDeriveSMTSort(Context context) {
    sortMap = new HashMap<>();
    this.context = context;
  }

  @Override
  public Optional<Sort> toSort(@NotNull ASTExpression nameExpression) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(nameExpression);
    Preconditions.checkNotNull(nameExpression.getEnclosingScope());
    SymTypeExpression typeOfExpr = TypeCheck3.typeOf(nameExpression);

    return toSort(typeOfExpr);
  }

  @Override
  public Optional<Sort> toSort(@NotNull SymTypeExpression typeExpression) {
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
      String fullName = typeExpression.asObjectType().getTypeInfo().getFullName();
      if (sortMap.containsKey(fullName)) {
        return Optional.of(sortMap.get(fullName));
      }
      String[] s = VariableArcMill.typeDispatcher().asOOSymbolsOOType(typeExpression.asObjectType().getTypeInfo()).getSpannedScope().getLocalFieldSymbols().stream().map(FieldSymbol::getName).toArray(String[]::new);
      Sort  enumSort = context.mkEnumSort(fullName, s);
      sortMap.put(fullName, enumSort);
      return Optional.of(enumSort);
    }

    return Optional.empty();
  }
}
