/* (c) https://github.com/MontiCore/monticore */
package components;

/*
 * Invalid model: The inner component directly extends itself (circular inheritance).
 */
component CircularInheritance3 {
  component Inner extends Inner { }
}
