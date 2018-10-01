package components;

/*
 * Valid model.
 * Used as the first test model to implement the basic generator test framework.
 */
component EmptyComponent(Integer test){

  port in List<Integer> inIntEmpty;
  port out String outString;

  // Empty body
  automaton Test{
    state Start, End;
    initial Start;
    Start -> End;
  }
}