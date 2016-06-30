package componentTest.gen;

public abstract class SimpleOut extends
    sim.generic.ASimpleOutComponent<java.lang.Integer> implements
    componentTest.gen.interfaces.ISimpleOut {

  protected sim.generic.IIncomingPort<java.lang.String> str1;
  protected sim.generic.IIncomingPort<java.lang.String> str2;

  public SimpleOut() {
    super();
  }

  @Override
  public sim.generic.IIncomingPort<java.lang.String> getStr1() {
    return this.str1;
  }

  @Override
  public sim.generic.IIncomingPort<java.lang.String> getStr2() {
    return this.str2;
  }

  @Override
  public sim.generic.IOutgoingPort<java.lang.Integer> getInteger() {
    return super.getPortOut();
  }

  @Override
  public void checkInvariants() throws sim.error.InvariantInjuredException {
  }

  @Override
  protected boolean areTicksReceivedOnAllPorts() {
    boolean ticksRcv = str1.hasTickReceived() && str2.hasTickReceived();
    return ticksRcv;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void compute(sim.generic.IIncomingPort<?> port) {
    sim.generic.TickedMessage lastMessage = port.getIncomingStream()
        .peekLastMessage();
    // handle ticks
    if (lastMessage != null && lastMessage.isTick()
        && areTicksReceivedOnAllPorts()) {
      str1.getIncomingStream().pollLastMessage();
      str2.getIncomingStream().pollLastMessage();
      sendTick();
      incLocalTime();
    }
    else // handle messages
      if (lastMessage != null && !lastMessage.isTick()
          && lastMessage instanceof sim.generic.Message) {
        if (port == str1) {
          sim.generic.Message<java.lang.String> msg = (sim.generic.Message<java.lang.String>) str1
              .getIncomingStream().pollLastMessage();
          str1MessageReceived(msg);
        }
        else if (port == str2) {
          sim.generic.Message<java.lang.String> msg = (sim.generic.Message<java.lang.String>) str2
              .getIncomingStream().pollLastMessage();
          str2MessageReceived(msg);
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
    sim.generic.TickedMessage lastMessage = port.getIncomingStream()
        .peekLastMessage();
    // handle ticks
    if (lastMessage != null && lastMessage.isTick()
        && areTicksReceivedOnAllPorts()) {
      str1.getIncomingStream().pollLastMessage();
      str2.getIncomingStream().pollLastMessage();
      sendTick();
      incLocalTime();
    }
    else // handle messages
      if (lastMessage != null && !lastMessage.isTick()
          && lastMessage instanceof sim.generic.Message) {
        while (port.getIncomingStream().peekLastMessage() != null
            && !port.getIncomingStream().peekLastMessage().isTick()) {
          if (port == str1) {
            sim.generic.Message<java.lang.String> msg = (sim.generic.Message<java.lang.String>) str1
                .getIncomingStream().pollLastMessage();
            str1MessageReceived(msg);
          }
          else if (port == str2) {
            sim.generic.Message<java.lang.String> msg = (sim.generic.Message<java.lang.String>) str2
                .getIncomingStream().pollLastMessage();
            str2MessageReceived(msg);
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
  public void setup(sim.IScheduler scheduler,
      sim.error.ISimulationErrorHandler errorHandler) {
    this._errorHandler = errorHandler;
    this.str1 = new sim.generic.Port<java.lang.String>(java.lang.String.class);
    this.str1.setScheduler(scheduler);
    this.str1.setComponent(this);
    this.str2 = new sim.generic.Port<java.lang.String>(java.lang.String.class);
    this.str2.setScheduler(scheduler);
    this.str2.setComponent(this);
  }
}