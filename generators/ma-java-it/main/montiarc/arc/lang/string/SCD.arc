/* (c) https://github.com/MontiCore/monticore */
package arc.lang.string;

import arc.lang.Strings.SF;
import arc.lang.Strings.SFs;

component SCD {
  port in SF sf, in SFs sfs;

  port out String o1, o2, o3;

  SF vsf = SF.SF("");
  SFs vsfs = SFs.SFs("", "");

  <<sync>> automaton {
    initial state s;

    s -> s / {
      o1 = vsf.s;
      o2 = vsfs.v1;
      o3 = vsfs.v2;
      vsf = sf;
      vsfs = sfs;
    };
  }
}
