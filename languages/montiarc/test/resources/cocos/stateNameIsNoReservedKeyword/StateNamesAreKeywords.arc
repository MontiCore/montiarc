/* (c) https://github.com/MontiCore/monticore */
package stateNameIsNoReservedKeyword;

/**
 * Invalid model, as long as 'reserved[0-10]' and 'keyword[0-10]' are keywords which must not be used as port names.
 */
component StateNamesAreKeywords (int paramDummy1, float paramDummy2) {

  automaton {
    initial state reserved0;
    state keyword0;
  }
}