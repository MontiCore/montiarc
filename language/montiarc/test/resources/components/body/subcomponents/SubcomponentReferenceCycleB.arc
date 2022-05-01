/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

/**
 * Invalid model. SubcomponentReferenceCycleA references SubcomponentReferenceCycleB
 * as a subcomponent.
 *
 * @implements [Hab16] R13: Subcomponent reference cycles are forbidden. (p. 68,
 * lst. 3.48)
 */
component SubcomponentReferenceCycleB {

  component SubcomponentReferenceCycleA myB;

}
