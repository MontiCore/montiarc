package robotmodels.time;

import robotmodels.time.types.*;

component Timer(Long delay) {
    port
        in TimerCmd timerCmd,
        out TimerSignal timerSignal;
}