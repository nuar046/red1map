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
import java.util.Iterator; 
import java.io.IOException; 

import org.adempiere.exceptions.AdempiereException; 
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellAddress;
import org.compiere.model.MField;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.compiere.process.ProcessInfoParameter;

import org.compiere.process.SvrProcess;

	public class TransposeField extends SvrProcess { 
	private String File_Directory = "";
	HSSFWorkbook workbook = new HSSFWorkbook(); 
	private String BaseField = ""; 
	private String TargetField = ""; 
	private String SplitSymbol = "";
	private String holdsource = "";
	private String holdtarget = "";
	private int sourcecol = -1;
	private int targetcol = -1;
	private boolean BeforeAfter = false;
	
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
					;
				else if(name.equals("File_Directory")){
					File_Directory = (String)p.getParameter();
			}
				else if(name.equals("BaseField")){
					BaseField = (String)p.getParameter();
			}
				else if(name.equals("TargetField")){
					TargetField = (String)p.getParameter();
			}
				else if(name.equals("SplitSymbol")){
					SplitSymbol = (String)p.getParameter();
			}				else if(name.equals("BeforeAfter")){
				BeforeAfter = "Y".equals(p.getParameter());
		}
		}
	}

	protected String doIt() throws IOException {
		FileInputStream file = new FileInputStream(File_Directory);  
		workbook = new HSSFWorkbook(file); 
		workbook.setForceFormulaRecalculation(true);
		String message = ""; 
        int rowno = 0; 
		HSSFSheet sheet = workbook.getSheet("Master");
		if (sheet==null) {
			sheet = workbook.getSheetAt(0);
			rowno = 1;
		}
        String sheetname = sheet.getSheetName();
        HSSFRow holderrow = null;
        
		//look for source col
		 Iterator cellIterator = sheet.getRow(rowno).cellIterator();
         while (cellIterator.hasNext()) {
            HSSFCell cell = (HSSFCell) cellIterator.next();
            if (cell==null)
            	throw new AdempiereException("No BaseField Cell");
            if (BaseField.equals(cell.getStringCellValue())){
            	holderrow = sheet.getRow(rowno+1);
            	if (holderrow==null)
            		throw new AdempiereException("No source row");
            	sourcecol = cell.getColumnIndex();
            	break;
            }
         }
         if (sourcecol==-1)
        	 return "not found";
         //look for target column
         cellIterator = sheet.getRow(rowno).cellIterator();
         while (cellIterator.hasNext()) {
             HSSFCell cell = (HSSFCell) cellIterator.next();
             if (TargetField.equals(cell.getStringCellValue())) {
            	 targetcol = cell.getColumnIndex(); 
            	 break;
             }
         }
         if (holdsource+holdtarget=="")
          	throw new AdempiereException("Value not found on 3rd row or no Base/Target on 1st row!");
         rowno++;
         while (true) {
        	holderrow = (HSSFRow) sheet.getRow(rowno);
        	if (holderrow==null)
        		break;
        	HSSFCell sourcecell = holderrow.getCell(sourcecol);
        	HSSFCell targetcell = holderrow.getCell(targetcol);

         	holdsource = sourcecell.getStringCellValue();
         	String[]holder = holdsource.split(SplitSymbol);
         	if (holder.length==1) {
         		rowno++;
         		continue;
         	}
        	if (TargetField.isBlank()) {
        		if (BeforeAfter)
             		holdsource = holder[0];
             		else
             			holdsource = holder[1];
        		sourcecell.setCellFormula(null);
        		sourcecell.setCellValue(holdsource);
        	} else {
        		if (BeforeAfter)
             		holdtarget = holder[1];
             		else
             			holdtarget = holder[0];
        		targetcell.setCellFormula(null);
            	targetcell.setCellValue(holdtarget);
        	}
        		
        	rowno++; 
         } 
         writeModelSheet(sheet);
		return "Rows done:"+rowno;
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
