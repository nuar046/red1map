/**



	public class ImportFromExcel extends SvrProcess {

	CellReference cr = null;
		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("File_Directory")){
					File_Directory = (String)p.getParameter();
			}
				else if(name.equals("Description")){
					Description = (String)p.getParameter();
			}
		}
	}
