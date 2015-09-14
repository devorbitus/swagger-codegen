package io.swagger.codegen;

public class CodegenColumn implements Cloneable {
	
	public String columnName;
	public String getter;
	public String setter;
	public Integer inputIndex;
	public String inputName;
	public Boolean addId;
	public Boolean addTimestamp;
	public Boolean hasMore = null;
	
	public CodegenColumn(){}
	
	public CodegenColumn(CodegenColumn col) {
		this.columnName = col.columnName;
		this.getter = col.getter;
		this.setter = col.setter;
		this.inputIndex = col.inputIndex;
		this.inputName = col.inputName;
		this.addId = col.addId;
		this.addTimestamp = col.addTimestamp;
		this.hasMore = col.hasMore;
	}

	public CodegenColumn clone(){
		CodegenColumn cc = new CodegenColumn(this);
		return cc;
	}
}
