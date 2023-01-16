/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTCharLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicLongLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsHandler;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

public class MCCommonLiterals2SMT implements MCCommonLiteralsHandler {

  protected final IDeriveSMTExpr deriveSMTExpr;
  protected MCCommonLiteralsTraverser traverser;

  public MCCommonLiterals2SMT(@NotNull IDeriveSMTExpr deriveSMTExpr) {
    Preconditions.checkNotNull(deriveSMTExpr);
    this.deriveSMTExpr = deriveSMTExpr;
  }

  @Override
  public void setTraverser(@NotNull MCCommonLiteralsTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public MCCommonLiteralsTraverser getTraverser() {
    return this.traverser;
  }

  protected Expr2SMTResult getResult() {
    return this.deriveSMTExpr.getResult();
  }

  protected Context getContext() {
    return this.deriveSMTExpr.getContext();
  }

  @Override
  public void handle(@NotNull ASTBooleanLiteral node) {
    Preconditions.checkNotNull(node);
    this.getResult().setValue(this.getContext().mkBool(node.getValue()));
  }

  @Override
  public void handle(@NotNull ASTCharLiteral node) {
    Preconditions.checkNotNull(node);
    this.getResult().setValue(this.getContext().mkInt(node.getValue()));
  }

  @Override
  public void handle(@NotNull ASTStringLiteral node) {
    Preconditions.checkNotNull(node);
    this.getResult().setValue(this.getContext().mkString(node.getValue()));
  }

  @Override
  public void handle(@NotNull ASTNatLiteral node) {
    Preconditions.checkNotNull(node);
    this.getResult().setValue(this.getContext().mkInt(node.getValue()));
  }

  @Override
  public void handle(@NotNull ASTSignedNatLiteral node) {
    Preconditions.checkNotNull(node);
    this.getResult().setValue(this.getContext().mkInt(node.getValue()));
  }

  @Override
  public void handle(@NotNull ASTBasicLongLiteral node) {
    Preconditions.checkNotNull(node);
    this.getResult().setValue(this.getContext().mkInt(node.getValue()));
  }

  @Override
  public void handle(@NotNull ASTSignedBasicLongLiteral node) {
    Preconditions.checkNotNull(node);
    this.getResult().setValue(this.getContext().mkInt(node.getValue()));
  }

  @Override
  public void handle(@NotNull ASTBasicFloatLiteral node) {
    Preconditions.checkNotNull(node);
    this.getResult().setValue(this.getContext().mkFP(node.getValue(), this.getContext().mkFPSortSingle()));
  }

  @Override
  public void handle(@NotNull ASTSignedBasicFloatLiteral node) {
    Preconditions.checkNotNull(node);
    this.getResult().setValue(this.getContext().mkFP(node.getValue(), this.getContext().mkFPSortSingle()));
  }

  @Override
  public void handle(@NotNull ASTBasicDoubleLiteral node) {
    Preconditions.checkNotNull(node);
    this.getResult().setValue(this.getContext().mkFP(node.getValue(), this.getContext().mkFPSortDouble()));
  }

  @Override
  public void handle(@NotNull ASTSignedBasicDoubleLiteral node) {
    Preconditions.checkNotNull(node);
    this.getResult().setValue(this.getContext().mkFP(node.getValue(), this.getContext().mkFPSortDouble()));
  }
}
