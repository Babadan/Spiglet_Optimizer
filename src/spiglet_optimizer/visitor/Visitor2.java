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
import syntaxtree.IntegerLiteral;
import syntaxtree.JumpStmt;
import syntaxtree.Label;
import syntaxtree.Node;
import syntaxtree.NodeToken;
import syntaxtree.Procedure;
import syntaxtree.Stmt;
import syntaxtree.StmtExp;
import syntaxtree.Temp;
import visitor.GJDepthFirst;

/**
 *
 * @author lumberjack
 */
public class Visitor2 extends  GJDepthFirst <Symbol,InfoTable>  {
    
    
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
        it.instruction_num = 0;
        
        it.currentmethod = "MAIN";
        
        for(Node node : n.f1.f0.nodes){
            s1 = node.accept(this, it);    
        }
        
        for(Node node : n.f3.nodes){
           node.accept(this, it);
        }
        
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
        
        s1=n.f0.accept(this, it);
        if(s1==null || s1.origin==null || s1.origin.compareTo("JumpStmt")!=0){
            it.write_next(null, 0);
        }
        
        if(s1!=null && s1.origin!=null && s1.origin.compareTo("ErrorStmt")==0){
            it.write_next(null, 3);
        }

        
        it.instruction_num++;
        
        s.ident = null;
        s.instruction = null;
        s.origin = "Stmt";
                
        return s;
    }
    
    /* f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
    public Symbol visit(Procedure n, InfoTable it) {
        Symbol s = new Symbol();
        Symbol s1 ;
        
        s1 = n.f0.accept(this, it);
        
        it.currentmethod = s1.ident; 
        n.f2.accept(this, it);
        
        n.f4.accept(this, it);
        
        s.ident = null;
        s.instruction = null;
        s.origin = "Procedure";
        
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
        
        it.write_next(s2.instruction,2);
        
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
        
        it.write_next(s1.instruction,2);
        
        s.ident =null;
        s.instruction = "JUMP " + s1.instruction;
        s.origin = "JumpStmt";
        
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
        
        for(Node node : n.f1.f0.nodes){
            s1 = node.accept(this, argu);
        }
        
        s2 = n.f3.accept(this, argu);
        it.instruction_num++;
        
        return s;
    }
    
    /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
    public Symbol visit(Call n, InfoTable argu) {
        Symbol s = new Symbol();
        Symbol s1,s2 ;
        
        String str = new String(""); 
        
        s.ident =null;
        s1 = n.f1.accept(this, argu);
       
        for(Node node : n.f3.nodes){
            s2 = node.accept(this, argu);
            str = str + " " + s2.instruction;
        }
        
        s.instruction= new String("CALL " + s1.instruction + str );
        return s;
    }
    
      /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
    public Symbol visit(BinOp n, InfoTable argu) {
        Symbol s = new Symbol();
        Symbol s1;
        Symbol s2;
        Symbol s3;
        s1 = n.f0.accept(this, argu);
        s2 = n.f1.accept(this, argu);
        s3 = n.f2.accept(this, argu);
        
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
        s.origin = new String("TEMP");
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
