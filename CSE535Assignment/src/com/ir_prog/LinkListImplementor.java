package com.ir_prog;

public class LinkListImplementor {

	// a reference to the first TermNode in the list
	private TermNode head;
	private DocumentTermNode docHead;
	private TermNode workingTermNode;
	// a reference to the TermNode to return when next() is called
	//private TermNode iterator;

	/* constructor
	 * creates a linked list with no items in it
	 */
	public LinkListImplementor()
	{ 
		head = null; 
		docHead = null;
		workingTermNode=null;
		//head.docTermNode=docHead;
	}


	public void addTermDecPostingSize(String item, int postingSize)
	{
		//System.out.println("FUNCTION: addTermDecPostingSize");
		// make the new TermNode to insert into list
		TermNode newTermNode = new TermNode(item, postingSize);
		// first see if the list is empty
		////System.out.println("before if");
		////System.out.println("head:"+head.term);
		////System.out.println( head == null);

		if (head == null)
		{
			//System.out.println("add "+item+" and size "+postingSize);
			head = newTermNode;

			//docHead = head.docTermNode;
		}
		else if (postingSize > head.postingSize)
		{
			//System.out.println("add "+item +"before"+head.term);
			newTermNode.nextTerm = head;
			head = newTermNode;
			//docHead = head.docTermNode;
		}
		else
		{
			////System.out.println("in else");
			TermNode after = head.nextTerm;
			TermNode before = head;
			////System.out.println(after != null);
			while (after != null)
			{
				if (postingSize > head.postingSize)
					break;
				before = after;
				after = after.nextTerm;
			}
			////System.out.println(before.nextTerm);
			newTermNode.nextTerm = before.nextTerm;
			////System.out.println(newTermNode.nextTerm);
			before.nextTerm = newTermNode;
			//docHead = newTermNode.docTermNode;
			//System.out.println("add "+item +"after"+before.term);
		}
		docHead=null;
		workingTermNode = newTermNode;
	}


	public void addDocumentIncreasingId(int documentId, int termFreq)
	{
		//System.out.println("FUNCTION: addDocumentIncreasingId");
		// make the new TermNode to insert into list
		DocumentTermNode newDocNode = new DocumentTermNode(documentId, termFreq);
		// first see if the list is empty
		/*if(workingTermNode.docTermNode==null)
			docHead=null;
		else
			docHead=workingTermNode.docTermNode;
		 */

		if (docHead == null)
		{
			//System.out.println("add "+documentId +" to front");
			docHead = newDocNode;
			workingTermNode.docTermNode=newDocNode;
			return;
		}
		else if (documentId < docHead.docId)
		{
			//System.out.println("add "+documentId +"before"+docHead.docId);
			newDocNode.nextDocument = docHead;
			docHead = newDocNode;
			//head.docTermNode=docHead;
			workingTermNode.docTermNode=newDocNode;			
			return;
		}
		else
		{
			DocumentTermNode after = docHead.nextDocument;
			DocumentTermNode before = docHead;
			while (after != null)
			{
				if (documentId < after.docId)
					break;
				before = after;
				after = after.nextDocument;
			}
			// insert between before & after
			before.nextDocument=newDocNode;
			newDocNode.nextDocument = after;
			//workingTermNode.docTermNode=newDocNode;			
			//System.out.println("add "+documentId +" after "+before.docId);
		}
	}

	public void addDocumentDecreasingFreq(int documentId, int termFreq)
	{
		//System.out.println("FUNCTION: addDocumentDecreasingFreq");
		DocumentTermNode newDocNode = new DocumentTermNode(documentId, termFreq);
		if (docHead == null)
		{
			//System.out.println("add "+documentId +" to front");
			docHead = newDocNode;
			workingTermNode.docTermNode=newDocNode;
			return;
		}
		else if (termFreq > docHead.termFrequency)
		{
			//System.out.println("add "+termFreq +"before"+docHead.termFrequency);
			newDocNode.nextDocument = docHead;
			docHead = newDocNode;
			//head.docTermNode=docHead;
			workingTermNode.docTermNode=newDocNode;
			return;
		}
		else
		{
			DocumentTermNode after = docHead.nextDocument;
			DocumentTermNode before = docHead;
			while (after != null)
			{
				if (termFreq > docHead.termFrequency)
					break;
				before = after;
				after = after.nextDocument;
			}
			before.nextDocument = newDocNode;
			newDocNode.nextDocument=after;
			//System.out.println("add "+termFreq +" after "+before.termFrequency);
		}
	}

	public String getTopK(int K)
	{
		//System.out.println("FUNCTION: getTopK "+K);
		String result="";
		int i=0;
		TermNode after = head.nextTerm;
		while(after!=null && i<K)
		{
			result+=after.term+", ";
			after = after.nextTerm;
			i++;
		}
		if(result.length()>2)
			result=result.substring(0, result.length()-2);
		return result;
	}

	public String getPostings(String query)
	{
		//System.out.println("FUNCTION: getPostings "+query);
		String result="";
		TermNode testTermNode=head;

		while(testTermNode!=null )
		{
			if(testTermNode.term.equals(query))
			{
				DocumentTermNode after = testTermNode.docTermNode;
				//System.out.println(after!=null);
				//System.out.println();
				while(after!=null )
				{
					result+=after.docId+", ";
					after = after.nextDocument;
				}
				break;
			}
			testTermNode = testTermNode.nextTerm;
		}
		if(result.length()>2)
			result=result.substring(0, result.length()-2);
		else
			result="term not found";
		//System.out.println(result);

		return result;
	}

	public void traverseList()
	{
		TermNode testTermNode=head;
		DocumentTermNode testDocHead = null;

		while(testTermNode!=null)
		{
			testDocHead=testTermNode.docTermNode;
			System.out.print("term:"+testTermNode.term+": ");
			while(testDocHead!=null)
			{
				System.out.print(testDocHead.docId+", ");
				testDocHead=testDocHead.nextDocument;
			}
			//System.out.println("");
			testTermNode=testTermNode.nextTerm;
		}
	}

}
