/* (c) https://github.com/MontiCore/monticore */

/*
 * Invalid model: The component contains subcomponents sub1 and sub2 of its own
 * type (the component directly references itself). The components are source
 * and target of a connector.
 */
component SelfReferentialComponentWithComposition {

  port in int i;
  port out int o;

  SelfReferentialComponentWithComposition sub1, sub2;

  i -> sub1.i;

  sub1.o -> sub2.i;

  sub2.o -> o;

}
