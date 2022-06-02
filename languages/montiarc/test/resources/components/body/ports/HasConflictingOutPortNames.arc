/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

/*
 * Valid model.
 *
 * Has port names which could create conflicts in the generation process.
 * This model is therefore used to assert that the conflict resolving is
 * working in the generator.
 */
component HasConflictingOutPortNames{

  port out String input;
  port out String output;
  port out String behaviorImpl;
  port out String currentState;

  automaton Test {
    state Initial;

    initial Initial;
  }
}
