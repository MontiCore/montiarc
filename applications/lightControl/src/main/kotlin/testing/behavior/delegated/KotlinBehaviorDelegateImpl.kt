/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.delegated

class KotlinBehaviorDelegateImpl {

  fun compute(component: KotlinBehaviorDelegate.Interface) {
    component.outputValue = component.inputValue!! + 50
  }

  fun initialize(component: KotlinBehaviorDelegate.Interface) = Unit
}