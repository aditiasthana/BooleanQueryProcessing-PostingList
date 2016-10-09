package com.ir_prog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class CSE535Assignment {

	private static BufferedReader indexFile;
	private static BufferedWriter logFile;
	private static int termsToRetrieve;
	private static BufferedReader queryFile;
	private static int comparisonCount=0;
	private static double time=0;
	private static int optimizedComparisonCount=0;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		LinkListImplementor incDocIdList = new LinkListImplementor();
		LinkListImplementor decTermFreqList = new LinkListImplementor();
		try{		
			if(args.length>0)
			{
				indexFile = new BufferedReader(new FileReader(args[0]));
				logFile= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "utf-8"));
				termsToRetrieve=Integer.parseInt(args[2]);
				queryFile = new BufferedReader(new FileReader(args[3]));
				String str;
				
				// creating index linked list 
				
				while ((str = indexFile.readLine()) != null)
				{    
					str= str.replace("\\", "===");
					String [] postingArr= str.split("===");
					String postingList=postingArr[2].substring(1, postingArr[2].length());

					postingList= postingList.replace(", ", "/");
					postingList= postingList.substring(1,postingList.length()-1);
					String [] postingListArr=postingList.split("/");

					String term = postingArr[0];
					int postingSize = Integer.parseInt(postingArr[1].substring(1, postingArr[1].length()));
					//System.out.println("adding new term: "+term +" and postingSize:"+postingSize);
					incDocIdList.addTermDecPostingSize(term, postingSize);
					decTermFreqList.addTermDecPostingSize(term, postingSize);
					//incTermFreqList.addTermDecPostingSize(term, postingSize);

					for(int j=0;j<postingListArr.length;j+=2)
					{
						//System.out.println("j="+j/2);
						int docId= Integer.parseInt(postingListArr[j]);
						int frequency = Integer.parseInt(postingListArr[j+1]);

						incDocIdList.addDocumentIncreasingId(docId, frequency);
						decTermFreqList.addDocumentDecreasingFreq(docId, frequency);

					}
				}
				indexFile.close();
				/*//System.out.println("");
					incDocIdList.traverseList();
					//System.out.println("");
					decTermFreqList.traverseList();
				 */


				// getTopK terms
				logFile.write("FUNCTION: getTopK "+termsToRetrieve+"\n");
				String result = incDocIdList.getTopK(termsToRetrieve);
				//System.out.println("Result: "+result);
				logFile.write("Result: "+result+"\n");

				while ((str = queryFile.readLine()) != null )
				{
					String [] queryArr=str.split(" ");

					// getPostings

					ArrayList<String>incIdPostingArrList= new ArrayList<String>();
					ArrayList<String>decFreqPostingArrList= new ArrayList<String>();
					ArrayList<String>incFreqPostingArrList= new ArrayList<String>();

					for(String queryTerm : queryArr)
					{
						logFile.write("FUNCTION: getPostings "+queryTerm+"\n");
						result=incDocIdList.getPostings(queryTerm);
						incIdPostingArrList.add(result);
						logFile.write("Ordered by doc IDs: "+result+"\n");
						result=decTermFreqList.getPostings(queryTerm);
						decFreqPostingArrList.add(result);
						logFile.write("Ordered by TF: "+result+"\n");
					}

					for(String temp : decFreqPostingArrList)
						incFreqPostingArrList.add(0, temp);


					// termAtATimeQueryAnd

					str=str.replaceAll(" ", ", ");
					logFile.write("FUNCTION: termAtATimeQueryAnd "+str+"\n");

					if(decFreqPostingArrList.contains("term not found"))
						logFile.write("terms not found\n");
					else
					{
						//System.out.println("FUNCTION: termAtATimeQueryAnd");
						String [] resultArr = termAtATimeQueryAnd(decFreqPostingArrList,incFreqPostingArrList);
						//	//System.out.println(resultArr[0]+", "+resultArr[1]);

						logFile.write(resultArr[0] +" documents are found\n");
						logFile.write(resultArr[1] +" comparisions are made\n");
						logFile.write(resultArr[2] +" seconds are used\n");
						logFile.write(resultArr[3] +" comparisons are made with optimization (optional bonus part)\n");
						logFile.write("Result: "+resultArr[4]+"\n");
					}

					// termAtATimeQueryOr

					logFile.write("FUNCTION: termAtATimeQueryOr "+str+"\n");


					//System.out.println("FUNCTION: termAtATimeQueryOr");
					String [] resultArr = termAtATimeQueryOr(decFreqPostingArrList,incFreqPostingArrList);
					//System.out.println(resultArr[0]+", "+resultArr[1]);

					//System.out.println("Ans:"+resultArr[0]);
					if(resultArr[0].equalsIgnoreCase("0"))
						logFile.write("terms not found\n");
					else
					{
						logFile.write(resultArr[0] +" documents are found\n");
						logFile.write(resultArr[1] +" comparisions are made\n");
						logFile.write(resultArr[2] +" seconds are used\n");
						logFile.write(resultArr[3] +" comparisons are made with optimization (optional bonus part)\n");
						logFile.write("Result: "+resultArr[4]+"\n");
					}

					// documentAtATimeQueryAnd

					logFile.write("FUNCTION: documentAtATimeQueryAnd "+str+"\n");
					//System.out.println("FUNCTION: documentAtATimeQueryAnd");
					resultArr = documentAtATimeQueryAnd(incIdPostingArrList);
					//System.out.println(resultArr[0]+", "+resultArr[1]);

					//System.out.println("Ans:"+resultArr[0]);
					if(resultArr[0].equalsIgnoreCase("0"))
						logFile.write("terms not found\n");
					else
					{
						logFile.write(resultArr[0] +" documents are found\n");
						logFile.write(resultArr[1] +" comparisions are made\n");
						logFile.write(resultArr[2] +" seconds are used\n");
						logFile.write("Result: "+resultArr[3]+"\n");
					}

					// documentAtATimeQueryOr

					logFile.write("FUNCTION: documentAtATimeQueryOr "+str+"\n");
					//System.out.println("FUNCTION: documentAtATimeQueryOr");
					resultArr = documentAtATimeQueryOr(incIdPostingArrList);
					//System.out.println(resultArr[0]+", "+resultArr[1]);

					//System.out.println("Ans:"+resultArr[0]);
					if(resultArr[0].equalsIgnoreCase("0"))
						logFile.write("terms not found\n");
					else
					{
						logFile.write(resultArr[0] +" documents are found\n");
						logFile.write(resultArr[1] +" comparisions are made\n");
						logFile.write(resultArr[2] +" seconds are used\n");
						logFile.write("Result: "+resultArr[3]+"\n");
					}
				}
				queryFile.close();
			}
			else
				System.err.println("No arguments passed");
		}
		catch(Exception e){
			System.err.println("In catch of main function: "+e.getMessage());
			System.err.println(e.getStackTrace());
			//System.err.println(e.printStackTrace(s););
		}
		finally {
			logFile.close();
		}
	}

	public static String[] termAtATimeQueryAnd (ArrayList<String> postingArrList, ArrayList<String> incFreqPostingArrList)
	{
		String[] result = null;
		comparisonCount=0;
		time=0;
		optimizedComparisonCount=0;
		//System.out.println("Size:"+postingArrList.size());
		if(postingArrList.size()>1 && !postingArrList.contains("term not found"))
		{

			String [] p1=postingArrList.get(0).split(", ");
			String [] p2=postingArrList.get(1).split(", ");
			long startTime = System.currentTimeMillis();
			//System.out.println("startTime:"+startTime);
			result= taatAndAlgo(p1, p2);

			for(int i=2;i<postingArrList.size() && result!=null;i++)
			{
				p1=postingArrList.get(i).split(", ");
				result= taatAndAlgo(p1, result);
			}
			//System.out.println("elapsed:"+System.currentTimeMillis());
			time = System.currentTimeMillis()-startTime;
			time/=1000;

		}
		String resultStr="";		
		String[] resultArr = new String [5];
		if(result!=null)
		{
			resultArr[0]= ""+result.length;
		}
		else
			resultArr[0]="0";
		resultArr[1]= ""+comparisonCount;
		resultArr[2]=""+time;

		if(result!=null)
		{
			result=bubbleSortArray(result);
			for(int i=0;i<result.length;i++)
				resultStr+=result[i]+", ";
			resultStr=resultStr.substring(0, resultStr.length()-2);
		}

		resultArr[4]=resultStr;

		//Optimized And

		if(incFreqPostingArrList.size()>1 && !resultArr[0].equals("0"))
		{
			if(!incFreqPostingArrList.contains("term not found"))
			{			
				String [] p1=incFreqPostingArrList.get(0).split(", ");
				result= bubbleSortArray(p1);

				for(int i=1;i<incFreqPostingArrList.size();i++)
				{
					if(result!=null)
					{
						p1=incFreqPostingArrList.get(i).split(", ");
						p1=bubbleSortArray(p1);
						result= taatOptimizedAndAlgo(p1, result);
					}
					else
						break;
				}
			}
		}
		if(result!=null)
		{
			resultArr[0]= ""+result.length;
		}
		else
			resultArr[0]="0";
		resultArr[3]=""+optimizedComparisonCount;



		return resultArr;
	}

	public static String[] bubbleSortArray(String[] p1)
	{
		//System.out.println("FUNCTION: bubbleSortArray : ");

		boolean swapped = true;
		int j = 0;
		String tmp;
		while (swapped) {
			swapped = false;
			j++;
			for (int i = 0; i < p1.length - j; i++) {
				if (Integer.parseInt(p1[i]) > Integer.parseInt(p1[i+1])) {
					tmp = p1[i];
					p1[i] = p1[i + 1];
					p1[i + 1] = tmp;
					swapped = true;
				}
			}
		}
		return p1;
	}

	public static String[] taatAndAlgo (String [] p1, String [] p2)
	{
		//System.out.println("FUNCTION: taatAndAlgo");
		ArrayList<String> resultArrList = new ArrayList<String>();
		//int i=0,j=0;
		//System.out.println("p1:"+p1.length+" p2:"+p2.length);

		for(int i=0;i<p1.length;i++)
		{
			for(int j=0;j<p2.length;j++)
			{
				comparisonCount++;
				//System.out.println("i:"+i+" j:"+j+" p1:"+p1[i]+" p2:"+p2[j]);
				if(Integer.parseInt(p1[i]) == Integer.parseInt(p2[j]))
					resultArrList.add(p1[i]);
			}
		}

		//System.out.println("Length:"+resultArrList.size());
		if(resultArrList.size()>0)
		{
			String[] result = resultArrList.toArray(new String[resultArrList.size()]);
			return result;
		}
		return null;
	}

	public static String[] taatOptimizedAndAlgo (String [] p1, String [] p2)
	{
		//System.out.println("FUNCTION: taatOptimizedAndAlgo");
		ArrayList<String> resultArrList = new ArrayList<String>();
		int i=0,j=0;
		//System.out.println("p1:"+p1.length+" p2:"+p2.length);
		while(i<p1.length && j<p2.length)
		{
			//System.out.println("i:"+i+" j:"+j+" p1:"+p1[i]+" p2:"+p2[j]);
			//System.out.println(Integer.parseInt(p1[i]) == Integer.parseInt(p2[j]));
			//System.out.println(Integer.parseInt(p1[i]) < Integer.parseInt(p2[j]));
			optimizedComparisonCount++;
			if(Integer.parseInt(p1[i]) == Integer.parseInt(p2[j]))
			{
				resultArrList.add(p1[i]);
				i++;
				j++;
			}
			else if( Integer.parseInt(p1[i]) < Integer.parseInt(p2[j]) )
				i++;
			else
				j++;
			//System.out.println("i:"+i+" j:"+j);
		}
		//System.out.println("Length:"+resultArrList.size());
		if(resultArrList.size()>0)
		{
			String[] result = resultArrList.toArray(new String[resultArrList.size()]);
			return result;
		}
		return null;
	}

	public static String[] termAtATimeQueryOr (ArrayList<String> postingArrList, ArrayList<String> incFreqPostingArrList)
	{
		String[] result = null;
		comparisonCount=0;
		time=0;
		optimizedComparisonCount=0;
		//System.out.println("Size:"+postingArrList.size());
		
		boolean postingExistFlag=false;
		for(int i=0;i<postingArrList.size();i++)
		{
			if(!postingArrList.get(i).equals("term not found"))
			{
				postingExistFlag=true;
				break;
			}
			
		}
		
		if(postingArrList.size()>1 && postingExistFlag)
		{
			long startTime = System.currentTimeMillis();
			if(!postingArrList.get(0).equalsIgnoreCase("term not found") && 
					!postingArrList.get(1).equalsIgnoreCase("term not found"))
			{
				String [] p1=postingArrList.get(0).split(", ");
				String [] p2=postingArrList.get(1).split(", ");
				result= taatOrAlgo(p1, p2);						
			}
			else if(postingArrList.get(0).equalsIgnoreCase("term not found"))
			{
				String [] p2=postingArrList.get(1).split(", ");
				result=new String [p2.length];
				for(int i=0;i<p2.length;i++)
					result[i]=p2[i];
			}
			else if(postingArrList.get(1).equalsIgnoreCase("term not found"))
			{
				String [] p1=postingArrList.get(0).split(", ");
				result=new String [p1.length];
				for(int i=0;i<p1.length;i++)
					result[i]=p1[i];
			}

			for(int i=2;i<postingArrList.size();i++)
			{
				if(!postingArrList.get(i).equalsIgnoreCase("term not found") && result!=null)
				{
					//String [] p1=postingArrList.get(0).split(", ");
					String [] p1=postingArrList.get(i).split(", ");
					result= taatOrAlgo(p1, result);
				}
				else
				{
					String [] p1=postingArrList.get(i).split(", ");
					result=new String [p1.length];
					for(int j=0;j<p1.length;j++)
						result[j]=p1[j];
				}
			}
			//System.out.println("elapsed:"+System.currentTimeMillis());
			time = System.currentTimeMillis()-startTime;
			time/=1000;
		}
		String resultStr="";
		String[] resultArr = new String [5];
		if(result!=null)
			resultArr[0]= ""+result.length;
		else
			resultArr[0]="0";
		resultArr[1]= ""+comparisonCount;
		resultArr[2]=""+time;

		if(result!=null)
		{
			result=bubbleSortArray(result);
			for(int i=0;i<result.length;i++)
				resultStr+=result[i]+", ";
			resultStr=resultStr.substring(0, resultStr.length()-2);
		}
		resultArr[4]=resultStr;

		//Optimized Or
		if(incFreqPostingArrList.size()>1 && !resultArr[0].equals("0"))
		{
			if(!incFreqPostingArrList.get(0).equalsIgnoreCase("term not found") && 
					!incFreqPostingArrList.get(1).equalsIgnoreCase("term not found"))
			{
				String [] p1=incFreqPostingArrList.get(0).split(", ");
				String [] p2=incFreqPostingArrList.get(1).split(", ");
				p1=bubbleSortArray(p1);
				p2=bubbleSortArray(p2);
				result= taatOptimizedOrAlgo(p1, p2);

			}
			else if(postingArrList.get(0).equalsIgnoreCase("term not found"))
			{
				String [] p2=postingArrList.get(1).split(", ");
				result=new String [p2.length];
				for(int i=0;i<p2.length;i++)
					result[i]=p2[i];
			}
			else if(postingArrList.get(1).equalsIgnoreCase("term not found"))
			{
				String [] p1=postingArrList.get(0).split(", ");
				result=new String [p1.length];
				for(int i=0;i<p1.length;i++)
					result[i]=p1[i];
			}

			for(int i=2;i<postingArrList.size();i++)
			{
				if(!postingArrList.get(i).equalsIgnoreCase("term not found") && result!=null)
				{
					String [] p1=postingArrList.get(i).split(", ");
					p1=bubbleSortArray(p1);
					result= taatOptimizedOrAlgo(p1, result);
				}
				else if(!postingArrList.get(i).equalsIgnoreCase("term not found"))
				{	
					String [] p1=postingArrList.get(i).split(", ");
					result=new String [p1.length];
					for(int j=0;j<p1.length;j++)
						result[j]=p1[j];
				}
			}
		}
		resultArr[3]=""+optimizedComparisonCount;
		return resultArr;
	}

	public static String[] taatOrAlgo (String [] p1, String [] p2)
	{
		//System.out.println("FUNCTION: taatOrAlgo");
		ArrayList<String> resultArrList = new ArrayList<String>();
		//int i=0,j=0;
		//System.out.println("p1:"+p1.length+" p2:"+p2.length);
		//System.out.println("p1[0]:"+p1[0]+" p2[0]:"+p2[0]);

		if(!p1[0].equalsIgnoreCase("term not found") && !p2[0].equalsIgnoreCase("term not found"))
		{
			//System.out.println("in if");
			for(int i=0;i<p1.length;i++)
				resultArrList.add(p1[i]);

			for(int i=0;i<p2.length;i++)
			{
				boolean matchFlag=false;
				for(int j=0;j<resultArrList.size();j++)
				{
					comparisonCount++;
					if(Integer.parseInt(p2[i]) == Integer.parseInt(resultArrList.get(j)))
					{
						matchFlag=true;
						break;
					}
				}
				if(!matchFlag)
					resultArrList.add(p2[i]);
			}
		}
		else if(!p1[0].equalsIgnoreCase("term not found"))
		{
			//System.out.println("in else if 1");
			for(int i=0;i<p1.length;i++)
				resultArrList.add(p1[i]);
		}
		else if(!p2[0].equalsIgnoreCase("term not found"))
		{
			//System.out.println("in else if 2");
			for(int i=0;i<p2.length;i++)
				resultArrList.add(p2[i]);
		}

		//System.out.println("Length:"+resultArrList.size());
		if(resultArrList.size()>0)
		{
			String[] result = resultArrList.toArray(new String[resultArrList.size()]);
			return result;
		}
		return null;
	}

	public static String[] taatOptimizedOrAlgo (String [] p1, String [] p2)
	{
		//System.out.println("FUNCTION: taatOptimizedOrAlgo");
		ArrayList<String> resultArrList = new ArrayList<String>();
		int i=0,j=0;
		//System.out.println("p1:"+p1.length+" p2:"+p2.length);

		if(!p1[0].equalsIgnoreCase("term not found") && !p2[0].equalsIgnoreCase("term not found"))
		{
			while(i<p1.length && j<p2.length)
			{
				//System.out.println("i:"+i+" j:"+j+" p1:"+p1[i]+" p2:"+p2[j]);
				//System.out.println(Integer.parseInt(p1[i]) == Integer.parseInt(p2[j]));
				//System.out.println(Integer.parseInt(p1[i]) < Integer.parseInt(p2[j]));
				optimizedComparisonCount++;
				if(Integer.parseInt(p1[i]) == Integer.parseInt(p2[j]))
				{
					resultArrList.add(p1[i]);
					i++;
					j++;
				}
				else if( Integer.parseInt(p1[i]) < Integer.parseInt(p2[j]) )
				{
					resultArrList.add(p1[i]);
					i++;
				}
				else
				{
					resultArrList.add(p2[j]);
					j++;
				}
				//System.out.println("i:"+i+" j:"+j);
			}
			if(i!=p1.length)
			{
				for(;i<p1.length;i++)
					resultArrList.add(p1[i]);
			}
			if(j!=p2.length)
			{
				for(;j<p2.length;j++)
					resultArrList.add(p2[j]);
			}
		}
		else if(!p2[0].equalsIgnoreCase("term not found"))
		{
			for(int k=0;k<p2.length;k++)
				resultArrList.add(p2[k]);
		}
		else if(!p1[0].equalsIgnoreCase("term not found"))
		{
			for(int k=0;k<p1.length;k++)
				resultArrList.add(p1[k]);
		}

		//System.out.println("Length:"+resultArrList.size());
		if(resultArrList.size()>0)
		{
			String[] result = resultArrList.toArray(new String[resultArrList.size()]);
			return result;
		}
		return null;
	}

	public static String[] documentAtATimeQueryAnd (ArrayList<String> postingArrList)
	{
		String[] result = null;
		comparisonCount=0;
		time=0;
		//System.out.println("Size:"+postingArrList.size());
		int numberOfTerms=postingArrList.size();

		// Check if there is data to traverse
		if(postingArrList.size()>1 && !postingArrList.contains("term not found"))
		{
			ArrayList<ArrayList<Integer>> docArrList = new ArrayList<ArrayList<Integer>>();

			// create arraylist to traverse postings
			for(int i=0;i<postingArrList.size();i++)
			{
				String tempString = postingArrList.get(i);
				String[] tempArr = tempString.split(", ");
				ArrayList<Integer> tempArrList = new ArrayList<Integer>();
				for(int j=0;j<tempArr.length;j++)
					tempArrList.add(Integer.parseInt(tempArr[j]));
				docArrList.add(tempArrList);
			}

			ArrayList<Integer> pointerArr = new ArrayList<Integer>();
			boolean traversFlag = true;;
			for(int i=0;i<postingArrList.size();i++)
			{
				pointerArr.add(0);
				//traversFlag.add(true);
			}

			// to do coding
			long startTime= System.currentTimeMillis();
			ArrayList<Integer>intersectionResultSet=new ArrayList<Integer>();
			while(traversFlag)
			{
				// find if all pointed elements are same
				boolean diffElementFlag = false;
				ArrayList<Integer> tempArrList = new ArrayList<Integer>();
				tempArrList.add(docArrList.get(0).get(pointerArr.get(0)));

				// check if elements are same or different
				for(int i=0;i<numberOfTerms-1;i++)
				{
					comparisonCount++;
					tempArrList.add(docArrList.get(i+1).get(pointerArr.get(i+1)));
					//System.out.println(docArrList.get(i).get(pointerArr.get(i)).getClass());
					//System.out.println(docArrList.get(i+1).get(pointerArr.get(i+1)).getClass());
					//System.out.println(docArrList.get(i).get(pointerArr.get(i)).intValue() != docArrList.get(i+1).get(pointerArr.get(i+1)).intValue());
					if(docArrList.get(i).get(pointerArr.get(i)).intValue() != docArrList.get(i+1).get(pointerArr.get(i+1)).intValue())
					{
						diffElementFlag=true;
						//break;
					}
				}
				// find highest and update pointerArr
				if(diffElementFlag)
				{
					int maxElement=0;
					for(int i=0;i<numberOfTerms-1;i++)
					{
						comparisonCount++;
						if(tempArrList.get(i)>tempArrList.get(i+1))
						{
							maxElement=tempArrList.get(i);
						}
						else
							maxElement=tempArrList.get(i+1);
					}
					//int maxElement=tempArrList.get(maxIndex);
					for(int i=0;i<numberOfTerms && traversFlag;i++)
					{
						for(int j=pointerArr.get(i);j<docArrList.get(i).size();j++)
						{
							comparisonCount++;
							//System.out.println(maxElement <= docArrList.get(i).get(j));
							if(maxElement <= docArrList.get(i).get(j).intValue())
							{
								maxElement=docArrList.get(i).get(j);

								if(j < docArrList.get(i).size())
								{
									pointerArr.set(i, j);
									break;
								}
								else
								{
									traversFlag=false;
									break;
								}
							}
						}
					}	
				}
				else
				{
					intersectionResultSet.add(tempArrList.get(0));
					for(int j=0;j<numberOfTerms;j++)
					{
						if(pointerArr.get(j)+1 < docArrList.get(j).size())
							pointerArr.set(j, pointerArr.get(j)+1);
						else
						{
							traversFlag=false;
							break;
						}
					}
				}
			}
			time = System.currentTimeMillis()-startTime;
			time/= 1000;
			if(intersectionResultSet.size()>0)
			{
				result = new String [intersectionResultSet.size()]; 

				for(int i=0;i<intersectionResultSet.size();i++)
					result[i]=""+intersectionResultSet.get(i);
			}
		}

		String resultStr="";
		String[] resultArr = new String [4];
		if(result!=null)
			resultArr[0]= ""+result.length;
		else
			resultArr[0]="0";
		resultArr[1]= ""+comparisonCount;
		resultArr[2]=""+time;

		if(result!=null)
		{
			result=bubbleSortArray(result);
			for(int i=0;i<result.length;i++)
				resultStr+=result[i]+", ";
			resultStr=resultStr.substring(0, resultStr.length()-2);
		}

		resultArr[3]=resultStr;
		return resultArr;
	}


	public static String[] documentAtATimeQueryOr (ArrayList<String> postingArrList)
	{
		String[] result = null;
		comparisonCount=0;
		time=0;
		//System.out.println("Size:"+postingArrList.size());
		//int numberOfTerms=postingArrList.size();

		if(postingArrList.size()>1 )
		{
			ArrayList<ArrayList<Integer>> docArrList = new ArrayList<ArrayList<Integer>>();
			for(int i=0;i<postingArrList.size();i++)
			{
				String tempString = postingArrList.get(i);
				String[] tempArr = tempString.split(", ");
				ArrayList<Integer> tempArrList = new ArrayList<Integer>();
				for(int j=0;j<tempArr.length;j++)
				{	
					if(tempArr[j]!="term not found")
						tempArrList.add(Integer.parseInt(tempArr[j]));
				}
				if(!tempArrList.isEmpty())
					docArrList.add(tempArrList);
			}
			//System.out.println("splitted");
			ArrayList<Integer> pointerArr = new ArrayList<Integer>();
			ArrayList<Boolean> traversFlag = new ArrayList<Boolean>();
			for(int i=0;i<docArrList.size();i++)
			{
				pointerArr.add(0);
				traversFlag.add(true);
			}

			// to do coding
			long startTime= System.currentTimeMillis();
			ArrayList<Integer>unionResultSet=new ArrayList<Integer>();
			while(!docArrList.isEmpty())
			{
				// find if all pointed elements are same
				boolean diffElementFlag = false;
				ArrayList<Integer> tempArrList = new ArrayList<Integer>();
				tempArrList.add(docArrList.get(0).get(pointerArr.get(0)));


				for(int i=0;i<docArrList.size()-1;i++)
				{
					comparisonCount++;
					//System.out.println(docArrList.get(i).get(pointerArr.get(i)).getClass());
					//System.out.println(docArrList.get(i+1).get(pointerArr.get(i+1)).getClass());
					//System.out.println(docArrList.get(i).get(pointerArr.get(i)).intValue() != docArrList.get(i+1).get(pointerArr.get(i+1)).intValue());
					tempArrList.add(docArrList.get(i+1).get(pointerArr.get(i+1)));
					if(docArrList.get(i).get(pointerArr.get(i)).intValue() != docArrList.get(i+1).get(pointerArr.get(i+1)).intValue())
					{
						diffElementFlag=true;
						//break;
					}
				}
				// find lowest and update pointerArr
				if(diffElementFlag)
				{
					int minIndex=0;
					for(int i=0;i<tempArrList.size()-1;i++)
					{
						comparisonCount++;
						if(tempArrList.get(minIndex)>tempArrList.get(i+1))
							minIndex=i+1;
					}
					unionResultSet.add(tempArrList.get(minIndex));

					if(pointerArr.get(minIndex)+1 < docArrList.get(minIndex).size())
						pointerArr.set(minIndex, pointerArr.get(minIndex)+1);
					else
					{
						traversFlag.set(minIndex, false);
						//break;
					}
				}
				else
				{
					unionResultSet.add(tempArrList.get(0));
					for(int j=0;j<tempArrList.size();j++)
					{
						//comparisonCount++;
						if(pointerArr.get(j) < docArrList.get(j).size()-1)
							pointerArr.set(j, pointerArr.get(j)+1);
						else
						{
							traversFlag.set(j, false);
							//break;
						}
					}
				}
				// Check if any elements of all posting list has been traversed completely, if yes remove it from the working list

				if(traversFlag.contains(false))
				{
					int loopCount=traversFlag.size();
					for(int i=0;i<loopCount;i++)
					{
						if(!traversFlag.get(i))
						{
							tempArrList.remove(i);
							pointerArr.remove(i);
							docArrList.remove(i);
							traversFlag.remove(i);
						}
					}
				}
			}
			time = System.currentTimeMillis()-startTime;
			time/= 1000;
			if(unionResultSet.size()>0)
			{
				result = new String [unionResultSet.size()]; 

				for(int i=0;i<unionResultSet.size();i++)
					result[i]=""+unionResultSet.get(i);
			}
		}

		String resultStr="";
		String[] resultArr = new String [4];
		if(result!=null)
			resultArr[0]= ""+result.length;
		else
			resultArr[0]="0";
		resultArr[1]= ""+comparisonCount;
		resultArr[2]=""+time;

		if(result!=null)
		{
			result=bubbleSortArray(result);
			for(int i=0;i<result.length;i++)
				resultStr+=result[i]+", ";
			resultStr=resultStr.substring(0, resultStr.length()-2);
		}

		resultArr[3]=resultStr;
		return resultArr;
	}
}
