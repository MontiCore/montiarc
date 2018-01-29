package component.body.ajava;

/**
 * Invalid model. We allow only one init block per AJava component.
 */
component TwoInitBlocks {

	var Integer testVariable;
	var Integer testVariable2;

	init Block1 {
		testVariable = 10;
	}

	init Block2 {
		testVariable2 = 20;
	}
}