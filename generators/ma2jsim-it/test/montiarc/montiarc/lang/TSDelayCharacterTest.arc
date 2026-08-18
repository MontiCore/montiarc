/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.lang.Character;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[Character.MIN_VALUE, Character.MIN_VALUE, Character.MAX_VALUE, Character.MAX_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MAX_VALUE, Character.MAX_VALUE, Character.MAX_VALUE, Character.MAX_VALUE],
input=[
  Sync<Character.MIN_VALUE>,
  Sync<Character.MAX_VALUE>,
  Sync<Character.MIN_VALUE>,
  Sync<Character.MAX_VALUE>,
  Sync<Character.MIN_VALUE, Character.MIN_VALUE>,
  Sync<Character.MIN_VALUE, Character.MAX_VALUE>,
  Sync<Character.MAX_VALUE, Character.MIN_VALUE>,
  Sync<Character.MAX_VALUE, Character.MAX_VALUE>,
  Sync<Character.MIN_VALUE, Character.MIN_VALUE>,
  Sync<Character.MIN_VALUE, Character.MAX_VALUE>,
  Sync<Character.MAX_VALUE, Character.MIN_VALUE>,
  Sync<Character.MAX_VALUE, Character.MAX_VALUE>
], output=[
  <Character.MIN_VALUE>,
  <Character.MIN_VALUE>,
  <Character.MAX_VALUE>,
  <Character.MAX_VALUE>,
  <Character.MIN_VALUE, Tick, Character.MIN_VALUE>,
  <Character.MIN_VALUE, Tick, Character.MIN_VALUE>,
  <Character.MIN_VALUE, Tick, Character.MAX_VALUE>,
  <Character.MIN_VALUE, Tick, Character.MAX_VALUE>,
  <Character.MAX_VALUE, Tick, Character.MIN_VALUE>,
  <Character.MAX_VALUE, Tick, Character.MIN_VALUE>,
  <Character.MAX_VALUE, Tick, Character.MAX_VALUE>,
  <Character.MAX_VALUE, Tick, Character.MAX_VALUE>
]>>
component TSDelayCharacterTest(char init, SyncStream<char> input, EventStream<char> output) {
  TSDelayCharacter sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<char> generator(input);

  AssertEqualsTimed<char> assertions(output);
}
