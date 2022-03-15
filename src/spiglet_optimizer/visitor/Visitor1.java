/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spiglet_optimizer.visitor;

import spiglet_optimizer.Info_table.InfoTable;
import spiglet_optimizer.Info_table.Symbol;
import syntaxtree.BinOp;
import syntaxtree.CJumpStmt;
import syntaxtree.Call;
import syntaxtree.ErrorStmt;
import syntaxtree.Goal;
import syntaxtree.HAllocate;
import syntaxtree.HLoadStmt;
import syntaxtree.HStoreStmt;
import syntaxtree.IntegerLiteral;
import syntaxtree.JumpStmt;
import syntaxtree.Label;
import syntaxtree.MoveStmt;
import syntaxtree.NoOpStmt;
import syntaxtree.Node;
import syntaxtree.NodeOptional;
import syntaxtree.NodeToken;
import syntaxtree.PrintStmt;
import syntaxtree.Procedure;
import syntaxtree.Stmt;
import syntaxtree.StmtExp;
import syntaxtree.StmtList;
import syntaxtree.Temp;
import visitor.GJDepthFirst;

/**
 *
 * @author lumberjack
 */
public class Visitor1 extends  GJDepthFirst <Symbol,InfoTable>  {
    
   
    public Symbol visit(NodeOptional n, InfoTable it) {
        if ( n.present() ){
            Symbol s = n.node.accept(this,it);
            if(s.origin.compareTo("Label")==0){
                it.add_Label_Num(s.instruction,it.instruction_num);
            }
            return s;
        }
        else
            return null;
    }
    
    /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
    public Symbol visit(Goal n, InfoTable it) {
        Symbol s = new Symbol();
        Symbol s1,s2;
        //String str = new String();
       
        it.currentmethod = "MAIN";
        it.add_fuct("MAIN");
        
        it.add_Label_Num("MAIN",it.instruction_num);
        
        for(Node node : n.f1.f0.nodes){
            s1 = node.accept(this, it);    
        }
        it.write_instruction("instruction(\"MAIN\", "+ it.instruction_num +", "+ "\"END\")");
        
        for(Node node : n.f3.nodes){
           node.accept(this, it);
        }
        
        it.write_vars();
        

        return s;
    }
    
    
    /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    */
    public Symbol visit(Stmt n, InfoTable it) {
        Symbol s = new Symbol();
        Symbol s1;
        String str = new String();
        String str1 = new String();
        
        s1 = n.f0.choice.accept(this, it);
        str = "instruction(\"" + it.currentmethod + "\", " +  String.valueOf(it.instruction_num)
                      + ", \"" + s1.instruction + "\")";
        
        it.write_instruction(str);
        
        
        
        it.instruction_num++;
        

        s.ident = null;
        s.instruction = null;
        s.origin = "Stmt";
                
        return s;
    }
    
    /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
    public Symbol visit(Procedure n, InfoTable it) {
        Symbol s = new Symbol();
        Symbol s1 ;
        
        s1 = n.f0.accept(this, it);
        it.add_Label_Num(s1.instruction, it.instruction_num);
       // it.add_Funct_Label_Num(s1.instruction, it.instruction_num);
        it.currentmethod = s1.ident;
        it.add_fuct(it.currentmethod);
        
        n.f2.accept(this, it);
        
        n.f4.accept(this, it);
        
        s.ident = null;
        s.instruction = null;
        s.origin = "Procedure";
        
        return s;
    }
    
    /**
    * f0 -> "NOOP"
    */
    public Symbol visit(NoOpStmt n, InfoTable argu) {
        Symbol s = new Symbol();
        s.ident=null;
        s.instruction = "NOOP";
        s.origin = "NoOpStmt";
        return s;
    }
    
    /**
    * f0 -> "ERROR"
    */
    public Symbol visit(ErrorStmt n, InfoTable argu) {
        Symbol s = new Symbol();
        s.ident=null;
        s.instruction = "ERROR";
        s.origin = "ErrorStmt";
        return s;
    }
    
