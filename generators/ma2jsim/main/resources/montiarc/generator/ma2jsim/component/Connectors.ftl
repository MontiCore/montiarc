<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign index = 0>
<#list helper.getVariantHelper().getVariants(ast) as variant>
<#if !variant.isAtomic()>
protected void <@MethodNames.connectorSetup/>${helper.getVariantHelper().variantSuffix(variant)}() {
<#list variant.getAstNode().getConnectors() as connector>
  <#assign source = connector.getSource()>
  <#assign sourceType = helper.getComponentHelper().getTypeOfPortIfPresent(source).get()>
  <#assign unboxSourceType = SymTypeRelations.unbox(sourceType)>
  <#list connector.getTargetList() as target>
  <#assign targetType = helper.getComponentHelper().getTypeOfPortIfPresent(target).get()>
  <#assign unboxTargetType = SymTypeRelations.unbox(targetType)>
  <#-- If source and target are of different primitive type (ignoring boxing) -->
  <#if unboxSourceType.isPrimitive() && unboxTargetType.isPrimitive()
    && unboxSourceType.getPrimitiveName() != unboxTargetType.getPrimitiveName()>
  <#-- Declare a port to cast from the source type to the target type -->
  <#assign castName = prefixes.cast() + "_" + index>
  montiarc.rte.port.InOutPort<<@Util.getPortTypeString sourceType/>, <@Util.getPortTypeString targetType/>> ${castName}
  <#assign index = index + 1>
  <#-- If the source has type char -->
  <#if SymTypeRelations.isChar(sourceType)>
  <#if SymTypeRelations.isInt(targetType)>
    = new montiarc.rte.port.Char2IntegerPort("", this);
  <#elseif SymTypeRelations.isLong(targetType)>
    = new montiarc.rte.port.Char2LongPort("", this);
  <#elseif SymTypeRelations.isFloat(targetType)>
    = new montiarc.rte.port.Char2FloatPort("", this);
  <#elseif SymTypeRelations.isDouble(targetType)>
    = new montiarc.rte.port.Char2DoublePort("", this);
  </#if>
  <#-- Else the source has a numeric type -->
  <#else>
  <#if SymTypeRelations.isByte(targetType)>
    = new montiarc.rte.port.Number2BytePort<>("", this);
  <#elseif SymTypeRelations.isShort(targetType)>
    = new montiarc.rte.port.Number2ShortPort<>("", this);
  <#elseif SymTypeRelations.isInt(targetType)>
    = new montiarc.rte.port.Number2IntegerPort<>("", this);
  <#elseif SymTypeRelations.isLong(targetType)>
    = new montiarc.rte.port.Number2LongPort<>("", this);
  <#elseif SymTypeRelations.isFloat(targetType)>
    = new montiarc.rte.port.Number2FloatPort<>("", this);
  <#elseif SymTypeRelations.isDouble(targetType)>
    = new montiarc.rte.port.Number2DoublePort<>("", this);
  </#if>
  <#-- Connect source and target with casting port -->
  ${castName}.connect(<@portAccessorOf target/>);
  <@portAccessorOf source/>.connect(${castName});
  </#if>
  <#-- Else connect source and target without casting -->
  <#else>
  <@portAccessorOf source/>.connect(<@portAccessorOf target/>);
  </#if>
  </#list>
</#list>
}
</#if>
</#list>

<#-- connectorEndPoint may be the source of a connector, or one of its targets -->
<#macro portAccessorOf connectorEndPoint>
  <#assign IsSubcompTheOwner = connectorEndPoint.isPresentComponent()>

  <#assign variantSuffix = IsSubcompTheOwner?then(
    helper.getVariantHelper().portVariantSuffix(connectorEndPoint.getComponentSymbol(), connectorEndPoint.getPortSymbol()),
    helper.getVariantHelper().portVariantSuffix(ast, connectorEndPoint.getPortSymbol())
  )>

  <#if IsSubcompTheOwner>
    <#assign owningComp = connectorEndPoint.getComponentSymbol()>
    <#assign compAccessor = prefixes.subcomp() + connectorEndPoint.getComponent() + helper.getVariantHelper().subcomponentVariantSuffix(ast, owningComp) + "()">
    ${compAccessor}.${prefixes.port()}${connectorEndPoint.getPort()}${variantSuffix}()

  <#else>
    <#-- We directly access the field because we need to know that the port is an InOutPort -->
    ${prefixes.port()}${connectorEndPoint.getPort()}${variantSuffix}

  </#if>
</#macro>
