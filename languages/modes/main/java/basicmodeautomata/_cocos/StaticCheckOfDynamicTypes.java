/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTComponentTypeBuilder;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._visitor.ArcBasisVisitor2;
import basicmodeautomata.BasicModeAutomataMill;
import basicmodeautomata._symboltable.DynmaicVersionOfAComponentSymbol;
import basicmodeautomata._symboltable.ModeSymbol;
import basicmodeautomata.util.ComponentModeTool;
import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Ensures that the component types that can exist in different modes, are checked with the some cocos.
 */
public class StaticCheckOfDynamicTypes implements ArcBasisASTComponentTypeCoCo {

  protected final Consumer<ASTComponentType> checkCoCos;
  protected final ComponentModeTool helper = BasicModeAutomataMill.getModeTool();

  /**
   * Applies the given cocos to the found dynamic component versions.
   * @param cocos cocos to apply; Only {@link ArcBasisASTComponentTypeCoCo} are kept,
   *              other given visitors are ignored.
   *              The coco checker is allowed to contain this coco.
   *              Example: <code>checker.getTraverser().getArcBasisVisitorList()</code>
   *              This constructor copies the list internally, which means, items added to
   *              the list after calling this constructor are ignored
   * @param more additional cocos to add to the list
   */
  public StaticCheckOfDynamicTypes(Collection<ArcBasisVisitor2> cocos, ArcBasisASTComponentTypeCoCo... more) {
    checkCoCos = Stream.concat(
        cocos.stream()
          .filter(v -> v instanceof ArcBasisASTComponentTypeCoCo)
          .map(v -> (ArcBasisASTComponentTypeCoCo) v),
          Arrays.stream(more)
        )
        .<Consumer<ASTComponentType>>map(v -> v::check)
        .reduce(Consumer::andThen)
        .orElseThrow(() -> new IllegalArgumentException("There are no CoCos given."));
  }

  /**
   * Applies the given cocos to the found dynamic component versions.
   * @param cocos provider for cocos to apply; Only {@link ArcBasisASTComponentTypeCoCo} are kept,
   *              other given visitors are ignored.
   *              The coco checker is allowed to contain this coco.
   *              Example: <code>checker.getTraverser()::getArcBasisVisitorList</code>
   *              This is a call-by-reference, which means that cocos added to this list
   *              after calling this constructor are still honored
   * @param more additional cocos to check
   */
  public StaticCheckOfDynamicTypes(Supplier<List<ArcBasisVisitor2>> cocos, ArcBasisASTComponentTypeCoCo... more) {
    checkCoCos = (type) -> Stream.concat(
        cocos.get().stream()
            .filter(v -> v instanceof ArcBasisASTComponentTypeCoCo)
            .map(v -> (ArcBasisASTComponentTypeCoCo) v),
        Arrays.stream(more)
    ).forEach(coco -> coco.check(type));
  }

  @Override
  public void check(ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    // avoid recursion
    if(isDynamicVersion(node)){
      return;
    }
    helper.streamModes(node).map(mode -> copyComponentForMode(node, mode)).forEach(checkCoCos);
  }

  /**
   * Creates a modified copy af a component.
   * The created copy also has a copy of the symbol of the original node.
   * It also has the same enclosing scope, but is not registered to that scope, the scope only knows the static version.
   * The body contains the elements in the modes body additionally to the given body.
   * The name is modified so the mode's name is also reflected in coco-error messages
   * Otherwise, it shares its attributes with the given ast-node.
   * @param staticAST the original ast node of the component
   * @param mode a mode which describes a version of the original component
   * @return a copy of the given component, that also contains elements of the given mode
   */
  public ASTComponentType copyComponentForMode(ASTComponentType staticAST, ModeSymbol mode){
    Preconditions.checkNotNull(mode);
    Preconditions.checkNotNull(staticAST);
    // copy ast-node
    ASTComponentType dynamicAST = new ASTComponentTypeBuilder()
      .setHead(staticAST.getHead())
      .setName(buildName(staticAST, mode))
      .setBody(helper.mergeBodies(Arrays.asList(staticAST.getBody(), mode.getBody())))
      .setComponentInstancesList(staticAST.getComponentInstanceList())
      .set_SourcePositionStart(staticAST.get_SourcePositionStart())
      .set_SourcePositionEnd(staticAST.get_SourcePositionEnd())
      .build();
    // only add scope to node, but not node to scope, since the node is a throwaway item
    dynamicAST.setEnclosingScope(staticAST.getEnclosingScope());
    dynamicAST.setSpannedScope(staticAST.getSpannedScope());

    dynamicAST.setSymbol(new DynmaicVersionOfAComponentSymbol(dynamicAST, staticAST.getSymbol(), mode));
    return dynamicAST;
  }

  /**
   * builds a component-name so that it contains the mode-name as well and makes sense in error-messages of cocos.
   * The Method {@link #isDynamicVersion(ASTComponentType)} can detect whether the name was built with this method.
   * @return advanced, distorted name for a component
   * @param component ast-node that contains the original name
   * @param mode mode who's name should be incorporated into the new name as well
   */
  protected String buildName(ASTComponentType component, ModeSymbol mode){
    return String.format("%s' in mode '%s", component.getName(), mode.getName());
  }

  /**
   * @return true, if the given component has a name that was created by {@link #buildName(ASTComponentType, ModeSymbol)}
   */
  protected boolean isDynamicVersion(ASTComponentType component){
    return component.getName().contains(" ");
  }
}