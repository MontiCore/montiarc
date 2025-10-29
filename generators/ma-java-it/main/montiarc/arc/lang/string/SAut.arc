/* (c) https://github.com/MontiCore/monticore */
package arc.lang.string;

component SAut() {
  port sync in String i;
  port sync out String o;

  automaton {
    initial state s1;
    state s2;

    s1 -> s1 [i.isBlank()] / {
      o = "inBlank";
    }

    s1 -> s2 [!i.isBlank()] / {
      o = i;
    }

    s2 -> s2 [!i.isBlank()] / {
      o = i;
    }

    s2 -> s1 [i.isBlank()] / {
      o = "toBlank";
    }
  }
}
