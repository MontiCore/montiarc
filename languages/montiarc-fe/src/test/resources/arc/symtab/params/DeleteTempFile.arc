package params;

/**
 * Creates a file object for file names received on port {@link fileName} 
 * and deletes the file after a delay of {@code deleteAfter} time units.
 *
 * @sideEffects File IO, deletes files with name {@link fileName}
 * <br>
 * <br>
 * Copyright (c) 2012 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Date: 2013-04-05 17:19:31 +0200 (Fri, 05 Apr 2013) $<br>
 * @rev $Revision: 2165 $ 
 * @param deleteAfter wait 'deleteAfter' time units before delete the temp file. 
 */
component DeleteTempFile(int deleteAfter) {
    
    timing instant;
    
	port
		in String fileName;
		
    component ConstantDelay<String>(deleteAfter) delay;
    
    component InternDeleteTempFile del {
        port
            in String fileName;
    }
    
    connect fileName -> delay.portIn;
    connect delay.portOut -> del.fileName;
}
