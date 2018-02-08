package components.body.autoconnect;

import components.body.autoconnect.dummycomponents.*;

/** 
 * Invalid model. Various missing autoconnect partners.
 *
 * @implements [Hab16] CV5: In decomposed components, all ports should be used in at least one connector. (p.71 Lst. 3.52)
 * @implements [Hab16] CV6: All ports of subcomponents should be used in at least one connector. (p.72 Lst. 3.53)
 */
component AutoConnectPorts {
	autoconnect port;
	
	port 
		in String strIn, // connected
		in Integer intIn, // connected
		out String strOut, // connected
		out String strOut2, // not connected
		out Integer intOut; // connected

	/*
		 in: strIn (String)
		 out: data (String)

		 data unconnected
	*/
	component A;

	/*
		 in: strIn (String), intIn (Integer)
		 out: myInt (Integer)

		 strIn unconnected
	*/
	component B;

	/*
		in: intIn (Integer)
		out: bb (bool)
	*/
	component C;

	/*
		in: dataSthElse (String), myInt, bool
		out: strOut, sthElse (String), intOut

		dataSthElse unconnected
		sthElse unconnected
	*/
	component D;

	connect strIn -> a.strIn;
	connect c.bb -> d.bool;
	connect d.intOut -> intOut;
	
	/** expected additional connectors:
	intIn -> c.intIn;
	intIn -> b.intIn
	
	d.strOut -> strOut;
	b.myInt -> d.myInt;
    	
	*/
}