/**

import java.sql.ResultSet;
import java.util.Properties;

public class MCE_RegisterEvents extends X_CE_RegisterEvents{

	private static final long serialVersionUID = -1L;

	public MCE_RegisterEvents(Properties ctx, int id, String trxName) {
		super(ctx,id,trxName);
		}

	public MCE_RegisterEvents(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
}