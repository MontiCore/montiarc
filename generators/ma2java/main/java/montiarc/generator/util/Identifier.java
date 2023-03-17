/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

/**
 * This class checks whether component parameter, variable, subcomponent instance, or port names are equal to names used
 * in the generated code.
 */
public class Identifier {

  protected final static String CURRENT_STATE_NAME = "currentState";

  protected String currentStateName = CURRENT_STATE_NAME;

  public String getCurrentStateName() {
    return this.currentStateName;
  }
}
