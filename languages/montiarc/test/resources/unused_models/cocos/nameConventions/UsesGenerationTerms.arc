/* (c) https://github.com/MontiCore/monticore */
package components.body.variables;

/*
 * Valid model.
 *
 * Has variable names which could create conflicts in the generation process.
 * This model is therefore used to assert that the conflict resolving is
 * working in the generator.
 */
component UsesGenerationTerms{

  String input;
  String output;
  String behaviorImpl;
  String currentState;

  automaton Test {
    state Initial;

    initial Initial;
  }
}
