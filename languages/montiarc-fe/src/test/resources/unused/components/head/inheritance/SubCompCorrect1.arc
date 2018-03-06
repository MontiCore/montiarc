package components.head.inheritance;

import types.SuperComp;

/**
* Correct component.
* @implements [Hab16] R14: Components that inherit from a parametrized 
* component provide configuration parameters with the same types, 
* but are allowed to provide more parameters. (p. 69, lst. 3.49) 
*/
component SubCompCorrect1(int a, String b, double d) extends SuperComp {

}