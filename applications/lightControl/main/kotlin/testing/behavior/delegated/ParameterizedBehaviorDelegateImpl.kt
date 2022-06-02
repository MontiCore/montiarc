/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.delegated

class ParameterizedBehaviorDelegateImpl(private val p: Int) {

  fun compute(component: ParameterizedBehaviorDelegate.Interface) {
    component.outputValue = component.inputValue + 2 * p
  }

  fun initialize(component: ParameterizedBehaviorDelegate.Interface) {
    component.outputValue = 52
  }
}