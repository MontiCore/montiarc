package robotmodels.data;

/* 	
	Wenn "choosePort" den Wert "true" empfängt wird "inPort1" verwendet.
 	Wenn "choosePort" den Wert "false" empfängt wird "inPort2" verwendet.
 	Initial wird "inPort1" verwendet
*/

component Switch <T>{

    port
    	in T inPort1,
    	in T inPort2,
    	in Boolean choosePort,
    	
    	out T outPort;
    	
}