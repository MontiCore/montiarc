/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.delegated

class StateHavingBehaviorDelegateImpl {

  fun compute(component: StateHavingBehaviorDelegate.Interface) {
    component.outputValue = state
    state *= component.inputValue!!
  }

  fun initialize(component: StateHavingBehaviorDelegate.Interface) {
    component.outputValue = -3
  }

  var state = 1
}