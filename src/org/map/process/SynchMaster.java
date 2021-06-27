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
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.util.CellAddress;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

	public class SynchMaster extends SvrProcess {
	private int count = 1;
	private String File_Directory = ""; 
	private int AD_Window_ID = 0;
	private boolean PopulateTranslationFile = false;
	private boolean CreateTranslationTable = false;
	HSSFWorkbook workbook = new HSSFWorkbook(); 
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
					;
				else if(name.equals("File_Directory")){
					File_Directory = (String)p.getParameter();
				} else if(name.equals("PopulateTranslationFile")){
				PopulateTranslationFile = "Y".equals(p.getParameter());
				}
				else if(name.equals("CreateTranslationTable")){
					CreateTranslationTable = "Y".equals(p.getParameter());
		}
		}
	}

	protected String doIt() throws IOException {
		FileInputStream file = new FileInputStream(File_Directory);  
		workbook = new HSSFWorkbook(file); 
		workbook.setForceFormulaRecalculation(true);
		String message = ""; 
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator rawIterator = sheet.rowIterator();	
		HSSFDataFormatter dataFormatter = new HSSFDataFormatter();
		HSSFRow row = (HSSFRow) rawIterator.next();
        String sheetname = sheet.getSheetName();
        HSSFSheet master = workbook.getSheet("Master");
        
		//look for AD names
		 Iterator cellIterator = sheet.getRow(0).cellIterator();
         while (cellIterator.hasNext()) {
            HSSFCell cell = (HSSFCell) cellIterator.next();
            String reference = cell.getCellFormula();
            CellAddress add =  cell.getAddress();
            
            String formula = sheetname+"!"+add.toString().charAt(0)+3;
            CellReference cr = new CellReference(reference.substring(reference.length()-2, reference.length()));
            int irow = cr.getRow()+1;
            int icol = cr.getCol();
            System.out.println(add+" "+cell.getRichStringCellValue()+"="+reference+" Formula:"+formula); 
             //iterate all columns having values till end
            HSSFRow masterrow = master.getRow(irow); 
			if (masterrow==null)
				masterrow=master.createRow(irow);
			HSSFCell mastercell = masterrow.getCell(icol);
			if (mastercell==null)
				mastercell=masterrow.createCell(icol);
            mastercell.setCellFormula(formula); 
            repeatRows(master,sheet,formula.substring(0, formula.length()-1),irow+1,icol);
        }
         writeModelSheet(master);
         
         if (PopulateTranslationFile)
        	 populateTranslationFile(sheet,master);
         if (CreateTranslationTable)
        	 createTranslationTable(sheet,master);
         
		return "Cells written : "+count;
	} 

	private void createTranslationTable(HSSFSheet sheet, HSSFSheet master) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Change Translation file fields with the Raw Sheet client values (2nd row based on the 1st row) 
	 * @param sheet
	 * @param master
	 */
	private void populateTranslationFile(HSSFSheet sheet, HSSFSheet master) {
		// Get System Language
		org.compiere.util.Language login = Env.getLoginLanguage(Env.getCtx());
	    String loginLanguage = login.getAD_Language();
	    //
	    //get Fields of Tabs/Tables that are refering to the 1/2 rows translation
	    
	    
 
	}

	private void repeatRows(HSSFSheet master,HSSFSheet raw, String formula,int irow, int icol) {
 		while (true) {
			HSSFRow rawrow = (HSSFRow) raw.getRow(irow+1);
			if (rawrow==null)
				break;
			HSSFRow masterrow = master.getRow(irow);
			if (masterrow==null)
				masterrow=master.createRow(irow);
			HSSFCell mastercell = masterrow.getCell(icol);
			if (mastercell==null)
				mastercell=masterrow.createCell(icol);
			mastercell.setCellFormula(formula+(irow+2));
			irow++;
			count++;
		}
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
