package factory;

component Timer {
  port in int time;
  port <<delayed>> out boolean timeout;

  int timer = 0;

  automaton {
    initial state idle;
    state running;

    idle -> running [time > 0] time / {
      timer = time;
    };

    running -> running [timer > 1] / {
      timer = timer - 1;
    };

    running -> idle [timer == 0] / {
      timeout = true;
    };
  }
}