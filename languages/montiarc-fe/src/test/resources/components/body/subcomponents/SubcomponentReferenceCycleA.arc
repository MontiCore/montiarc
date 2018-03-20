package components.body.subcomponents;

/**
* Invalid model. SubcomponentReferenceCycleB references SubcomponentReferenceCycleA
* as a subcomponent.
*
* @implements [Hab16] R13: Subcomponent reference cycles are forbidden. (p. 68,
* lst. 3.48)
*/

component SubcomponentReferenceCycleA {

  component SubcomponentReferenceCycleB myB;

}
