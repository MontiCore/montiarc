package simTypes.gen.interfaces;

public interface ISimple extends sim.generic.IComponent,
    simTypes.gen.ports.SimplePortInterface,
    sim.generic.IIncomingPort<java.lang.String>,
    sim.generic.IOutgoingPort<java.lang.Integer> {
}