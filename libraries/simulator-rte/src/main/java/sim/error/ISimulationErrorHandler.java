/* (c) https://github.com/MontiCore/monticore */
package sim.error;

import java.util.List;

import org.slf4j.Logger;

/**
 * Handles errors that occur during the simulation.
 * 
 * 
 */
public interface ISimulationErrorHandler {
    
    /**
     * Adds and displays the problem report.
     * 
     * @param report the problem report to add
     */
    void addReport(ArcSimProblemReport report);
    
    /**
     * Initializes the error handler.
     */
    void init();
    
    /**
     * @return a list of problem reports
     */
    List<ArcSimProblemReport> getReports();
    
    /**
     * 
     * @return a logger used to log simulation events.
     */
    Logger getLogger();
    
}
