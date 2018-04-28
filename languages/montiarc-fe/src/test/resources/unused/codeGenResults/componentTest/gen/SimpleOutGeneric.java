package componentTest.gen;

public abstract class SimpleOutGeneric<T> extends
    sim.generic.ASimpleOutComponent<T> implements
    componentTest.gen.interfaces.ISimpleOutGeneric<T> {
  protected sim.generic.IIncomingPort<java.lang.String> string;
  protected sim.generic.IIncomingPort<T> input1;
  protected sim.generic.IIncomingPort<T> input2;
  protected java.lang.Class<T> _genericType;

  public SimpleOutGeneric(java.lang.Class<T> _genericType) {
    super();
    this._genericType = _genericType;
  }

  @Override
  public sim.generic.IIncomingPort<java.lang.String> getString() {
    return this.string;
  }

  @Override
  public sim.generic.IIncomingPort<T> getInput1() {
    return this.input1;
  }

  @Override
  public sim.generic.IIncomingPort<T> getInput2() {
    return this.input2;
  }

  @Override
  public sim.generic.IOutgoingPort<T> getOutput() {
    return super.getPortOut();
  }

  @Override
  public void checkInvariants() throws sim.error.InvariantInjuredException {
  }

  @Override
  protected boolean areTicksReceivedOnAllPorts() {
    boolean ticksRcv = string.hasTickReceived() && input1.hasTickReceived()
        && input2.hasTickReceived();
    return ticksRcv;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void compute(sim.generic.IIncomingPort<?> port) {
    sim.generic.TickedMessage lastMessage = port.getIncomingStream().peekLastMessage();
    // handle ticks
    if (lastMessage != null && lastMessage.isTick()
        && areTicksReceivedOnAllPorts()) {
      string.getIncomingStream().pollLastMessage();
      input1.getIncomingStream().pollLastMessage();
      input2.getIncomingStream().pollLastMessage();
      sendTick();
      incLocalTime();
    }
    else // handle messages
      if (lastMessage != null && !lastMessage.isTick()
          && lastMessage instanceof sim.generic.Message) {
        if (port == string) {
          sim.generic.Message<java.lang.String> msg = (sim.generic.Message<java.lang.String>) string
              .getIncomingStream().pollLastMessage();
          stringMessageReceived(msg);
        }
        else if (port == input1) {
          sim.generic.Message<T> msg = (sim.generic.Message<T>) input1
              .getIncomingStream().pollLastMessage();
          input1MessageReceived(msg);
        }
        else if (port == input2) {
          sim.generic.Message<T> msg = (sim.generic.Message<T>) input2
              .getIncomingStream().pollLastMessage();
          input2MessageReceived(msg);
        }
      }
    try {
      this.checkInvariants();
    }
    catch (sim.error.InvariantInjuredException e) {
      if (_errorHandler != null) {
        StringBuilder key = new StringBuilder();
        key.append(port.getComponent().getLocalTime());
        for (int i = key.toString().length(); i < 6; i++) {
          key.insert(0, "0");
        }
        key.append(": ");
        key.append(port.getComponent().toString());
        _errorHandler.addError(key.toString(), e.getMessage());
      }
      else {
        e.printStackTrace();
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void computeAll(sim.generic.IIncomingPort<?> port) {
    sim.generic.TickedMessage lastMessage = port.getIncomingStream().peekLastMessage();
    // handle ticks
    if (lastMessage != null && lastMessage.isTick()
        && areTicksReceivedOnAllPorts()) {
      string.getIncomingStream().pollLastMessage();
      input1.getIncomingStream().pollLastMessage();
      input2.getIncomingStream().pollLastMessage();
      sendTick();
      incLocalTime();
    }
    else // handle messages
      if (lastMessage != null && !lastMessage.isTick()
          && lastMessage instanceof sim.generic.Message) {
        while (port.getIncomingStream().peekLastMessage() != null && !port.getIncomingStream().peekLastMessage().isTick()) {
          if (port == string) {
            sim.generic.Message<java.lang.String> msg = (sim.generic.Message<java.lang.String>) string
                .getIncomingStream().pollLastMessage();
            stringMessageReceived(msg);
          }
          else if (port == input1) {
            sim.generic.Message<T> msg = (sim.generic.Message<T>) input1
                .getIncomingStream().pollLastMessage();
            input1MessageReceived(msg);
          }
          else if (port == input2) {
            sim.generic.Message<T> msg = (sim.generic.Message<T>) input2
                .getIncomingStream().pollLastMessage();
            input2MessageReceived(msg);
          }
        }
      }
    try {
      this.checkInvariants();
    }
    catch (sim.error.InvariantInjuredException e) {
      if (_errorHandler != null) {
        StringBuilder key = new StringBuilder();
        key.append(port.getComponent().getLocalTime());
        for (int i = key.toString().length(); i < 6; i++) {
          key.insert(0, "0");
        }
        key.append(": ");
        key.append(port.getComponent().toString());
        _errorHandler.addError(key.toString(), e.getMessage());
      }
      else {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void setup(sim.IScheduler scheduler, sim.error.ISimulationErrorHandler errorHandler) {
    this._errorHandler = errorHandler;
    this.string = new sim.generic.Port<java.lang.String>(java.lang.String.class);
    this.string.setScheduler(scheduler);
    this.string.setComponent(this);
    this.input1 = new sim.generic.Port<T>(_genericType);
    this.input1.setScheduler(scheduler);
    this.input1.setComponent(this);
    this.input2 = new sim.generic.Port<T>(_genericType);
    this.input2.setScheduler(scheduler);
    this.input2.setComponent(this);
  }
}