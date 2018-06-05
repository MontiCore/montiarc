package components.body.automaton.transition.assignments;


/**
* Invalid model.  
* @implements [Wor16] AC5: The automaton’s valuations and assignments use only
* allowed Java/P modeling elements.
*/
component UseOfForbiddenExpressions {

    port 
        in Integer i,
        out String x;
        
    int y;

    automaton ForbiddenExpressions {
        state S;
        initial S;
    
        S -> S [i instanceof Integer] / {"Hi"}; //forbidden instanceof expression
        
        
    }
}