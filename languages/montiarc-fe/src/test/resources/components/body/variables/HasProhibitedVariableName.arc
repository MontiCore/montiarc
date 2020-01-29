/* (c) https://github.com/MontiCore/monticore */
package components.body.variables;

/*
 * Invalid model.
 * Uses prohibited variable names.
 */
 component HasProhibitedVariableName{
  String r__input;
  String r__currentState;
  String r__result;
  String r__behaviorImpl;

  automaton Implementation{
    state Initial;
    initial Initial;
  }
}