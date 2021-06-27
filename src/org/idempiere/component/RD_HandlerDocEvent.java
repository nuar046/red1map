/**
* Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.
* If you shall try to cheat and find a loophole in this license, then KARMA will exact your share,
* and your worldly gain shall come to naught and those who share shall gain eventually above you.
* In compliance with previous GPLv2.0 works of Jorg Janke, Low Heng Sin, Carlos Ruiz and contributors.
* This Module Creator is an idea put together and coded by Redhuan D. Oon (red1@red1.org)
*/

package org.idempiere.component; 
import org.adempiere.base.event.AbstractEventHandler; 
import org.compiere.model.PO; 
import org.compiere.util.CLogger;
import org.osgi.service.event.Event;

public class RD_HandlerDocEvent extends AbstractEventHandler {
 	private static CLogger log = CLogger.getCLogger(RD_HandlerDocEvent.class); 

	@Override 
	protected void initialize() { 
	//register EventTopics and TableNames  
		log.info("Data MAP initialized");
		}

	@Override
	protected void doHandleEvent(Event event) { 
	 	      	 
	}
	  
}
