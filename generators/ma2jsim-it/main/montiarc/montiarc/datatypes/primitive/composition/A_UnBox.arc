/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

// Should compile, by allowing boxing of compatible types
component A_UnBox {

  // Boolean -> boolean -> Boolean
  BooleanWrapperOut booleanWrapperO;
  BooleanForward booleanF;
  BooleanWrapperIn booleanWrapperI;
  booleanWrapperO.p -> booleanF.pIn;
  booleanF.pOut -> booleanWrapperI.p;

  // Byte -> byte -> Byte
  ByteWrapperOut byteWrapperO;
  ByteForward byteF;
  ByteWrapperIn byteWrapperI;
  byteWrapperO.p -> byteF.pIn;
  byteF.pOut -> byteWrapperI.p;

  // Character -> char -> Character
  CharWrapperOut charWrapperO;
  CharForward charF;
  CharWrapperIn charWrapperI;
  charWrapperO.p -> charF.pIn;
  charF.pOut -> charWrapperI.p;

  // Double -> double -> Double
  DoubleWrapperOut doubleWrapperO;
  DoubleForward doubleF;
  DoubleWrapperIn doubleWrapperI;
  doubleWrapperO.p -> doubleF.pIn;
  doubleF.pOut -> doubleWrapperI.p;

  // Float -> float -> Float
  FloatWrapperOut floatWrapperO;
  FloatForward floatF;
  FloatWrapperIn floatWrapperI;
  floatWrapperO.p -> floatF.pIn;
  floatF.pOut -> floatWrapperI.p;

  // Integer -> int -> Integer
  IntWrapperOut intWrapperO;
  IntForward intF;
  IntWrapperIn intWrapperI;
  intWrapperO.p -> intF.pIn;
  intF.pOut -> intWrapperI.p;


  // Long -> long -> Long
  LongWrapperOut longWrapperO;
  LongForward longF;
  LongWrapperIn longWrapperI;
  longWrapperO.p -> longF.pIn;
  longF.pOut -> longWrapperI.p;

  // Short -> short -> Short
  ShortWrapperOut shortWrapperO;
  ShortForward shortF;
  ShortWrapperIn shortWrapperI;
  shortWrapperO.p -> shortF.pIn;
  shortF.pOut -> shortWrapperI.p;
}