    /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
    public Symbol visit(CJumpStmt n, InfoTable it) {
        Symbol s = new Symbol();
        Symbol s1 ;
        Symbol s2 = new Symbol();
        
        s1 = n.f1.accept(this, it);
        s2.ident = n.f2.f0.tokenImage;
        s2.instruction = n.f2.f0.tokenImage;
        
        
        it.write_VarUse(s1.instruction);
        
        
        s.ident = null;
        s.instruction = "CJUMP "+ s1.instruction + " "+ s2.instruction;
        s.origin = "CJumpStmt";
        
        return s;
    }
    
    /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
    public Symbol visit(JumpStmt n, InfoTable it) {
        Symbol s = new Symbol();
        Symbol s1 = new Symbol();
        
        s1.ident = n.f1.f0.tokenImage;
        s1.instruction = n.f1.f0.tokenImage;
        
        //it.add_Jump_Label_Num(s1.instruction, it.instruction_num+1);
        
        s.ident =null;
        s.instruction = "JUMP " + s1.instruction;
        s.origin = "JumpStmt";
        
        return s;
    }
    
    /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
    public Symbol visit(HStoreStmt n, InfoTable it) {
        
        Symbol s = new Symbol();
        Symbol s1 , s2;
        s1 = n.f1.accept(this, it);
        s2 = n.f3.accept(this, it);
        
        it.write_VarUse(s1.instruction);
        it.write_VarUse(s2.instruction);
        
        s.ident = null;
        s.instruction = "HSTORE " + s1.instruction + " " +n.f2.f0.tokenImage + " "+ s2.instruction; 
        s.origin = "HStoreStmt";
        
        return s;
    }
    
    /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
    public Symbol visit(HLoadStmt n, InfoTable it) {
        Symbol s = new Symbol();
        Symbol s1 , s2;
        s1 = n.f1.accept(this, it);
        s2 = n.f2.accept(this, it);
        
        it.write_VarUse(s2.instruction);
        
        it.write_VarDef(s1.instruction);
        
        s.ident = null;
        s.instruction = "HLOAD " + s1.instruction + " " + s2.instruction + " " +n.f3.f0.tokenImage; 
        s.origin = "HLoadStmt";
        
        return s;
    }
    
    /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
    public Symbol visit(MoveStmt n, InfoTable it) {
        Symbol s = new Symbol() ;
        Symbol s1,s2;
        s1 = n.f1.accept(this, it);
        s2 = n.f2.accept(this, it);
        s.ident = null;
        s.instruction = "MOVE "+ s1.instruction + " " + s2.instruction;
        s.origin = "MoveStmt";
        
        
        it.write_VarDef(s1.instruction);
        
        if(s2.origin!=null && s2.origin.compareTo("Temp")==0){
            it.write_VarUse(s2.instruction);
        }
        
        if(s1.origin!=null && s2.origin !=null && s1.origin.compareTo("Temp")==0 && s2.origin.compareTo("Temp")==0){
            it.write_VarMove(s1.instruction,s2.instruction);
        }
        
        if(s1.origin!=null && s2.origin !=null && s1.origin.compareTo("Temp")==0 && s2.origin.compareTo("IntegerLiteral")==0){
            it.write_ConstMove(s1.instruction,s2.instruction);
        }
        
        return s ;
    }
    
    /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
    public Symbol visit(PrintStmt n, InfoTable it) {
        Symbol s  = new Symbol();
        Symbol s1;
        
        s1 = n.f1.accept(this, it);
        
        if(s1.origin!=null && s1.origin.compareTo("Temp")==0){
            it.write_VarUse(s1.instruction);
        }
        
        
        s.ident = null;
        s.instruction = "PRINT " + s1.instruction;
        s.origin = "PrintStmt";
        
        
        return s;
    }
    
    
     /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
    public Symbol visit(StmtExp n, InfoTable argu) {
        
        InfoTable it = argu ;
        
        Symbol s = new Symbol() ;
        Symbol s1,s2;
        
        String str = new String(""); 
        
        for(Node node : n.f1.f0.nodes){
            s1 = node.accept(this, argu);
        }
        
        s2 = n.f3.accept(this, argu);
        str = "instruction(\""  + it.currentmethod + "\", " +  String.valueOf(it.instruction_num)
                  + ", \"RETURN " + s2.instruction + "\")";
        
        it.write_instruction(str);
        it.instruction_num++;
        it.write_instruction("instruction(\"" + it.currentmethod + "\", "+ it.instruction_num +", "+ "\"END\")");
        
        
        if(s2.origin!=null && s2.origin.compareTo("Temp")==0){
            it.write_VarUse(s2.instruction);
        }
        
        return s;
    }
    
