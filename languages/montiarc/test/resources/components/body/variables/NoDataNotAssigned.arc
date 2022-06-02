/* (c) https://github.com/MontiCore/monticore */
package components.body.variables;

/*
 * Invalid model. NoData assigned to variables
 * @Implements [Wor16] AT3: The special literal value NoData is not used for variables.
 */
component NoDataNotAssigned {
	port
	  in Integer input,
	  out Integer output;

	Integer buffer;


	automaton NoDataNotAssigned{
	  state S;
	  initial S;

	  S / {buffer = --}; //invalid, since buffer is variable.
	  S / {output = --}; //valid, since output is a port.
	}
}