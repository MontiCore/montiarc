/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;


/**
 * Invalid model.
 *
 * @implements [Wor16] AC5: The automaton’s valuations and assignments use
 * only allowed Java/P modeling elements. (p.100, Lst. 5.15)
 */
component UseOfForbiddenExpressions {

    port 
        in Integer i,
        out String x;
        
    int y;

    automaton ForbiddenExpressions {
        state S;
        initial S;
    
        S -> S [i instanceof Integer & i==2 | ("Hi".equals("Test") ^ true)] / {"Hi"};
        //forbidden instanceof expression and binary OR, AND, and XOR
    }
}
