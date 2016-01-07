
package labsd;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.io.*;
import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class lectorXML extends DefaultHandler {
    private final XMLReader xr;
    String tag;
    Documento documento;
    int numero = 0;
    

    public lectorXML() throws SAXException {
        xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(this);
        xr.setErrorHandler(this);
        
    }
    StringBuilder currentText = new StringBuilder();
    public void leer(final String archivoXML)  throws FileNotFoundException, IOException,SAXException {
        documento = new Documento();
        tag = "";
        FileReader fr = new FileReader(archivoXML);
        xr.parse(new InputSource(fr));
    }
    
    @Override
    public void startDocument() {
        //System.out.println("Comienzo del Documento XML");
    }
 
    @Override
    public void endDocument() {
        //System.out.println("Final del Documento XML");
    }
 
    @Override
    public void startElement(String uri, String name, String qName, Attributes atts) {
        tag = name;
    }
 
    @Override
    public void endElement(String uri, String name,
                                 String qName) {
        if(tag == "text"){
            //documento.mostrar();
            documento.agregarDocumento(numero);
            numero ++;
        }
        tag = "";
        
        
    }   
    
    StringTokenizer palabras;
    @Override  
    public void characters(char[] ch, int start, int length) throws SAXException {  
        
        if (currentText.length()>0){
            currentText = new StringBuilder();
            
        }
        else{
            
            if(tag == "title" || tag == "text"){
                currentText.append(ch, start, length);
                if(tag=="title"){
                    //System.out.println("NOMBRE: "+currentText.toString());
                    documento.setNombre(currentText.toString());
                }else{
                    documento.setTexto(currentText.toString());
                }
            }
            
        }
    }
}