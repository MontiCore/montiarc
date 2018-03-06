package components.head.inheritance;

import types.SuperComp;

/**
* Invalid model. Config args in wrong order.
*
* @implements [Hab16] R14: Components that inherit from a parametrized 
* component provide configuration parameters with the same types, 
* but are allowed to provide more parameters. (p. 69, lst. 3.49) 
*/
component SubCompWrong2(double d, int a, String b) extends SuperComp {

}