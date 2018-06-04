package components.body.variables;

/**
 * Invalid model. NoData assigned to variables
 * @Implements [Wor16] AT3: The special literal value NoDatais not used for variables.
 */
component NoDataNotAssigned {
	port
	  in Integer input,
	  out Integer output;

	Integer buffer;


	automaton NoDataNotAssigned{
	  state S;
	  initial S;

	  S / {buffer = --};
	}
}