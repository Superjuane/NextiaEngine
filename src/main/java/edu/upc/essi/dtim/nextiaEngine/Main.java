package edu.upc.essi.dtim.nextiaEngine;

import edu.upc.essi.dtim.Queries.*;
import edu.upc.essi.dtim.Graph.*;
import edu.upc.essi.dtim.nextiaEngine.temp.DataFrame_MM;
import edu.upc.essi.dtim.nextiaEngine.temp.JenaGraph;
import edu.upc.essi.dtim.nextiaEngine.temp.RDF;
import edu.upc.essi.dtim.nextiaEngine.temp.RDFS;
import edu.upc.essi.dtim.vocabulary.Nextia;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import static edu.upc.essi.dtim.vocabulary.Nextia.IntegratedDatatypeProperty;

import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
            G_DataFrame.addTriple(new Triple(new URI(getUri(col)), RDFS.label,new URI(getUri(col))));
            //G_target.add(createIRI(name),DataFrame_MM.hasData,createIRI(h2));
            G_DataFrame.addTriple(new Triple(new URI(getUri(name)), DataFrame_MM.hasData, new URI(getUri(col))));
            //G_target.add(createIRI(h2),DataFrame_MM.hasDataType,DataFrame_MM.String);
            G_DataFrame.addTriple(new Triple(new URI(getUri(col)), DataFrame_MM.hasDataType,DataFrame_MM.String));
        }


        GraphQueries engine = new GraphQueriesJenaImpl();
        List<Map<String, String>> result;
        //* ejecutamos las 6 engine.graphquery.execQuery(g, forEachProdRule)

                // Rule 1. Instances of J:Object(dataframe) are translated to instances of rdfs:Class .
//        G_source.runAQuery("SELECT ?df ?label WHERE { ?df <"+ org.apache.jena.vocabulary.RDF.type+"> <"+ DataFrame_MM.DataFrame+">. ?df <"+ org.apache.jena.vocabulary.RDFS.label+"> ?label }").forEachRemaining(res -> {
        Query q1 = new Query("1", "SELECT ?df ?label WHERE { ?df <"+ RDF.type.getURI()+"> <"+ DataFrame_MM.DataFrame.getURI()+">. ?df <"+ RDFS.label.getURI()+"> ?label }");
//        Query q1 = new Query("1", "SELECT ?a1 ?a2 WHERE { ?a1 <hola> <adeu>. ?a1 <is> ?a2 }");
//        Query q1 = new Query("1", "SELECT ?a1 WHERE { ?a1 <www.edu.upc.dtim/hola> <www.edu.upc.dtim/adeu> }");
//        Query q1 = new Query("1", "SELECT ?a1 ?a3 WHERE { ?a1 <www.edu.upc.dtim/is> ?a3 }");
//        Query q1 = new Query("1", "SELECT ?a1 ?a2 ?a3 WHERE { ?a1 ?a2 ?a3 }");


        result = engine.executeQuery(q1, G_DataFrame);

        System.out.println("Resultat de la query 1:");
        for(Map<String, String> res : result){
            System.out.println(res);
        }

        //* para cada result le agregamos una tripleta al grafo G_RDFS
//        while(result.iterator().hasNext()){
//            QuerySolution solution = result.iterator().next();
//            //G_target.add(res.getResource("df").getURI(), org.apache.jena.vocabulary.RDF.type, org.apache.jena.vocabulary.RDFS.Class);
//            G_RDFS.addTriple(new Triple(new URI(solution.get("df")), RDF.type, RDFS.Class));
//
//            //G_target.addLiteral(res.getResource("df").getURI(), org.apache.jena.vocabulary.RDFS.label, res.getLiteral("label") );
//            //! UNA URI COMO LITERAL?
//            G_RDFS.addTriple(new Triple(new URI(solution.get("df")), RDFS.label, new URI(solution.get("label"))));
//        }
        for(Map<String, String> res : result){
            G_RDFS.addTriple(new Triple(new URI(res.get("df")), RDF.type, RDFS.Class));
            G_RDFS.addTriple(new Triple(new URI(res.get("df")), RDFS.label, new URI(res.get("label"))));
        }
        System.out.println(G_RDFS);
    }
}