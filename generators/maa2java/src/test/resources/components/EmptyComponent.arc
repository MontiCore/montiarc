package components;

/*
 * Valid model.
 * Used as the first test model to implement the basic generator test framework.
 */
component EmptyComponent{

  port in List<Integer> inInt;

  // Empty body
  automaton Test{
    state Start;
    initial Start;
  }
}