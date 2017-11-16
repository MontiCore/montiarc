package invalid;


component InitBlockWithoutAJava {

	var Integer integerVariable;
	var String stringVariable;

	init {
		integerVariable = 10;
		stringVariable = "Test String";
	}

	init {

	}

	//Error: Component has at least one init block, but it does not vontain an AJava compute method
}