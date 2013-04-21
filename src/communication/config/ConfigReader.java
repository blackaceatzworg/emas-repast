package communication.config;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigReader {

	private static final String HOSTS_FILE_NAME = "config\\hosts.xml";
	private static String rmiHost;
	private static Integer rmiPort;
	private static List <String> hosts;
	private static String localHost;
	
	static {
		try {

			Document doc = initialize();
			readRmiConfiguration(doc);
			readLocalHostConfiguration(doc);
			readHostsConfiguration(doc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void readRmiConfiguration(Document doc){
		Node rmiRegistry = doc.getElementsByTagName("rmiregistry").item(0);
		if (rmiRegistry.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) rmiRegistry;
			rmiHost = getValue("rmihost", element);
			rmiPort = Integer.parseInt(getValue("rmiport", element));
		}
	}
	
	private static void readLocalHostConfiguration(Document doc) {
		hosts = new LinkedList <String> ();
		Node node = doc.getElementsByTagName("localhost").item(0);
		localHost = node.getChildNodes().item(0).getNodeValue();
	}

	private static void readHostsConfiguration(Document doc) {
		hosts = new LinkedList <String> ();
		NodeList nodes = doc.getElementsByTagName("host");
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			hosts.add(node.getChildNodes().item(0).getNodeValue());
		}
	}

	private static Document initialize() throws ParserConfigurationException, SAXException, IOException {
		File hostsFile = new File(HOSTS_FILE_NAME);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(hostsFile);
		doc.getDocumentElement().normalize();
		return doc;
	}

	public static String getLocalHost(){
		return localHost;
	}
	
	public static String getRmiHost(){
		return rmiHost;
	}
	
	public static Integer getRmiPort(){
		return rmiPort;
	}
	
	public static List<String> getHosts(){
		return hosts;
	}

	private static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
}
