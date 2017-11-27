package invalid;

/*
 * Component used to test the correctness of the context condition InputPortChangedInCompute.
 */
component ChangesIncomingPortInCompute {

	port
		in Integer incomingInt,
		out Integer outgoingInt;

	compute {

		incomingInt = 10; // Error: incoming port was changed
		outgoingInt = incomingInt;
		incomingInt++; //Error
		++incomingInt; //Error
		incomingInt += 10; // Error
		outgoingInt += 10;
	}
}