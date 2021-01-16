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
	public ArrayList<Tuple> cons;
	//public ArrayList<Structure> poid;
	
	public int involved = -1;
	public int involving = -1;
	public ArrayList<ArrayList<Integer>> ImplicantValues;
	public int[] ImpliedValues;
	
	public VDD vdd;
	public UniqueHashTable uht;
	
	public Constraint(String name, 	int arity, int nbTuples) {
		this.name=name;
		this.arity=arity;
		this.nbTuples=nbTuples;
		
		scopeVar = new ArrayList<Var>(arity);
		cons = new ArrayList<Tuple>(nbTuples);
	}
	
	public Constraint() {
		scopeVar = new ArrayList<Var>();
		cons = new ArrayList<Tuple>();
	}
	
	public Constraint Copie() {
		Constraint c = new Constraint(this.name, this.arity, this.nbTuples);
		if(this.defaultCost!=null)
			c.defaultCost=this.defaultCost.copie();
		else
			c.defaultCost=null;
		c.softConstraint=this.softConstraint; 
		c.conflictsConstraint=this.conflictsConstraint; 
		 
		c.scopeVar= (ArrayList<Var>)this.scopeVar.clone();
		c.cons=new ArrayList<Tuple>();
		for(int i=0; i<this.cons.size(); i++)
			c.cons.add(this.cons.get(i).Copie());
				
		c.involved=this.involved;
		c.involving=this.involving;
		if(this.ImplicantValues!=null)
			c.ImplicantValues=(ArrayList<ArrayList<Integer>>)this.ImplicantValues.clone();
		else
			c.ImplicantValues=null;
		if(this.ImpliedValues!=null)
			c.ImpliedValues=ImpliedValues.clone();
		else
			c.ImpliedValues=null;
		
		return c;
	}
	
	public Structure[] getPoidTab() {
		Structure[] s=new Structure[nbTuples];
		for(int i=0; i<nbTuples; i++)
			s[i]=cons.get(i).poid;
		
		return s;
	}
	
	public int[][] getConsTab() {
		int[][] c=new int[nbTuples][arity];
		for(int i=0; i<nbTuples; i++) {
			for(int j=0; j<arity; j++) {
				c[i][j]=cons.get(i).tuple.get(j);
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
			if(i>=0 && scopeVar.get(i).pos>scopeVar.get(i+1).pos) {
				Collections.swap(scopeVar, i, i+1);
				for(int j=0; j<cons.size(); j++) {
					cons.get(j).swap(i, i+1);
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
	
	public boolean removeDoulonTuples() {
		boolean doublonRemoved=false;
		for(int i=0; i<cons.size(); i++) {
			for(int j=i+1; j<cons.size(); j++) {
				if(cons.get(i).equal(cons.get(j))) {
					cons.remove(j);
					doublonRemoved=true;
					j--;
				}
			}
		}
		return doublonRemoved;
	}
	
	public void updateImplicationBinary() {
		if(arity == 2 && softConstraint==false && conflictsConstraint==false) {
			for(int a=0; a<this.arity; a++) {
				if(this.nbTuples<=scopeVar.get(a).domain){
					boolean impl = true;
					
					doubleloop:
					for(int i=0; i<nbTuples; i++) {
						for(int j=i+1; j<nbTuples; j++) {
							if(cons.get(i).tuple.get(a).equals(cons.get(j).tuple.get(a))) {
								impl = false;
								break doubleloop;
							}
						}	
					}
					
					if(impl) {
						involving=a;
						involved=1-a;
						System.out.println(scopeVar.get(involving).name+"->"+scopeVar.get(involved).name+" ("+this.name+")");
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
		ArrayList<Tuple> newCons = new ArrayList<Tuple>();
		Tuple currTuple;
		ArrayList<Structure> newPoid = new ArrayList<Structure>();

		
		
		
		for(int i=0; i<scopeVar.size(); i++) {
			if(scopeVar.get(i).id == vb.id) {
				for(int j=0; j<nbTuples; j++) {
					val=cons.get(j).tuple.get(i);
					array=cI.getImplicantValues(val);
					
					for(Integer newVal:array) {
							currTuple=cons.get(j).Copie();
							currTuple.set(i, newVal);
							currTuple.poid=cons.get(i).poid.copie();
							newCons.add(currTuple);
						
						//si ca ne concorde pas, on ne la garde pas
					}
										
				}
				
		
					scopeVar.set(i, va);
					System.out.println("in "+this.name+" : " + vb.name +" remplaced by "+ va.name + " ("+cons.size()+"->"+newCons.size()+")");
					cons=newCons;
					nbTuples=cons.size();
				
					this.reorderConstraint();

				break;
			}
		}
	}
	public void replaceVariable2(Var vb, Var va, Constraint cI, int vaWasHere) {//!=-1
		int implied;
		int val;
		ArrayList<Tuple> newCons = new ArrayList<Tuple>();
		Tuple newTuple;
		
		
		for(int i=0; i<scopeVar.size(); i++) {
			if(scopeVar.get(i).id == vb.id) {
				
			    Iterator<Tuple> itr = cons.iterator();
			    int j=0;
			    while (itr.hasNext()) {
			    	Tuple tuple = itr.next();
			    	val=tuple.tuple.get(vaWasHere);
					implied=cI.getImpliedValues(val);
			    	if (tuple.tuple.get(i)==implied) {
			    		newTuple = tuple.Copie();
			    		newTuple.tuple.remove(i);
			    		newCons.add(newTuple);
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
					arity--;

				break;
			}
		}
		
	}
	
	/*public void a(int j) {
		cons.remove(j);
		poid.remove(j);
	}
	public void b(int i, int j) {
		cons.get(j).remove(i);
	}*/

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
				ImplicantValues.get(cons.get(i).tuple.get(involved)).add(cons.get(i).tuple.get(involving));
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
				ImpliedValues[cons.get(i).tuple.get(involving)]= cons.get(i).tuple.get(involved);
			}
			
			
		}
		
		return ImpliedValues[val];
	}
	
	public void toVDD(boolean arg_plus) {
		reorderConstraint();
		
		ArrayList<Integer> saveOrder=fakeOrder();
		
		uht=new UniqueHashTable(arity);
		vdd =new VDD(scopeVar, uht, arg_plus);
		
		//init nextTuples
		ArrayList<Integer> nextTuplesIn=new ArrayList<Integer>();
		for(int i=0; i<nbTuples; i++) {
			nextTuplesIn.add(i);
		}
		
		uht.removeFromTable(vdd.first.fils);
		addSupport(vdd.first.fils, 0, nextTuplesIn);
		uht.ajoutSansNormaliser(vdd.first.fils);

		uht.normaliser();
		uht.supprNeudNegatifs();
		uht.copieToNull();			//+ a remonter to null
		uht.cptTo(0);
		
		vdd.uht.rechercheNoeudInutile();

		goBackToRightOrder(saveOrder);
	}
	
	//todo PM
	public void addSupport(NodeDD node, int etage, ArrayList<Integer> tuplesIn) {
		NodeDD newFils;
		ArrayList<Integer> nextTuplesIn=new ArrayList<Integer>();
		for(int i=0; i<scopeVar.get(etage).domain; i++) {
			
			nextTuplesIn.clear();
			for(int j=0; j<tuplesIn.size(); j++) {
				if(cons.get(tuplesIn.get(j)).tuple.get(etage)==i) {
					nextTuplesIn.add(tuplesIn.get(j));
					tuplesIn.remove(j);			//remove index
					j--;
				}
			}
			if(nextTuplesIn.size()!=0) {
				if(etage+1<arity) {
					if(node.kids.get(i).fils.fathers.size()>1) {					//si il en reste non attribue
					
						newFils=new NodeDD(node.kids.get(i).fils, node.kids.get(i));
						newFils.cpt=1;

						addSupport(newFils, etage+1, nextTuplesIn);
					
						uht.ajoutSansNormaliser(newFils);
					}else {	//it's the last one, no duplicate
						uht.removeFromTable(node.kids.get(i).fils);
						addSupport(node.kids.get(i).fils, etage+1, nextTuplesIn);
						uht.ajoutSansNormaliser(node.kids.get(i).fils);

					}
					
				}else {
						//todo add valuation
				}
					

			}else {										
				node.kids.get(i).bottom++;
			}
			
		}
		
	}
	
	//put variable with wrong position indice to fake uht. saved indices are returned
	public ArrayList<Integer> fakeOrder(){
		ArrayList<Integer> saveOrder=new ArrayList<Integer>();
		for(int i=0; i<scopeVar.size(); i++) {
			saveOrder.add(scopeVar.get(i).pos);
			scopeVar.get(i).pos=i;
		}
		return saveOrder;
	}
	//put variable with wrong position indice to fake uht. saved indices are returned
	public void goBackToRightOrder(ArrayList<Integer> order){
		for(int i=0; i<scopeVar.size(); i++) {
			scopeVar.get(i).pos=order.get(i);
		}
	}
	
}


