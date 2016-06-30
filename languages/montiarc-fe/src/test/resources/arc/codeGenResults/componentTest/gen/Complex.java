package componentTest.gen;

public abstract class Complex extends sim.generic.AComponent implements
    componentTest.gen.interfaces.IComplex {

  protected final componentTest.gen.helper.MessageFactory _messageFactory;

  protected sim.generic.IIncomingPort<java.lang.String> str2;
  protected sim.generic.IIncomingPort<java.lang.String> str1;
  protected sim.generic.IOutgoingPort<java.lang.Integer> int1;
  protected sim.generic.IOutgoingPort<java.lang.Integer> int2;

  public Complex() {
    super();
    this._messageFactory = componentTest.gen.helper.MessageFactory
        .getInstance();
  }

  @Override
  public sim.generic.IIncomingPort<java.lang.String> getStr2() {
    return this.str2;
  }

  @Override
  public sim.generic.IIncomingPort<java.lang.String> getStr1() {
    return this.str1;
  }

  @Override
  public sim.generic.IOutgoingPort<java.lang.Integer> getInt1() {
    return this.int1;
  }

  @Override
  public sim.generic.IOutgoingPort<java.lang.Integer> getInt2() {
    return this.int2;
  }

  @Override
  public void checkInvariants() throws sim.error.InvariantInjuredException {
  }

  @Override
  protected boolean areTicksReceivedOnAllPorts() {
    boolean ticksRcv = str2.hasTickReceived() && str1.hasTickReceived();
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
      str2.getIncomingStream().pollLastMessage();
      str1.getIncomingStream().pollLastMessage();
      sendTick();
      incLocalTime();
    }
    else // handle messages
      if (lastMessage != null && !lastMessage.isTick()
          && lastMessage instanceof sim.generic.Message) {
        if (port == str2) {
          sim.generic.Message<java.lang.String> msg = (sim.generic.Message<java.lang.String>) str2
              .getIncomingStream().pollLastMessage();
          str2MessageReceived(msg);
        }
        else if (port == str1) {
          sim.generic.Message<java.lang.String> msg = (sim.generic.Message<java.lang.String>) str1
              .getIncomingStream().pollLastMessage();
          str1MessageReceived(msg);
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
      str2.getIncomingStream().pollLastMessage();
      str1.getIncomingStream().pollLastMessage();
      sendTick();
      incLocalTime();
    }
    else // handle messages
      if (lastMessage != null && !lastMessage.isTick()
          && lastMessage instanceof sim.generic.Message) {
        while (port.getIncomingStream().peekLastMessage() != null
            && !port.getIncomingStream().peekLastMessage().isTick()) {
          if (port == str2) {
            sim.generic.Message<java.lang.String> msg = (sim.generic.Message<java.lang.String>) str2
                .getIncomingStream().pollLastMessage();
            str2MessageReceived(msg);
          }
          else if (port == str1) {
            sim.generic.Message<java.lang.String> msg = (sim.generic.Message<java.lang.String>) str1
                .getIncomingStream().pollLastMessage();
            str1MessageReceived(msg);
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
  protected void sendTick() {
    sim.generic.Tick<java.lang.Integer> int1Tick = this._messageFactory
        .getTick("java.lang.Integer");
    getInt1().sendMessage(int1Tick);
    sim.generic.Tick<java.lang.Integer> int2Tick = this._messageFactory
        .getTick("java.lang.Integer");
    getInt2().sendMessage(int2Tick);
  }

  @Override
  public void setup(sim.IScheduler scheduler,
      sim.error.ISimulationErrorHandler errorHandler) {
    this._errorHandler = errorHandler;
    this.str2 = new sim.generic.Port<java.lang.String>(java.lang.String.class);
    this.str2.setScheduler(scheduler);
    this.str2.setComponent(this);
    this.str1 = new sim.generic.Port<java.lang.String>(java.lang.String.class);
    this.str1.setScheduler(scheduler);
    this.str1.setComponent(this);
    this.int1 = new sim.generic.Port<java.lang.Integer>(java.lang.Integer.class);
    this.int1.setComponent(this);
    this.int2 = new sim.generic.Port<java.lang.Integer>(java.lang.Integer.class);
    this.int2.setComponent(this);
  }
}