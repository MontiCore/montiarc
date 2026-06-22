/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.IntSeq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;

import static montiarc.rte.msg.MessageFactory.msg;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class ForEachLoopTest {

  private ForEachLoopComp sut;
  private PortObserver<Integer> port_o;
  private PortObserver<Character> port_c;

  @BeforeEach
  void init() {
    // Given
    sut = new ForEachLoopCompBuilder().setName("sut").build();

    port_o = new PortObserver<>();
    port_c = new PortObserver<>();

    sut.port_o().connect(port_o);
    sut.port_c().connect(port_c);
  }

  @Test
  void testList() {
    // When
    sut.port_list().receive(msg(List.of(0, 1)));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(0), msg(1));
  }

  @Test
  void testSet() {
    // When
    sut.port_set().receive(msg(new LinkedHashSet<>(List.of(0, 1))));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(0), msg(1));
  }

  @Test
  void testIterable() {
    // When
    sut.port_iter().receive(msg(List.of(0, 1)));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(0), msg(1));
  }

  @Test
  void testObject() {
    // When
    sut.port_obj().receive(msg(new IntSeq(0, 1)));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(0), msg(1));
  }

  @Test
  void testString() {
    // When
    sut.port_str().receive(msg("ab"));

    sut.runToCompletion();

    // Then
    assertThat(port_c.getObservedMessages()).containsExactly(msg('a'), msg('b'));
  }

  @Test
  void testListConstructor() {
    // When
    sut.port_i().receive(msg(1));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(0), msg(1));
  }

  @Test
  void testSetConstructor() {
    // When
    sut.port_i().receive(msg(2));

    sut.runToCompletion();

    // Then
    assertThat(port_o.getObservedMessages()).containsExactly(msg(2), msg(3));
  }
}
