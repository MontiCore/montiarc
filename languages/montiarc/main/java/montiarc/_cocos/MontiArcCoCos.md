<!-- (c) https://github.com/MontiCore/monticore -->

# MontiArc Context Conditions (CoCos)

Legend:

- ☑ Intentionally included
- ☒ Intentionally not included
- ☐ Inclusion pending or to be discussed

## ArcBasis CoCos

| in MA | VariantCoCo | CoCo(s)                             | Language | Code(s)                                                       | 
|-------|-------------|-------------------------------------|----------|---------------------------------------------------------------|
| ☑     | ☒           | CircularInheritance                 | ArcBasis | 0xC1100                                                       |
| ☑     | ☑           | PortsConnected                      | ArcBasis | 0xC1103, 0xC1104                                              |
| ☑     | ☑           | PortUniqueSender                    | ArcBasis | 0xC1105                                                       |
| ☑     | ☑           | SubPortsConnected                   | ArcBasis | 0xC1106, 0xC1107                                              |
| ☑     | ☑           | ConnectorPortsExist                 | ArcBasis | 0xC1108, 0xC1109                                              |
| ☑     | ☑           | ConnectorTypesFit                   | ArcBasis | 0xC1110                                                       |
| ☑     | ☑           | ConnectorDirectionsFit              | ArcBasis | 0xC1111, 0xC1112                                              |
| ☑     | ☑           | ConnectorTimingsFit                 | ArcBasis | 0xC1113                                                       |
| ☑     | ☒           | OnlyOneTiming                       | ArcBasis | 0xC1114                                                       |
| ☑     | ☒           | AtomicMaxOneBehavior                | ArcBasis | 0xC1115                                                       |
| ☑     | ☒           | DelayOutPortOnly                    | ArcBasis | 0xC1116                                                       |
| ☑     | ☑           | FeedbackStrongCausality             | ArcBasis | 0xC1117                                                       |
| ☑     | ☒           | ConfigurationParameterAssignment    | ArcBasis | 0xC1119, 0xC1120, 0xC1121, 0xC1122, 0xC1123, 0xC1124, 0xC1125 |
| ☑     | ☒           | OptionalConfigurationParametersLast | ArcBasis | 0xC1126                                                       |
| ☑     | ☒           | NoSubcomponentReferenceCycle        | ArcBasis | 0xC1127                                                       |
| ☑     | ☒           | ParameterHeritage                   | ArcBasis | 0xC1128, 0xC1129, 0xC1130, 0xC1131                            |
| ☑     | ☑           | PortHeritageTypeFits                | ArcBasis | 0xC1132, 0xC1133, 0xC1134                                     |
| ☑     | ☒           | FieldInitOmitPortReferences         | ArcBasis | 0xC1135                                                       |
| ☑     | ☒           | FieldInitTypeFits                   | ArcBasis | 0xC1137                                                       |
| ☑     | ☒           | ParameterDefaultValueOmitsPortRef   | ArcBasis | 0xC1138                                                       |
| ☑     | ☒           | ParameterDefaultValueTypeFits       | ArcBasis | 0xC1139                                                       |
| ☑     | ☒           | ComponentArgumentsOmitPortRef       | ArcBasis | 0xC1141                                                       |
| ☑     | ☒           | ComponentNameCapitalization         | ArcBasis | 0xC1143                                                       |
| ☑     | ☒           | SubcomponentNameCapitalization      | ArcBasis | 0xC1144                                                       |
| ☑     | ☒           | PortNameCapitalization              | ArcBasis | 0xC1145                                                       |
| ☑     | ☒           | FieldNameCapitalization             | ArcBasis | 0xC1146                                                       |
| ☑     | ☒           | ParameterNameCapitalization         | ArcBasis | 0xC1147                                                       |
| ☑     | ☑           | UniqueIdentifierNames               | ArcBasis | 0xC1148                                                       |
| ☒     | ☒           | RestrictedIdentifier                | ArcBasis | 0xC1149                                                       |
| ☑     | ☒           | CompArgNoAssignmentExpr             | ArcBasis | 0xC1154, 0xC1155, 0xC1156, 0xC1157, 0xC1158                   |
| ☐     | ☒           | ImportsAreUnique                    |          |                                                               |
| ☐     | ☒           | ImportsAreUsed                      |          |                                                               | 

## MontiArc CoCos

| in MA | CoCo(s)        | Language | Code(s) | 
|-------|----------------|----------|---------|
| ☑     | RootNoInstance | MontiArc | 0xC1010 |

## GenericArc CoCos

| in MA | CoCo(s)                         | Language   | Code                      | Notes                                                                    |
|-------|---------------------------------|------------|---------------------------|--------------------------------------------------------------------------|
| ☑     | TypeParameterCapitalization     | GenericArc | 0xC1201                   |                                                                          |
| ☑     | SubcomponentTypeBound           | GenericArc | 0xC1202, 0xC1203, 0xC1204 |                                                                          |
| ☑     | ComponentHeritageTypeBound      | GenericArc | 0xC1205, 0xC1206, 0xC1207 |                                                                          |

# VariableArc CoCos

