/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.delegated;

import com.google.common.base.Preconditions;

public class JavaBehaviorDelegateImpl {

  public void compute(JavaBehaviorDelegate.Interface component) {
    Preconditions.checkNotNull(component.getInputValue());
    component.setOutputValue(4 * component.getInputValue());
  }

  public void initialize(JavaBehaviorDelegate.Interface component) {
    component.setOutputValue(20);
  }
}
