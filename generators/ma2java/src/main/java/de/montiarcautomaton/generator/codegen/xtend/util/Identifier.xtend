/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.codegen.xtend.util

import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import arcbasis._symboltable.ComponentInstanceSymbol
import arcbasis._symboltable.ComponentTypeSymbol
import arcbasis._symboltable.PortSymbol

/**
 * This class checks whether component parameter, variable, subcomponent instance, or
 * port names are equal to names used in the generated code.
 * 
 * NOTICE: createInstance() has to be called for every component symbol again.
 */
class Identifier {

  static Identifier instance;

  def static createInstance(ComponentTypeSymbol comp) {
    instance = new Identifier()
    instance.checkIdentifiers(comp)
  }

  String resultName = "result";

  String inputName = "input";

  String behaviorImplName = "behaviorImpl";

  String currentStateName = "currentState";

  /**
   * Checks whether component parameter, variable, subcomponent instance, or
   * port names contain the identifier given as the parameter.
   * 
   * @param identifier The name to check
   * @return True, iff. there is at least one identifier that is equal to the
   * given identifier
   */
  def boolean containsIdentifier(String identifier, ComponentTypeSymbol component) {

    for (PortSymbol portSymbol : component.getPorts()) {
      if (portSymbol.getName().equals(identifier)) {
        return true;
      }
    }

    for (VariableSymbol parameter : component.getParameters()) {
      if (parameter.getName().equals(identifier)) {
        return true;
      }
    }

    for (VariableSymbol field : component.getFields()) {
      if (field.getName().equals(identifier)) {
        return true;
      }
    }

    for (ComponentInstanceSymbol instanceSymbol : component.getSubComponents()) {
      if (instanceSymbol.getName().equals(identifier)) {
        return true;
      }
    }
    return false;
  }

  def private void checkIdentifiers(ComponentTypeSymbol comp) {
    if (containsIdentifier("result", comp)) {
      resultName = "r__result";
    }
    if (this.containsIdentifier("input", comp)) {
      inputName = "r__input";
    }
    if (this.containsIdentifier("behaviorImpl", comp)) {
      behaviorImplName = "r__behaviorImpl";
    }

    if (this.containsIdentifier("currentState", comp)) {
      currentStateName = "r__currentState";
    }
  }

  /**
   * @return behaviorImplName
   */
  def static String getBehaviorImplName() {
    return instance.behaviorImplName;
  }

  /**
   * @return resultName
   */
  def static String getResultName() {
    return instance.resultName;
  }

  /**
   * @return inputName
   */
  def static String getInputName() {
    return instance.inputName;
  }

  /**
   * @return currentStateName
   */
  def static String getCurrentStateName() {
    return instance.currentStateName;
  }
}