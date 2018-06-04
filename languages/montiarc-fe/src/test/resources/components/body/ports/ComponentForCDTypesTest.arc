package components.body.ports;

import types.CDTestTypes.*;

/*
 * Valid model.
 * TODO Add test
 */
component ComponentForCDTypesTest {
	port 
	  in TypeWithFields portInUnqualified,
		in c.testTypes.TypeWithFields portInQualified,
		in SubTypeOne,
		in SubTypeTwo,
		in InterfaceTypeThree,
		in EnumType,
		in GenericClassType<java.lang.String>,
		in GenericInterfaceType<String>;
}