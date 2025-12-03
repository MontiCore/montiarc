/* (c) https://github.com/MontiCore/monticore */

/*
 * Invalid model: The component contains a subcomponent subB, defined by
 * external component 'SelfReferentialComponent4A', that contains a
 * subcomponent subA of this component's type (the component indirectly
 * references itself).
 */
component SelfReferentialComponent4B {

  SelfReferentialComponent4A subA;

}
