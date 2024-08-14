<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature()}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign syncMsg>${ast.getName()}${suffixes.syncMsg()}<@Util.printTypeParameters ast false/></#assign>
<#assign inPorts = ast.getSymbol().getAllIncomingPorts()>

<#--
  - The explicit declaration of the signature action lambda, unwrapping the synced input objects to individual lambda parameters
  - is used because we do not want to use the synced input object as the lambda argument (in which case we would unwrap
  - it to the individual ports's values in the lambda body), because the lambda arguments name may be in conflict with
  - names of ports / fields / ...
  -->
protected interface ${ast.getName()}${suffixes.msgAction()}<@Util.printTypeParameters ast/>
  extends montiarc.rte.automaton.Action<${syncMsg}> {
  @Override
  default void execute(${syncMsg} msg) {
    realExecute(
      <#list inPorts as inPort> msg.${inPort.getName()}${helper.portVariantSuffix(ast, inPort)} <#sep>,</#list>
    );
  }
  void realExecute(
    <#list inPorts as inPort>
      <@Util.getTypeString inPort.getType()/> ${inPort.getName()}
      <#sep>,</#list>
  );
}