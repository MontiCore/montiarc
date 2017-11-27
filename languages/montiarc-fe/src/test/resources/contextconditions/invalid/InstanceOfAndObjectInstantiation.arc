package contextconditions.invalid;

component InstanceOfAndObjectInstantiation{

port in Integer[] values,
     out Integer result;

var Integer storage;

automaton InstanceOfAndObjectInstantiation{

	state S4;
	state S1, S2;
	initial S1 / {result = 255};
	
	S1->S2 [values.length == 5] {storage == 0} / {storage = new Integer(0)};
	S2->S1 [storage instanceof Object] {storage == 5} / {result = storage};
	
}
}