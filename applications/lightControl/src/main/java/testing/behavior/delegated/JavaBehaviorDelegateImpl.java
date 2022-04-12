/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.delegated;

public class JavaBehaviorDelegateImpl {

  public void compute(JavaBehaviorDelegate.Interface component) {
    component.setOutputValue(4 * component.getInputValue());
  }

  public void initialize(JavaBehaviorDelegate.Interface component) {

  }
}
