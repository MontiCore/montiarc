/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.lang.Character;

<<test, ticks=[1,1,1,1,1,1,2,2,2,2], input=[
  <Character.MIN_VALUE>,
  <Character.MAX_VALUE>,
  <Character.MIN_VALUE, Character.MIN_VALUE>,
  <Character.MIN_VALUE, Character.MAX_VALUE>,
  <Character.MAX_VALUE, Character.MIN_VALUE>,
  <Character.MAX_VALUE, Character.MAX_VALUE>,
  <Character.MIN_VALUE, Tick, Character.MIN_VALUE>,
  <Character.MIN_VALUE, Tick, Character.MAX_VALUE>,
  <Character.MAX_VALUE, Tick, Character.MIN_VALUE>,
  <Character.MAX_VALUE, Tick, Character.MAX_VALUE>
], output=[
  <Tick, Character.MIN_VALUE>,
  <Tick, Character.MAX_VALUE>,
  <Tick, Character.MIN_VALUE, Character.MIN_VALUE>,
  <Tick, Character.MIN_VALUE, Character.MAX_VALUE>,
  <Tick, Character.MAX_VALUE, Character.MIN_VALUE>,
  <Tick, Character.MAX_VALUE, Character.MAX_VALUE>,
  <Tick, Character.MIN_VALUE, Tick, Character.MIN_VALUE>,
  <Tick, Character.MIN_VALUE, Tick, Character.MAX_VALUE>,
  <Tick, Character.MAX_VALUE, Tick, Character.MIN_VALUE>,
  <Tick, Character.MAX_VALUE, Tick, Character.MAX_VALUE>
]>>
component DelayCharacterTest(EventStream<char> input, EventStream<char> output) {
  DelayCharacter sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<Character> generator(input);

  AssertEqualsTimed<Character> assertions(output);
}
