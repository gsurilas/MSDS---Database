import java.util.*;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;


public class driver {
 public static void main(String[] args) throws Exception{
     FdList fdlist=new FdList();
     Relation r=null;
     boolean first=true;
     //Read the input file
     File myFile=new File("C:\\Users\\George\\Documents\\Rutgers\\Database\\Project\\Project 2\\two.txt");
     Scanner sc=new Scanner(myFile);
     while (sc.hasNextLine()){
         String s1=sc.nextLine();
         if (first)
             r=new Relation(s1.replace(" ",""));
         else{
             int pos=s1.indexOf("->");
             if (pos>0){
                 String lhs=s1.substring(0, pos).replace(" ", "");
                 String rhs=s1.substring(pos+2, s1.length()).replace(" ", "");
                 fdlist.insert(new Fd(new Relation(lhs),new Relation(rhs)));
             }
         }
         first=false;
     }
     sc.close();

     System.out.println("-------------File Read----------------");
     System.out.println(r);
     System.out.println(fdlist);
     System.out.println("--------------------------------------");
  
     System.out.println("Below are all the non-trivial functional dependencies");
     
     
     //Compute the non-trivial functional dependencies
     Relation powersetfirst = r.powerSetFirst();
     Relation closurefirst = fdlist.closure(powersetfirst);
     if(powersetfirst.equals(closurefirst) == false){
         System.out.println(powersetfirst.toString() + "->" + closurefirst.toString());
     }
                
     //Get the next powerset of the relation.
     Relation powersetnext = r.powerSetNext();
        
     //Boolean that checks that the next powerset is not the first powerset.
     //When the first powerset is returned, we've looped through all possible powersets        
     boolean nextnotfirst = true;
     while(nextnotfirst){
         Relation closurenext = fdlist.closure(powersetnext);
         if(!powersetnext.equals(closurenext)){
             System.out.println(powersetnext.toString() + "->" + closurenext.toString());
         }
         powersetnext = r.powerSetNext();
         if(powersetnext.equals(powersetfirst)){
             nextnotfirst = false;
         }
     }

     System.out.println("Below are all the non-trivial functional dependencies that are BCNF violations");
        
     //Compute the non-trivial functional dependencies
     Relation powersetfirst_bcnf = r.powerSetFirst();
     Relation closurefirst_bcnf = fdlist.closure(powersetfirst_bcnf);
     if(!powersetfirst_bcnf.equals(closurefirst_bcnf)){
         Fd fdfirst_bcnf = new Fd(powersetfirst_bcnf,closurefirst_bcnf);
         if(fdfirst_bcnf.BCNFviolation(r)){
             System.out.println(powersetfirst_bcnf.toString() + "->" + closurefirst_bcnf.toString());
         }
     }
                
     //Get the next powerset of the relation.
     Relation powersetnext_bcnf = r.powerSetNext();
        
     //Boolean that checks that the next powerset is not the first powerset.
     //When the first powerset is returned, we've looped through all possible powersets        
     boolean nextnotfirst_bcnf = true;
     while(nextnotfirst_bcnf){
         Relation closurenext_bcnf = fdlist.closure(powersetnext_bcnf);
         if(!powersetnext_bcnf.equals(closurenext_bcnf)){
             Fd fdnext_bcnf = new Fd(powersetnext_bcnf, closurenext_bcnf);
             if(fdnext_bcnf.BCNFviolation(r)){
                 System.out.println(powersetnext_bcnf.toString() + "->" + closurenext_bcnf.toString());
             }
         }
         powersetnext_bcnf = r.powerSetNext();
         if(powersetnext_bcnf.equals(powersetfirst_bcnf)){
             nextnotfirst_bcnf = false;
         }
     }


 }
 
}

class Relation 
{
        
     
    //Below are the methods for the Relation class
    //Array with length 127 for the 127 ASCII characters
    private int[] relation = new int[127];
    List<String> powerSet = new ArrayList<String>();
    int setindex = 1;
    
    //Relation Constructor
    //Takes given string, converts the characters in string to their ASCII values
    //and inputs 1 in the array indices corresponding to the ASCII values of the chars
    public Relation(String in_r){
        for(int i = 0; i<in_r.length(); i++){
            char temp = in_r.charAt(i);
            int ascii_value = (int) temp;
            relation[ascii_value] = 1;    
        }
    }
    
    //Starts with blank string. Converts the array indices with 1 in them into their ASCII values
    //and concatenates onto the blank string
    public String toString(){
        
        String s = "";
        
        for(int i = 0; i < 127; i++){
            if(relation[i] == 1){
                char temp = (char) i;
                s = s + temp;
            }
        }
        return s;
    }
    
    //Convert both the relations to Strings using toString method.
    //Check if the two strings are equal
    public boolean equals(Relation r2){ 
        
        boolean equals;
        
        String temp1 = this.toString();
        String temp2 = r2.toString();
        if(temp1.equals(temp2)){
            equals = true;
        }
        else{
            equals = false;
        }
        return equals;
    }
    
    
    //Converts char c to its ascii value and checks if there is a 1 in that slot of the relation
    public boolean contains(char c){
        
        boolean contains;
        int ascii_value = (int) c;
        if(relation[ascii_value] == 1){
            contains = true;
        }
        else{
            contains = false;
        }      
        return contains;     
    }
    
