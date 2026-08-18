/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test={
  [Event<String><"c", "check", "ccc -> c", "check", Tick>, Event<String><"-> c", "ccc", "ccc -> c", "ccc", Tick>],
  [Event<String><"c", "check", "ccc -> cc", "check", Tick>, Event<String><"-> c", "ccc", "ccc -> cc", "ccc", Tick>],
  [Event<String><"d", "check", "ddd -> d", "check", Tick>, Event<String><"-> d", "ddd_d", "ddd -> d", "ddd_d", Tick>],
  [Event<String><"d", "check", "ddd -> dd", "check", Tick>, Event<String><"-> d", "ddd_d", "ddd -> dd", "ddd_d", Tick>],
  [Event<String><"f", "check", "fff -> f", "check", Tick>, Event<String><"-> f", "fff_fff", "fff -> f", "fff_fff", Tick>],
  [Event<String><"f", "check", "fff -> ff", "check", Tick>, Event<String><"-> f", "fff_fff", "fff -> ff", "fff_fff", Tick>],
  [Event<String><"f", "check", "fff_ff -> fff", "check", Tick>, Event<String><"-> f", "fff_fff", "fff_ff -> fff", "fff_fff", Tick>],
  [Event<String><"f", "check", "fff_ff -> fff_f", "check", Tick>, Event<String><"-> f", "fff_fff", "fff_ff -> fff_f", "fff_fff", Tick>],
  [Event<String><"f", "check", "fff_fff -> fff_f", "check", Tick>, Event<String><"-> f", "fff_fff", "fff_fff -> fff_f", "fff_fff", Tick>],
  [Event<String><"f", "check", "fff_fff -> fff_ff", "check", Tick>, Event<String><"-> f", "fff_fff", "fff_fff -> fff_ff", "fff_fff", Tick>],
  [Event<String><"b", "check", "b -> bz", "check", Tick>, Event<String><"-> b", "bb", "b -> bz", "bz", Tick>],
  [Event<String><"c", "check", "c -> cz", "check", Tick>, Event<String><"-> c", "ccc", "c -> cz", "czz", Tick>],
  [Event<String><"c", "check", "c -> czz", "check", Tick>, Event<String><"-> c", "ccc", "c -> czz", "czz", Tick>],
  [Event<String><"d", "check", "d -> dz", "check", Tick>, Event<String><"-> d", "ddd_d", "d -> dz", "dzz_z", Tick>],
  [Event<String><"d", "check", "d -> dzz", "check", Tick>, Event<String><"-> d", "ddd_d", "d -> dzz", "dzz_z", Tick>],
  [Event<String><"d", "check", "dd -> ddz", "check", Tick>, Event<String><"-> d", "ddd_d", "dd -> ddz", "ddz_z", Tick>],
  [Event<String><"f", "check", "f -> ffz", "check", Tick>, Event<String><"-> f", "fff_fff", "f -> ffz", "ffz_zzz", Tick>],
  [Event<String><"f", "check", "f -> fzz_z", "check", Tick>, Event<String><"-> f", "fff_fff", "f -> fzz_z", "fzz_zzz", Tick>],
  [Event<String><"f", "check", "fff -> fff_zz", "check", Tick>, Event<String><"-> f", "fff_fff", "fff -> fff_zz", "fff_zzz", Tick>],
  [Event<String><"f", "check", "fff -> fff_zzz", "check", Tick>, Event<String><"-> f", "fff_fff", "fff -> fff_zzz", "fff_zzz", Tick>],
  [Event<String><"f", "check", "fff_f -> fff_fz", "check", Tick>, Event<String><"-> f", "fff_fff", "fff_f -> fff_fz", "fff_fzz", Tick>],
  [Event<String><"b", "check", "bb -> bz", "check", Tick>, Event<String><"-> b", "bb", "bb -> bz", "bz", Tick>],
  [Event<String><"c", "check", "cc -> cz", "check", Tick>, Event<String><"-> c", "ccc", "cc -> cz", "czz", Tick>],
  [Event<String><"d", "check", "dd -> dz", "check", Tick>, Event<String><"-> d", "ddd_d", "dd -> dz", "dzz_z", Tick>],
  [Event<String><"e", "check", "ee -> ez", "check", Tick>, Event<String><"-> e", "eee_ee", "ee -> ez", "ezz_zz", Tick>],
  [Event<String><"c", "check", "ccc -> ccz", "check", Tick>, Event<String><"-> c", "ccc", "ccc -> ccz", "ccz", Tick>],
  [Event<String><"c", "check", "ccc -> czz", "check", Tick>, Event<String><"-> c", "ccc", "ccc -> czz", "czz", Tick>],
  [Event<String><"f", "check", "fff -> fzz", "check", Tick>, Event<String><"-> f", "fff_fff", "fff -> fzz", "fzz_zzz", Tick>],
  [Event<String><"f", "check", "fff_fff -> fff_ffz", "check", Tick>, Event<String><"-> f", "fff_fff", "fff_fff -> fff_ffz", "fff_ffz", Tick>],
  [Event<String><"f", "check", "fff_fff -> fff_zzz", "check", Tick>, Event<String><"-> f", "fff_fff", "fff_fff -> fff_zzz", "fff_zzz", Tick>],
  [Event<String><"b", "check", "b -> b", "check", Tick>, Event<String><"-> b", "bb", "b -> b", "bb", Tick>],
  [Event<String><"c", "check", "c -> c", "check", Tick>, Event<String><"-> c", "ccc", "c -> c", "ccc", Tick>],
  [Event<String><"c", "check", "cc -> cc", "check", Tick>, Event<String><"-> c", "ccc", "cc -> cc", "ccc", Tick>],
  [Event<String><"f", "check", "fff -> fff", "check", Tick>, Event<String><"-> f", "fff_fff", "fff -> fff", "fff_fff", Tick>],
  [Event<String><"f", "check", "fff_ff -> fff_ff", "check", Tick>, Event<String><"-> f", "fff_fff", "fff_ff -> fff_ff", "fff_fff", Tick>],
  [Event<String><"f", "check", "fff_fff -> fff_fff", "check", Tick>, Event<String><"-> f", "fff_fff", "fff_fff -> fff_fff", "fff_fff", Tick>]
}>>
component NestedTransitionsInSameHierarchyTest(EventStream<String> input, EventStream<String> expected) {
  NestedTransitionsInSameHierarchy sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<String> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
