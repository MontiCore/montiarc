/* (c) https://github.com/MontiCore/monticore */
package parser;

/*
 * Valid model using ArcAG.
 */
component AG {
  port in int a;
  port out int b;

  assume: true;
  guarantee: b == a;
}
