package edu.upc.essi.dtim.nextiaEngine;

import edu.upc.essi.dtim.Queries.*;
import edu.upc.essi.dtim.Graph.*;

import java.util.List;
import java.util.Map;

public interface GraphQueries {
    public List<Map<String, String>> executeQuery (Query q, Graph g);
    //public void executeQuery(String q, Graph g);
}

