package components;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;

/*
 * Valid model.
 */
component NameSpaceHidingSuper {

    port
        in String sIn;
    
    component HasStringInputAndOutput foo;
}
