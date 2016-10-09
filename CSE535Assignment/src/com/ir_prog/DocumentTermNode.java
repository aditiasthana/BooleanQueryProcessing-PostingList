package com.ir_prog;

public class DocumentTermNode {

	public Integer docId;
	public Integer termFrequency;
	public DocumentTermNode nextDocument;

	public DocumentTermNode(int documentId, int termFreq)
	{
		docId = documentId;
		termFrequency=termFreq;
		nextDocument = null;
	}
}
