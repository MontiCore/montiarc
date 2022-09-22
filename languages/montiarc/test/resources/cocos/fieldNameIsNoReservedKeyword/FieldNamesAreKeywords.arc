/* (c) https://github.com/MontiCore/monticore */
package fieldNameIsNoReservedKeyword;

/**
 * Invalid model, as long as 'reserved[0-10]' and 'keyword[0-10]' are keywords which must not be used as port names.
 */
component FieldNamesAreKeywords {
  int reserved0 = 0;
  boolean keyword0 = true;
}