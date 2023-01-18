/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

import components.body.subcomponents._subcomponents.HasGenericInput;

/*
 * Invalid model.
 *
 * @implements [Hab16] R8: The target port in a connection has to be compatible
 * to the source port, i.e., the type of the target port is identical or a
 * supertype of the source port type. (p. 66, lst. 3.43)
 */
component GenericSourceTypeNotSubtypeOfTargetType<T> {
  port in T inT;

  component HasGenericInput<String> sub;

  connect inT -> sub.inT;
    // ERROR: Source Type 'T' is not a subtype of target type 'String'
}
