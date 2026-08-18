/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import java.lang.Iterable;
import java.lang.String;
import java.util.List;
import java.util.Set;
import montiarc.types.I1;
import montiarc.types.I2;
import montiarc.types.IntSeq;
import montiarc.types.MI1;
import montiarc.types.MI2;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;

<<test={
  // inI             inList                         inSet                         inIter                         inObj                             inStr                  expO                   expC
  [Untimed<int><>,   Untimed<List<int>><[0, 1]>,    Untimed<Set<int>><>,          Untimed<Iterable<int>><>,      Untimed<IntSeq><>,                Untimed<String><>,     Untimed<int><0, 1>,    Untimed<char><>],
  [Untimed<int><>,   Untimed<List<int>><>,          Untimed<Set<int>><{0, 1}>,    Untimed<Iterable<int>><>,      Untimed<IntSeq><>,                Untimed<String><>,     Untimed<int><0, 1>,    Untimed<char><>],
  [Untimed<int><>,   Untimed<List<int>><>,          Untimed<Set<int>><>,          Untimed<Iterable<int>><[0, 1]>,Untimed<IntSeq><>,                Untimed<String><>,     Untimed<int><0, 1>,    Untimed<char><>],
  [Untimed<int><>,   Untimed<List<int>><>,          Untimed<Set<int>><>,          Untimed<Iterable<int>><>,      Untimed<IntSeq><IntSeq.IntSeq()>, Untimed<String><>,     Untimed<int><>,        Untimed<char><>],
  [Untimed<int><>,   Untimed<List<int>><>,          Untimed<Set<int>><>,          Untimed<Iterable<int>><>,      Untimed<IntSeq><>,                Untimed<String><"ab">, Untimed<int><>,        Untimed<char><'a', 'b'>],
  [Untimed<int><1>,  Untimed<List<int>><>,          Untimed<Set<int>><>,          Untimed<Iterable<int>><>,      Untimed<IntSeq><>,                Untimed<String><>,     Untimed<int><0, 1>,    Untimed<char><>],
  [Untimed<int><2>,  Untimed<List<int>><>,          Untimed<Set<int>><>,          Untimed<Iterable<int>><>,      Untimed<IntSeq><>,                Untimed<String><>,     Untimed<int><2, 3>,    Untimed<char><>]
}>>
component ForEachLoopTest(
  UntimedStream<Integer> inI,
  UntimedStream<List<Integer>> inList,
  UntimedStream<Set<Integer>> inSet,
  UntimedStream<Iterable<Integer>> inIter,
  UntimedStream<IntSeq> inObj,
  UntimedStream<String> inStr,
  UntimedStream<Integer> expO,
  UntimedStream<Character> expC
) {
  ForEachLoop sut;

  genI.out -> sut.i;
  genList.out -> sut.list;
  genSet.out -> sut.set;
  genIter.out -> sut.iter;
  genObj.out -> sut.obj;
  genStr.out -> sut.str;

  sut.o -> assertO.actual;
  sut.c -> assertC.actual;

  EmitList<Integer> genI(inI);
  EmitList<List<Integer>> genList(inList);
  EmitList<Set<Integer>> genSet(inSet);
  EmitList<Iterable<Integer>> genIter(inIter);
  EmitList<IntSeq> genObj(inObj);
  EmitList<String> genStr(inStr);

  AssertEqualsUntimed<Integer> assertO(expO);
  AssertEqualsUntimed<Character> assertC(expC);
}
