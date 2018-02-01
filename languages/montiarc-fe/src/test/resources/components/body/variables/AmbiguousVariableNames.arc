package components.body.variables;

/**
 * Invalid model. Variable names 'a' and 'string' used twice.
 */
component AmbiguousVariableNames {
	Integer a;
	Double a;
	String string;
	String;
}