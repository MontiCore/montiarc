package components.head.generics;

/**
* Valid component.
* 
* @implements [Hab16] R15: Components that inherit from a generic 
* component have to assign concrete type arguments to all generic type parameters. 
* (p. 69, lst. 3.50)
*/
component SubCompExtendsGenericCompValid extends SuperGenericComp<String, Integer> {

}
