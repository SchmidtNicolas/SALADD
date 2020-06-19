package compilateur;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Constraint {
	public String name;
	public int arity;
	public int nbTuples;
	public Structure defaultCost; 
	public boolean softConstraint; 
	public boolean conflictsConstraint; 
	
	public ArrayList<Integer> scopeID;
	public ArrayList<ArrayList<Integer>> cons;
	public ArrayList<Structure> poid;
	
	public Constraint(String name, 	int arity, int nbTuples) {
		this.name=name;
		this.arity=arity;
		this.nbTuples=nbTuples;
		
		scopeID = new ArrayList<Integer>(arity);
		cons = new ArrayList<ArrayList<Integer>>(nbTuples);
		poid = new ArrayList<Structure>(nbTuples);
	}
	
	public Constraint() {
		scopeID = new ArrayList<Integer>();
		cons = new ArrayList<ArrayList<Integer>>();
		poid = new ArrayList<Structure>();
	}
	
	public Structure[] getPoidTab() {
		Structure[] s=new Structure[nbTuples];
		for(int i=0; i<nbTuples; i++)
			s[i]=poid.get(i);
		
		return s;
	}
	
	public int[][] getConsTab() {
		int[][] c=new int[nbTuples][arity];
		for(int i=0; i<nbTuples; i++) {
			for(int j=0; j<arity; j++) {
				c[i][j]=cons.get(i).get(j);
			}
		}
		return c;
		
	}
	
	
	public void setScopeID(String scope, ArrayList<Var> vars) {
		String scopeS[]=scope.split(" ");
		for(int i=0; i<scopeS.length; i++) {
			for(int j=0; j<vars.size(); j++) {
				if(scopeS[i].compareTo(vars.get(j).name)==0) {
					scopeID.add(j);
					break;
				}
			}
		}
	}
	
	//reordonner la contrainte pour respecter l'ordre naturel
	public void reorderConstraint(){
		for(int i=0; i<arity-1; i++) {
			if(i>=0 && scopeID.get(i)>scopeID.get(i+1)) {
				Collections.swap(scopeID, i, i+1);
				for(int j=0; j<cons.size(); j++) {
					Collections.swap(cons.get(j), i, i+1);
				}
				i-=2;
			}
		}
	}
	
	public float computPercentOfRefusedTuples(ArrayList<Var> vars) {
		float productOfDomains=1;
		float divi=0;
		for(int i=0; i<scopeID.size(); i++) {
			productOfDomains*=vars.get(scopeID.get(i)).domain;
		}
		
		if(conflictsConstraint) {
			divi=nbTuples/productOfDomains;
		}else {
			divi=(productOfDomains-nbTuples)/productOfDomains;
		}
		
		return divi*100;
	}
	
}


