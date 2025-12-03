/* (c) https://github.com/MontiCore/monticore */

/*
 * Invalid model: The component contains a subcomponent subB, defined by
 * external component 'SelfReferentialComponent5B', that contains a
 * subcomponent subA of this component's type (the component indirectly
 * references itself). The external component 'SelfReferentialComponent5B' is
 * resolved via a serialized symbol table.
 */
component SelfReferentialComponent5A {

  SelfReferentialComponent5B subB;

}
