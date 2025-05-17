/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import de.se_rwth.commons.logging.LogStub;
import montiarc.rte.port.PortObserver;
import montiarc.types.OnOff;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static montiarc.rte.msg.MessageFactory.tk;

public class NestedDelayedLoopTest {
    
    @Test
    public void foo() {
        LogStub.init();
        NestedDelayedLoopComp sut = new NestedDelayedLoopCompBuilder().setName("SUT").build();

        PortObserver<OnOff> port = new PortObserver<>();
        sut.port_o().connect(port);

        int num_ticks = 20;
        
        for (int i = 0 ; i < num_ticks ; i++) {
            sut.port_i().receive(tk());
        }
        
        sut.runToCompletion();
        Assertions.assertEquals(21, port.getObservedMessages().size());
    }
}
