package hello;

import java.util.HashMap;
import java.util.Map;

public class row {
	
	private String path;
	private String filename;
	private Map<String,String> columns;
	private String rowkey;
	
	
	public row(String path, String filename) {
		super();
		setPath(path);
		this.filename = filename;
		setRowkey();
		initColumns();
		
	}

	private void initColumns() {
		// TODO Auto-generated method stub
		this.columns = new HashMap<String,String>();
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
		this.rowkey = this.path.concat(this.filename);
	}
	public Map<String,String> getColumns() {
		return columns;
	}
	public void setColumns(Map<String,String> columns) {
		this.columns = columns;
	}
}
