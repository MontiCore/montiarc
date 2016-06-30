package instance;

component D extends C {
  ports in Integer portD1,
        in Integer portD2,
        out Integer portD3;

  component B largeB;

  connect portD1 -> largeB.portB1;
  connect portD2 -> largeB.portB2;
  connect largeB.portB3 -> portD3;
}