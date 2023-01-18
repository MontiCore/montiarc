/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;


import types.Types.*;

/*
* Valid model.
*/
component AssignsAttributeOfCD {

  port in Car c,
       out String s;

  List<String> l;
  Position wheelPosition;
  Integer i;

  automaton {
    state S1, S2, S3, S4;

    initial S1 / {l = new ArrayList<String>(), i = Character.SIZE};
    S1 -> S2 / {wheelPosition = c.w.p};
    S2 -> S3 / {s = l.get(0)};
    S4 -> S4 [wheelPosition == Position.FRONT] / {s = "Hello World"};
  }

}
