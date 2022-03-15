import org.deri.iris.Configuration;
import org.deri.iris.KnowledgeBase;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.optimisations.magicsets.MagicSets;
import org.deri.iris.storage.IRelation;

import syntaxtree.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import spiglet_optimizer.Info_table.InfoTable;
import spiglet_optimizer.visitor.Visitor1;
import spiglet_optimizer.visitor.Visitor2;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 *
 * @author lumberjack
 */
public class Spiglet_Optimizer {

    /**
     * @param args the command line arguments
     */
    
	static final String spfilespath = "../SP_files/" ;
    static final String path = "../Datalog/Facts/" ;
	static final String outputpath = "../Output/" ;
    
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
                if(args.length < 1){
                System.err.println("Usage: java Driver <inputFile>");
                System.exit(1);
            }
            for(String filename : args){
                
                FileInputStream fis = null;
            
                try{
                    fis = new FileInputStream(spfilespath+filename);
                                
                } catch (FileNotFoundException ex) {
                   ex.printStackTrace();
                }
                SpigletParser parser1 = new SpigletParser(fis);
                System.err.println("Program parsed successfully.");
                Visitor1 visitor = new Visitor1();
                Visitor2 visitor2 = new Visitor2();
                Goal root = null;
               
                try {
                    root = parser1.Goal();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                
                
                
                String [] n = filename.split("\\.");
                if(n.length==0){
                    System.err.println("Wrong name for file , file need extension . file.java");
                }
                InfoTable it = new InfoTable(path+n[0]);
                
                //try{    
                    root.accept(visitor,it);
                    root.accept(visitor2, it);
                    //root.accept(visitor2,file);
                    //root.accept(visitor3,file);
                //} catch (Semantic_Exception ex) {
                    //System.err.println("Type error found in "+filename);
                    //System.err.println(ex);
                    //System.err.println();
                //}
                it.close_inswriter();
                it.close_varwriter();
                it.close_nextwriter();
                it.close_varMovewriter();
                it.close_constMovewriter();
                it.close_varUsewriter();
                it.close_varDefwriter();
//                    
            
            
            Parser parser = new Parser();

          
            Map<IPredicate, IRelation> factMap = new HashMap<>();

            /** The following loop -- given a project directory -- will list and read parse all fact files in its "/facts"
             *  subdirectory. This allows you to have multiple .iris files with your program facts. For instance you can
             *  have one file for each relation's facts as our examples show.
             */
            final File factsDirectory = new File(path+n[0]);
            if (factsDirectory.isDirectory()) {
                for (final File fileEntry : factsDirectory.listFiles()) {
                    
                    if (fileEntry.isDirectory())
                        System.out.println("Omitting directory " + fileEntry.getPath());

                    else {
                        Reader factsReader;
                        try {
                            factsReader = new FileReader(fileEntry);
                            parser.parse(factsReader);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Spiglet_Optimizer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        

                        // Retrieve the facts and put all of them in factMap
                        factMap.putAll(parser.getFacts());
                    }
                }
            }
            else {
                System.err.println("Invalid facts directory path");
                System.exit(-1);
            }

            File rulesFile = new File(path + "../rules.iris");
            Reader rulesReader = new FileReader(rulesFile);

            File queriesFile = new File(path + "../queries.iris");
            Reader queriesReader = new FileReader(queriesFile);

            // Parse rules file.
            parser.parse(rulesReader);
            // Retrieve the rules from the parsed file.
            List<IRule> rules = parser.getRules();

            // Parse queries file.
            parser.parse(queriesReader);
            // Retrieve the queries from the parsed file.
            List<IQuery> queries = parser.getQueries();

            // Create a default configuration.
            Configuration configuration = new Configuration();

            // Enable Magic Sets together with rule filtering.
            configuration.programOptmimisers.add(new MagicSets());

            // Create the knowledge base.
            IKnowledgeBase knowledgeBase = new KnowledgeBase(factMap, rules, configuration);

            // Evaluate all queries over the knowledge base.
            for (IQuery query : queries) {
                List<IVariable> variableBindings = new ArrayList<>();
                IRelation relation = knowledgeBase.execute(query, variableBindings);

                // Output the variable bindings.
                PrintWriter printer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputpath+n[0]+query.toString().split("\\ ")[1].split("\\(")[0])));
                 System.out.println("\n" + query.toString() + "\n" + variableBindings);
                 printer.println(query.toString() + "\n" + variableBindings);

                // Output each tuple in the relation, where the term at position i
                // corresponds to the variable at position i in the variable
                // bindings list.
                for (int i = 0; i < relation.size(); i++) {
                    System.out.println(relation.get(i));
                    printer.println(relation.get(i));
                }
                printer.close();
            }
            
            }
    }
}

    
    
    
