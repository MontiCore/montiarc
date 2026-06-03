<!-- (c) https://github.com/MontiCore/monticore -->

# MontiArc Context Conditions (CoCos)

#### Legend

- ☑ Intentionally included
- ☒ Intentionally not included
- ☐ Inclusion pending or to be discussed

#### Code Ranges

| Language                                | Range             |
|-----------------------------------------|-------------------|
| [MontiArc](#montiarc-cocos)             | 0xC1000 - 0xC1099 |
| [ArcBasis](#arcbasis-cocos)             | 0xC1100 - 0xC1299 |
| [Automaton](#automaton-cocos)           | 0xC1300 - 0xC1349 |
| [Modes](#mode-automata)                 | 0xC1350 - 0xC1399 |
| [VariableArc](#variablearc-cocos)       | 0xC1400 - 0xC1449 |
| [ComfortableArc](#comfortablearc-cocos) | 0xC1450 - 0xC1499 |
| [Unit](#maunit-cocos)                   | 0xC1500 - 0xC1549 |
| [ArcCompute](#arccompute-cocos)         | 0xC1550 - 0xC1599 |

## ArcBasis CoCos

| in MA | VariantCoCo | CoCo(s)                                 | Language | Code(s)                                                       |
|-------|-------------|-----------------------------------------|----------|---------------------------------------------------------------|
| ☑     | ☒           | CircularInheritance                     | ArcBasis | 0xC1100                                                       |
| ☑     | ☑           | PortsConnected                          | ArcBasis | 0xC1103, 0xC1104                                              |
| ☑     | ☑           | PortUniqueSender                        | ArcBasis | 0xC1105                                                       |
| ☑     | ☑           | SubPortsConnected                       | ArcBasis | 0xC1106, 0xC1107                                              |
| ☑     | ☑           | ConnectorPortsExist                     | ArcBasis | 0xC1108, 0xC1109                                              |
| ☑     | ☑           | ConnectorTypesFit                       | ArcBasis | 0xC1110                                                       |
| ☑     | ☑           | ConnectorDirectionsFit                  | ArcBasis | 0xC1111, 0xC1112                                              |
| ☑     | ☑           | ConnectorTimingsFit                     | ArcBasis | 0xC1113                                                       |
| ☑     | ☒           | OnlyOneTiming                           | ArcBasis | 0xC1114                                                       |
| ☑     | ☑           | AtomicMaxOneBehavior                    | ArcBasis | 0xC1115                                                       |
| ☑     | ☑           | BehaviorInDecomposed                    | ArcBasis | 0xC1116                                                       |
| ☑     | ☑           | FeedbackStrongCausality                 | ArcBasis | 0xC1117                                                       |
| ☑     | ☒           | ConfigurationParameterAssignment        | ArcBasis | 0xC1119, 0xC1120, 0xC1121, 0xC1122, 0xC1123, 0xC1124, 0xC1125 |
| ☑     | ☒           | OptionalConfigurationParametersLast     | ArcBasis | 0xC1126                                                       |
| ☑     | ☒           | NoSubcomponentReferenceCycle            | ArcBasis | 0xC1127                                                       |
| ☑     | ☑           | PortHeritageTypeFits                    | ArcBasis | 0xC1132, 0xC1133, 0xC1134                                     |
| ☑     | ☒           | FieldInitOmitPortReferences             | ArcBasis | 0xC1135                                                       |
| ☑     | ☒           | FieldInitTypeFits                       | ArcBasis | 0xC1137                                                       |
| ☑     | ☒           | ParameterDefaultValueOmitsPortRef       | ArcBasis | 0xC1138                                                       |
| ☑     | ☒           | ParameterDefaultValueTypeFits           | ArcBasis | 0xC1139                                                       |
| ☑     | ☒           | ComponentArgumentsOmitPortRef           | ArcBasis | 0xC1141                                                       |
| ☑     | ☒           | ComponentNameCapitalization             | ArcBasis | 0xC1143                                                       |
| ☑     | ☒           | SubcomponentNameCapitalization          | ArcBasis | 0xC1144                                                       |
| ☑     | ☒           | PortNameCapitalization                  | ArcBasis | 0xC1145                                                       |
| ☑     | ☒           | FieldNameCapitalization                 | ArcBasis | 0xC1146                                                       |
| ☑     | ☒           | ParameterNameCapitalization             | ArcBasis | 0xC1147                                                       |
| ☑     | ☑           | UniqueIdentifierNames                   | ArcBasis | 0xC1148                                                       |
| ☒     | ☒           | RestrictedIdentifier                    | ArcBasis | 0xC1149                                                       |
| ☑     | ☒           | CompArgNoAssignmentExpr                 | ArcBasis | 0xC1154, 0xC1155, 0xC1156, 0xC1157, 0xC1158                   |
| ☐     | ☒           | ImportsAreUnique                        |          |                                                               |
| ☐     | ☒           | ImportsAreUsed                          |          |                                                               |
| ☑     | ☑           | AtomicNoConnector                       | ArcBasis | 0xC1174                                                       |
| ☑     | ☒           | TypeParameterCapitalization             | ArcBasis | 0xC1175                                                       |
| ☑     | ☒           | TypeBound                               | ArcBasis | 0xC1176, 0xC1177, 0xC1178                                     |
| ☑     | ☒           | ComponentHeritageRawType                | ArcBasis | 0xC1182                                                       |
| ☑     | ☒           | RefinementRawType                       | ArcBasis | 0xC1182                                                       |
| ☑     | ☒           | RefinementPortsMatch                    | ArcBasis | 0xC1184, 0xC1185, 0xC1186, 0xC1187, 0xC1188, 0xC1189          |
| ☑     | ☒           | CheckNoFieldDependencyCycles            | ArcBasis | 0xC1190                                                       |
| ☑     | ☒           | PortInheritanceTiming                   | ArcBasis | 0xC1191                                                       |
| ☑     | ☒           | OnlyAssignmentOrCallExpressionStatement | ArcBasis | 0xC1194                                                       |

## MontiArc CoCos

| in MA | CoCo(s)              | Language | Code(s) | 
|-------|----------------------|----------|---------|
| ☑     | RootNoInstance       | MontiArc | 0xC1010 |
| ☑     | ImportedSymbolExists | MontiArc | 0xC1024 |

## VariableArc CoCos

| in MA | CoCo(s)                        | Language    | Code                                        | Notes |
|-------|--------------------------------|-------------|---------------------------------------------|-------|
| ☑     | ConstraintSatisfied4Comp       | VariableArc | 0xC1401                                     |       |
| ☑     | ConstraintsOmitPortReferences  | VariableArc | 0xC1175                                     |       |
| ☑     | ConstraintsOmitFieldReferences | VariableArc | 0xC1415                                     |       |
| ☑     | ConstraintIsBoolean            | VariableArc | 0xC1400                                     |       |
| ☑     | ConstraintNoAssignmentExpr     | VariableArc | 0xC1154, 0xC1155, 0xC1156, 0xC1157, 0xC1158 |       |
| ☑     | ConstraintSmtConvertible       | VariableArc | 0xC1417                                     |       |
| ☑     | FeatureNameCapitalization      | VariableArc | 0xC1402                                     |       |
| ☑     | FeatureUsage                   | VariableArc | 0xC1403                                     |       |
| ☑     | SubcomponentsConstraint        | VariableArc | 0xC1404                                     |       |
| ☑     | VarIfOmitPortReferences        | VariableArc | 0xC1175                                     |       |
| ☑     | VarIfOmitFieldReferences       | VariableArc | 0xC1416                                     |       |
| ☑     | VarIfIsBoolean                 | VariableArc | 0xC1404                                     |       |
| ☑     | VarIfNoAssignmentExpr          | VariableArc | 0xC1154, 0xC1155, 0xC1156, 0xC1157, 0xC1158 |       |
| ☑     | VarIfSmtConvertible            | VariableArc | 0xC1417                                     |       |
| ☑     | FeatureNamedTick               | VariableArc | 0xC1173                                     |       |

## Mode Automata

| in MA | CoCo(s)                           | Language | Code    | Notes |
|-------|-----------------------------------|----------|---------|-------|
| ☑     | MaxOneModeAutomaton               | Modes    | 0xC1350 |       |
| ☑     | ModeAutomataInDecomposedComponent | Modes    | 0xC1351 |       |
| ☑     | ModeAutomatonContainsNoStates     | Modes    | 0xC1352 |       |
| ☑     | StatechartContainsNoModes         | Modes    | 0xC1353 |       |
| ☑     | ModeOmitPortDefinition            | Modes    | 0xC1354 |       |
| ☑     | NoCodeBlockInModeTransitions      | Modes    | 0xC1355 |       |

## Automaton CoCos

| in MA | CoCo(s)                                  | Language           | Notes                                                                                                                                                                  |
|-------|------------------------------------------|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ☑     | UniqueStates                             | SCBasis            |                                                                                                                                                                        |
| ☑     | TransitionSourceAndTargetExist           | SCBasis            |                                                                                                                                                                        |
| ☑     | AtLeastOneInitialState                   | SCBasis            |                                                                                                                                                                        |
| ☑     | MaxOneInitialState                       | SCBasis            | Checks that there is only one top-level state per automaton. (This coco does not check sub states.)                                                                    |
| ☑     | NoInputPortsInInitialOutputDecl          | ArcAutomaton       |                                                                                                                                                                        |
| ☑     | TransitionPreconditionsAreBoolean        | SCTransitions4Code |                                                                                                                                                                        |
| ☑     | AnteBlocksOnlyForInitialStates           | SCTransitions4Code | Checks that AnteBlocks in state declarations occur only for initial states, as they declare actions initially performed at component instantiation.                    |
| ☑     | Unsupported automaton modeling elements: | MontiArc           | Finale states                                                                                                                                                          |
| ☒     | PackageCorrespondsToFolders              | SCBasis            | Not applicable - we do not have statechart artifacts                                                                                                                   |
| ☒     | SCFileExtension\[is.sc\]                 | SCBasis            | Not applicable - we do not have statechart artifacts                                                                                                                   |
| ☒     | SCNameIsArtifactName                     | SCBasis            | Not applicable - we do not have statechart artifacts                                                                                                                   |
| ☒     | CapitalStateNames                        | SCBasis            | Warns if a state name starts with a lower case letter. At a discussion we found this to be unnecessary.                                                                |
| ☑     | TransitionUsesEventDependentPorts        | ArcAutomaton       | Tick-triggered transitions must only access values of synced incoming ports; Message-event triggered transitions must only access values of event-based incoming ports |
| ☑     | NoInputPortsInStateActions               | ArcAutomaton       | This regards state entry, do, and exit actions.                                                                                                                        |

## ArcCompute CoCos

| in MA | VariantCoCo | CoCo(s)                   | Language   | Code(s) |
|-------|-------------|---------------------------|------------|---------|
| ☑     | ☑           | NoInitBlockWithoutCompute | ArcCompute | 0xC1550 |
| ☑     | ☑           | MaxOneInit                | ArcCompute | 0xC1551 |

## ComfortableArc Cocos

| in MA | CoCo(s)             | Language       | Code    | Notes                                                                                                                                    |
|-------|---------------------|----------------|---------|------------------------------------------------------------------------------------------------------------------------------------------|
| ☑     | MaxOneAutoConnect   | ComfortableArc | 0xC1450 | There may only be one `autoconnect` declaration per component. It is also allowed to omit it in which case `autoconnect off` is implied. |
| ☑     | AtomicNoAutoConnect | ComfortableArc | 0xC1451 | There should be no `autoconnect` declarations in atomic components, as atomic components have no subcomponents.                          |

## MaUnit CoCos

| in MA | CoCo(s)                     | Language | Code(s)                            |
|-------|-----------------------------|----------|------------------------------------|
| ☑     | UnitTestConfiguredCorrectly | Unit     | 0xC1500, 0xC1501, 0xC1502, 0xC1503 |

## Statement CoCos

| in MA | VariantCoCo | CoCo(s)                                    | Language   | Notes |
|-------|-------------|--------------------------------------------|------------|-------|
| ☑     | ☑           | ExpressionStatementIsValid                 | Statements |       |
| ☑     | ☑           | VarDeclarationInitializationHasCorrectType | Statements |       |
| ☑     | ☑           | ForConditionHasBooleanType                 | Statements |       |
| ☑     | ☑           | ForEachIsValid                             | Statements |       |
| ☑     | ☑           | IfConditionHasBooleanType                  | Statements |       |
| ☑     | ☑           | SwitchCaseTypesValid                       | Statements |       |
