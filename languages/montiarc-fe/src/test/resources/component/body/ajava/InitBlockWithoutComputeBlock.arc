package component.body.ajava;

/**
 * Invalid model. Components with init block must provide a compute
 * block as well.
 */
component InitBlockWithoutComputeBlock {

	var Integer integerVariable;
	var String stringVariable;

	init {
		integerVariable = 10;
		stringVariable = "Test String";
	}
}