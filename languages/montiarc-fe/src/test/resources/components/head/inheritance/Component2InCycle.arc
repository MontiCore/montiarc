/* (c) https://github.com/MontiCore/monticore */
package components.head.inheritance;

/*
 * Invalid model.
 *
 * @implements [Hab16] R11: Inheritance cycles of component types are
 * forbidden. (p. 67, Lst. 3.46)
 */
component Component2InCycle extends Component1InCycle {
  // Empty body
}