/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

/*
 * Valid model.
 *
 * Has parameter names which could create conflicts in the generation process.
 * This model is therefore used to assert that the conflict resolving is
 * working in the generator.
 */
component HasConflictingParameterNames(
  String input,
  String output,
  String behaviorImpl,
  String currentState
){
  automaton Test {
    state Initial;

    initial Initial;
  }
}
