package b;

component TestComp {
	port 
		in InterfaceType,
		in EnumType,
		in ClassTypeWithInterfaces,
		in GenericInterfaceType<int>,
		in GenericClassType<String>,
		out String;
}