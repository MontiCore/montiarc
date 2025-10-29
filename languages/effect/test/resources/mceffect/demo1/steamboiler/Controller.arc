/* (c) https://github.com/MontiCore/monticore */
package steamboiler;

component Controller {

  port in double waterLevel;
  port out boolean signal;

  <<delayed>> automaton {
    initial state Anon;
    Anon -> Anon [waterLevel == 0 ] waterLevel / { signal = false; }
    Anon -> Anon [waterLevel != 0 ] waterLevel / { signal = true; }
  }
}
