/* (c) https://github.com/MontiCore/monticore */
package arc.lang.string;

component SAut() {
  port in String i;
  port out String o;

  <<sync>> automaton {
    initial state s1;
    state s2;

    s1 -> s1 [i.isBlank()] / {
      o = "inBlank";
    };

    s1 -> s2 [!i.isBlank()] / {
      o = i;
    };

    s2 -> s2 [!i.isBlank()] / {
      o = i;
    };

    s2 -> s1 [i.isBlank()] / {
      o = "toBlank";
    };
  }
}
