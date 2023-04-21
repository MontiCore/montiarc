/* (c) https://github.com/MontiCore/monticore */
package montiarc.scheduling;

import montiarc.rte.component.IComponent;
import montiarc.rte.port.TimeAwareOutPort;
import montiarc.rte.port.TimeAwarePortForward;
import montiarc.rte.scheduling.ISchedule;

import java.util.List;

public class Scheduled implements IComponent<TimeAwarePortForward<?>, TimeAwareOutPort<?>> {

  protected final String qualifiedInstanceName;

  @Override
  public String getQualifiedInstanceName() {
    return qualifiedInstanceName;
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

  TimeAwarePortForward<Boolean> trigger = new TimeAwarePortForward<>(getQualifiedInstanceName() + ".trigger");

  ScheduledInner a, b, c, d, e, f, g, h, i, j, k;

  public Scheduled(String qualifiedInstanceName, ISchedule scheduler,
                   StringBuilder trace) { // last parameter is from the model, first 2 are "default"
    this.qualifiedInstanceName = qualifiedInstanceName;
    this.scheduler = scheduler;
    this.trace = trace;

    this.a = new ScheduledInner(this.qualifiedInstanceName + ".a", this.scheduler, this.trace, "a");
    this.b = new ScheduledInner(this.qualifiedInstanceName + ".b", this.scheduler, this.trace, "b");
    this.c = new ScheduledInner(this.qualifiedInstanceName + ".c", this.scheduler, this.trace, "c");
    this.d = new ScheduledInner(this.qualifiedInstanceName + ".d", this.scheduler, this.trace, "d");
    this.e = new ScheduledInner(this.qualifiedInstanceName + ".e", this.scheduler, this.trace, "e");
    this.f = new ScheduledInner(this.qualifiedInstanceName + ".f", this.scheduler, this.trace, "f");
    this.g = new ScheduledInner(this.qualifiedInstanceName + ".g", this.scheduler, this.trace, "g");
    this.h = new ScheduledInner(this.qualifiedInstanceName + ".h", this.scheduler, this.trace, "h");
    this.i = new ScheduledInner(this.qualifiedInstanceName + ".i", this.scheduler, this.trace, "i");
    this.j = new ScheduledInner(this.qualifiedInstanceName + ".j", this.scheduler, this.trace, "j");
    this.k = new ScheduledInner(this.qualifiedInstanceName + ".k", this.scheduler, this.trace, "k");

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
