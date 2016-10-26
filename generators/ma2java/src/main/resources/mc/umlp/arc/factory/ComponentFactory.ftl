${tc.params("String _package", "String factoryName", "de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", 
"java.util.List<de.monticore.symboltable.types.JAttributeSymbol> configParams", "de.montiarc.generator.codegen.GeneratorHelper helper", "boolean hwcExists")}

package ${_package};

public class ${factoryName} {

  /** Factory singleton instance. */
  protected static ${factoryName} instance;

  /** Protected default constructor */
  protected ${factoryName}() {
  
  }
  
  /**
  * Registers a concrete factory instance that is used to produce
  * instances of I${compSym.getName()}
  * 
  * @param factory the factory instance that is to be used
  */
  public static void register(${factoryName} factory) {
    if(instance == null || instance.getClass().equals(${factoryName}.class)) {
      instance = factory;
    }
    else {
      throw new RuntimeException("More then one concrete factory registered for ${factoryName}. Current factory class is: " +
                  instance.getClass().getName() + ", factory class to register: " + factory.getClass().getName());
    }
  }
  
  /**
  * Resets the ${factoryName} to use its default factory.
  */
  public static void reset() {
    instance = new ${factoryName}();
  }
  
  /**
  *
  * @return a new ${compSym.getName()} component
  */
  public static ${compSym.getPackageName()}.interfaces.I${compSym.getName()}<#t> create(${helper.printConfigParameters(configParams)}) {
    if (instance == null) {
      instance = new ${factoryName}();
    }
    return instance.doCreate(${helper.printConfigParametersNames(configParams)});
  }
  
  /**
  *
  * @return a new ${compSym.getName()} component
  */
  protected ${compSym.getPackageName()}.interfaces.I${compSym.getName()} <#t>
       doCreate(${helper.printConfigParameters(configParams)}) {<#lt>
    <#if compSym.isAtomic()>
      <#if hwcExists>
    return new ${compSym.getName()}${glex.getGlobalVar("DEFAULT_ATOMIC_COMPONENT_IMPL_POSTFIX")}(${helper.printConfigParameterNames(configParams)});
      <#else>
      throw new sim.error.NotImplementedYetException("Behavior implementation ${compSym.getName()}${glex.getGlobalVar("DEFAULT_ATOMIC_COMPONENT_IMPL_POSTFIX")} does not exist. Either create it or subtype the factory "
          + "${factoryName} and overwrite its doCreate method to inject instances of your preferred behavior implementation for component ${ast.getName()}.");
      </#if>
    <#else>
      return new ${compSym.getPackageName()}.${compSym.getName()}(${helper.printConfigParametersNames(configParams)});
    </#if>
    }
  
}
  
