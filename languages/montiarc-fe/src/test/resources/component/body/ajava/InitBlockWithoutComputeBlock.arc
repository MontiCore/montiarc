package component.body.ajava;

/**
 * Invalid model. Components with init block must provide a compute
 * block as well.
 */
component InitBlockWithoutComputeBlock {

	Integer integerVariable;
	String stringVariable;

	init {
		integerVariable = 10;
		stringVariable = "Test String";
	}
}