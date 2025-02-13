/* (c) https://github.com/MontiCore/monticore */
package arc.lang.string;

component SExpr {
  port in String i;
  port out String o;

  String v1 = "";
  java.lang.String v2 = "";

  automaton {
     initial state s;
     s -> s / { o = i; };
     s -> s / { o = ""; };
     s -> s / { o = v1; };
     s -> s / { o = v2; };
     s -> s / { o = i + i; };
     s -> s / { o = i + ""; };
     s -> s / { o = i + v1; };
     s -> s / { o = i + v2; };
     s -> s / { o = "" + i; };
     s -> s / { o = "" + ""; };
     s -> s / { o = "" + v1; };
     s -> s / { o = "" + v2; };
     s -> s / { o = v1 + i; };
     s -> s / { o = v1 + ""; };
     s -> s / { o = v1 + v1; };
     s -> s / { o = v1 + v2; };
     s -> s / { o = v2 + i; };
     s -> s / { o = v2 + ""; };
     s -> s / { o = v2 + v1; };
     s -> s / { o = v2 + v2; };
  }
}
