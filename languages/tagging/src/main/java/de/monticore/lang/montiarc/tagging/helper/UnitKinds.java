package de.monticore.lang.montiarc.tagging.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * Created by MichaelvonWenckstern on 28.06.2016.
 */
public class UnitKinds {
  protected static UnitKinds instance = null;

  protected UnitKinds() {
    createHashMap();
  }

  protected static UnitKinds getInstance() {
    if (instance == null) {
      instance = new UnitKinds();
    }
    return instance;
  }

  public static boolean contains(String unitKind) {
    return getInstance().quantities.contains(unitKind);
  }

  @Override
  public String toString() {
    return quantities.stream().collect(Collectors.joining(", "));
  }

  public static String available() {
    return getInstance().toString();
  }

  protected HashSet<String> quantities;

  protected void createHashMap() {
    // copied from http://jscience.org/api/javax/measure/quantity/Quantity.html
    quantities = new LinkedHashSet<>(Arrays.asList(
        "Acceleration",
        "AmountOfSubstance",
        "Angle",
        "AngularAcceleration",
        "AngularVelocity",
        "Area",
        "CatalyticActivity",
        "DataAmount",
        "DataRate",
        "Dimensionless",
        "Duration",
        "DynamicViscosity",
        "ElectricCapacitance",
        "ElectricCharge",
        "ElectricConductance",
        "ElectricCurrent",
        "ElectricInductance",
        "ElectricPotential",
        "ElectricResistance",
        "Energy",
        "Force",
        "Frequency",
        "Illuminance",
        "KinematicViscosity",
        "Length",
        "LuminousFlux",
        "LuminousIntensity",
        "MagneticFlux",
        "MagneticFluxDensity",
        "Mass",
        "MassFlowRate",
        "Money",
        "Power",
        "Pressure",
        "RadiationDoseAbsorbed",
        "RadiationDoseEffective",
        "RadioactiveActivity",
        "SolidAngle",
        "Temperature",
        "Torque",
        "Velocity",
        "Volume",
        "VolumetricDensity",
        "VolumetricFlowRate"
    ));
  }
}
