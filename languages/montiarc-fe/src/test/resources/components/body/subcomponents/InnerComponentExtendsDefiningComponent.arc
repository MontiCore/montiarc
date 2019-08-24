/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

/**
* Invalid model. Inner must not extend its defining component.
* @implements [Hab16] R12: An inner component type definition must not extend
* the component type in which it is defined. (p. 68, lst. 3.47)
*/

component InnerComponentExtendsDefiningComponent {

  component Inner extends InnerComponentExtendsDefiningComponent {
  }

}
