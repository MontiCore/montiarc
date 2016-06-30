package componentTest;

public class _RunSimulation {

  public static void main(String[] args) {
    componentTest.gen.ArchOuter unit = new componentTest.gen.ArchOuter();
    unit.setName("componentTest.ArchOuter");
    sim.ArcSimulator sim = new sim.ArcSimulator(unit, new sim.RRScheduler(),
        componentTest.gen.helper.MessageFactory.getInstance());
    sim.error.ISimulationErrorHandler error = new sim.generic.gui.GuiSimulationErrorHandler();
    error.init();
    sim.setErrorHander(error);
    sim.init();
  }
}