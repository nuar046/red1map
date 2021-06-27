/**
* Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.
* If you shall try to cheat and find a loophole in this license, then KARMA will exact your share,
* and your worldly gain shall come to naught and those who share shall gain eventually above you.
* In compliance with previous GPLv2.0 works of Jorg Janke, Low Heng Sin, Carlos Ruiz and contributors.
* This Module Creator is an idea put together and coded by Redhuan D. Oon (red1@red1.org)
*/

package org.map.process;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.adempiere.exceptions.AdempiereException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

	public class MakeMigrateSheet extends SvrProcess {

	HSSFWorkbook workbook = new HSSFWorkbook(); 
	private String File_Directory = "";

	private int AD_Window_ID = 0;

	private int AD_Table_ID = 0;

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
				else if(name.equals("AD_Table_ID")){
					AD_Table_ID = p.getParameterAsInt();
			}
		}
	}

	protected String doIt() throws IOException {
		FileInputStream file = new FileInputStream(File_Directory);  
		workbook = new HSSFWorkbook(file); 
		workbook.setForceFormulaRecalculation(true);
		String message = ""; 
		if (AD_Table_ID==0)
			throw new AdempiereException("Select a Table");
		MTable table = new MTable(getCtx(), AD_Table_ID, get_TrxName());
		  
		HSSFSheet sheet = workbook.createSheet(table.getTableName());
		HSSFRow row = sheet.createRow(0);
		int c = 0;
		//fetch only mandatory columns
		List<MColumn> columns = new Query(getCtx(),MColumn.Table_Name,MColumn.COLUMNNAME_AD_Table_ID+"=?",get_TrxName())
				.setParameters(AD_Table_ID).list();
		for (MColumn column:columns) {
			if (column.isMandatory()) {
				if (column.isStandardColumn() || column.getColumnName().equals(table.getTableName()+"_ID"))
					continue;
				HSSFCell cell = row.createCell(c);
				cell.setCellValue(column.getColumnName());
				c++;
			}
		}
		writeModelSheet(sheet);
		
		return "Cells created: "+c;
	}
	
	private void writeModelSheet(HSSFSheet sheet) throws IOException {
		 
		FileOutputStream out = new FileOutputStream(File_Directory);
		if(out!=null)
		{
			workbook.write(out);
			out.close();
			workbook.close();
		}
	}
}
