/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

/*
 * Invalid model.
 * Mixing implicit and explicit identifiers for ports, which also results
 *   in ambiguous identifiers.
 *
 * Formerly named "E1" in MontiArc3.
 *
 * @implements [Hab16] CV7: Avoid using implicit and explicit names for
 *                            elements with the same type. (p. 72, Lst. 3.54)
 * @implements [Hab16] B1: All names of model elements within a component
                              namespace have to be unique (p.59)
 * TODO CV7 CoCo and Test
 */
component ImplicitAndExplicitPortNaming {

  port
    in String,
      // Warning: Implicit naming should be used for unique port types only
    out String,
      // Warning: Implicit naming should be used for unique port types only
      // ERROR: Identifier 'named' of type 'java.lang.Boolean' is already defined
    in Boolean named,
    out Boolean named,
      // ERROR: Identifier 'named' of type 'java.lang.Boolean' is already defined
    in Object,
    in Throwable,
      // Warning: Implicit naming should be used for unique port types only
    out Throwable throwable;
      // ERROR: Identifier 'named' of type 'java.lang.Boolean' is already defined
}
