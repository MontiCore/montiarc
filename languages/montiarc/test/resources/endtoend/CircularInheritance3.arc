/* (c) https://github.com/MontiCore/monticore */

/*
 * Invalid model: The inner component directly extends itself (circular inheritance).
 */
component CircularInheritance3 {
  component Inner extends Inner { }
}
