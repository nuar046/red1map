/**
import org.my.model.MCE_RegisterEvents;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.osgi.service.event.Event;

public class CE_RegisterEventsDocEvent extends AbstractEventHandler {
 	private static CLogger log = CLogger.getCLogger(CE_RegisterEventsDocEvent.class);
		private String trxName = "";
		private PO po = null;

	@Override 
	protected void initialize() { 
		
	}

	private String getAction(String docStatus) {
	protected void doHandleEvent(Event event){
		setPOTrx(event);
	}
	private void setPOTrx(Event event) { 

}