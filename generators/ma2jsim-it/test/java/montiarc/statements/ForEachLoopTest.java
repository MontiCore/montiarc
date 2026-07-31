/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import montiarc.types.I1;
import montiarc.types.I2;
import montiarc.types.IntSeq;
import montiarc.types.MI1;
import montiarc.types.MI2;
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
  private PortObserver<I1> port_o1;
  private PortObserver<I2> port_o2;

  @BeforeEach
  void init() {
    // Given
    sut = new ForEachLoopCompBuilder().setName("sut").build();

    port_o = new PortObserver<>();
    port_c = new PortObserver<>();
    port_o1 = new PortObserver<>();
    port_o2 = new PortObserver<>();

    sut.port_o().connect(port_o);
    sut.port_c().connect(port_c);
    sut.port_o1().connect(port_o1);
    sut.port_o2().connect(port_o2);
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

  @Test
  void testUnionListConstructor() {
    // When
    sut.port_i().receive(msg(3));

    sut.runToCompletion();

    // Then
    assertThat(port_o1.getObservedValues())
      .hasSize(2)
      .element(0).isInstanceOf(MI1.class);
    assertThat(port_o1.getObservedValues()).element(1).isInstanceOf(MI2.class);
    assertThat(port_o2.getObservedValues())
      .hasSize(2)
      .element(0).isInstanceOf(MI1.class);
    assertThat(port_o2.getObservedValues()).element(1).isInstanceOf(MI2.class);
  }

  @Test
  void testUnionSetConstructor() {
    // When
    sut.port_i().receive(msg(4));

    sut.runToCompletion();

    // Then
    assertThat(port_o1.getObservedValues())
      .hasSize(2)
      .anySatisfy(value -> assertThat(value).isInstanceOf(MI1.class))
      .anySatisfy(value -> assertThat(value).isInstanceOf(MI2.class));
    assertThat(port_o2.getObservedValues())
      .hasSize(2)
      .anySatisfy(value -> assertThat(value).isInstanceOf(MI1.class))
      .anySatisfy(value -> assertThat(value).isInstanceOf(MI2.class));
  }
}
