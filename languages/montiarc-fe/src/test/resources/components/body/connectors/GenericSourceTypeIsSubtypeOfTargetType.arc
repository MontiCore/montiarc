package components.body.connectors;

import components.body.subcomponents._subcomponents.HasGenericInput;
/*
 * Valid model.
 */
component GenericSourceTypeIsSubtypeOfTargetType<T extends Number & String> {
	port in T inT;

	component HasGenericInput<Number> sub;

	connect inT -> sub.inT;
}