/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical;

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

@JSimTest
class NestedTransitionsInSameHierarchyTest {

  @ParameterizedTest
  @MethodSource({"moveUpTheHierarchy",
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
        List.of(msg("c"),    msg("check"), msg("ccc -> c"), msg("check")),
        List.of(msg("-> c"), msg("ccc"),   msg("ccc -> c"), msg("ccc"))
      ),
      // ccc -> cc
      Arguments.of(
        List.of(msg("c"),    msg("check"), msg("ccc -> cc"), msg("check")),
        List.of(msg("-> c"), msg("ccc"),   msg("ccc -> cc"), msg("ccc"))
      ),
      // ddd -> d
      Arguments.of(
        List.of(msg("d"),    msg("check"), msg("ddd -> d"), msg("check")),
        List.of(msg("-> d"), msg("ddd_d"), msg("ddd -> d"), msg("ddd_d"))
      ),
      // ddd -> dd
      Arguments.of(
        List.of(msg("d"),    msg("check"), msg("ddd -> dd"), msg("check")),
        List.of(msg("-> d"), msg("ddd_d"), msg("ddd -> dd"), msg("ddd_d"))
      ),
      // fff -> f
      Arguments.of(
        List.of(msg("f"),    msg("check"), msg("fff -> f"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff -> f"), msg("fff_fff"))
      ),
      // fff -> ff
      Arguments.of(
        List.of(msg("f"),    msg("check"), msg("fff -> ff"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff -> ff"), msg("fff_fff"))
      ),
      // fff_ff -> fff
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff_ff -> fff"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff_ff -> fff"), msg("fff_fff"))
      ),
      // fff_ff -> fff_f
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff_ff -> fff_f"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff_ff -> fff_f"), msg("fff_fff"))
      ),
      // fff_fff -> fff_f
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff_fff -> fff_f"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff_fff -> fff_f"), msg("fff_fff"))
      ),
      // fff_fff -> fff_ff
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff_fff -> fff_ff"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff_fff -> fff_ff"), msg("fff_fff"))
      )
    );
  }

  static Stream<Arguments> moveDownTheHierarchy() {
    return Stream.of(
      // b -> bz
      Arguments.of(
        List.of(msg("b"),    msg("check"), msg("b -> bz"), msg("check")),
        List.of(msg("-> b"), msg("bb"),    msg("b -> bz"), msg("bz"))
      ),
      // c -> cz
      Arguments.of(
        List.of(msg("c"),    msg("check"), msg("c -> cz"), msg("check")),
        List.of(msg("-> c"), msg("ccc"),   msg("c -> cz"), msg("czz"))
      ),
      // c -> czz
      Arguments.of(
        List.of(msg("c"),    msg("check"), msg("c -> czz"), msg("check")),
        List.of(msg("-> c"), msg("ccc"),   msg("c -> czz"), msg("czz"))
      ),
      // d -> dz
      Arguments.of(
        List.of(msg("d"),    msg("check"), msg("d -> dz"), msg("check")),
        List.of(msg("-> d"), msg("ddd_d"), msg("d -> dz"), msg("dzz_z"))
      ),
      // d -> dzz
      Arguments.of(
        List.of(msg("d"),    msg("check"), msg("d -> dzz"), msg("check")),
        List.of(msg("-> d"), msg("ddd_d"), msg("d -> dzz"), msg("dzz_z"))
      ),
      // dd -> ddz
      Arguments.of(
        List.of(msg("d"),    msg("check"), msg("dd -> ddz"), msg("check")),
        List.of(msg("-> d"), msg("ddd_d"), msg("dd -> ddz"), msg("ddz_z"))
      ),
      // f -> ffz
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("f -> ffz"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("f -> ffz"), msg("ffz_zzz"))
      ),
      // f -> fzz_z
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("f -> fzz_z"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("f -> fzz_z"), msg("fzz_zzz"))
      ),
      // fff -> fff_zz
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff -> fff_zz"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff -> fff_zz"), msg("fff_zzz"))
      ),
      // fff -> fff_zzz
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff -> fff_zzz"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff -> fff_zzz"), msg("fff_zzz"))
      ),
      // fff_f -> fff_fz
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff_f -> fff_fz"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff_f -> fff_fz"), msg("fff_fzz"))
      )
    );
  }

  static Stream<Arguments> stayAtHierarchyLevel() {
    return Stream.of(
      // bb -> bz
      Arguments.of(
        List.of(msg("b"),    msg("check"), msg("bb -> bz"), msg("check")),
        List.of(msg("-> b"), msg("bb"),    msg("bb -> bz"), msg("bz"))
      ),
      // cc -> cz
      Arguments.of(
        List.of(msg("c"),    msg("check"), msg("cc -> cz"), msg("check")),
        List.of(msg("-> c"), msg("ccc"),   msg("cc -> cz"), msg("czz"))
      ),
      // dd -> dz
      Arguments.of(
        List.of(msg("d"),    msg("check"), msg("dd -> dz"), msg("check")),
        List.of(msg("-> d"), msg("ddd_d"), msg("dd -> dz"), msg("dzz_z"))
      ),
      // ee -> ez
      Arguments.of(
        List.of(msg("e"),    msg("check"),  msg("ee -> ez"), msg("check")),
        List.of(msg("-> e"), msg("eee_ee"), msg("ee -> ez"), msg("ezz_zz"))
      ),
      // ccc -> ccz
      Arguments.of(
        List.of(msg("c"),    msg("check"), msg("ccc -> ccz"), msg("check")),
        List.of(msg("-> c"), msg("ccc"),   msg("ccc -> ccz"), msg("ccz"))
      ),
      // ccc -> czz
      Arguments.of(
        List.of(msg("c"),    msg("check"), msg("ccc -> czz"), msg("check")),
        List.of(msg("-> c"), msg("ccc"),   msg("ccc -> czz"), msg("czz"))
      ),
      // fff -> fzz
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff -> fzz"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff -> fzz"), msg("fzz_zzz"))
      ),
      // fff_fff -> fff_ffz
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff_fff -> fff_ffz"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff_fff -> fff_ffz"), msg("fff_ffz"))
      ),
      // fff_fff -> fff_zzz
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff_fff -> fff_zzz"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff_fff -> fff_zzz"), msg("fff_zzz"))
      ),
      // b -> b
      Arguments.of(
        List.of(msg("b"),    msg("check"), msg("b -> b"), msg("check")),
        List.of(msg("-> b"), msg("bb"),    msg("b -> b"), msg("bb"))
      ),
      // c -> c
      Arguments.of(
        List.of(msg("c"),    msg("check"), msg("c -> c"), msg("check")),
        List.of(msg("-> c"), msg("ccc"),   msg("c -> c"), msg("ccc"))
      ),
      // cc -> cc
      Arguments.of(
        List.of(msg("c"),    msg("check"), msg("cc -> cc"), msg("check")),
        List.of(msg("-> c"), msg("ccc"),   msg("cc -> cc"), msg("ccc"))
      ),
      // fff -> fff
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff -> fff"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff -> fff"), msg("fff_fff"))
      ),
      // fff_ff -> fff_ff
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff_ff -> fff_ff"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff_ff -> fff_ff"), msg("fff_fff"))
      ),
      // fff_fff -> fff_fff
      Arguments.of(
        List.of(msg("f"),    msg("check"),   msg("fff_fff -> fff_fff"), msg("check")),
        List.of(msg("-> f"), msg("fff_fff"), msg("fff_fff -> fff_fff"), msg("fff_fff"))
      )
    );
  }
}
