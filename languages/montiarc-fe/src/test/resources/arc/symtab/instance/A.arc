package instance;

component A {
  port in Integer portA1,
        in Integer portA2,
        out Integer portA3;

  component B b1, b2;

  connect portA1 -> b1.portB1;
  connect portA2 -> b1.portB2;
  connect b1.portB3 -> portA3;
}