package components.body.ajava;

/**
 * Invalid model.
 * Cannot change values on incoming ports.
 * Test model for InputPortChangedInCompute
 *
 * @implements No literature reference, AJava CoCo
 */
component ChangeIncomingPortInCompute {

	port
		in Integer incomingInt,
		out Integer outgoingInt;

	compute {
		incomingInt = 10;             // Error: incoming port was changed
		outgoingInt = incomingInt;
		incomingInt += 10;            // Error
		outgoingInt += 10;
	}
}