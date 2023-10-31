/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Service class for manipulating symbols and scopes.
 */
public final class SymbolService {

  /**
   * Adds all variables to the scope and sets the scope as enclosing scope for each variable.
   *
   * @param scope  the scope to set as enclosing scope
   * @param variables the variables to add to the scope
   */
  public static void link(@NotNull IBasicSymbolsScope scope, @NotNull VariableSymbol... variables) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(variables);
    for (VariableSymbol variable : variables) {
      link(scope, variable);
    }
  }

  /**
   * Adds the variable to the scope and sets the scope as the variable's enclosing scope.
   *
   * @param scope the scope to set as enclosing scope
   * @param variable the variable to add to the scope
   */
  public static void link(@NotNull IBasicSymbolsScope scope, @NotNull VariableSymbol variable) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(variable);
    scope.add(variable);
    variable.setEnclosingScope(scope);
  }

  /**
   * Adds all fields to the scope and sets the scope as enclosing scope for each field.
   *
   * @param scope  the scope to set as enclosing scope
   * @param fields the fields to add to the scope
   */
  public static void link(@NotNull IOOSymbolsScope scope, @NotNull FieldSymbol... fields) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(fields);
    for (FieldSymbol field : fields) {
      link(scope, field);
    }
  }

  /**
   * Adds the field to the scope and sets the scope as the field's enclosing scope.
   *
   * @param scope the scope to set as enclosing scope
   * @param field the field to add to the scope
   */
  public static void link(@NotNull IOOSymbolsScope scope, @NotNull FieldSymbol field) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(field);
    scope.add(field);
    field.setEnclosingScope(scope);
  }

  /**
   * Adds all types to the scope and sets the scope as enclosing scope for each type.
   *
   * @param scope the scope to set as enclosing scope
   * @param types the types to add to the scope
   */
  public static void link(@NotNull IBasicSymbolsScope scope, @NotNull TypeSymbol... types) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(types);
    for (TypeSymbol type : types) {
      link(scope, type);
    }
  }

  /**
   * Adds the type to the scope and sets the scope as the type's enclosing scope.
   *
   * @param scope the scope to set as enclosing scope
   * @param type the type to add to the scope
   */
  public static void link(@NotNull IBasicSymbolsScope scope, @NotNull TypeSymbol type) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(type);
    scope.add(type);
    type.setEnclosingScope(scope);
  }

  /**
   * Adds all oo-types to the scope and sets the scope as enclosing scope for each ooType.
   *
   * @param scope the scope to set as enclosing scope
   * @param ooTypes the oo-types to add to the scope
   */
  public static void link(@NotNull IOOSymbolsScope scope, @NotNull OOTypeSymbol... ooTypes) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(ooTypes);
    for (OOTypeSymbol ooType : ooTypes) {
      link(scope, ooType);
    }
  }

  /**
   * Adds the oo-type to the scope and sets the scope as the ooType's enclosing scope.
   *
   * @param scope the scope to set as enclosing scope
   * @param ooType the oo-type to add to the scope
   */
  public static void link(@NotNull IOOSymbolsScope scope, @NotNull OOTypeSymbol ooType) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(ooType);
    scope.add(ooType);
    ooType.setEnclosingScope(scope);
  }

  /**
   * Adds all functions to the scope and sets the scope as enclosing scope for each function.
   *
   * @param scope the scope to set as enclosing scope
   * @param functions the functions to add to the scope
   */
  public static void link(@NotNull IBasicSymbolsScope scope, @NotNull FunctionSymbol... functions) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(functions);
    for (FunctionSymbol function: functions) {
      link(scope, function);
    }
  }

  /**
   * Adds the function to the scope and sets the scope as the function's enclosing scope.
   *
   * @param scope the scope to set as enclosing scope
   * @param function the function to add to the scope
   */
  public static void link(@NotNull IBasicSymbolsScope scope, @NotNull FunctionSymbol function) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(function);
    scope.add(function);
    function.setEnclosingScope(scope);
  }

  /**
   * Adds all methods to the scope and sets the scope as enclosing scope for each method.
   *
   * @param scope the scope to set as enclosing scope
   * @param methods the methods to add to the scope
   */
  public static void link(@NotNull IOOSymbolsScope scope, @NotNull MethodSymbol... methods) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(methods);
    for (MethodSymbol method: methods) {
      link(scope, method);
    }
  }

  /**
   * Adds the method to the scope and sets the scope as the method's enclosing scope.
   *
   * @param scope the scope to set as enclosing scope
   * @param method the method to add to the scope
   */
  public static void link(@NotNull IOOSymbolsScope scope, @NotNull MethodSymbol method) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(method);
    scope.add(method);
    method.setEnclosingScope(scope);
  }

  /**
   * Adds the type variables to the scope and sets the scope as enclosing scope for each type variable.
   *
   * @param scope the scope to set as enclosing scope
   * @param typeVariables the type variables to add to the scope
   */
  public static void link(@NotNull IOOSymbolsScope scope, @NotNull TypeVarSymbol... typeVariables) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(typeVariables);
    for(TypeVarSymbol typeVariable: typeVariables) {
      link(scope, typeVariable);
    }
  }

  /**
   * Adds the type variable to the scope and sets the scope as the type variable's enclosing scope.
   *
   * @param scope the scope to set as enclosing scope
   * @param typeVariable the type variable to add to the scope
   */
  public static void link(@NotNull IOOSymbolsScope scope, @NotNull TypeVarSymbol typeVariable) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(typeVariable);
    scope.add(typeVariable);
    typeVariable.setEnclosingScope(scope);
  }

  /**
   * Adds the component types to the scope and sets the scope as enclosing scope for each component type.
   *
   * @param scope the scope to set as enclosing scope
   * @param componentTypes the component types to add to the scope
   */
  public static void link(@NotNull IArcBasisScope scope, @NotNull ComponentTypeSymbol... componentTypes) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(componentTypes);
    for (ComponentTypeSymbol componentType: componentTypes) {
      link(scope, componentType);
    }
  }

  /**
   * Adds the component type to the scope and sets the scope as the component type's enclosing scope.
   *
   * @param scope the scope to set as enclosing scope
   * @param componentType the component type to add to the scope
   */
  public static void link(@NotNull IArcBasisScope scope, @NotNull ComponentTypeSymbol componentType) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(componentType);
    scope.add(componentType);
    componentType.setEnclosingScope(scope);
  }

  /**
   * Adds the components to the scope and sets the scope as enclosing scope for each component.
   *
   * @param scope the scope to set as enclosing scope
   * @param components the components to add to the scope
   */
  public static void link(@NotNull IArcBasisScope scope, @NotNull ComponentInstanceSymbol... components) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(components);
    for (ComponentInstanceSymbol component: components) {
      link(scope, component);
    }
  }

  /**
   * Adds the component to the scope and sets the scope as the component's enclosing scope.
   *
   * @param scope the scope to set as enclosing scope
   * @param component the component to add to the scope
   */
  public static void link(@NotNull IArcBasisScope scope, @NotNull ComponentInstanceSymbol component) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(component);
    scope.add(component);
    component.setEnclosingScope(scope);
  }

  /**
   * Adds the ports to the scope and sets the scope as enclosing scope for each component.
   *
   * @param scope the scope to set as enclosing scope
   * @param ports the ports to add to the scope
   */
  public static void link(@NotNull IArcBasisScope scope, @NotNull ArcPortSymbol... ports) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(ports);
    for (ArcPortSymbol port: ports) {
      link(scope, port);
    }
  }

  /**
   * Adds the port to the scope and sets the scope as the port's enclosing scope.
   *
   * @param scope the scope to set as enclosing scope
   * @param port the port to add to the scope
   */
  public static void link(@NotNull IArcBasisScope scope, @NotNull ArcPortSymbol port) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(port);
    scope.add(port);
    port.setEnclosingScope(scope);
  }
}
