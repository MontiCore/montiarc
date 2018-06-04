package components.body.ports;

import types.GenericInterface;
import types.MyEnum;
import types.InterfaceType;
import types.ImplementsInterfaceType;
import types.GenericType;

/*
 * Valid model.
 */
component JavaTypesAsPortTypes {
	port 
		in InterfaceType,
		in MyEnum,
		in ImplementsInterfaceType,
		in GenericInterface<int>,
		in GenericType<String>,
		out String;
}