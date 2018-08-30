package components.body.connectors;

import components.body.subcomponents._subcomponents.GenericIfRequired;
import types.GenericIfProvider;

/*
 * Invalid model.
 *
 * @implements [Hab16] R8: The target port in a connection has to be compatible
 * to the source port, i.e., the type of the target port is identical or a
 * supertype of the source port type. (p. 66, lst. 3.43)
 */
component GenericIfUsage<T> {
    
    //out MyGenericImpl<T> implOut, GenericInterface<T> ifOut
    component GenericIfProvider<T> p1, p2;
    
    //in MyGenericImpl<T> implIn, in GenericInterface<T> ifIn
    component GenericIfRequired<T> r1, r2;
    
    // all same type
    connect p1.implOut -> r1.implIn;
    connect p1.ifOut -> r1.ifIn;
    
    // invalid
    connect p2.implOut -> r2.ifIn;    
    connect p2.ifOut -> r2.implIn;

}
