package edu.upc.essi.dtim.nextiaEngine;

import edu.upc.essi.dtim.Queries.*;
import edu.upc.essi.dtim.Graph.*;
import edu.upc.essi.dtim.Queries.Query;
import edu.upc.essi.dtim.Queries.QuerySolution;
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

public class GraphQueriesJenaImpl implements GraphQueries {

    private HashSet<String> ExtractAtributeNames(String query){
        HashSet<String> result = new HashSet<>();
        Pattern pattern = Pattern.compile("\\?\\w+\\s");
        Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            String substring = matcher.group();
            result.add(substring.substring(1, substring.length()-1));
        }
        return result;
    }


    private String uri = "www.edu.upc.dtim/";

    String getUri(String name){return uri+name;}

    public Map<String,Integer> projectionOrder = Maps.newHashMap();
    private OntModel ontologyFromPattern(BasicPattern PHI_p) {
        OntModel o = ModelFactory.createOntologyModel();
        PHI_p.getList().forEach(t -> addTriple(o, t.getSubject().getURI(), t.getPredicate().getURI(), t.getObject().getURI()));
        return o;
    }
    private void addTriple(Model model, String s, String p, String o) {
        model.add(new ResourceImpl(s), new PropertyImpl(p), new ResourceImpl(o));
    }
//    private void ExtractAtributeNames(String SPARQL) {
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
    private JenaGraph MockModel() {
        JenaGraph graph = new JenaGraph();
        String name = "MockName";
//        graph.add(uri+"NombreA1", uri+"hola", uri+"adeu");
//        graph.addLiteral(uri+"NombreA1", uri+"is", "labelA1");
        graph.add(getUri(name), RDF.type.getURI(), DataFrame_MM.DataFrame.getURI());
        graph.addLiteral(getUri(name), RDFS.label.getURI(), name);
        graph.add(getUri("NADA"), RDF.type.getURI(), DataFrame_MM.DataFrame.getURI());
        graph.addLiteral(getUri("NADA"), RDFS.label.getURI(), "NADA");
        for(int i = 0; i < 2; ++i) {
            String col = "Col-"+i;
            graph.add(getUri(col), RDF.type.getURI(),DataFrame_MM.Data.getURI());
            graph.addLiteral(getUri(col), RDFS.label.getURI(),col);
            graph.add(getUri(name),DataFrame_MM.hasData.getURI(),getUri(col));
            graph.add(getUri(col),DataFrame_MM.hasDataType.getURI(),DataFrame_MM.String.getURI());
        }
        HashMap prefixes = new HashMap<>();
        prefixes.put("DTIM", "www.edu.upc.dtim/");
        prefixes.put("RDF", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixes.put("RDFS", "http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("METAMODEL", "https://www.essi.upc.edu/dtim/dataframe-metamodel#");
        graph.setPrefixes(prefixes);
        System.out.println("\n----------MockModel-----------");
        graph.write(System.out, "TLT");
        System.out.println("------------------------------");
        return graph;
    }


    @Override
    public /*QueryResult*/List<Map<String, String>> executeQuery(Query q, Graph g) {

        JenaGraph jenaGraph;

        jenaGraph = MockModel();
        //jenaGraph = adapt(g);

       HashSet<String> atributes = ExtractAtributeNames(q.getQueryText());
//        ExtractAtributeNames(q.getQueryText());

        //QueryResult queryResult = new QueryResult(q, g);
        List<Map<String, String>> queryResult = new ArrayList<>();

        System.out.println(q.getQueryText());

        jenaGraph.runAQuery(q.getQueryText()).forEachRemaining(res -> {
//           String s = res.getResource("df").getURI(); System.out.println("----------------------\n"+s);
//           System.out.println(res.getLiteral("label"));
//           String l = res.getResource("a2").getURI();System.out.println(l);
//           if (res.get("a3").isLiteral()){
//               String m = String.valueOf(res.getLiteral("a3")); System.out.println(m);
//           }
//           else {
//               String m = res.getResource("a3").getURI(); System.out.println(m);
//           }
            Map<String, String> mapResult = new HashMap<>();
            for(String atribute : atributes){
                String m;
                if (res.get(atribute).isLiteral()){
                    m = String.valueOf(res.getLiteral(atribute)); System.out.println("   -literal: "+ m);
                }
                else {
                    m = res.getResource(atribute).getURI(); System.out.println("   -literal: "+m);
                }
                mapResult.put(atribute, m);
            }
            queryResult.add(mapResult);
           System.out.println("----------------------");
//           QuerySolution qu = new QuerySolution();
//           qu.set("label", l);
            //queryResult.addQuerySolution();
        });

       //! q.setQueryResult(queryResult);


        return queryResult;
    }


    public JenaGraph adaptGraph (Graph graph){
        JenaGraph result = new JenaGraph();
        for (Triple triple : graph.getTriples()){
//            result.add(triple.getSubject().getURI(), triple.getPredicate().getURI(), triple.getObject());
        }
        return result;
    }
    public Model adapt2 (Graph graph){
        Model result = ModelFactory.createDefaultModel();
        for (Triple triple : graph.getTriples()){
            System.out.println(triple.getSubject().getURI());
            Resource subject = result.createResource(triple.getSubject().getURI());
            subject.addProperty(result.createProperty(triple.getPredicate().getURI()), result.createResource(triple.getObject().toString()));
        }
        return result;
    }

    public Model adapt(Graph graph) {
        Model model = ModelFactory.createDefaultModel();
        for (Triple triple : graph.getTriples()) {
            Resource subject = ResourceFactory.createResource(triple.getSubject().getURI());
            Property predicate = ResourceFactory.createProperty(triple.getPredicate().getURI());
            Statement statement = null;
            if (true /*triple.getObject().isURI()*/) {
                Resource object = ResourceFactory.createResource(triple.getObject().toString());
                statement = new StatementImpl(subject, predicate, object);
            } else {
                org.apache.jena.datatypes.RDFDatatype datatype = null;
                if (true /*triple.getObject().getLiteralDatatypeURI() != null*/) {
                    datatype = org.apache.jena.datatypes.TypeMapper.getInstance().getSafeTypeByName(triple.getObject().toString());
                }
                statement = new StatementImpl(subject, predicate, (RDFNode) triple.getObject(), (ModelCom) datatype);
            }
            model.add(statement);
        }
        return model;
    }
//    @Override
//    public void executeQuery(String q, Graph g) {
//
//
//    }
}
