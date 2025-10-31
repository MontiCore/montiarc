/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTVariableDeclaratorCollector implements MCVarDeclarationStatementsVisitor2 {
    private final List<ASTVariableDeclarator> declarators = new ArrayList<>();

    @Override
    public void visit(ASTVariableDeclarator node) {
        declarators.add(node);
    }

    public List<ASTVariableDeclarator> getDeclarators() {
        return declarators;
    }

    public void clearExpressions() {
        declarators.clear();
    }
}
