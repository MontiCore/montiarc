/* (c) https://github.com/MontiCore/monticore */
package componentTypeNameIsNoReservedKeyword;

/**
 * Invalid model, as long as 'Reserved[0-10]', 'Keyword[0-10]', 'TypeNamesAreKeywords' are keywords which must not be
 * used as port names.
 */
component TypeNamesAreKeywords {
  component Reserved0 {}
  Reserved0 dummy0, dummy1;

  component Keyword0 dummy2, dummy3 {}
}