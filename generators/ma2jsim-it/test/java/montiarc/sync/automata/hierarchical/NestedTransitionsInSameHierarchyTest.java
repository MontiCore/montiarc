/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

import com.google.common.base.Preconditions;
import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;

@JSimTest
class NestedTransitionsInSameHierarchyTest {

  @ParameterizedTest
  @MethodSource({
    "moveUpTheHierarchy",
    "moveDownTheHierarchy",
    "stayAtHierarchyLevel"})
  void testIO(@NotNull List<Message<String>> input,
              @NotNull List<Message<String>> expected) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(expected);

    // Given
    NestedTransitionsInSameHierarchyComp sut =
      new NestedTransitionsInSameHierarchyCompBuilder().setName("sut").build();
    PortObserver<String> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.init();

    for (Message<String> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.run();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> moveUpTheHierarchy() {
    return Stream.of(
      // ccc -> c
      Arguments.of(
        List.of(msg("c"),    tk(), msg("check"), tk(), msg("ccc -> c"), tk(), msg("check"), tk()),
        List.of(msg("-> c"), tk(), msg("ccc"),   tk(), msg("ccc -> c"), tk(), msg("ccc"), tk())
      ),
      // ccc -> cc
      Arguments.of(
        List.of(msg("c"),    tk(), msg("check"), tk(), msg("ccc -> cc"), tk(), msg("check"), tk()),
        List.of(msg("-> c"), tk(), msg("ccc"),   tk(), msg("ccc -> cc"), tk(), msg("ccc"), tk())
      ),
      // ddd -> d
      Arguments.of(
        List.of(msg("d"),    tk(), msg("check"), tk(), msg("ddd -> d"), tk(), msg("check"), tk()),
        List.of(msg("-> d"), tk(), msg("ddd_d"), tk(), msg("ddd -> d"), tk(), msg("ddd_d"), tk())
      ),
      // ddd -> dd
      Arguments.of(
        List.of(msg("d"),    tk(), msg("check"), tk(), msg("ddd -> dd"), tk(), msg("check"), tk()),
        List.of(msg("-> d"), tk(), msg("ddd_d"), tk(), msg("ddd -> dd"), tk(), msg("ddd_d"), tk())
      ),
      // fff -> f
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"), tk(), msg("fff -> f"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff -> f"), tk(), msg("fff_fff"), tk())
      ),
      // fff -> ff
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"), tk(), msg("fff -> ff"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff -> ff"), tk(), msg("fff_fff"), tk())
      ),
      // fff_ff -> fff
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff_ff -> fff"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff_ff -> fff"), tk(), msg("fff_fff"), tk())
      ),
      // fff_ff -> fff_f
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff_ff -> fff_f"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff_ff -> fff_f"), tk(), msg("fff_fff"), tk())
      ),
      // fff_fff -> fff_f
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff_fff -> fff_f"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff_fff -> fff_f"), tk(), msg("fff_fff"), tk())
      ),
      // fff_fff -> fff_ff
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff_fff -> fff_ff"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff_fff -> fff_ff"), tk(), msg("fff_fff"), tk())
      )
    );
  }

  static Stream<Arguments> moveDownTheHierarchy() {
    return Stream.of(
      // b -> bz
      Arguments.of(
        List.of(msg("b"),    tk(), msg("check"), tk(), msg("b -> bz"), tk(), msg("check"), tk()),
        List.of(msg("-> b"), tk(), msg("bb"),    tk(), msg("b -> bz"), tk(), msg("bz"), tk())
      ),
      // c -> cz
      Arguments.of(
        List.of(msg("c"),    tk(), msg("check"), tk(), msg("c -> cz"), tk(), msg("check"), tk()),
        List.of(msg("-> c"), tk(), msg("ccc"),   tk(), msg("c -> cz"), tk(), msg("czz"), tk())
      ),
      // c -> czz
      Arguments.of(
        List.of(msg("c"),    tk(), msg("check"), tk(), msg("c -> czz"), tk(), msg("check"), tk()),
        List.of(msg("-> c"), tk(), msg("ccc"),   tk(), msg("c -> czz"), tk(), msg("czz"), tk())
      ),
      // d -> dz
      Arguments.of(
        List.of(msg("d"),    tk(), msg("check"), tk(), msg("d -> dz"), tk(), msg("check"), tk()),
        List.of(msg("-> d"), tk(), msg("ddd_d"), tk(), msg("d -> dz"), tk(), msg("dzz_z"), tk())
      ),
      // d -> dzz
      Arguments.of(
        List.of(msg("d"),    tk(), msg("check"), tk(), msg("d -> dzz"), tk(), msg("check"), tk()),
        List.of(msg("-> d"), tk(), msg("ddd_d"), tk(), msg("d -> dzz"), tk(), msg("dzz_z"), tk())
      ),
      // dd -> ddz
      Arguments.of(
        List.of(msg("d"),    tk(), msg("check"), tk(), msg("dd -> ddz"), tk(), msg("check"), tk()),
        List.of(msg("-> d"), tk(), msg("ddd_d"), tk(), msg("dd -> ddz"), tk(), msg("ddz_z"), tk())
      ),
      // f -> ffz
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("f -> ffz"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("f -> ffz"), tk(), msg("ffz_zzz"), tk())
      ),
      // f -> fzz_z
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("f -> fzz_z"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("f -> fzz_z"), tk(), msg("fzz_zzz"), tk())
      ),
      // fff -> fff_zz
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff -> fff_zz"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff -> fff_zz"), tk(), msg("fff_zzz"), tk())
      ),
      // fff -> fff_zzz
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff -> fff_zzz"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff -> fff_zzz"), tk(), msg("fff_zzz"), tk())
      ),
      // fff_f -> fff_fz
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff_f -> fff_fz"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff_f -> fff_fz"), tk(), msg("fff_fzz"), tk())
      )
    );
  }

  static Stream<Arguments> stayAtHierarchyLevel() {
    return Stream.of(
      // bb -> bz
      Arguments.of(
        List.of(msg("b"),    tk(), msg("check"), tk(), msg("bb -> bz"), tk(), msg("check"), tk()),
        List.of(msg("-> b"), tk(), msg("bb"),    tk(), msg("bb -> bz"), tk(), msg("bz"), tk())
      ),
      // cc -> cz
      Arguments.of(
        List.of(msg("c"),    tk(), msg("check"), tk(), msg("cc -> cz"), tk(), msg("check"), tk()),
        List.of(msg("-> c"), tk(), msg("ccc"),   tk(), msg("cc -> cz"), tk(), msg("czz"), tk())
      ),
      // dd -> dz
      Arguments.of(
        List.of(msg("d"),    tk(), msg("check"), tk(), msg("dd -> dz"), tk(), msg("check"), tk()),
        List.of(msg("-> d"), tk(), msg("ddd_d"), tk(), msg("dd -> dz"), tk(), msg("dzz_z"), tk())
      ),
      // ee -> ez
      Arguments.of(
        List.of(msg("e"),    tk(), msg("check"),  tk(), msg("ee -> ez"), tk(), msg("check"), tk()),
        List.of(msg("-> e"), tk(), msg("eee_ee"), tk(), msg("ee -> ez"), tk(), msg("ezz_zz"), tk())
      ),
      // ccc -> ccz
      Arguments.of(
        List.of(msg("c"),    tk(), msg("check"), tk(), msg("ccc -> ccz"), tk(), msg("check"), tk()),
        List.of(msg("-> c"), tk(), msg("ccc"),   tk(), msg("ccc -> ccz"), tk(), msg("ccz"), tk())
      ),
      // ccc -> czz
      Arguments.of(
        List.of(msg("c"),    tk(), msg("check"), tk(), msg("ccc -> czz"), tk(), msg("check"), tk()),
        List.of(msg("-> c"), tk(), msg("ccc"),   tk(), msg("ccc -> czz"), tk(), msg("czz"), tk())
      ),
      // fff -> fzz
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff -> fzz"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff -> fzz"), tk(), msg("fzz_zzz"), tk())
      ),
      // fff_fff -> fff_ffz
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff_fff -> fff_ffz"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff_fff -> fff_ffz"), tk(), msg("fff_ffz"), tk())
      ),
      // fff_fff -> fff_zzz
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff_fff -> fff_zzz"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff_fff -> fff_zzz"), tk(), msg("fff_zzz"), tk())
      ),
      // b -> b
      Arguments.of(
        List.of(msg("b"),    tk(), msg("check"), tk(), msg("b -> b"), tk(), msg("check"), tk()),
        List.of(msg("-> b"), tk(), msg("bb"),    tk(), msg("b -> b"), tk(), msg("bb"), tk())
      ),
      // c -> c
      Arguments.of(
        List.of(msg("c"),    tk(), msg("check"), tk(), msg("c -> c"), tk(), msg("check"), tk()),
        List.of(msg("-> c"), tk(), msg("ccc"),   tk(), msg("c -> c"), tk(), msg("ccc"), tk())
      ),
      // cc -> cc
      Arguments.of(
        List.of(msg("c"),    tk(), msg("check"), tk(), msg("cc -> cc"), tk(), msg("check"), tk()),
        List.of(msg("-> c"), tk(), msg("ccc"),   tk(), msg("cc -> cc"), tk(), msg("ccc"), tk())
      ),
      // fff -> fff
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff -> fff"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff -> fff"), tk(), msg("fff_fff"), tk())
      ),
      // fff_ff -> fff_ff
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff_ff -> fff_ff"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff_ff -> fff_ff"), tk(), msg("fff_fff"), tk())
      ),
      // fff_fff -> fff_fff
      Arguments.of(
        List.of(msg("f"),    tk(), msg("check"),   tk(), msg("fff_fff -> fff_fff"), tk(), msg("check"), tk()),
        List.of(msg("-> f"), tk(), msg("fff_fff"), tk(), msg("fff_fff -> fff_fff"), tk(), msg("fff_fff"), tk())
      )
    );
  }
}
