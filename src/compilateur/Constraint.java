package compilateur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Constraint {
	public String name;
	public int arity;
	public int nbTuples;
	public Structure defaultCost; 
	public boolean softConstraint; 
	public boolean conflictsConstraint; 
	
	public ArrayList<Var> scopeVar;
	public ArrayList<ArrayList<Integer>> cons;
	public ArrayList<Structure> poid;
	
	public int involved = -1;
	public int involving = -1;
	public ArrayList<ArrayList<Integer>> ImplicantValues;
	public int[] ImpliedValues;
	
	public Constraint(String name, 	int arity, int nbTuples) {
		this.name=name;
		this.arity=arity;
		this.nbTuples=nbTuples;
		
		scopeVar = new ArrayList<Var>(arity);
		cons = new ArrayList<ArrayList<Integer>>(nbTuples);
		poid = new ArrayList<Structure>(nbTuples);
	}
	
	public Constraint() {
		scopeVar = new ArrayList<Var>();
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
					scopeVar.add(vars.get(j));
					break;
				}
			}
		}
	}
	
	//reordonner la contrainte pour respecter l'ordre naturel
	public void reorderConstraint(){
		for(int i=0; i<arity-1; i++) {
			if(i>=0 && scopeVar.get(i).id>scopeVar.get(i+1).id) {
				Collections.swap(scopeVar, i, i+1);
				for(int j=0; j<cons.size(); j++) {
					Collections.swap(cons.get(j), i, i+1);
				}
				i-=2;
			}
		}
	}
	
	public float computPercentOfRefusedTuples() {
		float productOfDomains=1;
		float divi=0;
		for(int i=0; i<scopeVar.size(); i++) {
			productOfDomains*=scopeVar.get(i).domain;
		}
		
		if(conflictsConstraint) {
			divi=nbTuples/productOfDomains;
		}else {
			divi=(productOfDomains-nbTuples)/productOfDomains;
		}
		
		return divi*100;
	}
	
	public void updateImplicationBinary() {
		if(arity == 2 && softConstraint==false && conflictsConstraint==false) {
			for(int a=0; a<this.arity; a++) {
				if(this.nbTuples<=scopeVar.get(a).domain){
					boolean impl = true;
					
					doubleloop:
					for(int i=0; i<nbTuples; i++) {
						for(int j=i+1; j<nbTuples; j++) {
							if(cons.get(i).get(a) == cons.get(j).get(a)) {
								impl = false;
								break doubleloop;
							}
						}	
					}
					
					if(impl) {
						involving=a;
						involved=1-a;
						System.out.println(scopeVar.get(involving).name+"->"+scopeVar.get(involved).name);
						break;
					}
					
				}
			}
		}
	}
	
	//a->b so b is to be remplaced by a 
	public void replaceVariable(Var vb, Var va, Constraint cI) {
		ArrayList<Integer> array;
		int val;
		
		int vaWasHere=-1;
		for(int i=0; i<scopeVar.size(); i++) {
			if(scopeVar.get(i).id == va.id) 
				vaWasHere=i;
		}
		
		if(vaWasHere==-1)
			replaceVariable1(vb, va, cI);
		else
			replaceVariable2(vb, va, cI, vaWasHere);

		
		/*ArrayList<ArrayList<Integer>> newCons = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> currTuple;
		ArrayList<Structure> newPoid = new ArrayList<Structure>();

		
		
		
		for(int i=0; i<scopeVar.size(); i++) {
			if(scopeVar.get(i).id == vb.id) {
				for(int j=0; j<nbTuples; j++) {
					val=cons.get(j).get(i);
					array=cI.getImplicantValues(val);
					
					for(Integer newVal:array) {
						if(vaWasHere==-1) {			//si va n'existait pas dans la contrainte
							currTuple=(ArrayList<Integer>) cons.get(j).clone();
							currTuple.set(i, newVal);
							newCons.add(currTuple);
							newPoid.add(poid.get(j).copie());
						}
						if(vaWasHere!=-1 && cons.get(j).get(vaWasHere)==newVal) {		//si va existait et que la valeur correspond, alors c'est tout bon, on enleve juste
							currTuple=(ArrayList<Integer>) cons.get(j).clone();
							currTuple.remove(i);
							newCons.add(currTuple);
							newPoid.add(poid.get(j).copie());
						}
						//si ca ne concorde pas, on ne la garde pas
					}
										
				}
				
				if(vaWasHere==-1){
					scopeVar.set(i, va);
					cons=newCons;
					nbTuples=cons.size();
					poid = newPoid;
					System.out.println("in "+this.name+" : " + vb.name +" remplaced by "+ va.name);
				}else {
					scopeVar.remove(i);
					cons=newCons;
					nbTuples=cons.size();
					poid = newPoid;
					arity--;
					System.out.println("in "+this.name+" : " + vb.name +" removed");

				}
				break;
			}
		}
		this.reorderConstraint();*/
	}
	
	
	public void replaceVariable1(Var vb, Var va, Constraint cI) {//==-1
		ArrayList<Integer> array;
		int val;
		ArrayList<ArrayList<Integer>> newCons = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> currTuple;
		ArrayList<Structure> newPoid = new ArrayList<Structure>();

		
		
		
		for(int i=0; i<scopeVar.size(); i++) {
			if(scopeVar.get(i).id == vb.id) {
				for(int j=0; j<nbTuples; j++) {
					val=cons.get(j).get(i);
					array=cI.getImplicantValues(val);
					
					for(Integer newVal:array) {
							currTuple=(ArrayList<Integer>) cons.get(j).clone();
							currTuple.set(i, newVal);
							newCons.add(currTuple);
							newPoid.add(poid.get(j).copie());
						
						//si ca ne concorde pas, on ne la garde pas
					}
										
				}
				
		
					scopeVar.set(i, va);
					System.out.println("in "+this.name+" : " + vb.name +" remplaced by "+ va.name + " ("+cons.size()+"->"+newCons.size()+")");
					cons=newCons;
					nbTuples=cons.size();
					poid = newPoid;
				
					this.reorderConstraint();

				break;
			}
		}
	}
	public void replaceVariable2(Var vb, Var va, Constraint cI, int vaWasHere) {//!=-1
		int implied;
		int val;
		ArrayList<ArrayList<Integer>> newCons = new ArrayList<ArrayList<Integer>>();
		ArrayList<Structure> newPoid = new ArrayList<Structure>();
		ArrayList<Integer> newTuple;
		
		
		for(int i=0; i<scopeVar.size(); i++) {
			if(scopeVar.get(i).id == vb.id) {
				
			    Iterator<ArrayList<Integer>> itr = cons.iterator();
			    int j=0;
			    while (itr.hasNext()) {
			    	ArrayList<Integer> tuple = itr.next();
			    	val=tuple.get(vaWasHere);
					implied=cI.getImpliedValues(val);
			    	if (tuple.get(i)==implied) {
			    		newTuple = (ArrayList<Integer>) tuple.clone();
			    		newTuple.remove(i);
			    		newCons.add(newTuple);
			    		newPoid.add(poid.get(j));
			    	}
			    	j++;
			    }
				
				/*for(int j=0; j<cons.size(); j++) {

					//System.out.println(j+"/"+cons.size());
					val=cons.get(j).get(vaWasHere);
					implied=cI.getImpliedValues(val);
					
	
						if(cons.get(j).get(i)!=implied) {
							cons.remove(j);
							poid.remove(j);
							j--;
						} else {
							cons.get(j).remove(i);
						}
						
						//si ca ne concorde pas, on ne la garde pas
				}*/

			
										
			
				
				
					scopeVar.remove(i);
					System.out.println("in "+this.name+" : " + vb.name +" removed" + " ("+cons.size()+"->"+newCons.size()+")");
					cons=newCons;
					nbTuples=cons.size();
					poid = newPoid;
					arity--;

				break;
			}
		}
		
	}
	
	public void a(int j) {
		cons.remove(j);
		poid.remove(j);
	}
	public void b(int i, int j) {
		cons.get(j).remove(i);
	}

	//if a->b and b=val, return arraylist(a)
	public ArrayList<Integer> getImplicantValues(int val){
		if(ImplicantValues==null) {
			//init
			Var impliedVar = scopeVar.get(involved);
			ImplicantValues = new ArrayList<ArrayList<Integer>>();
			for(int i=0; i<impliedVar.domain; i++) {
				ArrayList<Integer> array=new ArrayList<Integer>();
				ImplicantValues.add(array);
			}
			
			//assign
			for(int i=0; i<nbTuples; i++) {
				ImplicantValues.get(cons.get(i).get(involved)).add(cons.get(i).get(involving));
			}
			
			
		}
		
		return ImplicantValues.get(val);
	}
	
	//if a->b and a=val, return b
	public int getImpliedValues(int val){
		if(ImpliedValues==null) {
			//init
			Var impliedVar = scopeVar.get(involving);
			ImpliedValues = new int[impliedVar.domain];
			for(int i=0; i<impliedVar.domain; i++) {
				ImpliedValues[i]=-1;
			}
			
			//assign
			for(int i=0; i<nbTuples; i++) {
				ImpliedValues[cons.get(i).get(involving)]= cons.get(i).get(involved);
			}
			
			
		}
		
		return ImpliedValues[val];
	}
	
}


