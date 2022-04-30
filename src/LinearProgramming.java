import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class LinearProgramming {
	private static final boolean MAXIMIZE = true;
	private static final boolean MINIMIZE = false;
    private static final double EPSILON = 1.0E-10;
    private boolean maximizeOrMinimize;
    private int[][] a;   // tableaux
    private int m;          // number of constraints
    private int n;          // number of original variables
    
    private int[] basis;    // basis[i] = basic variable corresponding to row i
                            // only needed to print out solution, not book

    public LinearProgramming(int[][] A, int[] b, int[] c) {
    	this.maximizeOrMinimize = maximizeOrMinimize;
        m = b.length;
        n = c.length;
        for (int i = 0; i < m; i++)
            if (!(b[i] >= 0)) throw new IllegalArgumentException("RHS must be nonnegative");

        a = new int[m+1][n+m+1];      
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                a[i][j] = A[i][j];         
        
        
        for (int i = 0; i < m; i++)
            a[i][n+i] = 1;
        
        
        for (int j = 0; j < n; j++)
            a[m][j] = c[j];
        
        
        for (int i = 0; i < m; i++)
            a[i][m+n] = b[i];

        basis = new int[m];
        for (int i = 0; i < m; i++)
            basis[i] = n + i;

        solve();

        // check optimality conditions
        //assert check(A, b, c);
        if (check(A, b, c)) {
        	System.out.println("OPTIMAL");
        }
    
    }
    
    static void transpose(int A[][], int B[][],int N,int M) 
    { 
        int i, j; 
        for (i = 0; i < N; i++) 
            for (j = 0; j < M; j++) 
                B[i][j] = A[j][i]; 
    }  
    // run simplex algorithm starting from initial BFS basic feasible solution 
    private void solve() {
    	while(true) {
        	//show();
        	int q = 0;
        		// find entering column q
                q = bland();
                  //q = dantzig();
        	if (q == -1) break;
            // find leaving row p
            int p = minRatioRule(q);
            if (p == -1) throw new ArithmeticException("Linear program is unbounded");

            // pivot
            pivot(p, q);

            // update basis
            basis[p] = q;
        }
    }
    
   

    // lowest index of a non-basic column with a positive cost
    private int bland() {
        for (int j = 0; j < m+n; j++)
            if (a[m][j] > 0) return j;
        return -1;  // optimal
    }
    

   // index of a non-basic column with most positive cost
    private int dantzig() {
        int q = 0;
        for (int j = 1; j < m+n; j++)
            if (a[m][j] > a[m][q])
            	q = j;

        if (a[m][q] <= 0) return -1;  // optimal
        else return q;
    }
    

    // find row p using min ratio rule (-1 if no such row)
    // (smallest such index if there is a tie)
    private int minRatioRule(int q) {
        int p = -1;
        for (int i = 0; i < m; i++) {
            // if (a[i][q] <= 0) continue;
            if (a[i][q] <= EPSILON) continue;
            else if (p == -1) p = i;
            else if ((a[i][m+n] / a[i][q]) < (a[p][m+n] / a[p][q])) p = i;
        }
        return p;
    }

    // pivot on entry (p, q) using Gauss-Jordan elimination
    private void pivot(int p, int q) {

        // everything but row p and column q
        for (int i = 0; i <= m; i++)
            for (int j = 0; j <= m+n; j++)
                if (i != p && j != q) a[i][j] -= a[p][j] * a[i][q] / a[p][q];

        // zero out column q
        for (int i = 0; i <= m; i++)
            if (i != p) a[i][q] = 0;

        // scale row p
        for (int j = 0; j <= m+n; j++)
            if (j != q) a[p][j] /= a[p][q];
        a[p][q] = 1;
    }

    public int value() {
        return -a[m][m+n];
    }

    public int[] primal() {
        int[] x = new int[n];
        for (int i = 0; i < m; i++)
            if (basis[i] < n) 
            	x[basis[i]] = a[i][m+n];
        return x;
    }

    public int[] dual() {
        int[] y = new int[m];
        for (int i = 0; i < m; i++)
        {
        	y[i] = -a[m][n+i];
        }
            
        return y;
    }


    // is the solution primal feasible?
    private boolean isPrimalFeasible(int[][] A, int[] b) {
        int[] x = primal();

        // check that x >= 0
        for (int j = 0; j < x.length; j++) {
            if (x[j] < 0.0) {
                System.out.println("x[" + j + "] = " + x[j] + " is negative");
                return false;
            }
        }

        // check that Ax <= b
        for (int i = 0; i < m; i++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                sum += A[i][j] * x[j];
            }
            if (sum > b[i] + EPSILON) {
            	System.out.println("not primal feasible");
            	System.out.println("b[" + i + "] = " + b[i] + ", sum = " + sum);
                return false;
            }
        }
        return true;
    }

    // is the solution dual feasible?
    private boolean isDualFeasible(int[][] A, int[] c) {
        int[] y = dual();

        // check that y >= 0
        for (int i = 0; i < y.length; i++) {
            if (y[i] < 0) {
            	System.out.println("y[" + i + "] = " + y[i] + " is negative");
                return false;
            }
        }

        // check that yA >= c
        for (int j = 0; j < n; j++) {
            int sum = 0;
            for (int i = 0; i < m; i++) {
                sum += A[i][j] * y[i];
            }
            if (sum < c[j] - EPSILON) {
            	System.out.println("not dual feasible");
            	System.out.println("c[" + j + "] = " + c[j] + ", sum = " + sum);
                return false;
            }
        }
        return true;
    }

    // check that optimal value = cx = yb
    private boolean isOptimal(int[] b, int[] c) {
        int[] x = primal();
        int[] y = dual();
        int value = value();

        // check that value = cx = yb
        int value1 = 0;
        for (int j = 0; j < x.length; j++)
            value1 += c[j] * x[j];
        int value2 = 0;
        for (int i = 0; i < y.length; i++)
            value2 += y[i] * b[i];
        if (Math.abs(value - value1) > EPSILON || Math.abs(value - value2) > EPSILON) {
        	System.out.println("Not optimal : value = " + value + ", cx = " + value1 + ", yb = " + value2);
            return false;
        }

        return true;
    }

    private boolean check(int[][]A, int[] b, int[] c) {
        return isPrimalFeasible(A, b) && isDualFeasible(A, c) && isOptimal(b, c);
    }

    private static void test(boolean minOrMax, int[][] A, int[] b, int[] c) {
        LinearProgramming lp;
        try {
        	if (minOrMax ) {
        		lp = new LinearProgramming(A, b, c);
        	}
        	else {
        		int[][] D = new int[c.length][b.length];
        		for (int i = 0; i < c.length; i++) 
                    for (int j = 0; j < b.length; j++) 
                        D[i][j] = A[j][i]; 
        		lp = new LinearProgramming(D, c, b);
        	}
        }
        catch (ArithmeticException e) {
            System.out.println(e);
            return;
        }

        //System.out.println("value = " + lp.value() +" // test set size");
        int[] x = lp.primal();
        System.out.println("Solution du primal : ");
        for (int i = 0; i < x.length; i++)
        	System.out.println("x[" + (i+1) + "] = " + x[i]);
        int[] y = lp.dual();
        int[] indices = new int[y.length];
        int k=0;
        System.out.println();
        System.out.println("Solution du dual : ");
        for (int j = 0; j < y.length; j++)
        {
        	System.out.println("y[" + (j+1) + "] = " + y[j]);
        	if ( y[j] == 1 )
        	{
        		indices[k]=j;
        		k++;
        	}
        }
        try
        {
        	
        	//normalement un vecteur pas un fichier texte, à voir plus tard
        	FileReader fr2 = new FileReader("C:\\Users\\uber\\Desktop\\PFE\\testSuite.txt");
    		BufferedReader br2 = new BufferedReader(fr2);
    		//FileWriter fw = new FileWriter("/Users/Chaima/Desktop/FINAL/testReport_Minimized.txt");
        	//PrintWriter pw = new PrintWriter(fw);
    		
    		
    		
    		
    		
    		
    		File xmlFile1 = new File("C:\\Users\\uber\\Downloads\\vraptor4-master\\vraptor4-master\\pom.xml");
	        DocumentBuilderFactory dbFactory1 = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder1;
	            dBuilder1 = dbFactory1.newDocumentBuilder();
	            org.w3c.dom.Document doc1 = dBuilder1.parse(xmlFile1);
	            doc1.getDocumentElement().normalize();
	            NodeList node = doc1.getElementsByTagName("groupId");
	            for (int i=0; i<node.getLength(); i++)
				{
					Node groupid = node.item(i);
					if (groupid.getTextContent().equals("org.pitest"))
					{
						Element element = (Element) groupid;
						Element config = doc1.createElement("configuration");
						element.getParentNode().appendChild(config);
						Element targetTests = doc1.createElement("targetTests");
						config.appendChild(targetTests);
						
						
					}
				}
    		
        	
        	String str2 = "";
    		int p=0;int s=0;
    		System.out.println("List of test from text file");
    		Element targetTests = (Element) doc1.getElementsByTagName("targetTests").item(0);
        	while ((str2 = br2.readLine()) != null )
    		{
        		//System.out.println(p + " : " + str2);
        			if(indices[s] == p)
        			{
        				//System.out.println(indices[s] + " : " + str2);
        				//pw.println(str2);
        				Element element = doc1.createElement("param");
        				//element.setTextContent(indices[s] + " : " + str2);
        				str2 = str2.substring(0, str2.lastIndexOf(".")).concat("*");
        				element.setTextContent(str2);
        				targetTests.appendChild(element);
        				p++;
        				s++;
        			}
        			else
        				p++;
        	}
        	File f = new File("C:\\Users\\uber\\Downloads\\vraptor4-master\\vraptor4-master\\pom.xml");
        	Result result = new StreamResult(f);
        	DOMSource source = new DOMSource(doc1);
        	TransformerFactory tf = TransformerFactory.newInstance();
        	Transformer t = tf.newTransformer(new StreamSource("sample.xsl"));
        	t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
        	t.setOutputProperty(OutputKeys.INDENT,"yes");
        	t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        	t.transform(source,result);
        //br2.close();
        //pw.close();
        }
            	catch(Exception e)
            	{
            		e.printStackTrace();
            	}  
    }
    
    public static void testLP(int p, int q) {
    	boolean MaxMin = MINIMIZE;
    	int[][] tab = new int[p][q];
    	int[] b = new int[p];
    	for (int i=0;i<p;i++)
    	{
    		b[i] = 1;
    	}
    	int[] c = new int[q];
    	for (int i=0;i<q;i++)
    	{
    		c[i] = 1;
    	}
    	try
    	{
    		FileReader fr = new FileReader("C:\\Users\\uber\\Desktop\\PFE\\matrice_testReport.txt");
    		BufferedReader br = new BufferedReader(fr);
    		String str = "";
    		int i=0;
    		while ((str = br.readLine()) != null )
    		{
    				for (int j=0;j<q;j++)
    				{
    					tab[i][j]=Integer.parseInt(String.valueOf(str.charAt(j)));
    				}
    				i++;
    		}
    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
        test(MaxMin,tab, b, c);
    }
    
    public static void main(String[] args) {
    	String path = "C:\\Users\\uber\\Downloads\\vraptor4-master\\vraptor4-master";
    	
    	
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element racine = doc.createElement("Root");
		doc.appendChild(racine);
    	File f = new File("C:\\Users\\uber\\Desktop\\PFE\\testReport.xml");
    	Result result = new StreamResult(f);
    	DOMSource source = new DOMSource(doc);
    	TransformerFactory tf = TransformerFactory.newInstance();
    	Transformer t = tf.newTransformer();
    	t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
    	t.setOutputProperty(OutputKeys.INDENT,"yes");
    	t.transform(source,result);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String projectPath = "C:\\Users\\uber\\Downloads\\ChaimaTRIKI\\ChaimaTRIKI\\try3\\";
		try {
			Process pr= Runtime.getRuntime().exec("cmd /c \"cd " + projectPath + "&& mkdir TestsXml");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Test obj = new Test();
		try {
    		String s;
	    	//String cmd1 = "cmd /c \"cd " + path;
	    	//String  cmd2 = "mvn test org.pitest:pitest-maven:mutationCoverage ";
	    	//String cmd3 = "cd \" ";   
	    	
	    	
	    	PrintWriter out = new PrintWriter("testing.bat");
	    	out.println("@cd " + path);
	    	out.println("@mvn test org.pitest:pitest-maven:mutationCoverage");
	    	out.println("@exit");
	    	out.close();
	    	Process rt = Runtime.getRuntime().exec("cmd /c start " +"testing.bat");
	    	System.out.println(rt.getOutputStream());
		    BufferedReader stdInput = new BufferedReader(new 
		            InputStreamReader(rt.getInputStream()));
		    while((s=stdInput.readLine())!=null){
		     System.out.println(s); }
	    	rt.waitFor();
	    	System.out.println("Continue");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*Vector tab;
		tab = Test.pitreportsPath("C:\\Users\\Chaima\\Desktop\\auto-master\\auto-master");
		 System.out.println("Dans Main : ");
			Iterator<String> it = tab.iterator();
			while(it.hasNext())
			{
				//System.out.println("PATH : " + it.next());
				String latestReport = obj.latestReport(new File(it.next()));
				System.out.println(latestReport);
				obj.listFolder(new File(latestReport));
			}*/
			
			
		String latestReport = obj.latestReport(new File(Test.pitreportsPath(path)));
		System.out.println(" pit-rep :" + latestReport);
		obj.listFolder(new File(latestReport));
		
		
		
		
		try {
		File xmlFile = new File("C:\\Users\\uber\\Desktop\\PFE\\testReport.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // parse xml file and load into document
            org.w3c.dom.Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            TransformerFactory transformerFactory2 = TransformerFactory.newInstance();
	        Transformer transformer2 = transformerFactory2.newTransformer(new StreamSource("sample2.xsl"));
	        DOMSource source2 = new DOMSource(doc);
	        StreamResult result2 = new StreamResult(new File("C:\\Users\\uber\\Desktop\\PFE\\testReport.xml"));
	        transformer2.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer2.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	        transformer2.transform(source2, result2);
	        //System.out.println("XML file updated successfully");
	        
	        
            } catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		Matrice matrice = new Matrice();
        matrice.matrix("C:\\Users\\uber\\Desktop\\PFE\\testReport.xml");
        System.out.println("Nombre de lignes : " + matrice.getLignes() + "Nombre de colonnes : " + matrice.getColonnes());
		
    	testLP(matrice.getLignes(),matrice.getColonnes());
    	try {
    		System.out.println("Hello");
    		String s; 
	    	
	    	
	    	PrintWriter out = new PrintWriter("testing.bat");
	    	out.println("@cd " + path);
	    	out.println("@mvn test org.pitest:pitest-maven:mutationCoverage");
	    	out.println("@exit");
	    	out.close();
	    	Process rt = Runtime.getRuntime().exec("cmd /c start " +"testing.bat");
	    	System.out.println(rt.getOutputStream());
		    BufferedReader stdInput = new BufferedReader(new 
		            InputStreamReader(rt.getInputStream()));
		    while((s=stdInput.readLine())!=null){
		     System.out.println(s); }
	    	rt.waitFor();
	    	System.out.println("Continue");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}