    //Converts the relations to their String form then checks if the Relation contains r2, ignoring order
    public boolean subset(Relation r2){
        
        boolean subset = false;
        
        String temp1 = this.toString();
        String temp2 = r2.toString();
        
        if(temp2.length() > temp1.length()){
            return subset;
        }
               
        for(int i = 0; i<temp2.length(); i++){
            
            String s = String.valueOf(temp2.charAt(i));
            
            if(temp1.contains(s)){
                subset = true;
            }
            //If one false value is found then the relation can't be a subset. So we break out of the loop and return
            //false
            else{
                subset = false;
                break;
            }            
        }
        return subset;
    }
    
    //Helper method for the powerSetFirst and powerSetNext methods.
    //Makes use of bitwise operations.
    private List<String> relPowerSet(){
        
        String rel = this.toString();
        char[] set = rel.toCharArray();
        int n = set.length;
        
        for(int i =0; i < (1<<n); i++){
            String pSet = "";
            for(int j = 0; j <n; j++){
                if((i & (1 << j)) > 0)
                    pSet += set[j];
            }
            
            if(! pSet.equals("")){
                this.powerSet.add(pSet);
            }
        }
        
        return this.powerSet;
        
    }

    //Returns the first powerSet 
    public Relation powerSetFirst(){
        
        //Computes the powerSet using the private method
        //Takes the first string in the Powerset, converts it to 
        //a relation and returns it
        
        List<String> set = this.relPowerSet();
        Relation r = new Relation(set.get(0));
        return r;
    }
    
    //Returns the next powerSet. Use an index to keep track of what the next powerset is.
    //When the index becomes larger than the length of the set. It resets to 0 and return the first
    //powerset.
    public Relation powerSetNext(){
        
        //Computes the powerset using the private method.
        //Takes the indexed string in the powerset, converts to Relation
        //and returns it. If it gets to the end of the list it returns the
        //first relation in the powerset.
        
        List<String> set = this.relPowerSet();
        
        if(setindex > set.size()){
            setindex = 0;
        }
        
        Relation r = new Relation(set.get(setindex));
        setindex++;
        return r;
    }
    
    //Converts the relations to strings. computes the union of them and returns
    //the union as a relation.
    public Relation union(Relation r2){
        
        String temp1 = this.toString();
        String temp2 = r2.toString();
        String s = temp1 + temp2;
        
        int i = 0;
        
        while(i < s.length()){
        char c = s.charAt(i);
        if(i != s.lastIndexOf(c)) //If c occurs multiple times in s, remove first one
            s = s.substring(0, i) + s.substring(i+1, s.length());
        else i++; //otherwise move pointer forward
    }
        //Convert the new union-ized string back into a relation and return it.
        Relation r = new Relation(s);
        return r;
    }
    
    //Convert the relations to Strings and returns their intersection as a Relation
    public Relation intersect(Relation r2){
        
        String s = "";
        String temp1 = this.toString();
        String temp2 = r2.toString();
        
        for(char c : temp1.toCharArray()){
            if(temp2.indexOf(c) != -1 && s.indexOf(c) == -1)
                s += c;
        }

        Relation r = new Relation(s);       
        return r;
    }
    
    //Returns the set difference between two relations. So ABC.setDiff(A) should return
    //BC. Thought this could be useful in the BCNF decomposition, but I was unable to figure it out.
    public Relation setDiff(Relation r2){
                        
        String temp1 = this.toString();
        String temp2 = r2.toString();
        String setdiff = temp1.replaceAll(temp2," ");
        Relation r = new Relation(setdiff);
        return r;

    }
}

class Fd
{
    //store the FD in an array of length 2. LHS goes in index 0, RHS in index 1.
    private Relation[] fd = new Relation[2];
    
    public Fd(Relation in_lhs, Relation in_rhs){
        
        fd[0] = in_lhs;
        fd[1] = in_rhs;               
    }  
    
    public Relation getLHS(){
        return fd[0];
    }
    
    public Relation getRHS(){
        return fd[1];
    }
    
    //Extract the LHS and RHS of the FD, then use the .toString() method from the Relations class
    //Concatenate the two strings together with an "->" in the middle to better illustrate the FD.
    public String toString(){
        
        String temp1 = this.getLHS().toString();
        String temp2 = this.getRHS().toString();
        String s = temp1 + "->" + temp2;
        return s;
    }
    
