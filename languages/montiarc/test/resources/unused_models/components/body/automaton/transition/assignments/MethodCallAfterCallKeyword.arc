/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

/*
 * Invalid model.
 */
component MethodCallAfterCallKeyword {

   port
     in int a,
     out Integer b,
     out Integer c;


    automaton UseOfUndeclaredField {
        state A;
        initial A;

        A -> A /{c.toString(), b = a.toString()}; //wrong: there must be the call keyword before the method call

    }
}