    /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
    public Symbol visit(Call n, InfoTable it) {
        Symbol s = new Symbol();
        Symbol s1,s2 ;
        
        String str = new String(""); 
         
        
        s.ident =null;
        s1 = n.f1.accept(this, it);
       
        if(s1.origin!=null && s1.origin.compareTo("Temp")==0){
            it.write_VarUse(s1.instruction);
        }
        
        for(Node node : n.f3.nodes){
            s2 = node.accept(this, it);
            str = str + " " + s2.instruction;
            
            it.write_VarUse(s2.instruction);
        }
        
        s.instruction= new String("CALL " + s1.instruction + str );
        return s;
    }
     
    /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
    public Symbol visit(HAllocate n, InfoTable it) {
        Symbol s = new Symbol();
        Symbol s1;
        s1 = n.f1.accept(this, it);
        
        if(s1.origin!=null && s1.origin.compareTo("Temp")==0){
            it.write_VarUse(s1.instruction);
        }
        
        s.ident = null;
        s.instruction = new String("HALLOCATE " + s1.instruction);
        s.origin = new String("HAllocate");
        return s;
    }
    
    /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
    public Symbol visit(BinOp n, InfoTable it) {
        Symbol s = new Symbol();
        Symbol s1;
        Symbol s2;
        Symbol s3;
        s1 = n.f0.accept(this, it);
        s2 = n.f1.accept(this, it);
        s3 = n.f2.accept(this, it);
        
        if(s3.origin!=null && s3.origin.compareTo("Temp")==0){
            it.write_VarUse(s2.instruction);
            it.write_VarUse(s3.instruction);
        }
        else{
            it.write_VarUse(s2.instruction);
        }
        
        s.ident = null;
        s.instruction = new String(s1.ident+" "+s2.instruction+" "+s3.instruction);
        s.origin = new String("BinOp");
        
        return s;
    }
    
    public Symbol visit(NodeToken n, InfoTable argu) { 
        Symbol s= new Symbol();
        s.ident = n.tokenImage;
        s.instruction = n.tokenImage;
        switch (n.tokenImage) {
        
            case "PLUS":
                s.origin = new String("PLUS");
                break;
            case "MINUS":
                s.origin = new String("MINUS");
                break;
            case "TIMES":
                s.origin = new String("TIMES");
                break;
            case "LT":
                s.origin = new String("LT");
                break;
        }        
        
        return s; 
    }
   
    
    /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
    public Symbol visit(Temp n, InfoTable it) {
        Symbol s = new Symbol();
        s.ident = new String(n.f0.tokenImage);
        s.instruction = new String("TEMP "+ n.f1.f0.tokenImage);
        s.origin = new String("Temp");
        it.add_var(it.currentmethod,s.instruction);
        return s;
    }
   
    /**
    * f0 -> <INTEGER_LITERAL>
    */
    public Symbol visit(IntegerLiteral n, InfoTable argu) {
        Symbol s = new Symbol();
        s.ident = new String(n.f0.tokenImage);
        s.instruction = new String(n.f0.tokenImage);
        s.origin = new String("IntegerLiteral");
        return s;
    }
    
    /**
    * f0 -> <IDENTIFIER>
    */
    public Symbol visit(Label n, InfoTable it) {
        Symbol s = new Symbol();
        s.ident = new String(n.f0.tokenImage);
        s.instruction = new String(n.f0.tokenImage);
        s.origin = new String("Label"); 
        return s;
    }
    
    
}
