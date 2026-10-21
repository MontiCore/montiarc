/* (c) https://github.com/MontiCore/monticore */
package variablearc.smt;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeSort;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types3.TypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.smt.exp2smt.IDeriveSMTSort;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Derives a Z3 sort from an SymTypeExpression.
 * Enumerations are declared once per context, as Z3 rejects redeclaring an
 * enumeration sort, and shared by all conversions in that context until the
 * context is released.
 */
public final class VariableArcDeriveSMTSort implements IDeriveSMTSort {

  private static final Map<Context, Map<String, EnumSort<?>>> ENUMERATIONS =
    Collections.synchronizedMap(new IdentityHashMap<>());

  private final Context context;

  public VariableArcDeriveSMTSort(Context context) {
    this.context = context;
  }

  /**
   * @param name      the full name of the enumeration
   * @param constants the names of its constants
   * @return the enumeration sort of the context, declared on first use, whose
   * constants are named by {@link #constantName}
   */
  public static EnumSort<?> enumSort(@NotNull Context context, @NotNull String name, @NotNull List<String> constants) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(constants);
    return ENUMERATIONS.computeIfAbsent(context, c -> new ConcurrentHashMap<>()).computeIfAbsent(name, n ->
      context.mkEnumSort(name, constants.stream().map(constant -> constantName(name, constant)).toArray(String[]::new)));
  }

  /**
   * Forgets the enumeration sorts of the context, which is to be closed.
   */
  public static void release(@NotNull Context context) {
    Preconditions.checkNotNull(context);
    ENUMERATIONS.remove(context);
  }

  /**
   * @return the name of the constant of the enumeration in the context, which
   * is qualified by the enumeration to distinguish same-named constants of
   * different enumerations
   */
  public static String constantName(@NotNull String enumeration, @NotNull String constant) {
    Preconditions.checkNotNull(enumeration);
    Preconditions.checkNotNull(constant);
    return enumeration + "." + constant;
  }

  /**
   * @param sort an enumeration sort, which Z3 provides as datatype sort for
   *             the sort of a term
   * @return the index of the constant in the enumeration sort, or -1 if it
   * has none of the name
   */
  public static int indexOf(@NotNull Sort sort, @NotNull String constant) {
    Preconditions.checkNotNull(sort);
    Preconditions.checkNotNull(constant);
    String name = constantName(sort.getName().toString(), constant);
    FuncDecl<?>[] constants = sort instanceof EnumSort<?> enumeration ? enumeration.getConstDecls()
      : sort instanceof DatatypeSort<?> datatype ? datatype.getConstructors() : new FuncDecl<?>[0];
    for (int i = 0; i < constants.length; i++) {
      if (constants[i].getName().toString().equals(name)) {
        return i;
      }
    }
    return -1;
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
    } else if (typeExpression.isObjectType() && typeExpression.asObjectType().hasTypeInfo()
      && typeExpression.asObjectType().getTypeInfo() instanceof OOTypeSymbol ooType && ooType.isIsEnum()) {
      // Case Enums
      return Optional.of(enumSort(this.context, typeExpression.asObjectType().getTypeInfo().getFullName(),
        ooType.getSpannedScope().getLocalFieldSymbols().stream().map(FieldSymbol::getName).toList()));
    }

    return Optional.empty();
  }
}
