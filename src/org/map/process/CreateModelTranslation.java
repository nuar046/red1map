/**
* Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.
* If you shall try to cheat and find a loophole in this license, then KARMA will exact your share,
* and your worldly gain shall come to naught and those who share shall gain eventually above you.
* In compliance with previous GPLv2.0 works of Jorg Janke, Low Heng Sin, Carlos Ruiz and contributors.
* This Module Creator is an idea put together and coded by Redhuan D. Oon (red1@red1.org)
*/

package org.map.process;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Timestamp;

import java.io.FileInputStream; 
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator; 
import org.adempiere.exceptions.AdempiereException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.compiere.model.MColumn; 
import org.compiere.model.MSequence;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;

import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

	public class CreateModelTranslation extends SvrProcess {

	private String File_Directory = "";

	private int AD_Window_ID = 0; 

	private boolean IsActive = false; 
	private boolean Processed = false;
	private final String comma = ","; 
	private Timestamp Today = new Timestamp(System.currentTimeMillis());
	private int AD_User_ID = 0;
	private int AD_Client_ID = 0;
	private int AD_Org_ID = 0;
	private int cnt = 0;
	
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
					;
				else if(name.equals("File_Directory")){
					File_Directory = (String)p.getParameter();
			}
				else if(name.equals("AD_Window_ID")){
					AD_Window_ID = p.getParameterAsInt();
			}
		}
	}

	protected String doIt() throws IOException {
		AD_User_ID = Env.getAD_User_ID(getCtx());
		AD_Client_ID = Env.getAD_Client_ID(getCtx());
		AD_Org_ID = Env.getAD_Org_ID(getCtx());
		
		FileInputStream file = new FileInputStream(File_Directory);  
		HSSFWorkbook workbook = new HSSFWorkbook(file); 
		
		String message = "";
			
		for (int i=0;i<workbook.getNumberOfSheets();i++) {
			HSSFSheet sheet = workbook.getSheetAt(i);
			String sheetName = sheet.getSheetName();
		}	
		return "";
		
	}
}
