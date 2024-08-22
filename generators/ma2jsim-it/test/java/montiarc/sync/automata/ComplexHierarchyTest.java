/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.MessageFactory;
import montiarc.rte.msg.Message;
import montiarc.rte.port.InPort;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.tk;

@ExtendWith(MockitoExtension.class)
public class ComplexHierarchyTest {

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<String>> actual;

  /**
   * the target port of output port o
   */
  @Mock
  InPort<String> port_o;

  /**
   * @param ticks    the input stream on port i
   * @param expected the expected output stream on port o
   */
  @ParameterizedTest
  @MethodSource("io")
  void testIO(@NotNull int ticks,
              @NotNull List<Message<String>> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    ComplexHierarchyComp sut = new ComplexHierarchyCompBuilder().setName("sut").build();

    sut.port_o().connect(this.port_o);

    // when receiving a message, capture that message but do nothing else
    Mockito.doNothing().when(this.port_o).receive(this.actual.capture());

    // When
    sut.init();
    sut.run(ticks);

    // Then
    Assertions.assertThat(this.actual.getAllValues()).containsExactlyElementsOf(expected);
  }

  /**
   * @return An array of input messages and an array of the expected outputs the
   * automaton should produce for the given input.
   */
  protected static Stream<Arguments> io() {
    String path0 = "aIni->aEn->a1Ini->a1En";
    String path1 = "a1Ex->aEx->aToB->bEn";
    String path2 = "bEx->bToC->cEn->c1En";
    String path3 = "c1Ex->c1ToC2->c2En";
    String path4 = "c2Ex->cEx->cToD->dEn->d2En";
    String path5 = "d2Ex->d2ToD1->d1En";
    String path6 = "d1Ex->dEx->d1ToE->eEn" /*+ "->e1Ini"*/ + "->e1En";
    String path7 = "e1Ex->e1ToE2->e2En";
    String path8 = "e2Ex->eEx->eToF11->fEn->f1En->f11En";
    String path9 = "f11Ex->f1Ex->fEx->fToA->aEn" /*+ "->a1Ini"*/ + "->a1En";
    return Stream.of(
      Arguments.of(1, stringToMessages(path0, path1)),
      Arguments.of(2, stringToMessages(path0, path1, path2)),
      Arguments.of(3, stringToMessages(path0, path1, path2, path3)),
      Arguments.of(4, stringToMessages(path0, path1, path2, path3, path4)),
      Arguments.of(5, stringToMessages(path0, path1, path2, path3, path4, path5)),
      Arguments.of(6, stringToMessages(path0, path1, path2, path3, path4, path5, path6)),
      Arguments.of(7, stringToMessages(path0, path1, path2, path3, path4, path5, path6, path7)),
      Arguments.of(8, stringToMessages(path0, path1, path2, path3, path4, path5, path6, path7, path8)),
      Arguments.of(9, stringToMessages(path0, path1, path2, path3, path4, path5, path6, path7, path8, path9))
    );
  }

  protected static List<Message<String>> stringToMessages(String... s) {
    ArrayList<Message<String>> messages = new ArrayList<>();
    for (int i = 0; i < s.length; i++) {
      messages.addAll(Arrays.stream(s[i].split("->")).map(MessageFactory::msg).collect(Collectors.toList()));
      if (i > 0) messages.add(tk());
    }
    return messages;
  }
}
