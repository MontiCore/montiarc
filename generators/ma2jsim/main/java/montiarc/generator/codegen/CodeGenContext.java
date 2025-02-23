/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

/**
 * This context can be used to store and share a state across visitors during generation.
 */
public class CodeGenContext {

  protected boolean isInGenericTypeExpression;

  public CodeGenContext() {
    isInGenericTypeExpression = false;
  }

  /**
   * @return true if we are currently traversing a generic type expression
   */
  public boolean isInGenericTypeExpression() {
    return isInGenericTypeExpression;
  }

  public void setInGenericTypeExpression(boolean value) {
    isInGenericTypeExpression = value;
  }
}
