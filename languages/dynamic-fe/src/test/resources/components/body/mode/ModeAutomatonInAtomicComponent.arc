/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model
 */
component ModeAutomatonInAtomicComponent {

  port in String inS;

  modeautomaton {

    mode Start {}
    mode Mode2 {}

    initial Start;

    Start -> Mode2;
  }
}