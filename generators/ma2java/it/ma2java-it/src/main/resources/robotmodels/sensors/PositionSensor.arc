package robotmodels.sensors;

component PositionSensor {
    port
        out Float x,
        out Float y,
        out Float theta,
        out Float linearVelocity,
        out Float angularVelocity;
}