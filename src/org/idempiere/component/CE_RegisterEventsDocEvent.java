/*** Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.* If you shall try to cheat and find a loophole in this license, then KARMA will exact your share,* and your worldly gain shall come to naught and those who share shall gain eventually above you.* In compliance with previous GPLv2.0 works of Jorg Janke, Low Heng Sin, Carlos Ruiz and contributors.* This Module Creator is an idea put together and coded by Redhuan D. Oon (red1@red1.org)*/package org.idempiere.component;
import org.my.model.MCE_RegisterEvents;import java.sql.Timestamp;import java.util.List;import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;import org.compiere.model.MBPartner;import org.compiere.model.MBPartnerLocation;import org.compiere.model.MMovement;import org.compiere.model.MOrder;import org.compiere.model.MOrderLine;import org.compiere.model.MRequest;import org.compiere.model.MRequestType;import org.compiere.model.MTable;
import org.compiere.model.PO;import org.compiere.model.Query;import org.compiere.process.DocAction;
import org.compiere.util.CLogger;import org.compiere.util.Env;
import org.osgi.service.event.Event;

public class CE_RegisterEventsDocEvent extends AbstractEventHandler {
 	private static CLogger log = CLogger.getCLogger(CE_RegisterEventsDocEvent.class);
		private String trxName = "";
		private PO po = null;

	@Override 
	protected void initialize() { 		List<MCE_RegisterEvents> events = new Query(Env.getCtx(),MCE_RegisterEvents.Table_Name,"",null)				.list();		for (MCE_RegisterEvents event:events) { 			String action = getAction(event.getDocStatus());			registerTableEvent(action, event.getAD_Table().getTableName());  			log.info(action+" action registered to "+event.getAD_Table().getTableName());		}
		
	}

	private String getAction(String docStatus) {		if (docStatus.equals(DocAction.STATUS_InProgress))			return IEventTopics.DOC_AFTER_PREPARE;		else if (docStatus.equals(DocAction.STATUS_Completed))			return IEventTopics.DOC_AFTER_COMPLETE;		return null;	}		@Override 
	protected void doHandleEvent(Event event){
		setPOTrx(event);		log.info(event.getTopic());		//get the Sales Order document		//get the DatePromised 		Timestamp dateComplete = po.getUpdated();		Timestamp dateStart = po.getUpdated();				//create new Request event for the Calendar to display		MRequest request = new MRequest(Env.getCtx(),0,trxName);		if (po.get_TableName().equals(MOrder.Table_Name))			request.setC_Order_ID(po.get_ID());		request.setDateStartPlan(dateStart);		request.setDateCompletePlan(dateComplete);				//Summary Information that will be displayed on the Dashboard Calendar		//get Product Info and Location to Ship to 		request.setSummary(po.get_TableName()+" "+po.get_ID());				MRequestType rt = new Query(Env.getCtx(),MRequestType.Table_Name,"Name='Service Request'",null).first();		request.setR_RequestType_ID(rt.get_ID());		if (po.get_ColumnIndex("SalesRep_ID")>0)			request.setSalesRep_ID(Integer.parseInt(po.get_Value("SalesRep_ID").toString()));				request.saveEx(trxName);		log.info("Creating new Request "+request.get_ID());		//Assign DocType for CustomerReturn 				log.info("REQUEST for Calendar created from ");		
	}
	private void setPOTrx(Event event) { 		po=getPO(event);		trxName=po.get_TrxName();	}	

}
