package components.body.ports;

/*
 * Valid model.
 *
 * Has port names which could create conflicts in the generation process.
 * This model is therefore used to assert that the conflict resolving is
 * working in the generator.
 */
component HasConflictingInPortNames{

  port in String input;
  port in String output;
  port in String behaviorImpl;
  port in String currentState;

  automaton Test {
    state Initial;

    initial Initial;
  }
}