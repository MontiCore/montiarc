/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;

<<test={
  // inI                 inC                     inS                             inE                           expected
  [Untimed<1, 2, -1>,    Untimed<char><>,        Untimed<String><>,              Untimed<OnOff><>,             Untimed<1, 1, 2, -1, -1, -1>],
  [Untimed<int><>,       Untimed<'a', 'b', 'z'>, Untimed<String><>,              Untimed<OnOff><>,             Untimed<1, 2, -1>],
  [Untimed<int><>,       Untimed<char><>,        Untimed<String><"a", "b", "z">, Untimed<OnOff><>,             Untimed<1, 2, -1>],
  [Untimed<int><>,       Untimed<char><>,        Untimed<String><>,              Untimed<OnOff.ON, OnOff.OFF>, Untimed<1, 2>]
}>>
component SwitchFallThroughFixTest(
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
