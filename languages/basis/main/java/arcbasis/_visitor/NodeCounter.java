/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsInheritanceHandler;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsVisitor2;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.compsymbols._visitor.CompSymbolsInheritanceHandler;
import de.monticore.symbols.compsymbols._visitor.CompSymbolsVisitor2;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols._visitor.OOSymbolsInheritanceHandler;
import de.monticore.symbols.oosymbols._visitor.OOSymbolsVisitor2;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.visitor.IVisitor;
import org.codehaus.commons.nullanalysis.NotNull;

public class NodeCounter {

  protected Wrapper numSymbols;
  protected Wrapper numScopeSpanningSymbols;

  protected Wrapper numTypeSymbols;
  protected Wrapper numVarSymbols;
  protected Wrapper numTypeVarSymbols;
  protected Wrapper numFunctionSymbols;
  protected Wrapper numDiagramSymbols;

  protected Wrapper numOOTypeSymbols;
  protected Wrapper numFieldSymbols;
  protected Wrapper numMethodSymbols;

  protected Wrapper numPortSymbols;
  protected Wrapper numComponentSymbols;
  protected Wrapper numComponentTypeSymbols;

  public Integer getNumSymbols() {
    return numSymbols.getVal();
  }

  public Integer getNumScopeSpanningSymbols() {
    return numScopeSpanningSymbols.getVal();
  }

  public Integer getNumTypeSymbols() {
    return numTypeSymbols.getVal();
  }

  public Integer getNumVarSymbols() {
    return numVarSymbols.getVal();
  }

  public Integer getNumTypeVarSymbols() {
    return numTypeVarSymbols.getVal();
  }

  public Integer getNumFunctionSymbols() {
    return numFunctionSymbols.getVal();
  }

  public Integer getNumDiagramSymbols() {
    return numDiagramSymbols.getVal();
  }

  public Integer getNumOOTypeSymbols() {
    return numOOTypeSymbols.getVal();
  }

  public Integer getNumFieldSymbols() {
    return numFieldSymbols.getVal();
  }

  public Integer getNumMethodSymbols() {
    return numMethodSymbols.getVal();
  }

  public Integer getNumPortSymbols() {
    return numPortSymbols.getVal();
  }

  public Integer getNumComponentSymbols() {
    return numComponentSymbols.getVal();
  }

  public Integer getNumComponentTypeSymbols() {
    return numComponentTypeSymbols.getVal();
  }

  protected ArcBasisTraverser traverser;

  protected ArcBasisTraverser getTraverser() {
    return this.traverser;
  }

  public void countNodes(@NotNull ASTNode node) {
    Preconditions.checkNotNull(node);
    this.reset();
    node.accept(this.getTraverser());
  }

  public void reset() {
    this.numSymbols.setVal(0);
    this.numScopeSpanningSymbols.setVal(0);
    this.numTypeSymbols.setVal(0);
    this.numVarSymbols.setVal(0);
    this.numTypeVarSymbols.setVal(0);
    this.numFunctionSymbols.setVal(0);
    this.numDiagramSymbols.setVal(0);
    this.numOOTypeSymbols.setVal(0);
    this.numFieldSymbols.setVal(0);
    this.numMethodSymbols.setVal(0);
    this.numPortSymbols.setVal(0);
    this.numComponentSymbols.setVal(0);
    this.numComponentTypeSymbols.setVal(0);
  }

  public NodeCounter() {
    this(new Wrapper(0), new Wrapper(0), new Wrapper(0), new Wrapper(0), new Wrapper(0),
      new Wrapper(0), new Wrapper(0), new Wrapper(0), new Wrapper(0), new Wrapper(0),
      new Wrapper(0), new Wrapper(0), new Wrapper(0));
  }

