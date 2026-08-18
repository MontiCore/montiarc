/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;

<<test={
  // inI                 inC                    inS                     inE                    expected
  [Untimed<1>,           Untimed<char><>,       Untimed<String><>,      Untimed<OnOff><>,      Untimed<1, 1>],
  [Untimed<2>,           Untimed<char><>,       Untimed<String><>,      Untimed<OnOff><>,      Untimed<2, -1>],
  [Untimed<-1>,          Untimed<char><>,       Untimed<String><>,      Untimed<OnOff><>,      Untimed<-1, -1>],
  [Untimed<int><>,       Untimed<'a'>,          Untimed<String><>,      Untimed<OnOff><>,      Untimed<1>],
  [Untimed<int><>,       Untimed<'b'>,          Untimed<String><>,      Untimed<OnOff><>,      Untimed<2>],
  [Untimed<int><>,       Untimed<'0'>,          Untimed<String><>,      Untimed<OnOff><>,      Untimed<-1>],
  [Untimed<int><>,       Untimed<char><>,       Untimed<String><"a">,   Untimed<OnOff><>,      Untimed<1>],
  [Untimed<int><>,       Untimed<char><>,       Untimed<String><"b">,   Untimed<OnOff><>,      Untimed<2>],
  [Untimed<int><>,       Untimed<char><>,       Untimed<String><"0">,   Untimed<OnOff><>,      Untimed<-1>],
  [Untimed<int><>,       Untimed<char><>,       Untimed<String><>,      Untimed<OnOff.ON>,     Untimed<1>],
  [Untimed<int><>,       Untimed<char><>,       Untimed<String><>,      Untimed<OnOff.OFF>,    Untimed<2>]
}>>
component SwitchConditionalTest(
  UntimedStream<Integer> inI,
  UntimedStream<Character> inC,
  UntimedStream<String> inS,
  UntimedStream<OnOff> inE,
  UntimedStream<Integer> expected
) {
  SwitchConditional sut;

  genI.out -> sut.i;
  genC.out -> sut.c;
  genS.out -> sut.s;
  genE.out -> sut.e;
  sut.o -> assertions.actual;

  EmitList<Integer> genI(inI);
  EmitList<Character> genC(inC);
  EmitList<String> genS(inS);
  EmitList<OnOff> genE(inE);

  AssertEqualsUntimed<Integer> assertions(expected);
}
