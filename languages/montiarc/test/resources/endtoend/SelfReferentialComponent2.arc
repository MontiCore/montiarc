/* (c) https://github.com/MontiCore/monticore */

/*
 * Invalid model: The component contains subcomponents sub1 and sub2 of its own
 * type (the component directly references itself).
 */
component SelfReferentialComponent2 {

  SelfReferentialComponent2 sub1;
  SelfReferentialComponent2 sub2;

}
