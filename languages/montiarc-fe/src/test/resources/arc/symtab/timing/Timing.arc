package timing;

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