package components.body.connectors;

import components.body.subcomponents._subcomponents.GenericIfRequired;
import types.GenericIfProvider;

/*
 * Invalid model.
 *
 * @implements TODO
 * TODO: Add test
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
