/**
* Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.
* If you shall try to cheat and find a loophole in this license, then KARMA will exact your share,
* and your worldly gain shall come to naught and those who share shall gain eventually above you.
* In compliance with previous GPLv2.0 works of Jorg Janke, Low Heng Sin, Carlos Ruiz and contributors.
* This Module Creator is an idea put together and coded by Redhuan D. Oon (red1@red1.org)
*/

package org.map.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.adempiere.exceptions.AdempiereException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.compiere.model.MAttachment;
import org.compiere.model.MAttachmentEntry;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

	public class ExcelSalesOrder extends SvrProcess {

	File attachedFile = null;
	FileInputStream file = null;
	private String File_Directory = "";
	HSSFWorkbook workbook = new HSSFWorkbook(); 
	private int C_Order_ID = 0;
	private String Description = "";
	int cnt = 0;
	MOrder order = null;
	
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
					;
				else if(name.equals("File_Directory")){
					File_Directory = (String)p.getParameter();
			}
				else if(name.equals("C_Order_ID")){
					C_Order_ID = p.getParameterAsInt();
			}				
				else if(name.equals("Description")){
				Description = (String)p.getParameter();
			}
		}
	}

	protected String doIt() throws IOException {
		if (C_Order_ID==0)
			throw new AdempiereException("Select Sales Order to append order lines to.");
		order = new MOrder(getCtx(), C_Order_ID, get_TrxName());
		if (order==null)
			throw new AdempiereException("Sales Order not found");
		if (Description.length()==0)
			throw new AdempiereException("Put Sheet number, SheetNo, Start Row, Start Col numbers to get Products in Sheet");
		String splits[] = Description.split(",");
		if (splits.length!=3)
			throw new AdempiereException("Input 3 values = SheetNo, Start Row, Start Col to get Products");

		String attachment = "";
		if (File_Directory=="") { 
			attachment	= getAttachment();
			if (attachment.length()>0)
				throw new AdempiereException("No File_Directory nor Excel attached in Sales Order");
		
			if (attachedFile.toString().endsWith("xls"))
				file = new FileInputStream(attachedFile);
			else 
				throw new AdempiereException("Attached file is not XLS extension");
		}else {
			if (File_Directory.endsWith("xls"))
					file = new FileInputStream(File_Directory);  
			else 
				throw new AdempiereException("File Directory is not pointing to XLS file");
		}
		 
		workbook = new HSSFWorkbook(file);  
		HSSFSheet sheet = workbook.getSheetAt(Integer.parseInt(splits[0]));
		HSSFRow row = null;
		int rowstart = Integer.parseInt(splits[1]); 
		int rowend = sheet.getLastRowNum();
		int colstart = Integer.parseInt(splits[2]); 
        int colend = 0;
		for (int i=rowstart;i<rowend+1;i++) {
			 row = (HSSFRow)sheet.getRow(i);	
			 if (row==null)
				 continue;
			 colend=row.getLastCellNum();
	         iterateColumns(row, colstart, colend);  
		}  
		return "Products done:"+cnt;
	}

	private void iterateColumns(HSSFRow row, int colstart, int colend) {
		for (int i=colstart;i<colend+1;i++) {
			HSSFCell cell = (HSSFCell)row.getCell(i);
			if (cell==null)
				continue;
			if(cell.getCellType() ==  CellType.NUMERIC)
                		continue;
			String productstring = cell.getRichStringCellValue().toString().trim(); 
		    MProduct product = new Query(getCtx(), MProduct.Table_Name,MProduct.COLUMNNAME_Value+"=?",get_TrxName())
		          .setParameters(productstring)
		           .first();
		    if (product==null) {
				 productstring =  productstring.replaceAll("\\s","");
				 product = new Query(getCtx(), MProduct.Table_Name,MProduct.COLUMNNAME_Value+"=?",get_TrxName())
				          .setParameters(productstring)
				           .first();
		    }
		    if (product==null)
		         	continue;
		    cnt++;  
		    statusUpdate(cnt+" product added:"+productstring);
		    boolean found= false;
		    MOrderLine[] orderlines = order.getLines(true,null);        
		    for (MOrderLine orderline:orderlines) {
		    	if (orderline.getM_Product_ID()==product.get_ID()) {
		    		found=true;
		    		orderline.setQty(orderline.getQtyOrdered().add(Env.ONE));
		    		orderline.saveEx(get_TrxName());
		    		break;
		    	}
		    }
		    if (!found) {
		    	MOrderLine newline = new MOrderLine(order);
		    	newline.setQty(Env.ONE);
		    	newline.setM_Product_ID(product.get_ID());
		    	newline.saveEx(get_TrxName());
		    }
		}
	} 	
	
	private String getAttachment() { 
		MAttachment attachment = order.getAttachment();
		if (attachment == null) {
			return "Please attach CSV File containing Product items";
		}
		MAttachmentEntry entry = attachment.getEntry(0);
		if (entry == null) {
			return "NO XLS ATTACHED";
		}
		// Check filename is .csv
		attachedFile = entry.getFile();
		log.info("Attached File Name->" + attachedFile);
		
		return "";
	}
}
