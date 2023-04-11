/* (c) https://github.com/MontiCore/monticore */
package arcbasis;

import arcbasis._symboltable.ArcBasisScopesGenitorP2;
import arcbasis._symboltable.ArcBasisScopesGenitorP2Delegator;
import arcbasis._symboltable.ArcBasisScopesGenitorP3;
import arcbasis._symboltable.ArcBasisScopesGenitorP3Delegator;
import arcbasis.check.deser.ArcBasisCompTypeExprDeSer;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;

public class ArcBasisMill extends ArcBasisMillTOP {

  protected static ArcBasisMill millArcBasisScopesGenitorP2;

  protected static ArcBasisMill millArcBasisScopesGenitorP2Delegator;

  protected static ArcBasisMill millArcBasisScopesGenitorP3;

  protected static ArcBasisMill millArcBasisScopesGenitorP3Delegator;

  protected static ArcBasisMill millFullPrettyPrinter;

  protected static ArcBasisMill millCompTypeExprDeSer;

  public static ArcBasisScopesGenitorP2 scopesGenitorP2() {
    if (millArcBasisScopesGenitorP2 == null) {
      millArcBasisScopesGenitorP2 = getMill();
    }
    return millArcBasisScopesGenitorP2._scopesGenitorP2();
  }

  protected ArcBasisScopesGenitorP2 _scopesGenitorP2() {
    return new ArcBasisScopesGenitorP2();
  }

  public static ArcBasisScopesGenitorP2Delegator scopesGenitorP2Delegator() {
    if (millArcBasisScopesGenitorP2Delegator == null) {
      millArcBasisScopesGenitorP2Delegator = getMill();
    }
    return millArcBasisScopesGenitorP2Delegator._scopesGenitorP2Delegator();
  }

  protected ArcBasisScopesGenitorP2Delegator _scopesGenitorP2Delegator() {
    return new ArcBasisScopesGenitorP2Delegator();
  }

  public static ArcBasisScopesGenitorP3 scopesGenitorP3() {
    if (millArcBasisScopesGenitorP3 == null) {
      millArcBasisScopesGenitorP3 = getMill();
    }
    return millArcBasisScopesGenitorP3._scopesGenitorP3();
  }

  protected ArcBasisScopesGenitorP3 _scopesGenitorP3() {
    return new ArcBasisScopesGenitorP3();
  }

  public static ArcBasisScopesGenitorP3Delegator scopesGenitorP3Delegator() {
    if (millArcBasisScopesGenitorP3Delegator == null) {
      millArcBasisScopesGenitorP3Delegator = getMill();
    }
    return millArcBasisScopesGenitorP3Delegator._scopesGenitorP3Delegator();
  }

  protected ArcBasisScopesGenitorP3Delegator _scopesGenitorP3Delegator() {
    return new ArcBasisScopesGenitorP3Delegator();
  }

  public static ComposedCompTypeExprDeSer compTypeExprDeSer() {
    if (millCompTypeExprDeSer == null) {
      millCompTypeExprDeSer = getMill();
    }
    return millCompTypeExprDeSer._compTypeExprDeSer();
  }

  protected ComposedCompTypeExprDeSer _compTypeExprDeSer() {
    return new ArcBasisCompTypeExprDeSer();
  }

  public static void initMe(ArcBasisMill a) {
    ArcBasisMillTOP.initMe(a);
    millArcBasisScopesGenitorP2 = a;
    millArcBasisScopesGenitorP2Delegator = a;
    millArcBasisScopesGenitorP3 = a;
    millFullPrettyPrinter = a;
    millCompTypeExprDeSer = a;
  }

  public static void reset() {
    ArcBasisMillTOP.reset();
    millArcBasisScopesGenitorP2 = null;
    millArcBasisScopesGenitorP2Delegator = null;
    millArcBasisScopesGenitorP3 = null;
    millFullPrettyPrinter = null;
    millCompTypeExprDeSer = null;
  }
}