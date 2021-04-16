/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExistAndFit;

component EverythingMismatched {

  //source and target do not exist
  nonExistentSource1 -> nonExistentTarget1;

  //source and target type mismatch
  port in String sIn1;
  port out int iOut1;
  sIn1 -> iOut1;

  //source and target direction mismatch
  port in String sIn2;
  port out String sOut2;
  sOut2 -> sIn2;

  //source does not exist
  port out String sOut3;
  nonExistentSource2 -> sOut3;

  //source direction mismatch
  port out String sOut4, sOut5;
  sOut4 -> sOut5;

  //target does not exist
  port in String sIn6;
  sIn6 -> nonExistentTarget2;

  //target direction mismatch
  port in String sIn7, sIn8;
  sIn7 -> sIn8;
}