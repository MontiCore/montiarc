package components.body.ports;

/*
 * Invalid model.
 *
 * Formerly named "CG5<T>" in MontiArc3.
 *
 * @implements [Hab16] CV7: Avoid using implicit and explicit names for
 *                            elements with the same type. (p. 72, Lst. 3.54)
 * TODO Implement CV7 CoCo and adjust test
 */
component GenericPortsWithAndWithoutNames<T> {
  
  port 
    in T,
          //WARNING: The type 'T' is not unique, so the implicit port name 't' should not be used
    out T named,
    out String;
}