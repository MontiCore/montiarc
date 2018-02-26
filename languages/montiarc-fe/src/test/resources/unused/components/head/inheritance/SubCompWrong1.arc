package components.head.inheritance;

import types.SuperComp;

/**
* Invalid model. Config args not inherited.
*
* @implements [Hab16] R14: Components that inherit from a parametrized 
* component provide configuration parameters with the same types, 
* but are allowed to provide more parameters. (p. 69, lst. 3.49) 
*/
component SubCompWrong1 extends SuperComp {

}