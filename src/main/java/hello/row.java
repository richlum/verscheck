package hello;

public class row {
	
	private String rowkey;
	private String[] columns;
	
	
	public row(String rowkey, String[] columns) {
		super();
		this.rowkey = rowkey;
		this.columns = columns;
	}
	public String getRowkey() {
		return rowkey;
	}
	public void setRowkey(String rowkey) {
		this.rowkey = rowkey;
	}
	public String[] getColumns() {
		return columns;
	}
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
}
