/* (c) https://github.com/MontiCore/monticore */
package dsim.port

import dsim.log.ILoggable

/**
 * Interface extends IDataSource, IDataSink, IConnectorBase, IConnectorTarget.
 * Every Port can potentially be the base and the target of a connector (if
 * decomposed component).
 * Every Port can potentially be a Data Sink because of current implementation
 * of direct forwarding to all transitive receivers (see Port class)
 *
 * Every port can potentially be a Data Source because currently, all
 * info inside a component can be a transformation trigger, including data
 * in out ports in the model.
 */
interface IPort : IDataSource, IDataSink, ILoggable
