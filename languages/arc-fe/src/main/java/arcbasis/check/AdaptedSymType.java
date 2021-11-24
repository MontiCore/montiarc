/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.check.*;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AdaptedSymType {

  public static SymTypeExpression of(@NotNull SymTypeExpression original) {
    if(original.isArrayType()) {
      return new AdaptedSymType.Array((SymTypeArray) original);
    } else if(original.isTypeConstant()) {
      return new AdaptedSymType.Constant((SymTypeConstant) original);
    } else if(original.isGenericType()) {
      return new AdaptedSymType.OfGenerics((SymTypeOfGenerics) original);
    } else if(original.isNullType()) {
      return original;
    } else if(original.isObjectType()) {
      return new AdaptedSymType.OfObject((SymTypeOfObject) original);
    } else if(original instanceof SymTypeOfWildcard) {
      return new AdaptedSymType.OfWildcard((SymTypeOfWildcard) original);
    } else if(original.isTypeVariable()) {
      return new AdaptedSymType.Variable((SymTypeVariable) original);
    } else if(original.isVoidType()) {
      return original;
    } else {
      Log.error("ArcTypeCheck can not handle expressions that are not of known SymTypeExpression. Found: "
        + original.getClass().getName());
      throw new IllegalStateException();
    }
  }

  protected static class Array extends SymTypeArray {

    public Array(SymTypeArray original) {
      super(
        new TypeSymbolAdapter(original.getTypeInfo()),
        original.getDim(),
        AdaptedSymType.of(original.getArgument())
      );
    }

    @Override
    public String print() {
      return this.printFullName();
    }
  }

  private static class Constant extends SymTypeConstant {
    public Constant(SymTypeConstant original) {
      super(new TypeSymbolAdapter(original.getTypeInfo()));
    }
    // We do not need to override print for constants
  }

  private static class OfGenerics extends SymTypeOfGenerics {
    public OfGenerics(SymTypeOfGenerics original) {
      super(
        new TypeSymbolAdapter(original.getTypeInfo()),
        original.getArgumentList().stream().map(AdaptedSymType::of).collect(Collectors.toList())
      );
    }

    @Override
    public String print() {
      return this.printFullName();
    }
  }

  private static class OfObject extends SymTypeOfObject {
    public OfObject(SymTypeOfObject original) {
      super(new TypeSymbolAdapter(original.getTypeInfo()));
    }

    @Override
    public String print() {
      return this.printFullName();
    }
  }

  private static class OfWildcard extends SymTypeOfWildcard {
    public OfWildcard(SymTypeOfWildcard original) {
      super(
        original.isUpper(),
        AdaptedSymType.of(original.getBound())
      );
    }

    @Override
    public String print() {
      return this.printFullName();
    }
  }

  private static class Variable extends SymTypeVariable {
    public Variable(SymTypeVariable original) {
      super(new TypeSymbolAdapter(original.getTypeInfo()));
    }

    @Override
    public String print() {
      return this.printFullName();
    }
  }

  /**
   * This {@link TypeSymbol} sub class is not usable for anything else then TypeChecking.
   * E.g., it is not contained within a scope. However, it's super types refer to AdaptedSymTypeExpressions
   * (without surrogates), so that TypeChecks do not need to handle Surrogates.
   */
  protected static class TypeSymbolAdapter extends TypeSymbol {

    private final TypeSymbol original;

    public TypeSymbolAdapter(TypeSymbol original) {
      super(original.getName());
      this.original = skipSurrogate(original);

      this.setSuperTypesList(original.getSuperTypesList());
    }

    @Override
    public List<SymTypeExpression> getInterfaceList() {
      return original.getInterfaceList().stream().map(AdaptedSymType::of).collect(Collectors.toList());
    }

    // TODO: we might want to adapt getTypeParameterList. Then we also have to adapt TypeVarSymbol though

    @Override
    public String getPackageName() { return original.getPackageName(); }

    @Override
    public String getFullName() { return original.getFullName(); }

    @Override
    public String getName() { return original.getName(); }

    @Override
    public IBasicSymbolsScope getEnclosingScope() {
      return original.getEnclosingScope();
    }
  }

  /**
   * If {@code type} is a {@link TypeSymbol}, than {@code type} is directly returned. If {@code type} is a
   * {@link TypeSymbolSurrogate}, and the surrogate resolves to a TypeSymbol directly, then the TypeSymbol behind the
   * surrogate is returned. However, if The surrogate delegates to another surrogate, then this method logs an error.
   */
  private static TypeSymbol skipSurrogate(TypeSymbol type) {
    TypeSymbol realType = type instanceof TypeSymbolSurrogate ?
      ((TypeSymbolSurrogate) type).lazyLoadDelegate()
      : type;

    if(realType instanceof TypeSymbolSurrogate) {
      Log.error(String.format(
        "ArcTypeCheck can not handle TypeSymbolSurrogates that point to other TypeSymbolSurrogates. However this is "
          + "the case for surrogate '%s' in scope '%s'.",
        realType.getName(), realType.getPackageName()));
    }

    return realType;
  }
}
