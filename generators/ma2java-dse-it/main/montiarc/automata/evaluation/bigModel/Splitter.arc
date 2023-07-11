/* (c) https://github.com/MontiCore/monticore */
package evaluation.bigModel;

component Splitter(Integer parameter) {

  port <<sync>> in Integer ins,
       <<sync>> out Boolean o1, o2, o3, o4;

  automaton{
    initial state Idle;

    Idle -> Idle [ins == 1 && ins < parameter]/{
      o1 = true;
      o2 = false;
      o3 = false;
      o4 = false;
    };

    Idle -> Idle [ins == 2 && ins < parameter]/{
      o1 = false;
      o2 = true;
      o3 = false;
      o4 = false;
    };

    Idle -> Idle [ins == 3 && ins < parameter]/{
      o1 = false;
      o2 = false;
      o3 = true;
      o4 = false;
    };

    Idle -> Idle [ins == 4 && ins < parameter]/{
      o1 = false;
      o2 = false;
      o3 = false;
      o4 = true;
    };

    Idle -> Idle [ins != 1 && ins != 2 && ins != 3 && ins != 4]/{
      o1 = false;
      o2 = false;
      o3 = false;
      o4 = false;
    };
  }
}
