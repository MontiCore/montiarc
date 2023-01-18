/* (c) https://github.com/MontiCore/monticore */
package typeParamNameIsNoReservedKeyword;

/**
 * Invalid model, as long as 'Reserved[0-10]' and 'Keyword[0-10]' are keywords which must not be used as port names.
 */
component TypeParamNamesAreKeywords <
  Reserved0,
  Keyword0 extends int,
  Keyword1 extends int & double,
  Reserved1
> { }
