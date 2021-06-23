/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

/**
 * This class checks whether component parameter, variable, subcomponent instance, or
 * port names are equal to names used in the generated code.
 * 
 * NOTICE: createInstance() has to be called for every component symbol again.
 */

public class Identifier {
  private static Identifier instance;
  
  public static void createInstance(final ComponentTypeSymbol comp) {
    Identifier.instance = new Identifier();
    Identifier.instance.checkIdentifiers(comp);
  }
  
  private String resultName = "result";
  
  private String inputName = "input";
  
  private String behaviorImplName = "behaviorImpl";
  
  private String currentStateName = "currentState";
  
  /**
   * Checks whether component parameter, variable, subcomponent instance, or
   * port names contain the identifier given as the parameter.
   * 
   * @param identifier The name to check
   * @return True, iff. there is at least one identifier that is equal to the
   * given identifier
   */
  public boolean containsIdentifier(final String identifier, final ComponentTypeSymbol component) {
    for (final PortSymbol portSymbol : component.getPorts()) {
      if(portSymbol.getName().equals(identifier)) {
        return true;
      }
    }
    for (final VariableSymbol parameter : component.getParameters()) {
      if(parameter.getName().equals(identifier)) {
        return true;
      }
    }
    for (final VariableSymbol field : component.getFields()) {
      if(field.getName().equals(identifier)) {
        return true;
      }
    }
    for (final ComponentInstanceSymbol instanceSymbol : component.getSubComponents()) {
      if(instanceSymbol.getName().equals(identifier)) {
        return true;
      }
    }
    return false;
  }
  
  private void checkIdentifiers(final ComponentTypeSymbol comp) {
    if(this.containsIdentifier("result", comp)) {
      this.resultName = "r__result";
    }
    if(this.containsIdentifier("input", comp)) {
      this.inputName = "r__input";
    }
    if(this.containsIdentifier("behaviorImpl", comp)) {
      this.behaviorImplName = "r__behaviorImpl";
    }
    if(this.containsIdentifier("currentState", comp)) {
      this.currentStateName = "r__currentState";
    }
  }
  
  /**
   * @return behaviorImplName
   */
  public static String getBehaviorImplName() {
    return Identifier.instance.behaviorImplName;
  }
  
  /**
   * @return resultName
   */
  public static String getResultName() {
    return Identifier.instance.resultName;
  }
  
  /**
   * @return inputName
   */
  public static String getInputName() {
    return Identifier.instance.inputName;
  }
  
  /**
   * @return currentStateName
   */
  public static String getCurrentStateName() {
    return Identifier.instance.currentStateName;
  }
}