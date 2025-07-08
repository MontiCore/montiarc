/* (c) https://github.com/MontiCore/monticore */
package montiarc.compnames;

component WithModes {
  port sync in boolean i;

  Leaf alwaysPresent;

  mode automaton {
    initial mode M1 {
      Leaf inMode1;
      Leaf sharedNameInModes;
    }

    mode M2 {
      Leaf inMode2;
      Leaf sharedNameInModes;
    }

      M1 -> M2;
      M2 -> M1;
  }
}
