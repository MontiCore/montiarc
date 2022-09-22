/* (c) https://github.com/MontiCore/monticore */
package portNameIsNoReservedKeyword;

/**
 * Invalid model, as long as 'reserved[0-10]' and 'keyword[0-10]' are keywords which must not be used as port names.
 */
component PortNamesAreKeywords (int paramDummy1, float paramDummy2) {

  port in int reserved0, reserved1,
       out int reserved2, reserved3,
       in int reserved4,
       in int reserved5,
       out int reserved6;

  port out int keyword0, keyword1;

  // Other dummies
  int fieldDummy1, fieldDummy2;
  float fieldDummy3;

  component CompDummy compDummy1 {}
  CompDummy compDummy2;
}