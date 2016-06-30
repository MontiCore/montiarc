package instance;

component SuperComp1 extends D {
  ports in Integer port1,
        out Boolean port2;

  component A a1;
  component B b1, b3; // b1 hides the component b1 which is inherited from A (over D and over C)

  connect port1 -> b1.portB1, b1.portB2;
  connect b1.portB3 -> port2;
}