//package de.monticore.lang.montiarc.tag;
//
//import de.monticore.lang.montiarc.tagging._symboltable.TagKind;
//import de.monticore.lang.montiarc.tagging._symboltable.TagSymbol;
//
///**
// * Created by Michael von Wenckstern on 31.05.2016.
// */
//public class IsTraceableSymbol extends TagSymbol {
//  public static final TraceabilityKind KIND = TraceabilityKind.INSTANCE;
//
//  /**
//   * is marker symbol so it has no value by itself
//   */
//  public IsTraceableSymbol() {
//    // true to set that it is marked
//    super(KIND);
//  }
//
//  @Override
//  public String toString() {
//    return "IsTraceable";
//  }
//
//  public static class TraceabilityKind extends TagKind {
//    public static final TraceabilityKind INSTANCE = new TraceabilityKind();
//
//    protected TraceabilityKind() {
//    }
//  }
//}
