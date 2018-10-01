package components;

/*
 * Valid model.
 * Used as the first test model to implement the basic generator test framework.
 */
component AtomicWithAutomaton(Integer test){

  port in List<Integer> inIntList;
  port in Integer inInt;
  port out String outString;

  automaton TestAutomaton{
    state Start, End;
    initial Start;
    Start -> End [inInt == 10] / {outString = "Hi"};
  }
}