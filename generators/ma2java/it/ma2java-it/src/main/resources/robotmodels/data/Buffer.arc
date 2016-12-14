package robotmodels.data;

import robotmodels.data.types.BufferCommand;

/**
 * A Buffer that stores single elements of type T. 
 */
component Buffer<T>{

	port
		in T value,
		in BufferCommand cmd,		
		out T output;
}