  protected NodeCounter(@NotNull Wrapper numSymbols, @NotNull Wrapper numScopeSpanningSymbols,
    @NotNull Wrapper numTypeSymbols, @NotNull Wrapper numVarSymbols,
    @NotNull Wrapper numTypeVarSymbols, @NotNull Wrapper numFunctionSymbols,
    @NotNull Wrapper numDiagramSymbols, @NotNull Wrapper numOOTypeSymbols,
    @NotNull Wrapper numFieldSymbols, @NotNull Wrapper numMethodSymbols,
    @NotNull Wrapper numPortSymbols, @NotNull Wrapper numComponentSymbols,
    @NotNull Wrapper numComponentTypeSymbols) {
    this(
      new Visitor(Preconditions.checkNotNull(numSymbols), Preconditions.checkNotNull(numScopeSpanningSymbols)),
      new BasicSymbolsVisitor(Preconditions.checkNotNull(numTypeSymbols), Preconditions.checkNotNull(numVarSymbols),
        Preconditions.checkNotNull(numTypeVarSymbols), Preconditions.checkNotNull(numFunctionSymbols),
        Preconditions.checkNotNull(numDiagramSymbols)),
      new OOSymbolsVisitor(Preconditions.checkNotNull(numOOTypeSymbols), Preconditions.checkNotNull(numFieldSymbols),
        Preconditions.checkNotNull(numMethodSymbols)),
      new CompSymbolsVisitor(Preconditions.checkNotNull(numPortSymbols),
        Preconditions.checkNotNull(numComponentSymbols), Preconditions.checkNotNull(numComponentTypeSymbols))
    );
    this.numSymbols = numSymbols;
    this.numScopeSpanningSymbols = numScopeSpanningSymbols;
    this.numTypeSymbols = numTypeSymbols;
    this.numVarSymbols = numVarSymbols;
    this.numTypeVarSymbols = numTypeVarSymbols;
    this.numFunctionSymbols = numFunctionSymbols;
    this.numDiagramSymbols = numDiagramSymbols;
    this.numOOTypeSymbols = numOOTypeSymbols;
    this.numFieldSymbols = numFieldSymbols;
    this.numMethodSymbols = numMethodSymbols;
    this.numPortSymbols = numPortSymbols;
    this.numComponentSymbols = numComponentSymbols;
    this.numComponentTypeSymbols = numComponentTypeSymbols;
  }

  protected NodeCounter(@NotNull IVisitor visitor, @NotNull BasicSymbolsVisitor basicSymbolsVisitor,
    @NotNull OOSymbolsVisitor ooSymbolsVisitor, @NotNull CompSymbolsVisitor compSymbolsVisitor) {
    this.traverser = ArcBasisMill.traverser();

    this.traverser.add4IVisitor(Preconditions.checkNotNull(visitor));

    this.traverser.setBasicSymbolsHandler(new BasicSymbolsInheritanceHandler());
    this.traverser.add4BasicSymbols(Preconditions.checkNotNull(basicSymbolsVisitor));

    this.traverser.setOOSymbolsHandler(new OOSymbolsInheritanceHandler());
    this.traverser.add4OOSymbols(Preconditions.checkNotNull(ooSymbolsVisitor));

    this.traverser.setArcBasisHandler(new ArcBasisInheritanceHandler());
    this.traverser.setCompSymbolsHandler(new CompSymbolsInheritanceHandler());
    this.traverser.add4CompSymbols(Preconditions.checkNotNull(compSymbolsVisitor));
  }

  public static class BasicSymbolsVisitor implements BasicSymbolsVisitor2 {

    public BasicSymbolsVisitor(@NotNull Wrapper numTypeSymbols, @NotNull Wrapper numVarSymbols,
      @NotNull Wrapper numTypeVarSymbols, @NotNull Wrapper numFunctionSymbols,
      @NotNull Wrapper numDiagramSymbols) {
      this.numTypeSymbols = Preconditions.checkNotNull(numTypeSymbols);
      this.numVarSymbols = Preconditions.checkNotNull(numVarSymbols);
      this.numTypeVarSymbols = Preconditions.checkNotNull(numTypeVarSymbols);
      this.numFunctionSymbols = Preconditions.checkNotNull(numFunctionSymbols);
      this.numDiagramSymbols = Preconditions.checkNotNull(numDiagramSymbols);
    }

    protected Wrapper numTypeSymbols;
    protected Wrapper numVarSymbols;
    protected Wrapper numTypeVarSymbols;
    protected Wrapper numFunctionSymbols;
    protected Wrapper numDiagramSymbols;

    @Override
    public void visit(@NotNull TypeSymbol node) {
      Preconditions.checkNotNull(node);
      numTypeSymbols.inc();
    }

