package components.head.generics;

/**
* Invalid component. Not all of the generic type parameters of 
* super component SuperGenericComp are assigned.
* 
* @implements [Hab16] R15: Components that inherit from a generic 
* component have to assign concrete type arguments to all generic type parameters.
* (p. 69, lst. 3.50)
*/
component SubCompExtendsGenericCompInvalid1 extends SuperGenericComp<String> {

}
