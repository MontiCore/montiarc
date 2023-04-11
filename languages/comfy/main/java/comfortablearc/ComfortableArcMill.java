/* (c) https://github.com/MontiCore/monticore */
package comfortablearc;

import comfortablearc._symboltable.ComfortableArcScopesGenitorP2;
import comfortablearc._symboltable.ComfortableArcScopesGenitorP2Delegator;

public class ComfortableArcMill extends ComfortableArcMillTOP {

  protected static ComfortableArcMill millComfortableArcScopesGenitorP2;

  protected static ComfortableArcMill millComfortableArcScopesGenitorP2Delegator;

  public static ComfortableArcScopesGenitorP2 scopesGenitorP2() {
    if (millComfortableArcScopesGenitorP2 == null) {
      millComfortableArcScopesGenitorP2 = getMill();
    }
    return millComfortableArcScopesGenitorP2._scopesGenitorP2();
  }

  protected ComfortableArcScopesGenitorP2 _scopesGenitorP2() {
    return new ComfortableArcScopesGenitorP2();
  }

  public static ComfortableArcScopesGenitorP2Delegator scopesGenitorP2Delegator() {
    if (millComfortableArcScopesGenitorP2Delegator == null) {
      millComfortableArcScopesGenitorP2Delegator = getMill();
    }
    return millComfortableArcScopesGenitorP2Delegator._scopesGenitorP2Delegator();
  }

  protected ComfortableArcScopesGenitorP2Delegator _scopesGenitorP2Delegator() {
    return new ComfortableArcScopesGenitorP2Delegator();
  }

  public static void initMe(ComfortableArcMill a) {
    ComfortableArcMillTOP.initMe(a);
    millComfortableArcScopesGenitorP2 = a;
    millComfortableArcScopesGenitorP2Delegator = a;
  }

  public static void reset() {
    ComfortableArcMillTOP.reset();
    millComfortableArcScopesGenitorP2 = null;
    millComfortableArcScopesGenitorP2Delegator = null;
  }
}