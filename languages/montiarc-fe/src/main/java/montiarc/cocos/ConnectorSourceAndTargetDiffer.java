package montiarc.cocos;

import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcASTConnectorCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ConnectorSourceAndTargetDiffer implements MontiArcASTComponentCoCo {

    /**
     * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
     */
    @Override
    public void check(ASTComponent node) {
        ComponentSymbol componentSymbol = (ComponentSymbol) node.getSymbol().get();
        Collection<ConnectorSymbol> connectors = componentSymbol.getConnectors();

        for (ConnectorSymbol cs : connectors) {
            String connectorSource = cs.getSource();
            String connectorTarget = cs.getTarget();

            Optional<PortSymbol> source = componentSymbol.getSpannedScope()
                    .<PortSymbol> resolve(connectorSource, PortSymbol.KIND);
            Optional<PortSymbol> target = componentSymbol.getSpannedScope()
                    .<PortSymbol> resolve(connectorTarget, PortSymbol.KIND);

            if(source.isPresent()) {
                if(target.isPresent())
                    if(source.get().getEnclosingScope().equals(target.get().getEnclosingScope()))
                    {
                        //TODO: Correct Error Code
                        Log.error("0xC1001 Source and target port of connector are ports from the same component.",
                                node.get_SourcePositionStart());
                    }

            }
        }
    }
}
