import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Treatment implements Serializable{//début de la classe 
	//attributs 
	private String title ; //Class' title
	private String  path  ;
	private String  filename  ;
	private ArrayList<String> tests  ; 
	private ArrayList<String> time ; 
	
	
	/**
	 * 
	 */
	public Treatment(String path,String filename) {
		super();
		this.path = path;
		this.filename = filename;
	}
		
		//la classe ci-dessous permet d'extraire les tests examinés 
		public ArrayList<String> extractionTests()throws IOException
		{
			ArrayList<String>testsCollec = new ArrayList<String>(); 
			String[] tests = null; 
			ArrayList<String>testsVraptor = new ArrayList<String>();  
			//récupération du fichier HTML
			File input = new File(path) ; 
			/*parser le fichier html en utilisant la méthode parse 
			 * Jsoup.parse(file , charset ) 
			 */
		
			Document doc = Jsoup.parse(input, "UTF-8") ; 
			org.jsoup.nodes.Element titleBalise =doc.select("h1").first() ; 
			String title = titleBalise.wholeText() ; 
			/*Selection de la dernier balise <ul>
			 * qui contient les tests examinés 
			 * c'est la squelette utlisée pour tous les fichiers du rapport PITEST
			 */
			org.jsoup.nodes.Element part =doc.select("ul").last(); 
			tests = part.wholeText().split("\\(|\\)|' '");
	    	for(String elem : tests)
	    	{
	    		testsCollec.add(elem); 
	    	}
	  	
	    	for(String elem : testsCollec)
	    	{
	    		if(elem == " " ||elem=="ms")
	    		{
	    			testsCollec.remove(elem);
	    		}
	    		//System.out.println(elem);
	    	}

			return testsCollec ; 
		}

			public void ecrireFichierXml() throws IOException, DOMException, TransformerException
			{
                
				tests = extractionTests();
				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
				try
				{
					
					File xmlFile = new File("C:\\Users\\uber\\Desktop\\PFE\\testReport.xml");
			        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			        DocumentBuilder dBuilder;
			            dBuilder = dbFactory.newDocumentBuilder();

			            // parse xml file and load into document
			            org.w3c.dom.Document doc = dBuilder.parse(xmlFile);
			            doc.getDocumentElement().normalize();
			            Node root = doc.getFirstChild();
			            Element class2 = doc.createElement("Class");
						root.appendChild(class2);
			            
					
				final DocumentBuilder builder = factory.newDocumentBuilder(); 
				final org.w3c.dom.Document document =builder.newDocument() ; 
				final Element racine = (Element) document.createElement("GeneralReport"); 
				document.appendChild((org.w3c.dom.Node) racine) ;
	            
				final Element classTested =(Element) document.createElement("Class"); 
				racine.appendChild(classTested);
				//ajout des tests dans le fichier 
				for (int i=0;i<tests.size();i+=4) {
					final Element test = document.createElement("test") ;
					classTested.appendChild(test); 
					//test.appendChild(document.createTextNode(tests.get(i)));
					test.appendChild(document.createTextNode(tests.get(i)));
					
					
					Element test2 = doc.createElement("test");
					test2.appendChild(doc.createTextNode(tests.get(i)));
					class2.appendChild(test2);
				}
				
				//doc.appendChild(class2);
				
				
				TransformerFactory transformerFactory2 = TransformerFactory.newInstance();
		        Transformer transformer2 = transformerFactory2.newTransformer(new StreamSource("sample.xsl"));
		        DOMSource source2 = new DOMSource(doc);
		        StreamResult result2 = new StreamResult(new File("C:\\Users\\uber\\Desktop\\PFE\\testReport.xml"));
		        //transformer2.setOutputProperty(OutputKeys.VERSION,"1.0");
				//transformer2.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
				//transformer2.setOutputProperty(OutputKeys.STANDALONE,"yes");
		        transformer2.setOutputProperty(OutputKeys.INDENT, "yes");
		        transformer2.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		        transformer2.transform(source2, result2);
				
				/*
				 * pour pouvoir afficher le document xml nous allons utiliser les objets suivants : 
				 */
				final TransformerFactory transformerFactory = TransformerFactory.newInstance(); 
				final Transformer transformer = transformerFactory.newTransformer(); 
				final DOMSource source = new DOMSource((org.w3c.dom.Node) document);  
				final StreamResult sortie = new StreamResult(new File("C:\\Users\\uber\\Downloads\\ChaimaTRIKI\\ChaimaTRIKI\\try3\\TestsXml\\"+filename+".xml"));
				
				//transformer.setOutputProperty(OutputKeys.VERSION,"1.0");
				//transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
				//transformer.setOutputProperty(OutputKeys.STANDALONE,"yes");
				transformer.setOutputProperty(OutputKeys.INDENT,"yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				transformer.transform(source, sortie);
				}catch(final ParserConfigurationException e )
				{
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
							
			}

}

