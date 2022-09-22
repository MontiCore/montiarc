/* (c) https://github.com/MontiCore/monticore */
package identifiersAreNoJavaKeywords;

/**
 * Valid model.
 */
component WithoutJavaKeywords<NoJavaKeyword0, NoJavaKeyword1> (int noo, int javaa, int keywordsHere = 23) {
  port in int noJavaKeyword0, noJavaKeyword1,
       in int noJavaKeyword2,
       out int noJavaKeyword3, noJavaKeyword4,
       in int noJavaKeyword5;
  port out int noJavaKeyword6;

  int noJavaKeyword7 = 0;
  int noJavaKeyword8 = 0, noJavaKeyword9 = 0;

  component NoJavaKeyword noJavaKeyword10 {}

  NoJavaKeyword noJavaKeyword11, noJavaKeyword12;
}