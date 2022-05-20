/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata._symboltable;

import basicmodeautomata.BasicModeAutomataMill;
import basicmodeautomata._symboltable.IBasicModeAutomataScope;
import de.monticore.class2mc.adapters.JClass2TypeSymbolAdapter;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import openmodeautomata.runtime.ComponentType;
import openmodeautomata.runtime.SubcomponentBuilder;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoaderRepository;

public class OpenModeAutomataScopesGenitor {

  protected static JClass2TypeSymbolAdapter RTE_SYMBOL = null;
  protected static JClass2TypeSymbolAdapter SUB_SYMBOL = null;

  /**
   * adds methods which are required in transition reactions to the scope
   */
  public void run() {
    IBasicModeAutomataScope scope = BasicModeAutomataMill.globalScope();
    if (isSatisfied(scope)) {
      return;
    }
    addMethods(scope);
    addConstructors(scope);
  }

  /**
   * @return true, if the scope already contains the methods of {@link #addMethods(IBasicModeAutomataScope)}, false if not
   */
  public boolean isSatisfied(IBasicModeAutomataScope scope) {
    return getRuntimeEnvironment().getMethodList().stream()
        .allMatch(method -> scope.resolveMethod(method.getName()).isPresent());
  }

  /**
   * adds all methods of {@link ComponentType} to the global scope, so they can be used in mode-transition-reactions
   */
  protected void addMethods(IBasicModeAutomataScope scope) {
    getRuntimeEnvironment().getMethodList().forEach(scope::add);
  }

  /**
   * Creates a method for each component,
   * the method has the component's name and parameters and returns a {@link SubcomponentBuilder}.
   * Those methods are added to the global scope,
   * so that one create new components in reactions using kotlin (no <code>new</code> keyword) like constructor syntax.
   */
  protected void addConstructors(IBasicModeAutomataScope mainScope) {
    mainScope.getComponentTypeSymbols().values().forEach(component -> {
      IBasicModeAutomataScope methodScope = BasicModeAutomataMill.scope();
      component.getParameters().forEach(variable -> {
        methodScope.add(variable.deepClone());
      });
      component.getTypeParameters().forEach(generic -> {
        IBasicSymbolsScope originalScope = generic.getEnclosingScope();
        methodScope.add(generic);
        generic.setEnclosingScope(originalScope);
      });
      MethodSymbol method = BasicModeAutomataMill.methodSymbolBuilder()
          .setReturnType(SymTypeExpressionFactory.createTypeObject(getSubcomponentInstantiation()))
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
  public static JClass2TypeSymbolAdapter getRuntimeEnvironment() {
    if (RTE_SYMBOL == null) {
      RTE_SYMBOL = getSymbol(ComponentType.class);
    }
    return RTE_SYMBOL;
  }

  /**
   * @return a symbol describing the return type from instantiating a subcomponent
   */
  public static JClass2TypeSymbolAdapter getSubcomponentInstantiation() {
    if (SUB_SYMBOL == null) {
      SUB_SYMBOL = getSymbol(SubcomponentBuilder.class);
    }
    return SUB_SYMBOL;
  }

  /**
   * creates a symbol adapter using MontiCore's Class2MC functionality
   *
   * @return a symbol for that
   */
  public static JClass2TypeSymbolAdapter getSymbol(Class<?> origin) {
    ClassLoaderRepository repository = new ClassLoaderRepository(origin.getClassLoader());
    JavaClass componentRTE;
    try {
      componentRTE = repository.loadClass(origin);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Cannot find class for rte methods.", e);
    }
    return new JClass2TypeSymbolAdapter(componentRTE);
  }

}