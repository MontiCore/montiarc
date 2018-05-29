package components.body.automaton.transition.assignments;


/**
* Invalid model.  
* @implements [Wor16] AC5: The automaton’s valuations and assignments use only
* allowed Java/P modeling elements.
*/
component UseOfForbiddenExpressions {

    port 
        in Integer i,
        in Double[] o,
        out String x;
        
    int y;

    automaton ForbiddenExpressions {
        state S;
        initial S;
    
        S [o[1] == 2.0 && // forbidden array expression
        i instanceof Integer]//forbidden instanceof expression
        / {++y, --y,         //forbidden prefix expressions
        (Object) o,  //forbidden cast expressions
         x = "s"};
    }
}