/* (c) https://github.com/MontiCore/monticore */
package components;

/*
 * Invalid model: The component contains a subcomponent subB, defined by
 * external component 'SelfReferentialComponent4B', that contains a
 * subcomponent subA of this component's type (the component indirectly
 * references itself).
 */
component SelfReferentialComponent4A {

  SelfReferentialComponent4B subB;

}
