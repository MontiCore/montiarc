//package de.monticore.lang.montiarc.tag;
//
//import de.monticore.lang.montiarc.helper.IndentPrinter;
//import de.monticore.lang.montiarc.tagging._symboltable.TagKind;
//import de.monticore.lang.montiarc.tagging._symboltable.TagSymbol;
//import de.monticore.lang.montiarc.unit.Power;
//
///**
// * Created by Michael von Wenckstern on 31.05.2016.
// */
//public class PowerConsumptionSymbol extends TagSymbol {
//  public static final PowerConsumptionKind KIND = PowerConsumptionKind.INSTANCE;
//
//  /**
//   * is marker symbol so it has no value by itself
//   */
//  public PowerConsumptionSymbol(double value, Power unit) {
//    // true to set that it is marked
//    super(KIND, value, unit);
//  }
//
//  public double getNumber() {
//    return getValue(0);
//  }
//
//  public Power getUnit() {
//    return getValue(1);
//  }
//
//  @Override
//  public String toString() {
//    return IndentPrinter.groups("PowerConsumption = {0} {1}")
//        .params(getNumber(), getUnit())
//        .toString();
//  }
//
//  public static class PowerConsumptionKind extends TagKind {
//    public static final PowerConsumptionKind INSTANCE = new PowerConsumptionKind();
//
//    protected PowerConsumptionKind() {
//    }
//  }
//}
