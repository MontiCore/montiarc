/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.stereotypes.IStereotypeReference;
import de.monticore.values.MCValue;

import java.util.Map;
import java.util.Optional;

public final class VoidSymbol implements ISymbol {

  private static VoidSymbol INSTANCE;

  private VoidSymbol() {}

  public static VoidSymbol getInstance() {
    return INSTANCE == null ? new VoidSymbol() : INSTANCE;
  }

  @Override
  public String getName() {
    return "voidSymbol";
  }

  @Override
  public String getPackageName() {
    return null;
  }

  @Override
  public String getFullName() {
    return null;
  }

  @Override
  public IScope getEnclosingScope() {
    return null;
  }

  @Override
  public void setAccessModifier(AccessModifier accessModifier) {}

  @Override
  public boolean isPresentAstNode() {
    return false;
  }

  @Override
  public ASTNode getAstNode() {
    return null;
  }

  Map<de.monticore.symboltable.stereotypes.IStereotypeReference, Optional<MCValue>> stereoinfo
    = new java.util.HashMap<>()
    ;

  @Override
  public Map<IStereotypeReference, Optional<MCValue>> getStereoinfo() {
    return stereoinfo;
  }
}
