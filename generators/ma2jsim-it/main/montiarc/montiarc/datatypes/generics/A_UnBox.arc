/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

// Should compile, by allowing boxing of compatible types
component A_UnBox {

  // Boolean -> boolean -> Boolean
  Out<Boolean> booleanWrapperO;
  Forward<boolean> booleanF;
  In<Boolean> booleanWrapperI;
  booleanWrapperO.p -> booleanF.pIn;
  booleanF.pOut -> booleanWrapperI.p;

  // Byte -> byte -> Byte
  Out<Byte> byteWrapperO;
  Forward<byte> byteF;
  In<Byte> byteWrapperI;
  byteWrapperO.p -> byteF.pIn;
  byteF.pOut -> byteWrapperI.p;

  // Character -> char -> Character
  Out<Character> charWrapperO;
  Forward<char> charF;
  In<Character> charWrapperI;
  charWrapperO.p -> charF.pIn;
  charF.pOut -> charWrapperI.p;

  // Double -> double -> Double
  Out<Double> doubleWrapperO;
  Forward<double> doubleF;
  In<Double> doubleWrapperI;
  doubleWrapperO.p -> doubleF.pIn;
  doubleF.pOut -> doubleWrapperI.p;

  // Float -> float -> Float
  Out<Float> floatWrapperO;
  Forward<float> floatF;
  In<Float> floatWrapperI;
  floatWrapperO.p -> floatF.pIn;
  floatF.pOut -> floatWrapperI.p;

  // Integer -> int -> Integer
  Out<Integer> intWrapperO;
  Forward<int> intF;
  In<Integer> intWrapperI;
  intWrapperO.p -> intF.pIn;
  intF.pOut -> intWrapperI.p;


  // Long -> long -> Long
  Out<Long> longWrapperO;
  Forward<long> longF;
  In<Long> longWrapperI;
  longWrapperO.p -> longF.pIn;
  longF.pOut -> longWrapperI.p;

  // Short -> short -> Short
  Out<Short> shortWrapperO;
  Forward<short> shortF;
  In<Short> shortWrapperI;
  shortWrapperO.p -> shortF.pIn;
  shortF.pOut -> shortWrapperI.p;
}
