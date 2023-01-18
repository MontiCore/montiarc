/* (c) https://github.com/MontiCore/monticore */
package parameterNameIsNoReservedKeyword;

/**
 * Invalid model, as long as 'reserved[0-10]' and 'keyword[0-10]' are keywords which must not be used as port names.
 */
component ParameterNamesAreKeywords (
  int reserved0,
  float reserved1,
  double keyword0 = 23.2,
  boolean keyword1 = true
) { }