| in MA | CoCo(s)                        | Language    | Code                                        | Notes |
|-------|--------------------------------|-------------|---------------------------------------------|-------|
| ☑     | ConstraintSatisfied4Comp       | VariableArc | 0xC1401                                     |       |
| ☑     | ConstraintsOmitPortReferences  | VariableArc | 0xC1408                                     |       |
| ☑     | ConstraintsOmitFieldReferences | VariableArc | 0xC1415                                     |       |
| ☑     | ConstraintIsBoolean            | VariableArc | 0xC1400                                     |       |
| ☑     | ConstraintNoAssignmentExpr     | VariableArc | 0xC1154, 0xC1155, 0xC1156, 0xC1157, 0xC1158 |       |
| ☑     | ConstraintSmtConvertible       | VariableArc | 0xC1417                                     |       |
| ☑     | FeatureNameCapitalization      | VariableArc | 0xC1402                                     |       |
| ☑     | FeatureUsage                   | VariableArc | 0xC1403                                     |       |
| ☑     | SubcomponentsConstraint        | VariableArc | 0xC1404                                     |       |
| ☑     | VarIfOmitPortReferences        | VariableArc | 0xC1407                                     |       |
| ☑     | VarIfOmitFieldReferences       | VariableArc | 0xC1416                                     |       |
| ☑     | VarIfIsBoolean                 | VariableArc | 0xC1404                                     |       |
| ☑     | VarIfNoAssignmentExpr          | VariableArc | 0xC1154, 0xC1155, 0xC1156, 0xC1157, 0xC1158 |       |
| ☑     | VarIfSmtConvertible            | VariableArc | 0xC1417                                     |       |

# Mode Automata

| in MA | CoCo(s)                           | Language | Code    | Notes |
|-------|-----------------------------------|----------|---------|-------|
| ☑     | MaxOneModeAutomaton               | Modes    | 0xC1350 |       |
| ☑     | ModeAutomataInDecomposedComponent | Modes    | 0xC1351 |       |
| ☑     | ModeAutomatonContainsNoStates     | Modes    | 0xC1352 |       |
| ☑     | StatechartContainsNoModes         | Modes    | 0xC1353 |       |
| ☑     | ModeOmitPortDefinition            | Modes    | 0xC1354 |       |

## Automaton CoCos

| in MA | CoCo(s)                                                                | Language           | Notes                                                                                                                                                                                                                            |
|-------|------------------------------------------------------------------------|--------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ☑     | UniqueStates                                                           | SCBasis            |                                                                                                                                                                                                                                  |
| ☑     | TransitionSourceAndTargetExist                                         | SCBasis            |                                                                                                                                                                                                                                  |
| ☑     | AtLeastOneInitialState                                                 | SCBasis            |                                                                                                                                                                                                                                  |
| ☑     | MaxOneInitialState                                                     | SCBasis            | Checks that there is only one top-level state per automaton. (This coco does not check sub states.)                                                                                                                              |
| ☑     | NoInputPortsInInitialOutputDecl                                        | ArcAutomaton       |                                                                                                                                                                                                                                  |
| ☑     | TransitionPreconditionsAreBoolean                                      | SCTransitions4Code |                                                                                                                                                                                                                                  |
| ☑     | AnteBlocksOnlyForInitialStates                                         | SCTransitions4Code | Checks that AnteBlocks in state declarations occur only for initial states, as they declare actions initially performed at component instantiation.                                                                              |
| ☑     | Unsupported automaton modeling elements:                               | MontiArc           | Entry & exit actions, finale states, stereotypes. With stereotypes present, we only warn the user. With all other unsupported modelling elements, we throw errors.                                                               |
| ☒     | PackageCorrespondsToFolders                                            | SCBasis            | Not applicable - we do not have statechart artifacts                                                                                                                                                                             |
| ☒     | SCFileExtension\[is.sc\]                                               | SCBasis            | Not applicable - we do not have statechart artifacts                                                                                                                                                                             |
| ☒     | SCNameIsArtifactName                                                   | SCBasis            | Not applicable - we do not have statechart artifacts                                                                                                                                                                             |
| ☒     | CapitalStateNames                                                      | SCBasis            | Warns if a state name starts with a lower case letter. At a discussion we found this to be unneccessary.                                                                                                                         |

## ComfortableArc Cocos

| in MA | CoCo(s)             | Language       | Code    | Notes                                                                                                                                    |
|-------|---------------------|----------------|---------|------------------------------------------------------------------------------------------------------------------------------------------|
| ☑     | MaxOneAutoConnect   | ComfortableArc | 0xC1450 | There may only be one `autoconnect` declaration per component. It is also allowed to omit it in which case `autoconnect off` is implied. |
| ☑     | AtomicNoAutoConnect | ComfortableArc | 0xC1451 | There should be no `autoconnect` declarations in atomic components, as atomic components have no subcomponents.                          |

## Statement CoCos

| in MA | CoCo(s)                                    | Language   | Notes |        
|-------|--------------------------------------------|------------|-------|
| ☒     | ExpressionStatementIsValid                 | Statements |       |   
| ☒     | VarDeclarationInitializationHasCorrectType | Statements |       |   
| ☒     | ForConditionHasBooleanType                 | Statements |       |   
| ☒     | ForEachIsValid                             | Statements |       |          
| ☒     | IfConditionHasBooleanType                  | Statements |       |          
| ☒     | SwitchStatementValid                       | Statements |       |                
