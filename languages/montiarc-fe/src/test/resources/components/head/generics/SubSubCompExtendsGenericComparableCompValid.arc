package components.head.generics;

/**
* Valid model.
*
* @implements [Hab16] R15: Components that inherit from a generic 
* component have to assign concrete type arguments to all generic type parameters.
* (p. 69, lst. 3.50)
*/
component SubSubCompExtendsGenericComparableCompValid extends SubCompExtendsGenericComparableCompValid<String> {
  // Empty body
}
