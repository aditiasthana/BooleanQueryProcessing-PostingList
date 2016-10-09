package com.ir_prog;

public class TermNode {

	public String term;
	public Integer postingSize;
	public TermNode nextTerm;
	public DocumentTermNode docTermNode;

	public TermNode(String termValue, int postingSizeValue)
	{
		term = termValue;
		postingSize=postingSizeValue;
		nextTerm = null;
		docTermNode = null;
	}
}
