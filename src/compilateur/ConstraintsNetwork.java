package compilateur;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

import compilateur.heuristique_contraintes.HeuristiqueContraintes;
import compilateur.heuristique_variable.HeuristiqueVariable;

public class ConstraintsNetwork {
	private ArrayList<Constraint> constraints;
	private ArrayList<Var> vars;
	public int nbVariables;
	public int nbConstraints;
	
	public int[][] graphAdjVarVar;
	public ArrayList<ArrayList<Integer>> graphAdjConsVar;
	public ArrayList<Integer> OccurenceVariableDansContraintes;		//nombre de contraintes pour chaque variable

	
	public ConstraintsNetwork() {
		constraints=new ArrayList<Constraint>();
		vars=new ArrayList<Var>();
		graphAdjVarVar=null;
		graphAdjConsVar=new ArrayList<ArrayList<Integer>>();
		OccurenceVariableDansContraintes = new ArrayList<Integer>();
	}
	
	


public ArrayList<Var> getVar() {
	return vars;
}

public ArrayList<Var> getVarPos() {
	ArrayList<Var> varPos=new ArrayList<Var>();
	for(int i=0; i<nbVariables; i++) {
		for(int j=0; j<nbVariables; j++) {
			if(vars.get(j).pos==i) {
				varPos.add(vars.get(j));
				break;
			}
		}
	}
	return varPos;
	
}

public Var getVar(int num) {
	return vars.get(num);
}

public Var getVar(String name) {
	for (Var v : vars) {
		if(name.compareTo(v.name)==0)
			return v;
	}
	return null;
}

public Var getVarPos(int pos) {
	for (Var v : vars) {
		if(pos==v.pos)
			return v;
	}
	return null;
}

public Var getVarID(int id) {
	for (Var v : vars) {
		if(id==v.id)
			return v;
	}
	return null;
}

public void addVar(Var v) {
	vars.add(v);
	nbVariables=vars.size();
}

public void setVar(ArrayList<Var> tabVars) {
	vars=tabVars;
	nbVariables=vars.size();
}

public void addCons(Constraint c) {
	c.reorderConstraint();
	constraints.add(c);
	nbConstraints=constraints.size();
}

public ArrayList<Constraint> getCons() {
	return constraints;
}

public Constraint getCons(int num) {
	return constraints.get(num);
}

public Constraint getCons(String name) {
	for (Constraint c : constraints) {
		if(name.compareTo(c.name)==0)
			return c;
	}
	return null;
}

//return -1 for every var not in scope
public int[][] getFullCons(int num) {
	int[][] fullCons = new int[constraints.get(num).nbTuples][nbVariables];
	
	for(int i=0; i<constraints.get(num).nbTuples; i++) {
		for(int j=0; j<nbVariables; j++) {
			fullCons[i][j]=-1;	//default value
			for(int k=0; k<constraints.get(num).arity; k++) {
				if(getVar(constraints.get(num).scopeID.get(k)).pos==j) {
					fullCons[i][j]=constraints.get(num).cons.get(i).get(k);
					break;
				}
			}
		}
	}
	return fullCons;
}



public int getNbVariables() {
	return nbVariables;
}

public int getNbConstraints() {
	return nbConstraints;
}


//forme : [poid, v0=-1, v1=3, v2=2; v3=-1][poid, v0=-1, v1=4, v2=3; v3=-1] => (1,2) poid:3 2|4 3
public Structure getDefaultCost(int num){	
	return constraints.get(num).defaultCost;
}

public boolean getSoftConstraint(int num){
	return constraints.get(num).softConstraint;
}
public boolean getConflictsConstraint(int num){
	return constraints.get(num).conflictsConstraint;
}

public ArrayList<Structure> getPoid(int num) {
	return constraints.get(num).poid;
}


public void reorderAll() {
	for(Constraint c : constraints) {
		c.reorderConstraint();
	}
}


public void compactConstraint() {
	boolean egal=true;
	
	for(int i=0; i<constraints.size(); i++){
		if(constraints.get(i)!=null){
			for(int j=i+1; j<constraints.size(); j++){
				egal=true;
				if(constraints.get(j)!=null){
					if(constraints.get(i).softConstraint || constraints.get(i).conflictsConstraint){
						if(constraints.get(j).arity==constraints.get(i).arity &&
						   constraints.get(j).softConstraint==constraints.get(i).softConstraint && 
						   constraints.get(j).conflictsConstraint==constraints.get(i).conflictsConstraint &&
						   constraints.get(j).defaultCost.isNeutre() && constraints.get(i).defaultCost.isNeutre()) {

							for(int k=0; k<constraints.get(i).arity; k++){
								if(constraints.get(i).scopeID.get(k)!=constraints.get(j).scopeID.get(k)){
									egal=false;
									break;
								}
							}
							
							if(egal){
								constraints.get(i).nbTuples+=constraints.get(j).nbTuples;
								constraints.get(i).poid.addAll(constraints.get(j).poid);
								constraints.get(i).cons.addAll(constraints.get(j).cons);
								constraints.remove(j);
								
								j--;
							}
						}
					}
				}
			}
		}
	}
	nbConstraints=constraints.size();
}

//renvoie une table comptenant toutes les variables impliques dans chacunes des variables
//table[i][j] -> i : permet de changer de contrainte; j -> permet de parcourir les variables dans la contrainte
// /!\ taille des lignes variables

//variables avant changement
public ArrayList<ArrayList<Integer>> getInvolvedVariablesEntree() {
	return graphAdjConsVar;
}


//renvoie vrai si cette variable est inclue dans au moins une des contraintes
///form s to e (inclue) les contraintes commencent a 1
public boolean isVariableUtile(Var v, int s, int e){
	//System.out.println(v.name);
	for(int i=s; i<e; i++){
		if(constraints.get(i)!=null){
			for(int j=0; j<constraints.get(i).scopeID.size(); j++){
				if(constraints.get(i).scopeID.get(j)==v.id)
					return true;
			}
		}
	}
	//System.out.println("variable refusee : "+v.pos);
	return false;
}

public boolean isVariableUtile(Var v){
	for(int i=0; i<constraints.size(); i++){
		if(constraints.get(i)!=null){
			for(int j=0; j<constraints.get(i).scopeID.size(); j++){
				if(constraints.get(i).scopeID.get(j)==v.id)
					return true;
			}
		}
	}
	//System.out.println("variable refusee : "+v.pos);
	return false;	
}




public void actualise() {
	reorderAll();
	constructGraphAdjascenceConsVar();
	constructGraphAdjascenceVarVar();
	constructOccurenceVariableDansContraintes();
}

public void constructGraphAdjascenceConsVar() {
	for(int i=0; i<nbConstraints; i++){
		ArrayList<Integer> varCons = new ArrayList<Integer>();
		for(int j=0; j<constraints.get(i).arity; j++){
			varCons.add(constraints.get(i).scopeID.get(j));	      			
		}
		graphAdjConsVar.add(varCons);
	}
}

public void constructGraphAdjascenceVarVar() {
	
	//init
	graphAdjVarVar=new int[nbVariables][nbVariables];
	for(int i=0; i<nbVariables; i++) {
		for(int j=0; j<nbVariables; j++) {
			graphAdjVarVar[i][j]=0;
		}
	}
	
	//construct
	for(int cpt=0; cpt<nbConstraints; cpt++){
		int arity=constraints.get(cpt).arity;
		for(int i=0; i<constraints.get(cpt).arity; i++) {
			for(int j=i+1; j<constraints.get(cpt).arity; j++) {
				if(graphAdjVarVar[graphAdjConsVar.get(cpt).get(i)][graphAdjConsVar.get(cpt).get(j)]<arity) {
					graphAdjVarVar[graphAdjConsVar.get(cpt).get(i)][graphAdjConsVar.get(cpt).get(j)]=arity;
					graphAdjVarVar[graphAdjConsVar.get(cpt).get(j)][graphAdjConsVar.get(cpt).get(i)]=arity;
				}
			}
		}
	}
		
}

public void constructOccurenceVariableDansContraintes() {
	int varID;
	for(int i=0; i<nbVariables; i++)
		OccurenceVariableDansContraintes.add(0);
	
	for(int i=0; i<nbConstraints; i++){
		for(int j=0; j<constraints.get(i).arity; j++){
			varID=constraints.get(i).scopeID.get(j);
			OccurenceVariableDansContraintes.set(varID, OccurenceVariableDansContraintes.get(varID)+1);	      			
		}
	}
}


public void graphAdjascenceDot(String s, boolean valued, boolean hard) {
	FileWriter fW;
//	File f;
	
	String nomFichier=s;

	//fichier
	if(nomFichier.endsWith(".dot"))
		nomFichier=nomFichier.substring(0, nomFichier.length()-4);
	
	String name_file= "./" + nomFichier + ".dot";
	String name_pdf= "./" + nomFichier + ".pdf";
	try{
		fW = new FileWriter(name_file);
	
    	//entete
    	s="graph "+nomFichier+" {\n";
    	fW.write(s);
    	
    	//constraints
    	s="{node [width=.1,height=.1,label=\"\"]";
    	for(int cpt=0; cpt<nbConstraints; cpt++)
    	{
    		if(constraints.get(cpt).arity>2)
    			s+=" "+constraints.get(cpt).name;
    	}
    	s+="}\n";
    	fW.write(s);

    	
    	
    	for(int cpt=0; cpt<nbConstraints; cpt++)
    	{
    		if((constraints.get(cpt).softConstraint && valued) || (!constraints.get(cpt).softConstraint && hard)){
	    		if(constraints.get(cpt).arity>2) {
		    		for(int i=0; i<constraints.get(cpt).arity; i++){
			    		s=constraints.get(cpt).name + " -- " + vars.get(constraints.get(cpt).scopeID.get(i)).name + ";\n";
			    	    fW.write(s);
		    		}
	    		}else {
	    			if(constraints.get(cpt).arity==2) {
	    				s=vars.get(constraints.get(cpt).scopeID.get(0)).name + " -- " + vars.get(constraints.get(cpt).scopeID.get(1)).name + ";\n";
			    	    fW.write(s);
	    			}
	    		}
    		}
    	}
    	
    	//end
    	s="}\n";
    	fW.write(s);
    	fW.close(); 


	}catch(java.io.IOException exc){System.out.println("pb de fichier: " + exc);}
	
}

public void graphAdjascenceSimpleDot(String s) {
	FileWriter fW;
//	File f;
	
	String nomFichier=s;

	//fichier
	if(nomFichier.endsWith(".dot"))
		nomFichier=nomFichier.substring(0, nomFichier.length()-4);
	
	String name_file= "./" + nomFichier + ".dot";
	String name_pdf= "./" + nomFichier + ".pdf";
	try{
		fW = new FileWriter(name_file);
	
    	//entete
    	s="graph "+nomFichier+" {\n";
    	fW.write(s);
	    	
    	
    	for(int cpt=0; cpt<nbConstraints; cpt++)
    	{
    		for(int i=0; i<constraints.get(cpt).arity; i++)
    		{
	    		for(int j=i+1; j<constraints.get(cpt).arity; j++)
	    		{
	    			s=vars.get(constraints.get(cpt).scopeID.get(i)).name + " -- " + vars.get(constraints.get(cpt).scopeID.get(j)).name + ";\n";
	    	    	fW.write(s);
	    		}	    			
    		}
    	}
    	
    	//end
    	s="}\n";
    	fW.write(s);
    	fW.close(); 


	}catch(java.io.IOException exc){System.out.println("pb de fichier: " + exc);}
	
}


//suppression des variables pr�sentes que dans 1 contrainte
public void removeSimpleVariables() {
	boolean isUnique;
	
	for(int iC=0; iC<nbConstraints; iC++){
		for(int iV=0; iV<constraints.get(iC).arity; iV++){
			isUnique=true;
			
			outerloop:
			for(int jC=0; jC<nbConstraints; jC++){
	    		for(int jV=0; jV<constraints.get(jC).arity; jV++){
	    			if(constraints.get(iC).scopeID.get(iV) == constraints.get(jC).scopeID.get(jV)) {
	    				isUnique=false;
	    				break outerloop;
	    			}
	    		}
	    	}
			
			//remove var from constraint
			if(isUnique) {
				constraints.get(iC).name += "_m" + iV;
				constraints.get(iC).arity -= 1;
				
				constraints.get(iC).scopeID.remove(iV);
				for(int i=iV; i<constraints.get(iC).cons.size(); i++)
				{
					constraints.get(iC).cons.get(i).remove(iV);
				}
			}
		}
	}
}

protected void afficherOrdre(){
	System.out.println("ordre sur les variables : ");
	for (int i=0; i<vars.size(); i++) {
		for(int j=0; j<vars.size(); j++) {
			if(vars.get(j).pos==i) {
				System.out.println(i + " : " + vars.get(j).name);
				break;
			}
		}
	}

}


//choix de l'heuristique d'organisation des contraintes.
public void reorganiseContraintes(HeuristiqueContraintes heuristique){
	ArrayList<Integer> reorga;
	reorga=heuristique.reorganiseContraintes(vars, this);
	
	ArrayList<Constraint> constraintsTemp=new ArrayList<Constraint>();
	for(int i=0; i<reorga.size(); i++) {
		constraintsTemp.add(constraints.get(reorga.get(i)));
	}
	constraints=constraintsTemp;
	
}

protected void reordoner(HeuristiqueVariable methode){
	reordoner(methode, false);
}

//gros morceau !!!!!
protected void reordoner(HeuristiqueVariable methode, boolean reverse){
	boolean ok=true;
	
	actualise();
	
	//arrayList to static table
	int[][] contraintes=new int[graphAdjConsVar.size()][];
	for(int i=0; i<graphAdjConsVar.size(); i++) {
		contraintes[i]=new int[graphAdjConsVar.get(i).size()];
		for(int j=0; j<graphAdjConsVar.get(i).size(); j++) {
			contraintes[i][j]=graphAdjConsVar.get(i).get(j);
		}
	}
		
		
		
	ArrayList<Var> copie=new ArrayList<Var>();
	ArrayList<Var> listetriee=new ArrayList<Var>();
	
	for(int i=0; i<vars.size(); i++)
		copie.add(vars.get(i));
	
	
	listetriee=methode.reordoner(contraintes, copie, this);
	if(listetriee.size()!=vars.size()){
		System.out.println("Erreur heuristique variables : liste de variables retournée de mauvaise taille. taille liste : "+listetriee.size() +" / nombre de variables : "+vars.size());
		ok=false;
	}
	for(int i=0; i<listetriee.size(); i++){
		if(listetriee.get(i)==null){
			System.out.println("Erreur heuristique variables : valeure null dans liste retournée");
			ok=false;
			break;
		}
		for(int j=i+1; j<listetriee.size(); j++){
			if(listetriee.get(i).name.compareTo(listetriee.get(j).name)==0){
				System.out.println("Erreur heuristique variables : valeure "+listetriee.get(j).name+" en double dans la liste retournée");
				ok=false;
				break;
			}
		}
	}
	if(ok){
		actualiserPosVar(listetriee, reverse);
	}else{
		System.out.println("pas d'ordonnancement de variables");
	}

}

public void actualiserPosVar(ArrayList<Var> listetriee, boolean reverse) {
	
	
	if(reverse) {
		for(int i=0; i<(listetriee.size())/2; i++){
			Collections.swap(listetriee, listetriee.size()-i-1, i);
		}
	}
	
	//variables
	for(int i=0; i<listetriee.size(); i++) {
			listetriee.get(i).pos=i;
	}
	
}

}