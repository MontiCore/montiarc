/* (c) https://github.com/MontiCore/monticore */
package montiarc.ma2jsim.test;

component ModeTest {
  port sync in boolean i;

  component HelloWorld {
    automaton {
      initial state S;
      S -> S;
    }
  }

  component FooComp {
    automaton {
      initial state S;
      S -> S;
    }
  }

  mode automaton {
    initial mode M1 {
      HelloWorld helloWorld;
    }

    mode M2 {
      FooComp fooComp;
    }

    M1 -> M2;
    M2 -> M1;
  }
}
