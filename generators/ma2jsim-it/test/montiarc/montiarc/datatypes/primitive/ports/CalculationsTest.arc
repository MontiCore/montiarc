/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.lang.Byte;
import java.lang.Short;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Float;
import java.lang.Double;
import java.lang.Character;
import java.lang.Boolean;

<<test, ticks=3,
  inByte=Sync<Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.MIN_VALUE>,
  inShort=Sync<Short.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE>,
  inInt=Sync<int><1, -2, 0>,
  inLong=Sync<long><1L, -2L, 0L>,
  inFloat=Sync<float><1.0f, -2.0f, 0.0f>,
  inDouble=Sync<double><1.0, -2.0, 0.0>,
  inChar=Sync<char><'a', '-', '0'>,
  inBool=Sync<boolean><true, false, false>,
  expectedByte=Untimed<Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.MIN_VALUE>,
  expectedShort=Untimed<Short.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE>,
  expectedInt=Untimed<int><-1, 2, 0>,
  expectedLong=Untimed<long><-1L, 2L, 0L>,
  expectedFloat=Untimed<float><-1.0f, 2.0f, -0.0f>,
  expectedDouble=Untimed<double><-1.0, 2.0, -0.0>,
  expectedChar=Untimed<char><'a', '-', '0'>,
  expectedBool=Untimed<boolean><false, true, true>
>>
component CalculationsTest(
  SyncStream<byte> inByte,
  SyncStream<short> inShort,
  SyncStream<int> inInt,
  SyncStream<long> inLong,
  SyncStream<float> inFloat,
  SyncStream<double> inDouble,
  SyncStream<char> inChar,
  SyncStream<boolean> inBool,
  UntimedStream<byte> expectedByte,
  UntimedStream<short> expectedShort,
  UntimedStream<int> expectedInt,
  UntimedStream<long> expectedLong,
  UntimedStream<float> expectedFloat,
  UntimedStream<double> expectedDouble,
  UntimedStream<char> expectedChar,
  UntimedStream<boolean> expectedBool
) {
  Calculations sut;

  genByte.out -> sut.inByte;
  genShort.out -> sut.inShort;
  genInt.out -> sut.inInt;
  genLong.out -> sut.inLong;
  genFloat.out -> sut.inFloat;
  genDouble.out -> sut.inDouble;
  genChar.out -> sut.inChar;
  genBool.out -> sut.inBoolean;

  sut.outByte -> assertByte.actual;
  sut.outShort -> assertShort.actual;
  sut.outInt -> assertInt.actual;
  sut.outLong -> assertLong.actual;
  sut.outFloat -> assertFloat.actual;
  sut.outDouble -> assertDouble.actual;
  sut.outChar -> assertChar.actual;
  sut.outBoolean -> assertBool.actual;

  EmitSync<Byte> genByte(inByte);
  EmitSync<Short> genShort(inShort);
  EmitSync<Integer> genInt(inInt);
  EmitSync<Long> genLong(inLong);
  EmitSync<Float> genFloat(inFloat);
  EmitSync<Double> genDouble(inDouble);
  EmitSync<Character> genChar(inChar);
  EmitSync<Boolean> genBool(inBool);

  AssertEqualsUntimed<Byte> assertByte(expectedByte);
  AssertEqualsUntimed<Short> assertShort(expectedShort);
  AssertEqualsUntimed<Integer> assertInt(expectedInt);
  AssertEqualsUntimed<Long> assertLong(expectedLong);
  AssertEqualsUntimed<Float> assertFloat(expectedFloat);
  AssertEqualsUntimed<Double> assertDouble(expectedDouble);
  AssertEqualsUntimed<Character> assertChar(expectedChar);
  AssertEqualsUntimed<Boolean> assertBool(expectedBool);
}