    //A functional dependency F is not a BCNF violation with respect to a set of attributes r
    //if the left hand side of F contains only attributes in r, and all the
    //atrributes in A must appear in the left and right hand sides
    public boolean BCNFviolation(Relation r){
        
        boolean violation;
        //Get the lhs, rhs and union of the lhs/rhs of the FD
        Relation lhs = this.getLHS();
        Relation rhs = this.getRHS();
        Relation union = lhs.union(rhs);
        
        //Check if lhs of FD is contained by r and if union of lhs/rhs equals r
        if(r.subset(lhs) && union.equals(r)){
            violation = false;
        }
        else{
            violation = true;
        }

        return violation;
    }   
}

class FdList
{
    
    //Store the FDs in an array list
    ArrayList<Fd> fdlist = new ArrayList<Fd>();
    
    //Index will be used in the getNext method
    int nextfdindex = 1;
    
    //Constructor
    public FdList(){       
    }
    
    //Makes use of the Fd.toString() method and outputs the string form of each
    //Fd in the Fdlist
    public String toString(){
        
        String s = "";
        for(Fd fd : fdlist){
            String x = fd.toString() + " ";
            s = x + s;
        }
        return s;        
    }
    
    //Insert new Fd into Fdlist
    public void insert(Fd f){
        fdlist.add(f);
    }
    
    //Get method where you can specify the index
    public Fd get(int i){
        
        Fd fd = fdlist.get(i);
        return fd;
    }
    
    public Fd getFirst(){
        
        Fd fd = fdlist.get(0);
        return fd;
    }
    
    //Similar to powerset method. When the index is greater than the list size
    //it gets reset to 0 and we return the first FD.
    public Fd getNext(){
        
        int size = this.getSize();
        //if index greater than list size. just return first element
        //of list again
        
        if(nextfdindex >= size){
            nextfdindex = 0;
        }
        
        Fd fd = fdlist.get(nextfdindex);     
        nextfdindex++;  
               
        return fd;                                     
    }
    
    //returns the length of the Fd list. Use in getNext() method
    //so I don't go over the index length.
    public int getSize(){
        
        int size;
        size = fdlist.size();
        return size;
    }  
    
    //computes the closure of the attribute set r with respect to the whole FDlist
    //Goes through each FD and checks if r is contained in the LHS of the FD.
    //If it is, then the new closure set becomes r+rhs of FD. Then we loop through the FDlist again
    //checking if the lhs of any of the FDs is equal to some subset of the closure set.
    //If so, we continue adding relations to the closure set. If after a loop, no changes are made to the closure set
    //then we have finished computing the closure and we return the closure as a relation.
    public Relation closure(Relation r){
        
        boolean modified = true;      
        Relation closure = r;
        Relation closureBefore;
        
        //Do this until the closure set stops being modified
        while(modified){
            modified = false;   
            
            //Get the first FD from the fdlist. And check to see if its part of the closure set
            Fd fdfirst = this.getFirst();
            Relation firstlhs = fdfirst.getLHS();
            Relation firstrhs = fdfirst.getRHS();
            
            //This is a fail-safe to prevent the code from looping forever. It stores the closure before any operations are
            //done on the set. If at the end of this method, closureBefore = closure. Ie, the set hasn't been modified.
            //Then the closure set has been found, and we exit the while loop.
            closureBefore = closure;
                  
            if(closure.subset(firstlhs) == true){
                closure = closure.union(firstrhs);
                modified = true;
            }
            
            
            Fd fdnext = this.getNext();
            boolean nextnotfirst = true;
            
            //Continue getting the next fd in the list, until it loops back to the beginning of the list.
            //Add any FDs to the closure that are missing.
            while(nextnotfirst){
                Relation lhs = fdnext.getLHS();
                Relation rhs = fdnext.getRHS();
                if(closure.subset(lhs)){
                    closure = closure.union(rhs);
                    modified = true;
                }
                fdnext = this.getNext();
                
                Relation nextlhs = fdnext.getLHS();
                Relation nextrhs = fdnext.getRHS();
                if(nextlhs.equals(firstlhs) && nextrhs.equals(firstrhs)){
                    nextnotfirst = false;
                }                
            }
            
            if(closureBefore.equals(closure)){
                modified = false;
            }
        } 
        return closure;
    }
}

class RelList
{
    //Store Relations in an Array list. Most of the functionality here is just modified 
    //version of the Arraylist methods.
    
    ArrayList<Relation> relationList = new ArrayList<Relation>();
    int nextrelindex = 1;
    
    public RelList(){
    }
    
    public String toString(){
        
        String s = "";
        for(Relation rel : relationList){
            s = rel.toString() + " ";
        }
        return s;
    }
    
    public void insert(Relation r){
        
        relationList.add(r);
    }
    
    public Relation getFirst(){
        
        Relation rel = relationList.get(0);
        return rel;
    }
    
    public Relation getNext(){
        
        int size = this.getSize();
        if(nextrelindex > size){
            nextrelindex = 0;
        }
        
        Relation rel = relationList.get(nextrelindex);     
        nextrelindex++;  
        
        return rel;    
    }
    
    //Use in getNext method to prevent us from going over the number of Relations in the Relation list
    public int getSize(){
        
        int size;
        size = relationList.size();
        return size;
    }  
    
    
}