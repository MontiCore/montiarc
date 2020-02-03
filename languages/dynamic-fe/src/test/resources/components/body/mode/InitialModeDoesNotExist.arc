/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 */
component InitialModeDoesNotExist {

  port
    in Integer i,
    in String m,
	out Integer o;

  modeautomaton {
    mode M1 {}
    mode M2 {}

    initial M0;

    M1 -> M2;
  }

}