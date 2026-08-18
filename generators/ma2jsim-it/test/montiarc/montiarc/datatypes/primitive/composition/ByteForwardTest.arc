/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.lang.Byte;

<<test, testStream=[Sync<Byte.MIN_VALUE>, Sync<Byte.MIN_VALUE, Byte.MAX_VALUE>]>>
component ByteForwardTest(SyncStream<byte> testStream) {
  ByteForward sut;

  generator.out -> sut.pIn;
  sut.pOut -> assert.actual;

  EmitSync<Byte> generator(testStream);

  AssertEqualsUntimed<Byte> assert(testStream.untimed());
}
