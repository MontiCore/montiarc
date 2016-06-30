package c;

import c.testTypes.*;

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