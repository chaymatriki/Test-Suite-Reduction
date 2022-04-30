import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Matrice {
	private Vector vTestCases;
	private Vector vFeatures;
	private Vector vFDC;
	private Vector vAET;
	private Vector vEF;
	private int lignes;
	private int colonnes;
	private int[][] tab;
	
	public Vector getTestCases() {
		return vTestCases;
	}
	public Vector getvFDC() {
		return vFDC;
	}
	public Vector getvAET() {
		return vAET;
	}
	public Vector getvEF() {
		return vEF;
	}
	public int getLignes() {
		return lignes;
	}
	public int getColonnes() {
		return colonnes;
	}
	public int[][] getTab() {
		return tab;
	}
	
	
	public Matrice() {
		vTestCases = new Vector();
   	 vFeatures = new Vector();
   	 vFDC = new Vector();
   	 vAET = new Vector();
   	 vEF = new Vector();
   	 this.lignes=0;
		this.colonnes=0;
	}
	
	public  void matrix(String path) {
		Vector vTestCases = new Vector();
    	Vector vFeatures = new Vector();
		try
		{
			File xmlDoc = new File(path);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlDoc);
			NodeList tabFeatures = doc.getElementsByTagName("Class");
			
			for (int i=0; i<tabFeatures.getLength(); i++)
			{
				Node feature = tabFeatures.item(i);
				if (feature.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) feature;
					//vFeatures.add(element.getElementsByTagName("Name").item(0).getTextContent());
					vFeatures.add("Class " + i);
					
				}
			}
			NodeList testCases = doc.getElementsByTagName("test");
			for (int j=0; j<testCases.getLength(); j++)
			{
				Node testcase = testCases.item(j);
				String str = testcase.getTextContent();
				str = str.replace("\n", "").replace("\r", "");
				if (! vTestCases.contains(str))
				{
					
						vTestCases.add(str);
						//System.out.println(j + ": " + str);
				}
		}

			
			System.out.println("Features suite size : " + vFeatures.size());
			System.out.println("Test cases suite size : " + vTestCases.size());
            
            int[][] tab = new int[vFeatures.size()][vTestCases.size()];
            for (int i=0;i<vFeatures.size();i++)
            	for (int j=0;j<vTestCases.size();j++)
            		tab[i][j]=0;
            
            int k=0;
            System.out.println("nb lignes : " + vFeatures.size() + " nb colonnes : " + vTestCases.size());
            
            
            
            NodeList tabFeature = doc.getElementsByTagName("Class");
            for (int i=0; i<tabFeature.getLength(); i++)
			{
				//System.out.println("FEATURE "+ (i+1));
				
				Node feature = tabFeature.item(i);
				if (feature.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) feature;
					NodeList testCasesFeature = element.getElementsByTagName("test");
					for (int j=0; j<testCasesFeature.getLength(); j++)
					{
						Node testcasefeature = testCasesFeature.item(j);
						String str = testcasefeature.getTextContent();
						
							str = str.replace("\n", "").replace("\r", "");
							int index = vTestCases.indexOf(str);
		            		tab[k][index] = 1;
							//System.out.println("test case : " + testcasefeature.getTextContent());
						
					}
					k++;
				}
			}
            
			FileWriter fw = new FileWriter("C:\\Users\\uber\\Desktop\\PFE\\matrice_testReport.txt");
        	PrintWriter pw = new PrintWriter(fw);
        	
            	for (int i=0;i<vFeatures.size();i++)
            	{
            		String str = "";
                	for (int j=0;j<vTestCases.size();j++)
                	{
                		str += tab[i][j];
                	}
                	pw.println(str);
            	}
            	pw.close();
            	
            	FileWriter fw2 = new FileWriter("C:\\Users\\uber\\Desktop\\PFE\\testSuite.txt");
            	PrintWriter pw2 = new PrintWriter(fw2);
                	for (int i=0;i<vTestCases.size();i++)
                	{
                		String str = vTestCases.get(i).toString();
                		if(!str.equals(""))
                		{
                			pw2.println(str);
                        	//System.out.println(i + " : " + str);
                		}
                	}
                	pw2.close();
                	
                	
            	
            	this.colonnes=vTestCases.size();
        		this.lignes=vFeatures.size();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}

