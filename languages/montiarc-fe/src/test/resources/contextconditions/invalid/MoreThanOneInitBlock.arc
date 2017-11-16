package invalid;

component MoreThanOneInitBlock {

	var Integer testVariable;
	var Integer testVariable2;

	init Block1 {
		testVariable = 10;
	}

	init Block2 {
		testVariable2 = 20;
	}
}