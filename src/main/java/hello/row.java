package hello;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class row {
	
	private String path;
	private String filename;
	private String version;

	private Map<String,String> columns;
	private String rowkey;
	private List<String> collist;
	
	public List<String> getCollist() {
		return collist;
	}

	public void setCollist(List<String> collist) {
		this.collist = collist;
	}

	public row(String filename, String versionrequired) {
		super();
		this.filename = filename;
		this.version = versionrequired;
		setRowkey();
		initColumns();
	}

	private void initColumns() {
		// TODO Auto-generated method stub
		this.columns = new HashMap<String,String>();
		this.collist = new ArrayList<String>();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if (path.charAt(path.length()-1) == '\\') {
			this.path = path;
		} else {
			this.path = path.concat("\\");
		}
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getRowkey() {
		return rowkey;
	}
	public void setRowkey() {
		this.rowkey = this.filename;
	}
	public Map<String,String> getColumns() {
		return columns;
	}
	public void setColumns(Map<String,String> columns) {
		this.columns = columns;
	}
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
