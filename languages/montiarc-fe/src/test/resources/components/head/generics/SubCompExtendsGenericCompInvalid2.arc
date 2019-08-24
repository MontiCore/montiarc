/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

/*
 * Invalid component.
 * The second generic type parameters of super component SuperGenericComp
 * has to extend Number.
 *
 * @implements [Hab16] R15: Components that inherit from a generic component
 *  have to assign concrete type arguments to all generic type parameters.
 *  (p.69, lst. 3.50)
 */
component SubCompExtendsGenericCompInvalid2 extends SuperGenericComp<String, String> {

}
