package components.body.subcomponents._subcomponents;

import types.MyGenericImpl;
import types.GenericInterface;

/*
 * Valid model.
 */
component GenericIfRequired<T> {
    port
        in MyGenericImpl<T> implIn,
        in GenericInterface<T> ifIn;
}