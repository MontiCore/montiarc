<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Configuration template for MontiArc-compatible generation.

  If you want to use a custom configuration template (e.g. by specifying it with
  the '-ct' CLI argument), make sure it stays compatible with MontiArc, e.g., by
  applying the same decorators as this template does.
-->
${tc.signature("glex", "deConf")}
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="deConf" type="de.monticore.cd.codegen.DecoratorConfig" -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->

${deConf.withCopyCreator().defaultApply()}
