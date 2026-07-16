/* (c) https://github.com/MontiCore/monticore */
package consumer;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
public class TransitiveFmuTest {

  @Test
  @DisabledOnOs(architectures = "aarch64")
  public void testTransitiveFmu() {
    FmuConsumerComp sut = new FmuConsumerCompBuilder().setName("sut").build();

    PortObserver<Boolean> assertBool = new PortObserver<>();
    PortObserver<Double> assertContinuous = new PortObserver<>();
    PortObserver<Double> assertDiscrete = new PortObserver<>();
    PortObserver<Integer> assertInt32 = new PortObserver<>();
    PortObserver<String> assertString = new PortObserver<>();
    PortObserver<Integer> assertEnum = new PortObserver<>();

    sut.port_boolOut().connect(assertBool);
    sut.port_continuousOut().connect(assertContinuous);
    sut.port_discreteOut().connect(assertDiscrete);
    sut.port_int32Out().connect(assertInt32);
    sut.port_stringOut().connect(assertString);
    sut.port_enumOut().connect(assertEnum);

    sut.port_boolIn().receive(msg(true));
    sut.port_continuousIn().receive(msg(1.5));
    sut.port_discreteIn().receive(msg(2.0));
    sut.port_tunableParam().receive(msg(0.5));
    sut.port_int32In().receive(msg(42));
    sut.port_enumIn().receive(msg(1));
    sut.port_stringIn().receive(msg("hello"));

    sut.port_boolIn().receive(tk());
    sut.port_continuousIn().receive(tk());
    sut.port_discreteIn().receive(tk());
    sut.port_tunableParam().receive(tk());
    sut.port_int32In().receive(tk());
    sut.port_enumIn().receive(tk());
    sut.port_stringIn().receive(tk());

    sut.run(1, 1);

    Assertions.assertEquals(1, assertBool.getObservedValues().size());
    Assertions.assertEquals(1, assertContinuous.getObservedValues().size());
    Assertions.assertEquals(1, assertDiscrete.getObservedValues().size());
    Assertions.assertEquals(1, assertInt32.getObservedValues().size());
    Assertions.assertEquals(1, assertString.getObservedValues().size());
    Assertions.assertEquals(1, assertEnum.getObservedValues().size());

    Assertions.assertEquals(true, assertBool.getObservedValues().get(0));
    Assertions.assertEquals(1.5, assertContinuous.getObservedValues().get(0));
    Assertions.assertEquals(2.0, assertDiscrete.getObservedValues().get(0));
    Assertions.assertEquals(42, assertInt32.getObservedValues().get(0));
    Assertions.assertEquals("hello", assertString.getObservedValues().get(0));
    Assertions.assertEquals(1, assertEnum.getObservedValues().get(0));
  }
}
