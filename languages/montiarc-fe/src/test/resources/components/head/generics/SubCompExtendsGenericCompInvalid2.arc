package components.head.generics;

/**
* Invalid component. The second generic type parameters of super component SuperGenericComp has to extend Number.
* (p. 69, lst. 3.50)
*/

component SubCompExtendsGenericCompInvalid2 extends SuperGenericComp<String, String> {

}
