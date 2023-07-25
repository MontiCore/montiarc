/* (c) https://github.com/MontiCore/monticore */
package montiarc.scheduling;

import montiarc.rte.component.IComponent;
import montiarc.rte.port.TimeAwareOutPort;
import montiarc.rte.port.TimeAwarePortForward;
import montiarc.rte.scheduling.ISchedule;

import java.util.List;

public class Scheduled implements IComponent<TimeAwarePortForward<?>, TimeAwareOutPort<?>> {

  protected final String name;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<TimeAwarePortForward<?>> getAllInPorts() {
    return List.of(trigger);
  }

  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of();
  }

  ISchedule scheduler;
  StringBuilder trace;

  TimeAwarePortForward<Boolean> trigger = new TimeAwarePortForward<>(getName() + ".trigger");

  ScheduledInner a, b, c, d, e, f, g, h, i, j, k;

  public Scheduled(String name, ISchedule scheduler,
                   StringBuilder trace) { // last parameter is from the model, first 2 are "default"
    this.name = name;
    this.scheduler = scheduler;
    this.trace = trace;

    this.a = new ScheduledInner(this.name + ".a", this.scheduler, this.trace, "a");
    this.b = new ScheduledInner(this.name + ".b", this.scheduler, this.trace, "b");
    this.c = new ScheduledInner(this.name + ".c", this.scheduler, this.trace, "c");
    this.d = new ScheduledInner(this.name + ".d", this.scheduler, this.trace, "d");
    this.e = new ScheduledInner(this.name + ".e", this.scheduler, this.trace, "e");
    this.f = new ScheduledInner(this.name + ".f", this.scheduler, this.trace, "f");
    this.g = new ScheduledInner(this.name + ".g", this.scheduler, this.trace, "g");
    this.h = new ScheduledInner(this.name + ".h", this.scheduler, this.trace, "h");
    this.i = new ScheduledInner(this.name + ".i", this.scheduler, this.trace, "i");
    this.j = new ScheduledInner(this.name + ".j", this.scheduler, this.trace, "j");
    this.k = new ScheduledInner(this.name + ".k", this.scheduler, this.trace, "k");

    trigger.connect(a.trigger);
    trigger.connect(b.trigger);
    trigger.connect(c.trigger);

    a.output.connect(d.trigger);
    b.output.connect(e.trigger);
    b.output.connect(f.trigger);
    c.output.connect(g.trigger);

    d.output.connect(h.trigger);
    e.output.connect(i.trigger);
    f.output.connect(j.trigger);
    g.output.connect(k.trigger);
  }
}
