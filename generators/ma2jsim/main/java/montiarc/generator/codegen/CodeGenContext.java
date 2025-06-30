/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

/**
 * This context can be used to store and share a state across visitors during generation.
 */
public class CodeGenContext {

  protected int inGenericTypeExpressionLevel;

  public CodeGenContext() {
    inGenericTypeExpressionLevel = 0;
  }

  /**
   * @return true if we are currently traversing a generic type expression
   */
  public boolean isInGenericTypeExpression() {
    return inGenericTypeExpressionLevel > 0;
  }

  public void pushInGenericTypeExpression() {
    inGenericTypeExpressionLevel++;
  }

  public void popInGenericTypeExpression() {
    if (inGenericTypeExpressionLevel > 0) {
      inGenericTypeExpressionLevel--;
    }
  }
}
