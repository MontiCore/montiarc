package componentTest.gen;

public abstract class Merge<T> extends sim.generic.AComponent implements
    componentTest.gen.interfaces.IMerge<T> {
  protected final componentTest.gen.helper.MessageFactory _messageFactory;

  protected sim.generic.IIncomingPort<T> input4;
  protected sim.generic.IIncomingPort<T> input3;
  protected sim.generic.IIncomingPort<T> input2;
  protected sim.generic.IIncomingPort<T> input1;
  protected sim.generic.IOutgoingPort<T> output2;
  protected sim.generic.IOutgoingPort<T> output1;
  protected java.lang.Class<T> _genericType;

  public Merge(String encoding, java.lang.Class<T> _genericType) {
    super();
    this._messageFactory = componentTest.gen.helper.MessageFactory.getInstance();
    this._genericType = _genericType;
  }

  @Override
  public sim.generic.IIncomingPort<T> getInput4() {
    return this.input4;
  }

  @Override
  public sim.generic.IIncomingPort<T> getInput3() {
    return this.input3;
  }

  @Override
  public sim.generic.IIncomingPort<T> getInput2() {
    return this.input2;
  }

  @Override
  public sim.generic.IIncomingPort<T> getInput1() {
    return this.input1;
  }

  @Override
  public sim.generic.IOutgoingPort<T> getOutput2() {
    return this.output2;
  }

  @Override
  public sim.generic.IOutgoingPort<T> getOutput1() {
    return this.output1;
  }

  @Override
  public void checkInvariants() throws sim.error.InvariantInjuredException {
  }

  @Override
  protected boolean areTicksReceivedOnAllPorts() {
    boolean ticksRcv = input4.hasTickReceived() && input3.hasTickReceived()
        && input2.hasTickReceived() && input1.hasTickReceived();
    return ticksRcv;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void compute(sim.generic.IIncomingPort<?> port) {
    sim.generic.TickedMessage lastMessage = port.getIncomingStream().peekLastMessage();
    // handle ticks
    if (lastMessage != null && lastMessage.isTick()
        && areTicksReceivedOnAllPorts()) {
      input4.getIncomingStream().pollLastMessage();
      input3.getIncomingStream().pollLastMessage();
      input2.getIncomingStream().pollLastMessage();
      input1.getIncomingStream().pollLastMessage();
      sendTick();
      incLocalTime();
    }
    else // handle messages
      if (lastMessage != null && !lastMessage.isTick()
          && lastMessage instanceof sim.generic.Message) {
        if (port == input4) {
          sim.generic.Message<T> msg = (sim.generic.Message<T>) input4
              .getIncomingStream().pollLastMessage();
          input4MessageReceived(msg);
        }
        else if (port == input3) {
          sim.generic.Message<T> msg = (sim.generic.Message<T>) input3
              .getIncomingStream().pollLastMessage();
          input3MessageReceived(msg);
        }
        else if (port == input2) {
          sim.generic.Message<T> msg = (sim.generic.Message<T>) input2
              .getIncomingStream().pollLastMessage();
          input2MessageReceived(msg);
        }
        else if (port == input1) {
          sim.generic.Message<T> msg = (sim.generic.Message<T>) input1
              .getIncomingStream().pollLastMessage();
          input1MessageReceived(msg);
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
      input4.getIncomingStream().pollLastMessage();
      input3.getIncomingStream().pollLastMessage();
      input2.getIncomingStream().pollLastMessage();
      input1.getIncomingStream().pollLastMessage();
      sendTick();
      incLocalTime();
    }
    else // handle messages
      if (lastMessage != null && !lastMessage.isTick()
          && lastMessage instanceof sim.generic.Message) {
        while (port.getIncomingStream().peekLastMessage() != null && !port.getIncomingStream().peekLastMessage().isTick()) {
          if (port == input4) {
            sim.generic.Message<T> msg = (sim.generic.Message<T>) input4
                .getIncomingStream().pollLastMessage();
            input4MessageReceived(msg);
          }
          else if (port == input3) {
            sim.generic.Message<T> msg = (sim.generic.Message<T>) input3
                .getIncomingStream().pollLastMessage();
            input3MessageReceived(msg);
          }
          else if (port == input2) {
            sim.generic.Message<T> msg = (sim.generic.Message<T>) input2
                .getIncomingStream().pollLastMessage();
            input2MessageReceived(msg);
          }
          else if (port == input1) {
            sim.generic.Message<T> msg = (sim.generic.Message<T>) input1
                .getIncomingStream().pollLastMessage();
            input1MessageReceived(msg);
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
    sim.generic.Tick<T> output2Tick = this._messageFactory.getTick(_genericType
        .getName());
    getOutput2().sendMessage(output2Tick);
    sim.generic.Tick<T> output1Tick = this._messageFactory.getTick(_genericType
        .getName());
    getOutput1().sendMessage(output1Tick);
  }

  @Override
  public void setup(sim.IScheduler scheduler,
      sim.error.ISimulationErrorHandler errorHandler) {
    this._errorHandler = errorHandler;
    this.input4 = new sim.generic.Port<T>(_genericType);
    this.input4.setScheduler(scheduler);
    this.input4.setComponent(this);
    this.input3 = new sim.generic.Port<T>(_genericType);
    this.input3.setScheduler(scheduler);
    this.input3.setComponent(this);
    this.input2 = new sim.generic.Port<T>(_genericType);
    this.input2.setScheduler(scheduler);
    this.input2.setComponent(this);
    this.input1 = new sim.generic.Port<T>(_genericType);
    this.input1.setScheduler(scheduler);
    this.input1.setComponent(this);
    this.output2 = new sim.generic.Port<T>(_genericType);
    this.output2.setComponent(this);
    this.output1 = new sim.generic.Port<T>(_genericType);
    this.output1.setComponent(this);
  }
}