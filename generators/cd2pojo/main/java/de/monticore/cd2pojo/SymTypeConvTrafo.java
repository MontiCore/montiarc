/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.base.Preconditions;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._visitor.CDBasisHandler;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class SymTypeConvTrafo implements CDBasisVisitor2, CDBasisHandler {

  protected CD4CodeTraverser traverser;


  @Override
  public CD4CodeTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(CDBasisTraverser traverser) {}

  protected Collection<ICD4CodeArtifactScope> scopes;

  protected Collection<ICD4CodeArtifactScope> getScopes() {
    return scopes;
  }

  public SymTypeConvTrafo() {
    this.traverser = CD4CodeMill.traverser();
    this.scopes = new ArrayList<>();
    this.init();
  }

  /**
   * Transforms the class diagram symbol table to a symbol table that upholds
   * the monticore symbol table conventions to enable aggregation with languages
   * subject to these conventions.
   */
  public Collection<ICD4CodeArtifactScope> apply(@NotNull ICD4CodeArtifactScope as) {
    Preconditions.checkNotNull(as);
    this.getScopes().clear();
    as.accept(this.getTraverser());
    return this.getScopes();
  }

  @Override
  public void visit(@NotNull CDTypeSymbol node) {
    Preconditions.checkNotNull(node);
    String pkn = node.getEnclosingScope().getRealPackageName();
    ICD4CodeArtifactScope as = CD4CodeMill.artifactScope();
    CD4CodeMill.globalScope().addSubScope(as);
    as.setEnclosingScope(CD4CodeMill.globalScope());
    node.getSpannedScope().setEnclosingScope(null);
    node.getSpannedScope().setEnclosingScope(as);
    node.setEnclosingScope(as);
    as.add(node);
    as.setName(pkn + "." + node.getName());
    node.setPackageName(pkn);
    this.getScopes().add(as);
  }

  protected void init() {
    this.getTraverser().add4CDBasis(this);
    this.getTraverser().setCDBasisHandler(this);
  }
}
