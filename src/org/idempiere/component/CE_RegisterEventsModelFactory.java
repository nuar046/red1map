/**
import org.my.model.MCE_RegisterEvents;
import java.sql.ResultSet;
import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

public class CE_RegisterEventsModelFactory implements IModelFactory {
	@Override 	public Class<?> getClass(String tableName) {
		 if (tableName.equals(MCE_RegisterEvents.Table_Name)){
			 return MCE_RegisterEvents.class;
		 }
  		return null;
	}
	@Override	public PO getPO(String tableName, int Record_ID, String trxName) {
		 if (tableName.equals(MCE_RegisterEvents.Table_Name)) {
		     return new MCE_RegisterEvents(Env.getCtx(), Record_ID, trxName);
		 }
  		return null;
	}
	@Override	public PO getPO(String tableName, ResultSet rs, String trxName) {
		 if (tableName.equals(MCE_RegisterEvents.Table_Name)) {
		     return new MCE_RegisterEvents(Env.getCtx(), rs, trxName);
		   }
 		 return null;
	}
}