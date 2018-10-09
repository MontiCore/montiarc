package components.body.connectors;

component ConnectorSourceAndTargetFromSameInnerComponent {

  component B {
    port in Integer x;
    port out Integer y;
  }

  connect b.y -> b.x;

}