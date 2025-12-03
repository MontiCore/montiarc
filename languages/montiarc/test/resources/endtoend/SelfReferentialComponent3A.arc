/* (c) https://github.com/MontiCore/monticore */

/*
 * Invalid model: The component contains a subcomponent subB, defined by
 * inner component 'SelfReferentialComponent3B', that contains a
 * subcomponent subA of this component's type (the component indirectly
 * references itself). Likewise, inner component 'SelfReferentialComponent3B'
 * contains a subcomponent subA, defined by the outer component, that contains
 * a subcomponent subB of the inner component's type.
 */
component SelfReferentialComponent3A {

  SelfReferentialComponent3B subB;

  component SelfReferentialComponent3B {
    SelfReferentialComponent3A subA;
  }

}
