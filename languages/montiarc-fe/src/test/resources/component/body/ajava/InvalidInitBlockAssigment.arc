package component.body.ajava;

/**
 * Invalid model. Cannot assign String "yo" to integer variable
 */
component InvalidInitBlockAssigment {

	var Integer i;

	init {
		i = "yo";
	}

	compute {
	}
}