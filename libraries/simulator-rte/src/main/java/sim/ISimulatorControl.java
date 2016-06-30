package sim;

import java.io.File;

/**
 * Defines the methods needed for an architecture simulator.
 * 
 * <br>
 * <br>
 * Copyright (c) 2010 RWTH Aachen. All rights reserved.
 * 
 * @author Arne Haber
 * @version 25.11.2008
 */
public interface ISimulatorControl {
    
    /**
     * @return the delta between two scheduler steps in ms
     */
    int getSimulationSpeed();
    
    /**
     * Loads input data from the given ArcLoad file.
     * 
     * @param file ArcLoad file
     */
    void loadInputData(File file);
    
    /**
     * Pauses the simulation.
     */
    void pauseSimulation();
    
    /**
     * Restarts the simulation environment.
     */
    void restart();
    
    /**
     * Resumes the simulation, if it was paused.
     */
    void resumeSimulation();
    
    /**
     * Saves the current simulation history to an arc load file.
     * 
     * @param file ArcLoad file
     */
    void saveSimulationRun(File file);
    
    /**
     * Is called to schedule and compute the next port.
     */
    void scheduleNextPort();
    
    /**
     * Sets the speed from the simulator.
     * 
     * @param ms scheduler steps in ms
     */
    void setSimulationSpeed(int ms);
    
    /**
     * Starts the simulation.
     */
    void startSimulation();
    
}
