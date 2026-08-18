/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<String><"c", "check", "ccc -> c", "check">, Untimed<String><"-> c", "ccc", "ccc -> c", "ccc">],
  [Sync<String><"c", "check", "ccc -> cc", "check">, Untimed<String><"-> c", "ccc", "ccc -> cc", "ccc">],
  [Sync<String><"d", "check", "ddd -> d", "check">, Untimed<String><"-> d", "ddd_d", "ddd -> d", "ddd_d">],
  [Sync<String><"d", "check", "ddd -> dd", "check">, Untimed<String><"-> d", "ddd_d", "ddd -> dd", "ddd_d">],
  [Sync<String><"f", "check", "fff -> f", "check">, Untimed<String><"-> f", "fff_fff", "fff -> f", "fff_fff">],
  [Sync<String><"f", "check", "fff -> ff", "check">, Untimed<String><"-> f", "fff_fff", "fff -> ff", "fff_fff">],
  [Sync<String><"f", "check", "fff_ff -> fff", "check">, Untimed<String><"-> f", "fff_fff", "fff_ff -> fff", "fff_fff">],
  [Sync<String><"f", "check", "fff_ff -> fff_f", "check">, Untimed<String><"-> f", "fff_fff", "fff_ff -> fff_f", "fff_fff">],
  [Sync<String><"f", "check", "fff_fff -> fff_f", "check">, Untimed<String><"-> f", "fff_fff", "fff_fff -> fff_f", "fff_fff">],
  [Sync<String><"f", "check", "fff_fff -> fff_ff", "check">, Untimed<String><"-> f", "fff_fff", "fff_fff -> fff_ff", "fff_fff">],
  [Sync<String><"b", "check", "b -> bz", "check">, Untimed<String><"-> b", "bb", "b -> bz", "bz">],
  [Sync<String><"c", "check", "c -> cz", "check">, Untimed<String><"-> c", "ccc", "c -> cz", "czz">],
  [Sync<String><"c", "check", "c -> czz", "check">, Untimed<String><"-> c", "ccc", "c -> czz", "czz">],
  [Sync<String><"d", "check", "d -> dz", "check">, Untimed<String><"-> d", "ddd_d", "d -> dz", "dzz_z">],
  [Sync<String><"d", "check", "d -> dzz", "check">, Untimed<String><"-> d", "ddd_d", "d -> dzz", "dzz_z">],
  [Sync<String><"d", "check", "dd -> ddz", "check">, Untimed<String><"-> d", "ddd_d", "dd -> ddz", "ddz_z">],
  [Sync<String><"f", "check", "f -> ffz", "check">, Untimed<String><"-> f", "fff_fff", "f -> ffz", "ffz_zzz">],
  [Sync<String><"f", "check", "f -> fzz_z", "check">, Untimed<String><"-> f", "fff_fff", "f -> fzz_z", "fzz_zzz">],
  [Sync<String><"f", "check", "fff -> fff_zz", "check">, Untimed<String><"-> f", "fff_fff", "fff -> fff_zz", "fff_zzz">],
  [Sync<String><"f", "check", "fff -> fff_zzz", "check">, Untimed<String><"-> f", "fff_fff", "fff -> fff_zzz", "fff_zzz">],
  [Sync<String><"f", "check", "fff_f -> fff_fz", "check">, Untimed<String><"-> f", "fff_fff", "fff_f -> fff_fz", "fff_fzz">],
  [Sync<String><"b", "check", "bb -> bz", "check">, Untimed<String><"-> b", "bb", "bb -> bz", "bz">],
  [Sync<String><"c", "check", "cc -> cz", "check">, Untimed<String><"-> c", "ccc", "cc -> cz", "czz">],
  [Sync<String><"d", "check", "dd -> dz", "check">, Untimed<String><"-> d", "ddd_d", "dd -> dz", "dzz_z">],
  [Sync<String><"e", "check", "ee -> ez", "check">, Untimed<String><"-> e", "eee_ee", "ee -> ez", "ezz_zz">],
  [Sync<String><"c", "check", "ccc -> ccz", "check">, Untimed<String><"-> c", "ccc", "ccc -> ccz", "ccz">],
  [Sync<String><"c", "check", "ccc -> czz", "check">, Untimed<String><"-> c", "ccc", "ccc -> czz", "czz">],
  [Sync<String><"f", "check", "fff -> fzz", "check">, Untimed<String><"-> f", "fff_fff", "fff -> fzz", "fzz_zzz">],
  [Sync<String><"f", "check", "fff_fff -> fff_ffz", "check">, Untimed<String><"-> f", "fff_fff", "fff_fff -> fff_ffz", "fff_ffz">],
  [Sync<String><"f", "check", "fff_fff -> fff_zzz", "check">, Untimed<String><"-> f", "fff_fff", "fff_fff -> fff_zzz", "fff_zzz">],
  [Sync<String><"b", "check", "b -> b", "check">, Untimed<String><"-> b", "bb", "b -> b", "bb">],
  [Sync<String><"c", "check", "c -> c", "check">, Untimed<String><"-> c", "ccc", "c -> c", "ccc">],
  [Sync<String><"c", "check", "cc -> cc", "check">, Untimed<String><"-> c", "ccc", "cc -> cc", "ccc">],
  [Sync<String><"f", "check", "fff -> fff", "check">, Untimed<String><"-> f", "fff_fff", "fff -> fff", "fff_fff">],
  [Sync<String><"f", "check", "fff_ff -> fff_ff", "check">, Untimed<String><"-> f", "fff_fff", "fff_ff -> fff_ff", "fff_fff">],
  [Sync<String><"f", "check", "fff_fff -> fff_fff", "check">, Untimed<String><"-> f", "fff_fff", "fff_fff -> fff_fff", "fff_fff">]
}, ticks=[4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4]>>
component NestedTransitionsInSameHierarchyTest(SyncStream<String> input, UntimedStream<String> expected) {
  NestedTransitionsInSameHierarchy sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<String> generator(input);

  AssertEqualsUntimed<String> assertions(expected);
}
