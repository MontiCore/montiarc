/* (c) https://github.com/MontiCore/monticore */

/*
 * Invalid model: The component indirectly extends itself, as it extends
 * another component that extends this component (cyclic inheritance).
 */
component CircularInheritance2B extends CircularInheritance2A {

}
