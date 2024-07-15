/* (c) https://github.com/MontiCore/monticore */

package mceffect.steamboiler;

component Controller {

  port in double waterLevel;
  port <<delayed>> out boolean signal;

  automaton {
    initial state Anon;
    Anon -> Anon [waterLevel == 0]/{signal = false;};
    Anon -> Anon [waterLevel != 0]/{signal = true;};
  }
}