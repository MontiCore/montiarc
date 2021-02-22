/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import genericarc._symboltable.IGenericArcScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.*;
import java.util.stream.IntStream;

public class ComponentInstanceSymbol extends ComponentInstanceSymbolTOP {

  protected ComponentTypeSymbol type;
  protected ComponentTypeSymbol genericType;
  protected List<ASTExpression> arguments;
  protected List<SymTypeExpression> typeParameters;

  public ComponentTypeSymbol getGenericType() {
    ComponentTypeSymbol type = this.getType();
    return genericType != null ? genericType : type;
  }

  protected void setGenericType(@NotNull ComponentTypeSymbol type) {
    this.genericType = type;
  }

  /**
   * @param name the name of this component.
   */
  public ComponentInstanceSymbol(String name) {
    super(name);
    this.arguments = new ArrayList<>();
    this.typeParameters = new ArrayList<>();
  }

  /**
   * @return the type of this component
   */
  public ComponentTypeSymbol getType() {
    if (type instanceof ComponentTypeSymbolSurrogate) {
      if (type.getEnclosingScope() instanceof IGenericArcScope) {
        this.setGenericType(type);
      }
      this.type = ((ComponentTypeSymbolSurrogate) type).lazyLoadDelegate();
    }
    return this.type;
  }

  /**
   * @param type the type of this component
   */
  public void setType(@NotNull ComponentTypeSymbol type) {
    Preconditions.checkArgument(type != null);
    this.type = type;
  }

  /**
   * @return a {@code List} of the configuration arguments of this component.
   */
  public List<ASTExpression> getArguments() {
    return this.arguments;
  }

  /**
   * @param argument the configuration argument to add to this component.
   */
  public void addArgument(@NotNull ASTExpression argument) {
    Preconditions.checkArgument(argument != null);
    this.arguments.add(argument);
  }

  /**
   * @param arguments the configuration arguments to add to this component.
   * @see this#addArgument(ASTExpression)
   */
  public void addArguments(@NotNull List<ASTExpression> arguments) {
    Preconditions.checkArgument(arguments != null);
    Preconditions.checkArgument(!arguments.contains(null));
    for (ASTExpression argument : arguments) {
      this.addArgument(argument);
    }
  }

  /**
   * @return the expressions that fill the generic type parameters of this component
   */
  public List<SymTypeExpression> getTypeParameterExpressions() {
    return typeParameters;
  }

  /**
   * @param typeParameters type parameters of this component, in the same order as in the corresponding type
   */
  public void setTypeParameters(@NotNull List<SymTypeExpression> typeParameters) {
    this.typeParameters = Preconditions.checkNotNull(typeParameters);
  }

  /**
   * @return a map that maps the type parameter expressions of this component to their respective types of the component type
   */
  public Map<TypeVarSymbol, SymTypeExpression> getTypeParameterMapping(){
    List<TypeVarSymbol> types = getType().getTypeParameters();
    Preconditions.checkState(types.size() == getTypeParameterExpressions().size(),
        "The count of type parameters of this component instance and its component type do not match");
    Map<TypeVarSymbol, SymTypeExpression> map = new HashMap<>();
    IntStream.range(0, types.size()).forEach(i -> map.put(types.get(i), getTypeParameterExpressions().get(i)));
    return map;
  }
}