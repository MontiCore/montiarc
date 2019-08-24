/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

/**
* Invalid component. Generic type parameters of super component SuperGenericComp are not assigned.
* 
* @implements [Hab16] R15: Components that inherit from a generic 
* component have to assign concrete type arguments to all generic type parameters.
* (p. 69, lst. 3.50)
*/
component SubCompExtendsGenericCompInvalid0 extends SuperGenericComp {
    
}
