/* (c) https://github.com/MontiCore/monticore */
package sim.dummys;

public class AutomatonTest {

  /*@Test
  public void basicComponentTest() {
    //Create Schedule
    IScheduler scheduler = SchedulerFactory.createDefaultScheduler();
    ISimulationErrorHandler errorHandler = new SimpleErrorHandler();
    ComponentDummy cd = new ComponentDummy();
    Tick tick = Tick.get();

    cd.setup(scheduler, errorHandler);

    //first timeslot
    cd.getP1In().accept("Test1");
    cd.getP2In().accept("Test2");

    //end of first timeslot
    cd.getP1In().accept(tick);
    cd.getP2In().accept(tick);

    //second timeslot
    cd.getP1In().accept("Test3");
    cd.getP2In().accept("Test4");

    //end of second
    cd.getP1In().accept(tick);
    cd.getP2In().accept(tick);

    //third
    cd.getP1In().accept("Test5");

    //System.out.println(StreamPrinter.print(((TestPort) cd.getPOut()).getStream()));
    assertTrue(!((TestPort) cd.getPOut()).getStream().isEmpty());

  }

  @Test
  public void AutomatonTest() {
    IScheduler scheduler = SchedulerFactory.createDefaultScheduler();
    ISimulationErrorHandler errorHandler = new SimpleErrorHandler();
    int test1 = 2;
    int test2 = -4;

    Tick tick = Tick.get();

    DummyAutomaton da = new DummyAutomaton();
    da.setup(scheduler, errorHandler, new BackTrackHandler("serialser/"));
    da.setupAutomaton();

    //first timeslot
    da.getIn().accept(test1);


    //end of first timeslot
    da.getIn().accept(tick);

    //second timeslot
    da.getIn().accept(test2);

    //da.getIn().accept(tick);

    //System.out.println(StreamPrinter.print(((TestPort) da.getOut()).getStream()));
    assertTrue(!((TestPort) da.getOut()).getStream().isEmpty());
  }

  @Test
  public void SymbolicAutomatonTest() {
    IScheduler scheduler = SchedulerFactory.createDefaultScheduler();
    ISimulationErrorHandler errorHandler = new SimpleErrorHandler();

    SymbolicAutomaton sa = new SymbolicAutomaton();
    sa.setup(scheduler, errorHandler);
    sa.setupAutomaton();

    sa.getIn().accept(2);
    sa.getIn().accept(2);
    sa.getIn().accept(3);


    assertTrue(!((TestPort) sa.getOut()).getStream().isEmpty());
  }*/
}
