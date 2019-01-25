package portTest;

/*
 * Valid model.
 */
<<deploy>> component PortTest{
	component InComp ic;
	component OutComp oc;
	
	connect oc.outPort -> ic.inPort;
}
