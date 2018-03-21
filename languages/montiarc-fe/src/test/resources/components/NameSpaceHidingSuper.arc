package components;

import components.body.ports.ImplicitAndExplicitPortNaming;

/*
 * Valid model.
 */
component NameSpaceHidingSuper {

    port
        in String sIn;
    
    component ImplicitAndExplicitPortNaming foo;
}
