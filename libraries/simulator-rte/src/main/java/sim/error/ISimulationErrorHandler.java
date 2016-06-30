package sim.error;

import java.util.List;

import org.slf4j.Logger;

/**
 * Handles errors that occur during the simulation.
 * 
 * <br>
 * <br>
 * Copyright (c) 2010 RWTH Aachen. All rights reserved.
 * 
 * @author Arne Haber
 * @version 04.03.2009
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
