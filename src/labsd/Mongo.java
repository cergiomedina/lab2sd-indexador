
package labsd;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.MongoDatabase;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Mongo {

    
    
    static String doc_para_indexar;
    static String BaseDeDatos;
    static String ColeccionDocumentos;
    static String ColeccionIndice;

    public static void main(String[] args) {
        try{   

            
         // To connect to mongodb server
         MongoClient mongoClient = new MongoClient(  );
         cargarParametros();
         
         // Now connect to your databases
         DB db = mongoClient.getDB( BaseDeDatos );
         System.out.println("Conexión a "+BaseDeDatos+" realizada con éxito");
         if (!db.collectionExists(ColeccionDocumentos)){
            DBCollection documentos = db.createCollection(ColeccionDocumentos, new BasicDBObject());
             System.out.println("Colección "+ColeccionDocumentos+" creada");
         }
         if (!db.collectionExists(ColeccionIndice)){
            DBCollection indice = db.createCollection(ColeccionIndice, new BasicDBObject());
            
             System.out.println("Coleccion "+ColeccionIndice+" creada");
         }
         
            lectorXML lector = new lectorXML();     
            lector.leer(doc_para_indexar);

            
            
       }catch(Exception e){
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
       }
    }
    
    private static void cargarParametros() throws FileNotFoundException, IOException {
        // cargo archivo para indexar
        
        BufferedReader entrada = new BufferedReader(new FileReader("parametros.ini"));
        String linea;
        try {
            doc_para_indexar = entrada.readLine();  
            BaseDeDatos = entrada.readLine();
            ColeccionDocumentos = entrada.readLine();
            ColeccionIndice = entrada.readLine();
            entrada.close();
        } catch (IOException ex) {
            Logger.getLogger(Documento.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
        