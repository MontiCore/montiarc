/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc.rte.component.IUntimedComponent;
import montiarc.rte.port.TimeUnawareInPort;
import montiarc.rte.port.TimeUnawareOutPort;

import java.util.List;

public class TimeUnawareEventBasedInverter implements IUntimedComponent {

  protected final String qualifiedInstanceName;

  public TimeUnawareEventBasedInverter(String qualifiedInstanceName) {
    this.qualifiedInstanceName = qualifiedInstanceName;
  }

  @Override
  public String getQualifiedInstanceName() {
    return qualifiedInstanceName;
  }

  @Override
  public List<TimeUnawareInPort<?>> getAllInPorts() {
    return List.of(bIn, iIn);
  }

  @Override
  public List<TimeUnawareOutPort<?>> getAllOutPorts() {
    return List.of(bOut, iOut);
  }

  protected TimeUnawareInPort<Boolean> bIn = new TimeUnawareInPort<>(getQualifiedInstanceName() + ".bIn") {
    @Override
    protected void handleBuffer() {
      if (buffer.isEmpty()) return;

      handleMessageOnBIn();
    }
  };

  protected TimeUnawareInPort<Integer> iIn = new TimeUnawareInPort<>(getQualifiedInstanceName() + ".iIn") {
    @Override
    protected void handleBuffer() {
      if (buffer.isEmpty()) return;

      handleMessageOnIIn();
    }
  };

  protected TimeUnawareOutPort<Integer> iOut = new TimeUnawareOutPort<>(getQualifiedInstanceName() + ".iOut");
  protected TimeUnawareOutPort<Boolean> bOut = new TimeUnawareOutPort<>(getQualifiedInstanceName() + ".bOut");


  protected void handleMessageOnBIn() {
    if (this.bIn.isBufferEmpty()) { // sanity check, also required to set up shadowed variables correctly. If port is set up / used correctly, the buffer should never be empty at this point, making this check theoretically obsolete
      return;
    }
    Boolean bIn = this.bIn.pollBuffer().getValue(); // shadow port variable in order to use code directly from the model

    if (bIn != null) { // code/condition taken directly from respective guard statement

      Boolean bOut = null; // shadow port variable in order to use code directly from the model (we want to shadow only the ports on which data is actually written)

      bOut = !bIn; // code taken directly from respective transition body

      this.bOut.send(bOut); // send shadowed variables as messages - do not send messages on ports that were not affected in the respective transition body

      return; // technically not required here, but present for explanation's sake. If multiple transitions are available and their conditions hold, we want to execute only one transition
    }
  }


  protected void handleMessageOnIIn() {
    if (this.iIn.isBufferEmpty()) { // sanity check, also required to set up shadowed variables correctly. If port is set up / used correctly, the buffer should never be empty at this point, making this check theoretically obsolete
      return;
    }
    Integer iIn = this.iIn.pollBuffer().getValue(); // shadow port variable in order to use code directly from the model

    if (iIn != null) { // code/condition taken directly from respective guard statement

      Integer iOut = null; // shadow port variable in order to use code directly from the model (we want to shadow only the ports on which data is actually written)

      iOut = -1 * iIn; // code taken directly from respective transition body

      this.iOut.send(iOut); // send shadowed variables as messages - do not send messages on ports that were not affected in the respective transition body

      return; // technically not required here, but present for explanation's sake. If multiple transitions are available and their conditions hold, we want to execute only one transition
    }
  }
}
