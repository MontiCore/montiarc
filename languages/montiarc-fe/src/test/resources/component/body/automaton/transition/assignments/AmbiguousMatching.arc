package component.body.automaton.transition.assignments;

/**
 * Invalid model. Fails matching assignment pots twice.
 */
component AmbiguousMatching {

    port 
        in String i,
        out int s,      
        out int a;

    automaton AmbiguousMatching {
    	state Idle;	
    	
    	// This is wrong, because we can't find a match in variables or 
    	// outputs for a String type
    	initial Idle / {"Wrong"};
    	
    	// Stimulus is right, because only i matches String. The rreaction is 
    	// wrong because we have more than one match for an int type
    	Idle {"Right"} / {5};
    }
}