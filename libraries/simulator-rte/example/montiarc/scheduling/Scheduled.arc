/* (c) https://github.com/MontiCore/monticore */
package scheduling;

/**
 * Test model for scheduling. TODO: Behavior may not be specified correctly (using compute for notation simplicity currently)
 */
component Scheduled(StringBuilder trace) {

  port in Boolean trigger;

  component ScheduledInner(StringBuilder trace, String instanceName) {
    port in Boolean trigger;
    port out Boolean output;

    compute {
      trace.append(instanceName + "1");
      output = trigger;
      trace.append(instanceName + "2");
    }
  }

  ScheduledInner a(trace, "a"), b(trace, "b"), c(trace, "c"),
    d(trace, "d"), e(trace, "e"), f(trace, "f"), g(trace, "g"),
    h(trace, "h"), i(trace, "i"), j(trace, "j"), k(trace, "k");

  trigger -> a.trigger;
  trigger -> b.trigger;
  trigger -> c.trigger;

  a.output -> d.trigger;
  b.output -> e.trigger;
  b.output -> f.trigger;
  c.output -> g.trigger;

  d.output -> h.trigger;
  e.output -> i.trigger;
  f.output -> j.trigger;
  g.output -> k.trigger;

}
