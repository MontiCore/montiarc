package components.body.ajava;

/**
 * Invalid model.
 * Cannot assign String "yo" to integer variable
 *
 * @implements AJava CoCo. No literature
 */
component InvalidInitBlockAssigment {

	Integer i;

	init {
		i = "yo";
	}

	compute {
	}
}