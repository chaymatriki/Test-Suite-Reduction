import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
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

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Test {
	public String fileChooser()
	{  
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		String path = null ; 
		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			path = selectedFile.getAbsolutePath() ; 
			System.out.println(selectedFile.getAbsolutePath());
		}
		return path  ; 
	}
	void listFolder(File dir)
	{
		File [] subDirs = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		
		File [] files = dir.listFiles();
		for (File file : files)
		{
			if((!file.getName().equals("index.html"))&&(!file.getName().equals("style.css"))&&(!file.isDirectory()))
			{
				Treatment rt = new Treatment(file.getAbsolutePath(),file.getName()) ;  
				try {
					rt.ecrireFichierXml();
				} catch (DOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				}
			}
		}
		for (File folder : subDirs)
		{
			//System.out.println(" Directory of :" + folder.getName());
			listFolder(folder);
		}
	}
	
	String latestReport(File dir)
	{
		System.out.println(dir.getPath());
		System.out.println(dir.getAbsolutePath());
		try {
			System.out.println(dir.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File [] subDirs = dir.listFiles();
		String last = subDirs[subDirs.length-1].getAbsolutePath();
		return last;
	}
	
	String fixPath (String path) {
		String path2="";
		
		return path2;
	}
	
	static String pitreportsPath(String path)
	{
		//Vector tab = new Vector();
		 String s="";
		 String str="";
		try
		{
			Process p= Runtime.getRuntime().exec("cmd /c \"cd " + path + "&& dir pit-reports /AD /B /S\"");
			    System.out.println(p.getOutputStream());
			    BufferedReader stdInput = new BufferedReader(new 
			            InputStreamReader(p.getInputStream()));
			    //int i=0;
			    while((s=stdInput.readLine())!=null){
			     //System.out.println("Path is : " + s.replace("\\", "\\\\"));
			     str = s.replace("\\", "\\\\");
			     //tab.add(str);
			     //i++;
			    }
			    
			   
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	
	/*public static void main(String[] args) throws IOException, DOMException, TransformerException {
	  
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
	 
	 
		String path = "C:\\Users\\uber\\Downloads\\vraptor4-master\\vraptor4-master";
		String projectPath = "C:\\Users\\uber\\Downloads\\ChaimaTRIKI\\ChaimaTRIKI\\try3\\";
		Process pr= Runtime.getRuntime().exec("cmd /c \"cd " + projectPath + "&& mkdir VRaptorTestsXml");
		Test obj = new Test();
		try
		{
			//Process rt = Runtime.getRuntime().exec("cmd /c \"cd " + path + " && mvn test org.pitest:pitest-maven:mutationCoverage && pause && exit\"");
			Process rt = Runtime.getRuntime().exec("cmd /c \"cd " + path + " && mvn test org.pitest:pitest-maven:mutationCoverage \"");
			rt.waitFor();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		String latestReport = obj.latestReport(new File(pitreportsPath(path)));
		System.out.println("last rep : " + latestReport);
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
	        
	        
	        Matrice matrice = new Matrice();
	        matrice.matrix("C:\\Users\\uber\\Desktop\\PFE\\testReport.xml");
	        System.out.println("Nombre de lignes : " + matrice.getLignes() + "Nombre de colonnes : " + matrice.getColonnes());
	        
	        
            } catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
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
			}
		
	}*/

}
