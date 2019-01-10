package components.body.automaton;

import components.body.subcomponents._subcomponents.HasGenericInputAndOutputPort;

/*
 * Valid model
 */
component AutomatonWithGenerics extends HasGenericInputAndOutputPort<String> {

    automaton {
        state State1, State2;
        initial State1;

        State1 -> State2 [tIn != null] / {tOut = tIn};
    }
}