    @Override
    public void visit(@NotNull VariableSymbol node) {
      Preconditions.checkNotNull(node);
      numVarSymbols.inc();
    }

    @Override
    public void visit(@NotNull TypeVarSymbol node) {
      Preconditions.checkNotNull(node);
      numTypeVarSymbols.inc();
    }

    @Override
    public void visit(@NotNull FunctionSymbol node) {
      Preconditions.checkNotNull(node);
      numFunctionSymbols.inc();
    }

    @Override
    public void visit(@NotNull DiagramSymbol node) {
      Preconditions.checkNotNull(node);
      numDiagramSymbols.inc();
    }
  }

  public static class OOSymbolsVisitor implements OOSymbolsVisitor2 {

    protected Wrapper numOOTypeSymbols;
    protected Wrapper numFieldSymbols;
    protected Wrapper numMethodSymbols;

    public OOSymbolsVisitor(@NotNull Wrapper numOOTypeSymbols, @NotNull Wrapper numFieldSymbols,
      @NotNull Wrapper numMethodSymbols) {
      this.numOOTypeSymbols = Preconditions.checkNotNull(numOOTypeSymbols);
      this.numFieldSymbols = Preconditions.checkNotNull(numFieldSymbols);
      this.numMethodSymbols = Preconditions.checkNotNull(numMethodSymbols);
    }

    @Override
    public void visit(@NotNull OOTypeSymbol node) {
      Preconditions.checkNotNull(node);
      numOOTypeSymbols.inc();
    }

    @Override
    public void visit(@NotNull FieldSymbol node) {
      Preconditions.checkNotNull(node);
      numFieldSymbols.inc();
    }

    @Override
    public void visit(@NotNull MethodSymbol node) {
      Preconditions.checkNotNull(node);
      numMethodSymbols.inc();
    }
  }

  public static class Visitor implements IVisitor {

    protected Wrapper numSymbols;
    protected Wrapper numScopeSpanningSymbols;

    public Visitor(@NotNull Wrapper numSymbols, @NotNull Wrapper numScopeSpanningSymbols) {
      this.numSymbols = Preconditions.checkNotNull(numSymbols);
      this.numScopeSpanningSymbols = Preconditions.checkNotNull(numScopeSpanningSymbols);
    }

    @Override
    public void visit(@NotNull ISymbol symbol) {
      Preconditions.checkNotNull(symbol);
      numSymbols.inc();
    }

    @Override
    public void visit(@NotNull IScopeSpanningSymbol symbol) {
      Preconditions.checkNotNull(symbol);
      numScopeSpanningSymbols.inc();
    }
  }

  public static class CompSymbolsVisitor implements CompSymbolsVisitor2 {

    protected Wrapper numPortSymbols;
    protected Wrapper numComponentSymbols;
    protected Wrapper numComponentTypeSymbols;

    public CompSymbolsVisitor(@NotNull Wrapper numPortSymbols,
                           @NotNull Wrapper numComponentSymbols, @NotNull Wrapper numComponentTypeSymbols) {
      this.numPortSymbols = Preconditions.checkNotNull(numPortSymbols);
      this.numComponentSymbols = Preconditions.checkNotNull(numComponentSymbols);
      this.numComponentTypeSymbols = Preconditions.checkNotNull(numComponentTypeSymbols);
    }

    @Override
    public void visit(@NotNull PortSymbol node) {
      Preconditions.checkNotNull(node);
      numPortSymbols.inc();
    }

    @Override
    public void visit(@NotNull SubcomponentSymbol node) {
      Preconditions.checkNotNull(node);
      numComponentSymbols.inc();
    }

    @Override
    public void visit(@NotNull ComponentSymbol node) {
      Preconditions.checkNotNull(node);
      numComponentTypeSymbols.inc();
    }
  }

  protected static class Wrapper {

    protected Integer val;

    public Integer getVal() {
      return val;
    }

    public void setVal(@NotNull Integer val) {
      this.val = Preconditions.checkNotNull(val);
    }

    public void inc() {
      ++this.val;
    }

    public Wrapper(@NotNull Integer val) {
      this.val = Preconditions.checkNotNull(val);
    }
  }
}