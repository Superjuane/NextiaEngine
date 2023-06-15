package edu.upc.essi.dtim.nextiaEngine;
import edu.upc.essi.dtim.Graph.*;
import edu.upc.essi.dtim.Queries.Query;
import edu.upc.essi.dtim.nextiaEngine.temp.DataFrame_MM;
import edu.upc.essi.dtim.nextiaEngine.temp.JenaGraph;
import org.apache.jena.ext.com.google.common.collect.Maps;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.*;
import org.apache.jena.sparql.core.BasicPattern;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.upc.essi.dtim.nextiaEngine.temp.RDF;
import edu.upc.essi.dtim.nextiaEngine.temp.RDFS;

import edu.upc.essi.dtim.Queries.*;
import edu.upc.essi.dtim.Queries.QuerySolution;
import org.apache.jena.Jena;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.*;
import org.apache.jena.sparql.core.Var;

public class GraphQueriesJenaImpl implements GraphQueries {

    /**
     * Function that extracts the atributes from a SPARQL query
     * @param query String that contains the SPARQL query
     */
    private HashSet<String> ExtractAtributeNames(String query){
        HashSet<String> result = new HashSet<>();
        Pattern pattern = Pattern.compile("\\?\\w+\\s");
        Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            String substring = matcher.group();
            if (substring.equalsIgnoreCase("WHERE")) {
                break; // Stop matching if substring equals "WHERE"
            }
            result.add(substring.substring(1, substring.length()-1));
        }
        return result;
    }

//*--------------- Atribute extraction (failed)
//    public Map<String,Integer> projectionOrder = Maps.newHashMap();
//    private OntModel ontologyFromPattern(BasicPattern PHI_p) {
//        OntModel o = ModelFactory.createOntologyModel();
//        PHI_p.getList().forEach(t -> addTriple(o, t.getSubject().getURI(), t.getPredicate().getURI(), t.getObject().getURI()));
//        return o;
//    }
//    private void addTriple(Model model, String s, String p, String o) {
//        model.add(new ResourceImpl(s), new PropertyImpl(p), new ResourceImpl(o));
//    }
//    private void ExtractAtributeNamesNOP(String SPARQL) {
//        // Compile the SPARQL using ARQ and generate its <pi,phi> representation
//        org.apache.jena.query.Query q = QueryFactory.create(SPARQL);
//        Op ARQ = Algebra.compile(q);
//        Set<String> PI = Sets.newHashSet();
//        ((OpTable)((OpJoin)((OpProject)ARQ).getSubOp()).getLeft()).getTable().rows().forEachRemaining(r -> {
//            int i = 0;
//            for (Iterator<Var> it = r.vars(); it.hasNext(); ) {
//                Var v = it.next();
//                PI.add(r.get(v).getURI());
//                projectionOrder.put(r.get(v).getURI(),i);
//                ++i;
//            }
//        });
//        BasicPattern PHI_p = ((OpBGP)((OpJoin)((OpProject)ARQ).getSubOp()).getRight()).getPattern();
//        OntModel PHI_o_ontmodel = ontologyFromPattern(PHI_p);
//        Reasoner reasoner = ReasonerRegistry.getTransitiveReasoner(); //RDFS entailment subclass+superclass
//        InfModel PHI_o = ModelFactory.createInfModel(reasoner,PHI_o_ontmodel);
////        return new Tuple3<>(PI,PHI_p,PHI_o);
//    }

    /**
     * Function to execute queries given a graph
     * @param q query to be executed
     * @param g graph where the query will be executed
     * @return QueryResult set of pairs <atribute, value> that contains the result of the query
     */
    @Override
    public /*QueryResult*/List<Map<String, String>> executeQuery(Query q, Graph g) {

        JenaGraph jenaGraph;
        List<Map<String, String>> queryResult = new ArrayList<>();

        //jenaGraph = MockModel();
        jenaGraph = adapt(g);

       HashSet<String> atributes = ExtractAtributeNames(q.getQueryText());
//       ExtractAtributeNamesNOP(q.getQueryText());

       jenaGraph.runAQuery(q.getQueryText()).forEachRemaining(res -> {
            Map<String, String> mapResult = new HashMap<>();
            for(String atribute : atributes){
                String m;
                if (res.get(atribute).isLiteral()){
                    m = String.valueOf(res.getLiteral(atribute));
//                    System.out.println("   -literal: "+ m);
                }
                else {
                    m = res.getResource(atribute).getURI();
//                    System.out.println("   -resource: "+m);
                }
                mapResult.put(atribute, m);
            }
            queryResult.add(mapResult);
       });
       return queryResult;
    }
/**
     * Function to adapt a graph to a JenaGraph
     * @param graph graph to be adapted
     * @return JenaGraph JenaGraph adapted
     */
    public JenaGraph adapt (Graph graph){
        JenaGraph result = new JenaGraph();

        //we run through each triple of the graph
        for (Triple triple : graph.getTriples()){

            //this is to solve the problem of the triple being <URI, URI, OBJECT>
            URI x = null;
            if(triple.getObject().getClass() == URI.class){
                x = (URI) triple.getObject();
            }

            //we add the triple to the JenaGraph
            if(isLiteral(x.getURI())){
                result.addLiteral(triple.getSubject().getURI(), triple.getPredicate().getURI(), x.getURI());
            }
            else {
                result.add(triple.getSubject().getURI(), triple.getPredicate().getURI(), x.getURI());
            }
        }
        return result;
    }

    /**
     * Function to see if a string is a literal or not
     * @param uri uri to be analyzed
     * @return boolean true if it is a literal, false if not
     */
    private boolean isLiteral(String uri) {
        return !(uri.startsWith("http://") || uri.startsWith("www.") || uri.startsWith("https://"));
    }


    //*--------------- MOCK JENAGRAPH
//    private String uri = "www.edu.upc.dtim/";
//    String getUri(String name){return uri+name;}
//    private JenaGraph MockModel() {
//        JenaGraph graph = new JenaGraph();
//        String name = "MockName";
////        graph.add(uri+"NombreA1", uri+"hola", uri+"adeu");
////        graph.addLiteral(uri+"NombreA1", uri+"is", "labelA1");
//        graph.add(getUri(name), RDF.type.getURI(), DataFrame_MM.DataFrame.getURI());
//        graph.addLiteral(getUri(name), RDFS.label.getURI(), name);
//        for(int i = 0; i < 2; ++i) {
//            String col = "Col-"+i;
//            graph.add(getUri(col), RDF.type.getURI(),DataFrame_MM.Data.getURI());
//            graph.addLiteral(getUri(col), RDFS.label.getURI(),col);
//            graph.add(getUri(name),DataFrame_MM.hasData.getURI(),getUri(col));
//            graph.add(getUri(col),DataFrame_MM.hasDataType.getURI(),DataFrame_MM.String.getURI());
//        }
//        HashMap<String, String> prefixes = new HashMap<>();
//        prefixes.put("DTIM", "www.edu.upc.dtim/");
//        prefixes.put("RDF", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
//        prefixes.put("RDFS", "http://www.w3.org/2000/01/rdf-schema#");
//        prefixes.put("METAMODEL", "https://www.essi.upc.edu/dtim/dataframe-metamodel#");
//        graph.setPrefixes(prefixes);
//        System.out.println("\n----------MockModel-----------");
//        graph.write(System.out, "TLT");
//        System.out.println("------------------------------");
//        return graph;
//    }

}
