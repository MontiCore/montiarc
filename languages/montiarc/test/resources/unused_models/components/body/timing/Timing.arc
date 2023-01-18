/* (c) https://github.com/MontiCore/monticore */
package components.body.timing;

/*
 * Valid model.
 */
component Timing {

    component TimedInner {
        timing instant;
    }

    component TimedDelayingInner {
        timing delayed;
    }

    component TimeSyncInner {
        timing sync;
    }

    component TimeCausalSyncInner {
        timing causalsync;
    }

    component UntimedInner {
        timing untimed;
    }

}
