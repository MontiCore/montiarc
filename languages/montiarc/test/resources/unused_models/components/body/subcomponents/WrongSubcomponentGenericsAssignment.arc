/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents._subcomponents.HasGenericInputAndOutputPort;

/*
 * Invalid component.
 * Generic types of components are missing or incorrectly assigned.
 *
 * Formerly named "CG4" in MontiArc3.
 *
 * @implements [Hab16] R9: If a generic component type is instantiated as
 *  a subcomponent, all generic parameters have to be assigned.
 *  (p.66, Lst.3.44)
 */
component WrongSubcomponentGenericsAssignment {

  component HasGenericInputAndOutputPort pWrong1;
      // ERROR: Type parameter 'T' of HasGenericInputAndOutputPort is not assigned

  component HasGenericInputAndOutputPort pWrong2;
      // ERROR: Type parameter 'T' of HasGenericInputAndOutputPort is not assigned

  component HasGenericInputAndOutputPort<T> pWrong3;
      // ERROR: Can not resolve type parameter 'T'

  component components.body.subcomponents._subcomponents
      .HasGenericInputAndOutputPort<java.lang.String> pCorrect;

}
