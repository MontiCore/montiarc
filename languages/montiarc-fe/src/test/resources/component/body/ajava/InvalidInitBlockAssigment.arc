package component.body.ajava;

/**
 * Invalid model. Cannot assign String "yo" to integer variable
 */
component InvalidInitBlockAssigment {

	Integer i;

	init {
		i = "yo";
	}

	compute {
	}
}