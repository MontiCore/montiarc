/* (c) https://github.com/MontiCore/monticore */

/*
 * Invalid model: The component contains a subcomponent sub of its own type
 * (the component directly references itself).
 */
component SelfReferentialComponent1 {

  SelfReferentialComponent1 sub;

}
