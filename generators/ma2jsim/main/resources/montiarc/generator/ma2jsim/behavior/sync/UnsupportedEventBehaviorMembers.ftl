<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#-- @param calledFromVariabilityContext Whether this template was called with the ast / symbol
  --                                     infrastructure being set to consider a specific variant.
  --                                     (e.g., ast.getSymbol() is a VariantComponentTypeSymbol
  -->
${tc.signature("calledFromVariabilityContext")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign compSym = calledFromVariabilityContext?then(ast.getSymbol().getAdaptee(), ast.getSymbol())>

<#list compSym.getAllIncomingPorts() as portSym>
  <#assign methodName = prefixes.message() + portSym.getName() + helper.portVariantSuffix(ast, portSym)>
    @Override
    public void ${methodName}(<@Util.getTypeString portSym.getType()/> msg) {
      throw new UnsupportedOperationException("Message event method can not be invoked for sync behavior");
    }
</#list>
