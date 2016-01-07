
package labsd;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.client.FindIterable;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import jdk.jfr.events.FileReadEvent;

public class Documento {
    
    String nombre;
    StringBuilder texto;
    BasicDBObject doc;
    DB db;
    int cantidadPalabras;
    MongoClient mongoClient;
    DBCollection collection;
    DBCollection indice;
    String[] palabras;
    ArrayList<String> indice_palabra;
    ArrayList<Integer> indice_cantidad;
    int numero;
    String delimitadores= "[ |.,;?!¡¿\'\"\\[\\]]+";
    ArrayList<String> stopwords;
    
    public Documento() throws FileNotFoundException {
            nombre = "";
            stopwords = new ArrayList<String>();
            cargarStopwords();
            mongoClient = new MongoClient();
            db = mongoClient.getDB( "labSD" );
            collection = db.getCollection("documentos");
            doc = new BasicDBObject();
            texto = new StringBuilder();
   
    }

        
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        //System.out.println("DOCUMENTO NOMBRE: "+this.nombre);
    }

    public void agregarDocumento(int numero){
        this.numero = numero;
        doc.put("_id",numero);
        doc.put("titulo", nombre);
        doc.put("texto", texto.toString());
        cantidadPalabras = 0;
        cantidadPalabras = cantidadPalabras();
        doc.put("cantidad_palabras",cantidadPalabras);
        collection.insert(doc);
        
        //for (int i = 0; i < cantidadPalabras; i++) {
        //    System.out.println("PALABRA: "+palabras[i]);
        //}
        System.out.println("Indexando documento: "+numero);
        IndiceInvertido();
        // guardo el documento leido recien y reinicializo variables para leer el que viene
        //System.out.println("Documento < "+nombre+" > Palabras: "+cantidadPalabras);
        nombre = "";
        texto = null;
        texto = new StringBuilder();
        cantidadPalabras = 0;
    }


    void setTexto(String textoCompleto) {
        texto.append(textoCompleto);
    }
        
    int cantidadPalabras(){
        int cantidad = 0;
        // cuento la cantidad de palabras del documento y separo las palabras
        StringTokenizer palabras = new StringTokenizer(texto.toString());
        this.palabras = texto.toString().toLowerCase().split(delimitadores);
        
        cantidad = palabras.countTokens();
        return cantidad;
    }

    private void IndiceInvertido() {
        
        // lee las palabras del documento y las reduce para obtener la cantidad
        // de veces que se repiten, y las guarda en la db
        // en la coleccion "indice_invertido", con numero de documento
        // y palabras  --> palabra , repeticiones
        // solo si no existe, si existe solo actualiza la info de esa palabra
        
        indice = db.getCollection("indice_invertido");
        indice_cantidad= null;
        indice_palabra= null;
        indice_cantidad = new ArrayList<Integer>();
        indice_palabra = new ArrayList<String>();
        int cantidad_palabras_indice= 0;
        //System.out.println("PALABRAS: "+palabras.length);
        for (int i = 0; i <palabras.length; i++) {
          //  System.out.println("P: "+i+" : "+palabras.length);
            if(!indice_palabra.contains(palabras[i])){
                
                int contador_palabra = 1;
                for (int j = i; j < palabras.length-i; j++) {
                    if(palabras[j].equalsIgnoreCase(palabras[i])){
                        contador_palabra++;
                    }
                }
                indice_palabra.add(palabras[i]);
                indice_cantidad.add(contador_palabra);
                cantidad_palabras_indice ++;
            }
        }
        //System.out.println("LARGO: "+indice_palabra.size()+" - "+cantidad_palabras_indice);
        
        //System.out.println("Documento: "+nombre+" P-INDICE: "+indice_t_palabra.size());
        for (int i = 0; i < indice_cantidad.size(); i++) {
            BasicDBObject palabra = new BasicDBObject("palabra",indice_palabra.get(i));
            DBCursor cursor = indice.find(palabra);
            //System.out.println("cursor: "+cursor.count());
            if(!stopwords.contains(indice_palabra.get(i))){
                if(cursor.count()==0){
                  //  System.out.println("Palabra [ "+indice_palabra.get(i)+ " ] no existe");
                    BasicDBObject palabra_nueva = new BasicDBObject();
                    palabra_nueva.put("palabra",indice_palabra.get(i));
                    palabra_nueva.put("documentos", new BasicDBObject(nombre,indice_cantidad.get(i)));

                    indice.insert(palabra_nueva);
                }else{
                    DBObject consulta = new BasicDBObject("palabra",indice_palabra.get(i));
                    DBObject actualizar = new BasicDBObject();
                    actualizar.put("$set", new BasicDBObject("documentos."+nombre,indice_cantidad.get(i)));
                    WriteResult result = indice.update(consulta, actualizar);
               }        
           }
        }
        
    }

    private void cargarStopwords() throws FileNotFoundException {
        
        BufferedReader entrada = new BufferedReader(new FileReader("stopwords.txt"));
        String linea;
        try {
            while((linea = entrada.readLine())!= null){
                stopwords.add(linea);
            }
            entrada.close();
        } catch (IOException ex) {
            Logger.getLogger(Documento.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    
    
    
}
