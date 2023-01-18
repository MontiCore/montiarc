/* (c) https://github.com/MontiCore/monticore */
package componentInstanceNameIsNoReservedKeyword;

/**
 * Invalid model, as long as 'reserved[0-10]' and 'keyword[0-10]' are keywords which must not be used as port names.
 */
component InstanceNamesAreKeywords {

  component Inner reserved0, reserved1 {}
  Inner keyword0, keyword1;
}
