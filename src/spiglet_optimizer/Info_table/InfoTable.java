/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spiglet_optimizer.Info_table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lumberjack
 */
public class InfoTable {
    
    //public Vector<String> methodname ;
    //public Vector<Vector<String>> labels ;
    public int instruction_num = 0;
    public String currentmethod = null;
    
    LinkedHashMap<String,LinkedHashMap<String,String>> varnames;
    LinkedHashMap<String,Integer> labelnum;
    //LinkedHashMap<String,Integer> functlabelnum;
    //LinkedHashMap<String,Integer> jumpslabelnum;
    
    PrintWriter inswriter;
    PrintWriter varwriter;
    PrintWriter nextwriter;
    PrintWriter varmovewriter;
    PrintWriter constmovewriter;
    PrintWriter varusewriter;
    PrintWriter vardefwriter;
    
    
    
    File theDir ;

    public InfoTable(String path) {
        
        File theDir = new File(path);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + path);
            boolean result = false;

            try{
                theDir.mkdir();
                result = true;
            } 
            catch(SecurityException se){
                se.printStackTrace();
            }        
            if(result) {    
                System.out.println("DIR created");  
            }
        }
        
        try {
            inswriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path+"/instruction.iris")));
            varwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path+"/var.iris")));
            nextwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path+"/next.iris")));
            varmovewriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path+"/varMove.iris")));
            constmovewriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path+"/constMove.iris")));
            varusewriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path+"/varUse.iris")));
            vardefwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path+"/varDef.iris")));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        this.varnames = new LinkedHashMap<String,LinkedHashMap<String,String>>();
        this.labelnum = new LinkedHashMap<String,Integer>();
        //this.functlabelnum = new LinkedHashMap<String,Integer>();
        //this.jumpslabelnum = new LinkedHashMap<String,Integer>();
        //this.methodname = new Vector<String>();
        //this.labels = new Vector<Vector<String>>();
    }
    
    public boolean write_instruction(String instruction){
        this.inswriter.println(instruction + ".");
        return true;
    }
    
    public boolean write_vars(){
        Set functset =this.varnames.keySet();
        
        for(Object fun : functset){
            String funname = (String)fun;
            Set varset  = this.varnames.get(fun).keySet();
            
            for(Object var : varset){
                String varname = (String)var;
                this.varwriter.println("var(\""+funname+"\", "+"\""+ var +"\").");
            }
            
        }
        return true;
    }
    
    public boolean write_next(String label,Integer flag){
        
        String str = new String();
        str="next(\"" + this.currentmethod + "\", ";
        
        if(flag == 0){
            this.nextwriter.println(str + String.valueOf(instruction_num) + ", " + String.valueOf(this.instruction_num+1) + ").");
        }
        else if(flag == 1){
           this.nextwriter.println(str + String.valueOf(instruction_num) + ", " + String.valueOf(this.instruction_num+1) + ")."); 
           Integer i = this.find_next_instruction(label);
           this.nextwriter.println(str + String.valueOf(instruction_num) + ", " + i.toString() + ").");
        }
        else if(flag == 2){
            Integer i = this.find_next_instruction(label);
            this.nextwriter.println(str + String.valueOf(instruction_num) + ", " + i.toString()+").");
        }
        else if(flag == 3){
            this.nextwriter.println(str + String.valueOf(instruction_num) + ", " + String.valueOf(-1) + ").");
        }
        
//        else{
//            Set set = this.functlabelnum.keySet();
//            for(Object lab : set){
//                String label1 = (String) lab;
//                Integer i = this.functlabelnum.get(lab);
//                this.nextwriter.println("next(\"" + this.currentmethod + "\", " + String.valueOf(instruction_num) + ", " + i.toString() +")");
//            }
//        }
        return true;
    }
    
    public boolean write_VarMove(String var1, String var2){
        
        String str = new String();
        str="next(\"" + this.currentmethod + "\", ";
        this.varmovewriter.println("varMove(\"" +this.currentmethod + "\", " + this.instruction_num 
                                    + ", \"" + var1 + "\", \"" + var2 +"\").");
        return true;
        
    }
    
    public boolean write_ConstMove(String var1, String constant){
        
        String str = new String();
        str="next(\"" + this.currentmethod + "\", ";
        this.constmovewriter.println("constMove(\"" +this.currentmethod + "\", " + this.instruction_num 
                                    + ", \"" + var1 + "\", " + constant +").");
        return true;
        
    }
    
    public boolean write_VarUse(String vars){
        String str = new String();
        str = "varUse(\"" + this.currentmethod + "\", "+ this.instruction_num +", \"" + vars +"\").";
        this.varusewriter.println(str);
        return true;
    }
    
    public boolean write_VarDef(String vars){
        String str = new String();
        str = "varDef(\"" + this.currentmethod + "\", "+ this.instruction_num +", \"" + vars +"\").";
        this.vardefwriter.println(str);
        return true;
    }
    
    
    public boolean close_inswriter(){
        
        inswriter.close();
        return true;
    }
    
    public boolean close_varwriter(){
        
        this.varwriter.close();
        return true;
    }
    
    public boolean close_nextwriter(){
        this.nextwriter.close();
        return true;
    }
    
    public boolean close_constMovewriter(){
        this.constmovewriter.close();
        return true;
    }
    
    public boolean close_varUsewriter(){
        this.varusewriter.close();
        return true;
    }
     
    public boolean close_varDefwriter(){
        this.vardefwriter.close();
        return true;
    }
    
    
    public boolean close_varMovewriter(){
        this.varmovewriter.close();
        return true;
    }
    
    
    Integer find_next_instruction(String label){
        return (Integer)this.labelnum.get(label);
    }
    
    public boolean add_fuct(String functname){
        if(!this.varnames.containsKey(functname)){
            this.varnames.put(functname,new LinkedHashMap<String,String>());
        }
        return true;
    }
    
    public boolean add_var(String functname,String var){
        if(!this.varnames.containsKey(functname)){
            return false;
        }
        if(this.varnames.get(functname).containsKey(var)){
            return false;
        }
        this.varnames.get(functname).put(var, var);
        return true;
    }
    
    public boolean add_Label_Num(String label,Integer num){
        if(this.labelnum.containsKey(label)){
            return false;
        }
        this.labelnum.put(label, num);
        return true;
    }
    
//    public boolean add_Funct_Label_Num(String label,Integer num){
//        if(this.functlabelnum.containsKey(label)){
//            return false;
//        }
//        this.functlabelnum.put(label, num);
//        return true;
//    }
    
//    public boolean add_Jump_Label_Num(String label,Integer num){
//        if(this.jumpslabelnum.containsKey(label)){
//            return false;
//        }
//        this.jumpslabelnum.put(label, num);
//        return true;
//    }
    

    

    
    
}
