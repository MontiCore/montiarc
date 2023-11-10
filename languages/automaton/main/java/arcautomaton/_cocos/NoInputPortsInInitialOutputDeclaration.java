/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._visitor.ExpressionRootFinder;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._cocos.util.IPortReferenceInExpressionExtractor;
import arcbasis._cocos.util.PortReferenceExtractor4ExpressionBasis;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.scbasis._ast.ASTSCSAnte;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcAutomataError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static arcbasis._cocos.util.IPortReferenceInExpressionExtractor.PortReference;

/**
 * Checks that initial output declarations do not reference input ports. We can not access input ports in the initial
 * output declaration, because when setting up a component, the values of input ports are not defined yet.
 */
public class NoInputPortsInInitialOutputDeclaration implements ArcBasisASTComponentTypeCoCo {

  protected final IPortReferenceInExpressionExtractor portRefExtractor;

  protected final ExpressionRootFinder exprRootFinder;
  protected final ExpressionsBasisTraverser exprRootTraverser;

  public NoInputPortsInInitialOutputDeclaration() {
    this(new PortReferenceExtractor4ExpressionBasis());
  }

  public NoInputPortsInInitialOutputDeclaration(@NotNull IPortReferenceInExpressionExtractor portRefExtractor) {
    this.portRefExtractor = Preconditions.checkNotNull(portRefExtractor);

    this.exprRootFinder = new ExpressionRootFinder();
    this.exprRootTraverser = ArcAutomatonMill.inheritanceTraverser();
    this.exprRootTraverser.add4ExpressionsBasis(exprRootFinder);
  }

  @Override
  public void check(@NotNull ASTComponentType astComp) {
    Preconditions.checkNotNull(astComp);
    Preconditions.checkArgument(astComp.isPresentSymbol());

    ComponentTypeSymbol comp = astComp.getSymbol();
    HashSet<PortReference> portReferencesToLookFor = new HashSet<>(PortReference.ofComponentTypePorts(comp));

    for (ASTArcStatechart automaton : extractAutomatons(astComp)) {
      automaton.streamInitialOutput().forEach(initPair -> {
        ASTSCSAnte initBlock = initPair.getRight();
        exprRootFinder.reset();
        initBlock.accept(exprRootTraverser);
        Set<ASTExpression> rootExprs = exprRootFinder.getExpressionRoots();

        rootExprs.forEach(expr -> checkNoInputPortContained(expr, portReferencesToLookFor, comp));
      });
    }
  }

  protected static Collection<ASTArcStatechart> extractAutomatons(@NotNull ASTComponentType astComp) {
    Preconditions.checkNotNull(astComp);
    return astComp.getBody()
      .streamArcElements()
      .filter(ASTArcStatechart.class::isInstance)
      .map(ASTArcStatechart.class::cast)
      .collect(Collectors.toSet());
  }

  protected void checkNoInputPortContained(@NotNull ASTExpression expr,
                                           @NotNull HashSet<PortReference> portReferencesToLookFor,
                                           @NotNull ComponentTypeSymbol portOwner) {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(portReferencesToLookFor);
    Preconditions.checkNotNull(portOwner);

    HashMap<PortReference, SourcePosition> foundPortReferences = this.portRefExtractor.findPortReferences(
      expr, portReferencesToLookFor, ArcAutomatonMill.traverser()
    );

    for (PortReference portRef : foundPortReferences.keySet()) {
      SourcePosition portRefPosition = foundPortReferences.get(portRef);

      if(portOwner.getArcPort(portRef.toString(), true).isPresent()
        && portOwner.getArcPort(portRef.toString(), true).get().isIncoming()) {
        Log.error(ArcAutomataError.INPUT_PORT_IN_INITIAL_OUT_DECL.format(portRef.toString()), portRefPosition);
      }
    }
  }
}
