/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

component ReconnectionTest {

  component Inner {

    port in String strIn;
    port out String strOut;

    component InnerInner innerinner {
      port in String strIn;
      port out String strOut;

      compute {
        strOut = strIn;
      }
    }

    connect strIn -> innerinner.strIn;
    connect innerinner.strOut -> strOut;
  }

  component Inner a;
  component Inner b;

  port in String strIn;
  port out String strOut;

  modeautomaton {

    mode M1 {
      use a,b;
      connect strIn -> a.strIn;
      connect b.strOut -> strOut;
      connect a.strOut -> b.strIn;
    }
    mode M2 {
      use a,b;
      connect strIn -> b.strIn;
      connect a.strOut -> strOut;
      connect b.strOut -> a.strIn;
    }

    initial M1;

    M1 -> M2 [strIn.equals("M2")];
    M2 -> M1 [strIn.equals("M1")];
  }
}