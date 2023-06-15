package edu.upc.essi.dtim.nextiaEngine;

import edu.upc.essi.dtim.Queries.*;
import edu.upc.essi.dtim.Graph.*;
import edu.upc.essi.dtim.nextiaEngine.temp.*;
import edu.upc.essi.dtim.vocabulary.Nextia;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import static edu.upc.essi.dtim.vocabulary.Nextia.IntegratedDatatypeProperty;

import java.io.FileOutputStream;
import java.util.*;


class GlobalVocabularyMock{
    private static String uri = "www.edu.upc.dtim/";

    static String getUri(String name){return uri+name;}
}

public class Main extends GlobalVocabularyMock {
    public static void main(String[] args) {
        String name = "Coche"; //nom del graph glob al, node0

        //alias: 'd' // type: g (Core::Graph)
        Graph G_DataFrame = new LocalGraph(new URI(getUri(name)),new HashSet<Triple>());
        Graph G_RDFS = new LocalGraph(new URI(getUri(name)),new HashSet<Triple>());

        //* agregamos tripletes en d para el ejemplo
            //G_target.add(createIRI(name), RDF.type, DataFrame_MM.DataFrame);
        G_DataFrame.addTriple(new Triple(new URI(getUri(name)), RDF.type, DataFrame_MM.DataFrame));
            //G_target.addLiteral(createIRI(name), RDFS.label, name);
        G_DataFrame.addTriple(new Triple(new URI(getUri(name)), RDFS.label, new URI(name)));
        //!OJO: al label no hauria d'anar una URL, hauria d'anar un Literal o similar?

        for(int i = 0; i < 1; ++i) {
            String col = "Col-"+i;
            //G_target.add(createIRI(h2), org.apache.jena.vocabulary.RDF.type,DataFrame_MM.Data);
            G_DataFrame.addTriple(new Triple(new URI(getUri(col)), RDF.type, DataFrame_MM.Data));
            //G_target.addLiteral(createIRI(h2), org.apache.jena.vocabulary.RDFS.label,h2 );
            G_DataFrame.addTriple(new Triple(new URI(getUri(col)), RDFS.label,new URI(col)));
            //G_target.add(createIRI(name),DataFrame_MM.hasData,createIRI(h2));
            G_DataFrame.addTriple(new Triple(new URI(getUri(name)), DataFrame_MM.hasData, new URI(col)));
            //G_target.add(createIRI(h2),DataFrame_MM.hasDataType,DataFrame_MM.String);
            G_DataFrame.addTriple(new Triple(new URI(getUri(col)), DataFrame_MM.hasDataType,DataFrame_MM.String));
        }


        GraphQueries engine = new GraphQueriesJenaImpl();
        List<Map<String, String>> result;

        //* ejecutamos las 6 engine.graphquery.execQuery(g, forEachProdRule)

        // Rule 1. Instances of J:Object(dataframe) are translated to instances of rdfs:Class .
        Query q1 = new Query("1", "SELECT ?df ?label WHERE { ?df <"+ RDF.type.getURI()+"> <"+ DataFrame_MM.DataFrame.getURI()+">. ?df <"+ RDFS.label.getURI()+"> ?label }");
        result = engine.executeQuery(q1, G_DataFrame);
        for(Map<String, String> res : result){
            //System.out.println(res);
            G_RDFS.addTriple(new Triple(new URI(res.get("df")), RDF.type, RDFS.Class));
            G_RDFS.addTriple(new Triple(new URI(res.get("df")), RDFS.label, new URI(res.get("label"))));
        }

        // Rule 2. Instances of DF:data (columnes o keys) are translated to instances of rdf:Property .
        Query q2 = new Query("2", "SELECT ?df ?d ?label WHERE { ?df <"+ DataFrame_MM.hasData.getURI()+"> ?d. ?d <"+ RDFS.label.getURI()+"> ?label }");
        result = engine.executeQuery(q2, G_DataFrame);
        for(Map<String, String> res : result) {
            G_RDFS.addTriple(new Triple(new URI(res.get("d")), RDF.type, RDF.Property));
            G_RDFS.addTriple(new Triple(new URI(res.get("d")), RDFS.label, new URI(res.get("label"))));
            G_RDFS.addTriple(new Triple(new URI(res.get("d")), RDFS.domain, new URI(res.get("df"))));
        }

        // Rule 3. Array keys are also ContainerMembershipProperty
        Query q3 = new Query("3", "SELECT ?df ?d WHERE { ?df <"+ DataFrame_MM.hasData.getURI()+"> ?d. ?d <"+ DataFrame_MM.hasDataType.getURI()+"> ?a . ?a <"+ RDF.type.getURI()+"> <"+ DataFrame_MM.Array.getURI()+"> }");
        result = engine.executeQuery(q3, G_DataFrame);
        for(Map<String, String> res : result) {
            //System.out.println(res);
            G_RDFS.addTriple(new Triple(new URI(res.get("d")), RDF.type, RDFS.ContainerMembershipProperty));
        }

        //Rule 4. Range of primitives.
        Query q41 = new Query("4.1", "SELECT ?d WHERE { ?d <"+DataFrame_MM.hasDataType.getURI()+"> <"+DataFrame_MM.String.getURI()+"> . ?d <"+RDF.type.getURI()+"> <"+DataFrame_MM.Data.getURI()+"> }");
        result = engine.executeQuery(q41, G_DataFrame);
        for(Map<String, String> res : result) {
            //System.out.println(res);
            G_RDFS.addTriple(new Triple(new URI(res.get("d")), RDFS.range, XSD.xstring));
        }
        Query q42 = new Query("4.2", "SELECT ?d WHERE { ?d <"+DataFrame_MM.hasDataType.getURI()+"> <"+DataFrame_MM.Number.getURI()+"> . ?d <"+RDF.type.getURI()+"> <"+DataFrame_MM.Data.getURI()+"> }");
        result = engine.executeQuery(q42, G_DataFrame);
        for(Map<String, String> res : result) {
            //System.out.println(res);
            G_RDFS.addTriple(new Triple(new URI(res.get("d")), RDFS.range, XSD.xint));
        }

        //Rule 5. Range of dataframes.
        Query q5 = new Query("5", "SELECT ?d ?dt WHERE { ?d <"+DataFrame_MM.hasDataType.getURI()+"> ?dt . ?d <"+RDF.type.getURI()+"> <"+DataFrame_MM.Data.getURI()+"> . ?dt <"+RDF.type.getURI()+"> <"+DataFrame_MM.DataFrame.getURI()+"> }");
        result = engine.executeQuery(q5, G_DataFrame);
        for(Map<String, String> res : result) {
            //System.out.println(res);
            G_RDFS.addTriple(new Triple(new URI(res.get("d")), RDFS.range, new URI(res.get("dt"))));
        }

        System.out.println("\n\n\n===============================================================\n\n\n");

        GraphQueriesJenaImpl engine2 = new GraphQueriesJenaImpl();
        System.out.println("\n ------- Adapted Jena Graph -------\n");
        engine2.adapt(G_DataFrame).write(System.out, "TURTLE");


        System.out.println("\n\n\n ------- Translated Odin Graph -------\n");

        for(Triple t : G_RDFS.getTriples()){
            URI x = null;
            if(t.getObject().getClass() == URI.class){
                x = (URI) t.getObject();
            }
            System.out.println(prefixed(t.getSubject().getURI()) + "\n      " + prefixed(t.getPredicate().getURI()) + "\n      " + prefixed(x.getURI()));
        }
    }

    private static String prefixed(String inputURI) {
        Map<String, String> prefixes = new HashMap<>();
        prefixes.put("www.edu.upc.dtim/", "DTIM:");
        prefixes.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF:");
        prefixes.put("http://www.w3.org/2000/01/rdf-schema#", "RDFS:");
        prefixes.put("https://www.essi.upc.edu/dtim/dataframe-metamodel#", "METAMODEL:");
        prefixes.put("http://www.w3.org/2001/XMLSchema#", "XSD:");
        String outputURI = inputURI;
        for (Map.Entry<String, String> entry : prefixes.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            outputURI = inputURI.replace(key, value);
        }
        return outputURI;
    }
}