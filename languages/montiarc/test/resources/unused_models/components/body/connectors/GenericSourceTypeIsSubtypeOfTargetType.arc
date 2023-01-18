/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

import components.body.subcomponents._subcomponents.HasGenericInput;
/*
 * Valid model.
 */
component GenericSourceTypeIsSubtypeOfTargetType<T extends Number> {
  port in T inT;

  component HasGenericInput<Number> sub;

  connect inT -> sub.inT;
}
