package parserUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import exceptions.UnableToCreateFileException;

public class XmlBuilder {
	
	private Document doc;
	private File file;
	private TransformerFactory transformerFactory;
	private Transformer transformer;
	private DOMSource source;
	private StreamResult result;
	private Element nodeElement;
	private Element linksElement;

	public XmlBuilder(String fileName){
		this.file = new File(fileName);
	}
	
	public XmlBuilder setXmlStructure(){
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		    
		    // root elements
		    doc = docBuilder.newDocument();
		    Element rootElement = doc.createElement("Graph");
		    nodeElement = doc.createElement("Nodes");
		    linksElement = doc.createElement("Links");
		    
		    doc.appendChild(rootElement);
		    rootElement.appendChild(nodeElement);
		    
		    // append the number of links depending on each diagram
		    rootElement.appendChild(linksElement);
		    
		    transformerFactory = TransformerFactory.newInstance();
		    transformer = transformerFactory.newTransformer();
		    source = new DOMSource(doc);

		    // Output to console for testing
		    //StreamResult result = new StreamResult(System.out);
		    
		    return this;
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	public XmlBuilder appendNode(int idValue, String shape, String content) {
		Element node = doc.createElement("Node");
		node.setAttribute("tipo", shape);
		node.setAttribute("nombre", content);
		node.setAttribute("id", String.valueOf(idValue));
		
		if(doc.getElementsByTagName("Node").getLength() > 0 ) {
			Element lastNode = (Element)doc.getElementsByTagName("Node").item(doc.getElementsByTagName("Node").getLength()-1);
			
			lastNode.appendChild(node);
		} else {
			nodeElement.appendChild(node);
		}
		
		return this;
	}
/*
	public XmlBuilder setNodeShape(int idValue, String shape) {
		
		node.setAttribute("tipo", shape);
		return this;
	}
	
	public XmlBuilder setNodeContent(int idValue, String content) {
		Element node = (Element)doc.getElementsByTagName("Node").item(idValue);
		node.setAttribute("nombre", content);
		return this;
	}
*/
	public XmlBuilder appendLink(LinkedList<Integer> source, int destId, String tagLink) {
		if(source == null) return this;

		for (int i = 0; i < source.size(); i++){
			Element childLink = doc.createElement("Link");
			childLink.setAttribute("origin", String.valueOf(source.get(i)));
			childLink.setAttribute("target", String.valueOf(destId));

			if (tagLink == "decisión") {
				if(source.indexOf(source.get(i)) == 0) {
					childLink.setAttribute("tagLink", "true");

				} else if(source.indexOf(source.get(i)) == 1) {
					childLink.setAttribute("tagLink", "false");
				}

			}


			if(doc.getElementsByTagName("Link").getLength() > 0) {
				Element lastLink = (Element)doc.getElementsByTagName("Link").item(doc.getElementsByTagName("Link").getLength()-1);

				lastLink.appendChild(childLink);
			} else {
				linksElement.appendChild(childLink);
			}
		}

		return this;
	}
/*
	public XmlBuilder setLinkSourceId(int idValue, int sourceId) {
		Element node = (Element)doc.getElementsByTagName("Link").item(idValue);
		node.setAttribute("origin", String.valueOf(sourceId));
		return this;
	}

	public XmlBuilder setLinkDestId(int idValue, int destId) {
		Element node = (Element)doc.getElementsByTagName("Link").item(idValue);
		node.setAttribute("target", String.valueOf(destId));
		return this;
	}
	
	public XmlBuilder setTagLink(int idValue, String value) {
		Element node = (Element)doc.getElementsByTagName("Link").item(idValue);
		node.setAttribute("tagLink", String.valueOf(value));
		return this;
	}	
*/
	
	public XmlBuilder build() {
		try {
			//TODO cambiar path del file
			result = new StreamResult(this.file);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			new UnableToCreateFileException("mario");
		}
		return this;
	}
	
	/*--------------------GETTERS PARA TESTS-----------------*/
	public File getFile() {
		return file;
	}

}
