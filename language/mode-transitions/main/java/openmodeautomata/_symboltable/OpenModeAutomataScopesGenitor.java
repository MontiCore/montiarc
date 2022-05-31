/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata._symboltable;

import basicmodeautomata.BasicModeAutomataMill;
import basicmodeautomata._symboltable.*;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import openmodeautomata.runtime.ComponentType;
import openmodeautomata.runtime.SubcomponentInstance;

import java.util.function.Supplier;

public class OpenModeAutomataScopesGenitor {

  /**
   * adds methods which are required in transition reactions to the scope
   */
  public void run() {
    IBasicModeAutomataScope scope = BasicModeAutomataMill.globalScope();
    if (isApplicable(scope)) {
      Log.info("Add methods for mode-transition-reaction-rte", "OpenModes");
      addMethods(scope);
      addConstructors(scope);
    } else {
      Log.error("Failed to add mode-transition-reaction-runtime-environment-methods to the global scope.");
    }
  }

  /**
   * @return false, if the scope already contains the methods of {@link #addMethods(IBasicModeAutomataScope)}
   * or if there are no primitive types in the scope
   */
  public boolean isApplicable(IBasicModeAutomataScope scope) {
    boolean hasPrimitives = scope.resolveType("int").isPresent();
    return hasPrimitives && !getRuntimeEnvironment().getMethodList().stream().limit(3)
        .allMatch(method -> scope.resolveMethod(method.getName()).isPresent());
  }

  /**
   * adds all methods of {@link ComponentType} to the global scope, so they can be used in mode-transition-reactions
   */
  protected void addMethods(IBasicModeAutomataScope scope) {
    getRuntimeEnvironment().getMethodList().forEach(scope::add);
  }

  /**
   * recursive method to traverse all scopes because apparently {@link IBasicModeAutomataGlobalScope#getComponentTypeSymbols()} does not.
   */
  protected void addConstructors(IBasicModeAutomataScope scope){
    scope.getSubScopes().forEach(this::addConstructors);
    addConstructor(scope);
  }

  /**
   * Creates a method for each component,
   * the method has the component's name and parameters and returns a {@link SubcomponentInstance}.
   * Those methods are added to the global scope,
   * so that one create new components in reactions using kotlin (no <code>new</code> keyword) like constructor syntax.
   */
  protected void addConstructor(IBasicModeAutomataScope mainScope) {
    mainScope.getLocalComponentTypeSymbols().forEach(component -> {
      IBasicModeAutomataScope methodScope = BasicModeAutomataMill.scope();
      component.getParameters().forEach(variable ->
        methodScope.add(variable.deepClone())
      );
      component.getTypeParameters().forEach(generic -> {
        IBasicSymbolsScope originalScope = generic.getEnclosingScope();
        methodScope.add(generic);
        generic.setEnclosingScope(originalScope);
      });
      MethodSymbol method = BasicModeAutomataMill.methodSymbolBuilder()
          .setReturnType(SymTypeExpressionFactory.createTypeObject(getSubcomponent()))
          .setName(component.getName())
          .setFullName(component.getFullName())
          .setAstNodeAbsent()
          .setIsPublic(true)
          .setIsMethod(true)
          .setEnclosingScope(mainScope)
          .setSpannedScope(methodScope)
          .build();
      mainScope.add(method);
    });
  }

  /**
   * @return a symbol containing all methods which should be usable in mode automaton reactions
   */
  public OOTypeSymbol getRuntimeEnvironment() {
    return BasicModeAutomataMill.globalScope().resolveOOType(ComponentType.class.getSimpleName()).orElseThrow(createException());
  }

  /**
   * @return a symbol describing the return type from instantiating a subcomponent
   */
  public OOTypeSymbol getSubcomponent() {
    return BasicModeAutomataMill.globalScope().resolveOOType(SubcomponentInstance.class.getSimpleName()).orElseThrow(createException());
  }

  /**
   * @return provides an exception to throw when the required interfaces are not known to the scope
   */
  protected Supplier<RuntimeException> createException(){
    return () -> new RuntimeException("Cannot resolve reaction-runtime-interfaces. " +
        "Did you include the respective .sym-file in the model-path?");
  }

}