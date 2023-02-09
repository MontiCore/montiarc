/* (c) https://github.com/MontiCore/monticore */
package example;

component Setup {
  port in String sIn;
  port out String sOut;

  component Inner {
    port in String sIn;
    port out String sOut;
  }

  Inner i1;
  Inner i2;

  sIn -> i1.sIn;
  i1.sOut -> i2.sOut;
  i2.sOut -> sOut;
}
