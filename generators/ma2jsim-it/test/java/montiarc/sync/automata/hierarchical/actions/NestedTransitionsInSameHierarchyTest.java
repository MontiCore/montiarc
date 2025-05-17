/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical.actions;

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
    for (Message<String> msg : input) {
      sut.port_i().receive(msg);
    }

    sut.runToCompletion();

    // Then
    Assertions.assertThat(port_o.getObservedMessages()).containsExactlyElementsOf(expected);
  }

  static Stream<Arguments> moveUpTheHierarchy() {
    return Stream.of(
      // ccc -> c
      Arguments.of(
        List.of(msg("c"), tk(), msg("ccc -> c"), tk()),
        List.of(
          msg("INIT -> c"),
            enter("c"), enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"), tk(),
          exit("ccc"), exit("cc"), exit("c"),
            msg("ccc -> c"),
            enter("c"), enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"),
            tk()
        )
      ),
      // ccc -> cc
      Arguments.of(
        List.of(msg("c"), tk(), msg("ccc -> cc"), tk()),
        List.of(
          msg("INIT -> c"),
            enter("c"), enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"), tk(),
          exit("ccc"), exit("cc"),
            msg("ccc -> cc"),
            enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"),
          tk()
        )
      ),
      // ddd -> d
      Arguments.of(
        List.of(msg("d"), tk(), msg("ddd -> d"), tk()),
        List.of(
          msg("INIT -> d"),
            enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
            doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk(),
          exit("ddd_d"), exit("ddd"), exit("dd"), exit("d"),
            msg("ddd -> d"),
            enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
            doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk()
        )
      ),
      // ddd -> dd
      Arguments.of(
        List.of(msg("d"), tk(), msg("ddd -> dd"), tk()),
        List.of(
          msg("INIT -> d"),
            enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
            doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk(),
          exit("ddd_d"), exit("ddd"), exit("dd"),
            msg("ddd -> dd"),
            enter("dd"), enter("ddd"), enter("ddd_d"),
            doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk()
        )
      ),
      // fff -> f
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff -> f"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"), exit("fff"), exit("ff"), exit("f"),
            msg("fff -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk()
        )
      ),
      // fff -> ff
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff -> ff"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"), exit("fff"), exit("ff"),
            msg("fff -> ff"),
            enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk()
        )
      ),
      // fff_ff -> fff
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff_ff -> fff"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"), exit("fff"),
            msg("fff_ff -> fff"),
            enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk()
        )
      ),
      // fff_ff -> fff_f
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff_ff -> fff_f"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"),
            msg("fff_ff -> fff_f"),
            enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk()
        )
      ),
      // fff_fff -> fff_f
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff_fff -> fff_f"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"),
            msg("fff_fff -> fff_f"),
            enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk()
        )
      ),
      // fff_fff -> fff_ff
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff_fff -> fff_ff"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"),
            msg("fff_fff -> fff_ff"),
            enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk()
        )
      )
    );
  }

  static Stream<Arguments> moveDownTheHierarchy() {
    return Stream.of(
      // b -> bz
      Arguments.of(
        List.of(msg("b"), tk(), msg("b -> bz"), tk()),
        List.of(
          msg("INIT -> b"),
            enter("b"), enter("bb"),
            doo("b"), doo("bb"), tk(),
          exit("bb"), exit("b"),
            msg("b -> bz"),
            enter("b"), enter("bz"), enter("bzz"), enter("bzz_z"), enter("bzz_zz"), enter("bzz_zzz"),
            doo("b"), doo("bz"), doo("bzz"), doo("bzz_z"), doo("bzz_zz"), doo("bzz_zzz"), tk()
        )
      ),
      // c -> cz
      Arguments.of(
        List.of(msg("c"), tk(), msg("c -> cz"), tk()),
        List.of(
          msg("INIT -> c"),
            enter("c"), enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"), tk(),
          exit("ccc"), exit("cc"), exit("c"),
            msg("c -> cz"),
            enter("c"), enter("cz"), enter("czz"), enter("czz_z"), enter("czz_zz"), enter("czz_zzz"),
            doo("c"), doo("cz"), doo("czz"), doo("czz_z"), doo("czz_zz"), doo("czz_zzz"), tk()
        )
      ),
      // c -> czz
      Arguments.of(
        List.of(msg("c"), tk(), msg("c -> czz"), tk()),
        List.of(
          msg("INIT -> c"),
            enter("c"), enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"), tk(),
          exit("ccc"), exit("cc"), exit("c"),
            msg("c -> czz"),
            enter("c"), enter("cz"), enter("czz"), enter("czz_z"), enter("czz_zz"), enter("czz_zzz"),
            doo("c"), doo("cz"), doo("czz"), doo("czz_z"), doo("czz_zz"), doo("czz_zzz"), tk()
        )
      ),
      // d -> dz
      Arguments.of(
        List.of(msg("d"), tk(), msg("d -> dz"), tk()),
        List.of(
          msg("INIT -> d"),
            enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
            doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk(),
          exit("ddd_d"), exit("ddd"), exit("dd"), exit("d"),
            msg("d -> dz"),
            enter("d"), enter("dz"), enter("dzz"), enter("dzz_z"), enter("dzz_zz"), enter("dzz_zzz"),
            doo("d"), doo("dz"), doo("dzz"), doo("dzz_z"), doo("dzz_zz"), doo("dzz_zzz"), tk()
        )
      ),
      // d -> dzz
      Arguments.of(
        List.of(msg("d"), tk(), msg("d -> dzz"), tk()),
        List.of(
          msg("INIT -> d"),
            enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
            doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk(),
          exit("ddd_d"), exit("ddd"), exit("dd"), exit("d"),
            msg("d -> dzz"),
            enter("d"), enter("dz"), enter("dzz"), enter("dzz_z"), enter("dzz_zz"), enter("dzz_zzz"),
            doo("d"), doo("dz"), doo("dzz"), doo("dzz_z"), doo("dzz_zz"), doo("dzz_zzz"), tk()
        )
      ),
      // dd -> ddz
      Arguments.of(
        List.of(msg("d"), tk(), msg("dd -> ddz"), tk()),
        List.of(
          msg("INIT -> d"),
            enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
            doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk(),
          exit("ddd_d"), exit("ddd"), exit("dd"),
            msg("dd -> ddz"),
            enter("dd"), enter("ddz"), enter("ddz_z"), enter("ddz_zz"), enter("ddz_zzz"),
            doo("d"), doo("dd"), doo("ddz"), doo("ddz_z"), doo("ddz_zz"), doo("ddz_zzz"), tk()
        )
      ),
      // f -> ffz
      Arguments.of(
        List.of(msg("f"), tk(), msg("f -> ffz"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"), exit("fff"), exit("ff"), exit("f"),
            msg("f -> ffz"),
            enter("f"), enter("ff"), enter("ffz"), enter("ffz_z"), enter("ffz_zz"), enter("ffz_zzz"),
            doo("f"), doo("ff"), doo("ffz"), doo("ffz_z"), doo("ffz_zz"), doo("ffz_zzz"), tk()
        )
      ),
      // f -> fzz_z
      Arguments.of(
        List.of(msg("f"), tk(), msg("f -> fzz_z"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"), exit("fff"), exit("ff"), exit("f"),
            msg("f -> fzz_z"),
            enter("f"), enter("fz"), enter("fzz"), enter("fzz_z"), enter("fzz_zz"), enter("fzz_zzz"),
            doo("f"), doo("fz"), doo("fzz"), doo("fzz_z"), doo("fzz_zz"), doo("fzz_zzz"), tk()
        )
      ),
      // fff -> fff_zz
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff -> fff_zz"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"), exit("fff"),
            msg("fff -> fff_zz"),
            enter("fff"), enter("fff_z"), enter("fff_zz"), enter("fff_zzz"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_z"), doo("fff_zz"), doo("fff_zzz"), tk()
        )
      ),
      // fff -> fff_zzz
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff -> fff_zzz"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"), exit("fff"),
            msg("fff -> fff_zzz"),
            enter("fff"), enter("fff_z"), enter("fff_zz"), enter("fff_zzz"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_z"), doo("fff_zz"), doo("fff_zzz"), tk()
        )
      ),
      // fff_f -> fff_fz
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff_f -> fff_fz"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"),
            msg("fff_f -> fff_fz"),
            enter("fff_f"), enter("fff_fz"), enter("fff_fzz"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_fz"), doo("fff_fzz"), tk()
        )
      )
    );
  }

  static Stream<Arguments> stayAtHierarchyLevel() {
    return Stream.of(
      // bb -> bz
      Arguments.of(
        List.of(msg("b"), tk(), msg("bb -> bz"), tk()),
        List.of(
          msg("INIT -> b"),
            enter("b"), enter("bb"),
            doo("b"), doo("bb"), tk(),
          exit("bb"),
            msg("bb -> bz"),
            enter("bz"), enter("bzz"), enter("bzz_z"), enter("bzz_zz"), enter("bzz_zzz"),
            doo("b"), doo("bz"), doo("bzz"), doo("bzz_z"), doo("bzz_zz"), doo("bzz_zzz"), tk()
        )
      ),
      // cc -> cz
      Arguments.of(
        List.of(msg("c"), tk(), msg("cc -> cz"), tk()),
        List.of(
          msg("INIT -> c"),
            enter("c"), enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"), tk(),
          exit("ccc"), exit("cc"),
            msg("cc -> cz"),
            enter("cz"), enter("czz"), enter("czz_z"), enter("czz_zz"), enter("czz_zzz"),
            doo("c"), doo("cz"), doo("czz"), doo("czz_z"), doo("czz_zz"), doo("czz_zzz"), tk()
        )
      ),
      // dd -> dz
      Arguments.of(
        List.of(msg("d"), tk(), msg("dd -> dz"), tk()),
        List.of(
          msg("INIT -> d"),
            enter("d"), enter("dd"), enter("ddd"), enter("ddd_d"),
            doo("d"), doo("dd"), doo("ddd"), doo("ddd_d"), tk(),
          exit("ddd_d"), exit("ddd"), exit("dd"),
            msg("dd -> dz"),
            enter("dz"), enter("dzz"), enter("dzz_z"), enter("dzz_zz"), enter("dzz_zzz"),
            doo("d"), doo("dz"), doo("dzz"), doo("dzz_z"), doo("dzz_zz"), doo("dzz_zzz"), tk()
        )
      ),
      // ee -> ez
      Arguments.of(
        List.of(msg("e"),  tk(), msg("ee -> ez"), tk()),
        List.of(
          msg("INIT -> e"),
            enter("e"), enter("ee"), enter("eee"), enter("eee_e"), enter("eee_ee"),
            doo("e"), doo("ee"), doo("eee"), doo("eee_e"), doo("eee_ee"), tk(),
          exit("eee_ee"), exit("eee_e"), exit("eee"), exit("ee"),
            msg("ee -> ez"),
            enter("ez"), enter("ezz"), enter("ezz_z"), enter("ezz_zz"), enter("ezz_zzz"),
            doo("e"), doo("ez"), doo("ezz"), doo("ezz_z"), doo("ezz_zz"), doo("ezz_zzz"), tk()
        )
      ),
      // ccc -> ccz
      Arguments.of(
        List.of(msg("c"), tk(), msg("ccc -> ccz"), tk()),
        List.of(
          msg("INIT -> c"),
            enter("c"), enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"), tk(),
          exit("ccc"),
            msg("ccc -> ccz"),
            enter("ccz"), enter("ccz_z"), enter("ccz_zz"), enter("ccz_zzz"),
            doo("c"), doo("cc"), doo("ccz"), doo("ccz_z"), doo("ccz_zz"), doo("ccz_zzz"), tk()
        )
      ),
      // ccc -> czz
      Arguments.of(
        List.of(msg("c"), tk(), msg("ccc -> czz"), tk()),
        List.of(
          msg("INIT -> c"),
            enter("c"), enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"), tk(),
          exit("ccc"), exit("cc"),
            msg("ccc -> czz"),
            enter("cz"), enter("czz"), enter("czz_z"), enter("czz_zz"), enter("czz_zzz"),
            doo("c"), doo("cz"), doo("czz"), doo("czz_z"), doo("czz_zz"), doo("czz_zzz"), tk()
        )
      ),
      // fff -> fzz
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff -> fzz"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"), exit("fff"), exit("ff"),
            msg("fff -> fzz"),
            enter("fz"), enter("fzz"), enter("fzz_z"), enter("fzz_zz"), enter("fzz_zzz"),
            doo("f"), doo("fz"), doo("fzz"), doo("fzz_z"), doo("fzz_zz"), doo("fzz_zzz"), tk()
        )
      ),
      // fff_fff -> fff_ffz
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff_fff -> fff_ffz"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"),
            msg("fff_fff -> fff_ffz"),
            enter("fff_ffz"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_ffz"), tk()
        )
      ),
      // fff_fff -> fff_zzz
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff_fff -> fff_zzz"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"),
            msg("fff_fff -> fff_zzz"),
            enter("fff_z"), enter("fff_zz"), enter("fff_zzz"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_z"), doo("fff_zz"), doo("fff_zzz"), tk()
        )
      ),
      // b -> b
      Arguments.of(
        List.of(msg("b"), tk(), msg("b -> b"), tk()),
        List.of(
          msg("INIT -> b"),
            enter("b"), enter("bb"),
            doo("b"), doo("bb"), tk(),
          exit("bb"), exit("b"),
            msg("b -> b"),
            enter("b"), enter("bb"),
            doo("b"), doo("bb"), tk()
        )
      ),
      // c -> c
      Arguments.of(
        List.of(msg("c"), tk(), msg("c -> c"), tk()),
        List.of(
          msg("INIT -> c"),
            enter("c"), enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"), tk(),
          exit("ccc"), exit("cc"), exit("c"),
            msg("c -> c"),
            enter("c"), enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"), tk()
        )
      ),
      // cc -> cc
      Arguments.of(
        List.of(msg("c"), tk(), msg("cc -> cc"), tk()),
        List.of(
          msg("INIT -> c"),
            enter("c"), enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"), tk(),
          exit("ccc"), exit("cc"),
            msg("cc -> cc"),
            enter("cc"), enter("ccc"),
            doo("c"), doo("cc"), doo("ccc"), tk()
        )
      ),
      // fff -> fff
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff -> fff"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"), exit("fff_f"), exit("fff"),
            msg("fff -> fff"),
            enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk()
        )
      ),
      // fff_ff -> fff_ff
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff_ff -> fff_ff"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"), exit("fff_ff"),
            msg("fff_ff -> fff_ff"),
            enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk()
        )
      ),
      // fff_fff -> fff_fff
      Arguments.of(
        List.of(msg("f"), tk(), msg("fff_fff -> fff_fff"), tk()),
        List.of(
          msg("INIT -> f"),
            enter("f"), enter("ff"), enter("fff"), enter("fff_f"), enter("fff_ff"), enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk(),
          exit("fff_fff"),
            msg("fff_fff -> fff_fff"),
            enter("fff_fff"),
            doo("f"), doo("ff"), doo("fff"), doo("fff_f"), doo("fff_ff"), doo("fff_fff"), tk()
        )
      )
    );
  }

  private static Message<String> enter(String stateName) {
    return msg("-> " + stateName);
  }

  private static Message<String> doo(String stateName) {
    return msg("~ " + stateName);
  }

  private static Message<String> exit(String stateName) {
    return msg(stateName + " ->");
  }
}
