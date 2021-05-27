package compilateur;

/*   (C) Copyright 2013, Schmidt Nicolas
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.FileReader;
//import java.io.File;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import compilateur.test_independance.TestIndependance;


public class VDD{
	
//	private static int indice;
	
//	private final boolean COMB_UP=true;
//	private final boolean COMB_DOWN=false;
		
	public ArrayList <Var> variables;
	
	private MemoryManager memorymanager;
	
//	public Variance variance;
	
//    protected NodeDD first;							//the one first node (si plusieurs, creer plusieurs 
    protected Arc first;
    public NodeDDlast last;
    
//    protected UniqueTable ut;			//ensemble des neuds
    public UniqueHashTable uht;			//ensemble des neuds
    
    public boolean flagPlus=true;			//(plus,mult) add : false,false; sldd+ : true,false; sldd* : false,true; aadd : true,true
	public boolean flagMult=false;
	
	static int cptdot=0;
	
	public Structure min=null, max=null;

// constructeur
/*    public VDD(){
		lasts=new ArrayList<NodeDD>();
		ut=new UniqueTable();
    }
    
	public VDD(NodeDD start){
		lasts=new ArrayList<NodeDD>();
		ut=new UniqueTable();
		
	    first=start;
	    ut.add(first);
	}
	
    public VDD(UniqueTable u){
		lasts=new ArrayList<NodeDD>();
		ut=u;
    }
    
	public VDD(UniqueTable u, NodeDD start){
		lasts=new ArrayList<NodeDD>();
		ut=u;
		
	    first=start;
	    ut.add(first);
	}*/
    //creation class VDD a partir de lecture de fichier
    public VDD(Arc a,UniqueHashTable u, ArrayList<Var> v){
		uht=u;
		first=a;
		variables=v;
		last=new NodeDDlast();
		memorymanager = MemoryManager.getMemoryManager();
    } 
    
    //pour le add
    public VDD(UniqueHashTable u){
    	uht=u;
    	memorymanager = MemoryManager.getMemoryManager();
    }
	
    
    //cree un DD de 1 variable non bool a partir d'une UT existante
/*    public VDD(Var var, UniqueHashTable u){
		uht=u;
		variables=new ArrayList<Var>();
		
		NodeDD tete=new NodeDD(var, 0);
		first=new Arc(tete, 0);
		
		ut.add(tete);
		variables.add(var);
		
		for(int i=0; i<var.domain; i++)
			new Arc(tete, ut.getzero(), i);
    }*/ 
    
 
  //cree un DD de x variable
    public VDD(ArrayList<Var> liste, UniqueHashTable u, boolean plus){
    	memorymanager = MemoryManager.getMemoryManager();
    	uht=u;
		variables=liste;
		last=new NodeDDlast();
		Structure s;
		
		NodeDD tete=new NodeDD(variables.get(0));
		first=new Arc(tete, plus);
//		uht.add(tete);
		
		NodeDD precedant;
		precedant=tete;
		
		for(int i=1; i<variables.size(); i++){				//on ajoute chaque neud, avec un lien, avec n arcs
			NodeDD suivant=new NodeDD(variables.get(i));		//neuds : variable de 1 a x
			for(int j=0; j<variables.get(i-1).domain; j++){		//domaine : de 0 a x-1
				if(plus)
					s=new Sp();
				else
					s=new St();
				new Arc(precedant, suivant, j, s);		//int donc SLDD+
			}
			
			uht.ajoutSansNormaliser(precedant);	

			precedant=suivant;
		}
		
		
		for(int j=0; j<variables.get(variables.size()-1).domain; j++){		//on ajoute les feuilles au dernier
			if(plus)
				s=new Sp();
			else
				s=new St();
			new Arc(precedant, last, j, s);
		}
		
		uht.ajoutSansNormaliser(precedant);	
		
		uht.ajoutSansNormaliser(last);
		

		
    } 
	

    // attention si poids negatifs : conflits sur cpt (cpt:indicateur, cpt=-1:suppression)
    //recursif descend les valeurs sur les feuilles finales (gestion des arcs)

    public void combDownToADD(Arc curr){			//peigne vers le bas
    	
    	Structure valAmont=curr.s;
    	NodeDD currNode=curr.fils;
    	NodeDD pere=curr.pere;
    	
    	if (!currNode.isLeaf()) {						//cas pas feuille  		

				
			if (!valAmont.isNeutre()) { //nouvelle copie

				if(pere!=null)
					uht.removeFromTable(pere);
	    		//uht.removeFromTable(currNode);
	    		
				NodeDD nouveau = new NodeDD(curr.fils, curr); 
				nouveau.cpt=0;								// je sais pas si c'est utile, on est jamais trop prudent
				//uht.add(nouveau);
				
				for (int i = 0; i < nouveau.kids.size(); i++) {
					nouveau.kids.get(i).s.operation(valAmont); //on descend le poid.
				}
				curr.s.toNeutre(); //on supprime l'ancien
					
				uht.ajoutSansNormaliser(nouveau);
				if(pere!=null)
					uht.ajoutSansNormaliser(pere);
				if(currNode.fathers.size()<1){
					uht.removeDefinitely(currNode);
				}

					
			} else { //developpement neutre
					currNode.cpt = 0; //et le neud restera
			}
    	}

    }
    
    public void combDownSptToSp(Arc curr){			//peigne vers le bas, on garde le coef additif
    	
    	Spt valAmont=(Spt) curr.s;
    	NodeDD currNode=curr.fils;
    	NodeDD pere=curr.pere;
    	
    	if (!currNode.isLeaf()) {						//cas pas feuille  		

				
			if (valAmont.f!=1) { //nouvelle copie

				if(pere!=null)
					uht.removeFromTable(pere);
	    		//uht.removeFromTable(currNode);
	    		
				NodeDD nouveau = new NodeDD(curr.fils, curr); 
				nouveau.cpt=0;								// je sais pas si c'est utile, on est jamais trop prudent
				//uht.add(nouveau);
				
				for (int i = 0; i < nouveau.kids.size(); i++) {
					((Spt) nouveau.kids.get(i).s).multQetF(valAmont.f); //on descend le poid.
				}
				((Spt) curr.s).f=1; //on supprime l'ancien f
					
				uht.ajoutSansNormaliser(nouveau);
				if(pere!=null)
					uht.ajoutSansNormaliser(pere);
				if(currNode.fathers.size()<1){
					uht.removeDefinitely(currNode);
				}

					
			} else { //developpement neutre
					currNode.cpt = 0; //et le neud restera
			}
    	}

    }
    
    public void combDownSptToSt(Arc curr){			//peigne vers le bas, on garde le coef mult
    	Spt valAmont=(Spt) curr.s;
    	NodeDD currNode=curr.fils;
    	//NodeDD pere=curr.pere;
    	
    	if (!currNode.isLeaf()) {						//cas pas feuille  		

				
			if (valAmont.f!=1) { //nouvelle copie

				//if(pere!=null){
					//NodeDD prout=uht.recherche(pere);
					//if(prout==null){
					//	System.out.println("prout");
					//}
				//	uht.removeFromTable(pere);
	    		//uht.removeFromTable(currNode);
				//}
	    		
				NodeDD nouveau = new NodeDD(currNode, curr); 
				nouveau.cpt=0;								// je sais pas si c'est utile, on est jamais trop prudent
				//uht.add(nouveau);

				
				for (int i = 0; i < nouveau.kids.size(); i++) {
					((Spt) nouveau.kids.get(i).s).addQparF(valAmont); //on descend le poid.
				}
				((Spt) curr.s).q=0;{ //on supprime l'ancien f
				uht.ajoutSansNormaliser(nouveau);
				}

				//if(pere!=null){
				//	uht.ajoutSansNormaliser(pere);						//bug ici, id en double des fois. Voir "prout" plus haut
				//}
					
				if(currNode.fathers.size()<1){
					uht.removeDefinitely(currNode);
				}


					
			} else { //developpement neutre
					currNode.cpt = 0; //et le neud restera
			}

    	}


    }
    
    public void slddToAdd(){
        flagPlus=false;
        uht.combDown(this);
    	//final 
    	S nouv=new S();
		first.s=nouv;

		uht.SpToS();
		uht.normaliser();
    }
    
	//on applique un facteur eventuellement pour rendre la conversion plus precise
    public void addToSldd(int facteur){
        flagPlus=true;
    	//final 
    	Sp nouv=new Sp(0);
		first.s=nouv;
    	

    	uht.SToSp(facteur);
    	uht.normaliser();
    }
    
    public void slddMultToAdd(){
        flagMult=false;
        uht.combDown(this);
                
    	//final 
    	S nouv=new S();
		first.s=nouv;
    	

    	uht.StToS();
		uht.normaliser();

    }
    
    public void addToSlddMult(){
        flagMult=true;
                
    	//final 
    	St nouv=new St(1);
		first.s=nouv;
    	

    	uht.SToSt();
    	
    	uht.normaliser();
    }
    
	//on applique un facteur eventuellement pour rendre la conversion plus precise
    public void aaddToSldd(int facteur){
        flagMult=false;
        uht.combDownSptToSp(this);
        
    	//final 
    	Sp nouv=new Sp((int)((Spt)first.s).q);
		first.s=nouv;
    	
    	uht.SptToSp(facteur);	
    }
    
    public void aaddToSlddMult(){
        flagPlus=false;

        uht.combDownSptToSt(this);
uht.detect();
    	//final 
    	St nouv=new St((int)((Spt)first.s).f);
		first.s=nouv;
    	uht.SptToSt();	
    }
    
    public void slddToAadd(){
 //   	if(flagPlus==false)
//    		this.addToSldd();
    	flagMult=true;

    	//init 
    	Spt nouv=new Spt(((Sp)first.s).getVal(), 1);
		first.s=nouv;
    	
    	uht.SpToSpt();

    	//uht.testStructureUniforme();
		
    	uht.normaliser();

    }
    
    
    public void slddMultToAadd(){
    	 //   	if(flagPlus==false)
//    	    		this.addToSldd();
    	    	flagPlus=true;

    	    	//init 
    	    	Spt nouv=new Spt(0, ((St)first.s).getvaldouble());
    			first.s=nouv;
    	    	
    	    	uht.StToSpt();

    	    	//uht.testStructureUniforme();
    			
    	    	uht.normaliser();

    	    }
    
    


	
	//compte le nombre de passage dans chaques neud
	//resultat dans les cpt
    public long counting(){
    	long res=0;
    	if(first.actif && first.bottom==0){
    		first.fils.counting=1;
    	}
    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		res+=counting(uht.getLast().get(i));
    	}
    	
    	uht.countingToMoinsUn();
    	return res;
    }
    
	public long counting(NodeDD n){		
		long res=0;
		if(n.counting!=-1){								//sinon on partirai plusieurs fois de chaque sommets
			return n.counting;
		}else{
			for(int i=0; i<n.fathers.size(); i++){
				if(n.fathers.get(i).bottom==0 && n.fathers.get(i).actif)
					res+=counting(n.fathers.get(i).pere);
			}
			n.counting=res;
			return res;
		}
	}
	
	//compte le nombre de passage dans chaques neud
	//resultat dans les cpt
    public long countingFromBottom(){
    	long res=0;
    	for(int i=0; i<uht.getLast().size(); i++){
    		uht.getLast().get(i).counting=1;
    	}
    	
    	if(first.actif && first.bottom==0){
    		res+=countingFromBottom(first.fils);
    	}
    	
    	uht.countingToMoinsUn();
    	return res;
    }
	
	public long countingFromBottom(NodeDD n){		
		long res=0;
		if(n.counting!=-1){								//sinon on partirai plusieurs fois de chaque sommets
			return n.counting;
		}else{
			for(int i=0; i<n.kids.size(); i++){
				if(n.kids.get(i).bottom==0 && n.kids.get(i).actif)
					res+=countingFromBottom(n.kids.get(i).fils);
			}
			n.counting=res;
			return res;
		}
	}
	
	//chaque arc contient le nombre de fois qu'on est passe par lui
	public void countingValOnArc(){
    	uht.countingToMoinsUn();
    	for(int i=0; i<uht.getLast().size(); i++){
    		uht.getLast().get(i).counting=-1;
    		uht.getLast().get(i).counting2=-1;
    		uht.getLast().get(i).pondere=0;
    		uht.getLast().get(i).inference=0;
    	}    	
    	
		countingValOnArcDown();
		countingValOnArcUp();
    	uht.countingToMoinsUn();
	}
	
    public void countingValOnArcDown(){
    	
    	if(first.actif && first.bottom==0){
    		first.fils.counting=1;
    	}
    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		countingValOnArcDown(uht.getLast().get(i));
    	}
    }
    
	public long countingValOnArcDown(NodeDD n){		
		long res=0;
		if(n.counting==-1){			
			for(int i=0; i<n.fathers.size(); i++){
				if(n.fathers.get(i).bottom==0 && n.fathers.get(i).actif) {
					res+=countingValOnArcDown(n.fathers.get(i).pere);
				}
				
			}
			n.counting = res; 
		}
		return n.counting;
		
	}
	
    public void countingValOnArcUp(){
    	for(int i=0; i<uht.getLast().size(); i++){
    		uht.getLast().get(i).counting2=1;
    	}
    	
    	first.passage1=first.fils.counting;
    	
    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		countingValOnArcUp(first.fils);
    	}
    }
    
	public long countingValOnArcUp(NodeDD n){		
		long res=0;
		if(n.counting2==-1){			
			for(int i=0; i<n.kids.size(); i++){
				if(n.kids.get(i).bottom==0 && n.kids.get(i).actif) {
					res+=countingValOnArcUp(n.kids.get(i).fils);
					n.kids.get(i).passage1=n.counting*n.kids.get(i).fils.counting2;
				}

			}
			n.counting2=res;
		}
		return n.counting2;
	}
	
	//prend en compte la ponderation
	//cas de l'historique. (SLDD additif uniquement)
    public int countingpondere(){
    	int res=0;
    	if(first.actif && first.bottom==0){
    		first.fils.counting=1;
    		first.fils.pondere=(int)first.s.getvaldouble();
    	}
    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		res+=countingpondere(uht.getLast().get(i));
    	}
    	
    	uht.countingToMoinsUn();
    	return res;
    }
    
	public int countingpondere(NodeDD n){	
		n.counting=0;
		Arc arc;
		for(int i=0; i<n.fathers.size(); i++){
			arc = n.fathers.get(i);
			if(arc.actif && arc.bottom==0){ //sinon on partirai plusieurs fois de chaque sommets
				if(arc.pere.counting == -1)
					countingpondere(arc.pere);
				n.counting+=arc.pere.counting;
				n.pondere+=arc.pere.pondere + arc.s.getvaldouble()*arc.pere.counting;
			}
			
		}
		return n.pondere;
		
	}
	
	
	//compte le nombre de soution pour le choix v de var.
	public int countingpondereOnVal(Var var, int v){
    	int res=0;
    	if(first.actif && first.bottom==0){
    		first.fils.counting=1;
    		first.fils.pondere=(int)first.s.getvaldouble();
    	}
    	
    	conditioner(var, v);
    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		res+=countingpondere(uht.getLast().get(i));
    	}
    	
    	deconditioner(var);
    	uht.countingToMoinsUn();
    	
    	return res;
	}
	
	//identique a countingpondereOnVal sauf pour l'init et le deconditionnement
	// /!\ uht.countingToMoinsUn() ou uht.countingToMoinsUnUnderANode(var.pos) necessaire apres cette fonction
	public int countingpondereOnValAllege(Var var, int v){
    	int res=0;
    	conditioner(var, v);
    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		res+=countingpondere(uht.getLast().get(i));
    	}
    	
    	deconditioner(var);

    	return res;
	}
	
	//donne la probabilite de chacune des options en fonction de ce qui a deja ete conditionne
    public Map<String,Double> countingpondereOnFullDomain(Var var){
    	Map<String, Double> m=new HashMap<String, Double>();

    	int res=0;
    	int total=0;
    	int dom;
    	if(first.actif && first.bottom==0)
    		first.fils.counting=1;
    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		total+=countingpondere(uht.getLast().get(i));
    	}
    	    	
    	dom=var.domain;
    	for(int j=0; j<dom; j++){
        	uht.countingToMoinsUnUnderANode(var.pos);
    		res=countingpondereOnValAllege(var, j);		//////////////////BUUUUUUUUG
        	m.put(var.valeurs.get(j), (double)res/total);
    	}
    	uht.countingToMoinsUn();

    	return m;
    	
    }
    
	//donne la probabilite de chacune des options en fonction de ce qui a deja ete conditionne
    public Map<String,Double> countingpondereOnPossibleDomain(Var var, ArrayList<String> possibles){
    	Map<String, Double> m=new HashMap<String, Double>();

    	int res=0;
    	int total=0;
    	int dom;
    	if(first.actif && first.bottom==0)
    		first.fils.counting=1;
    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		total+=countingpondere(uht.getLast().get(i));
    	}
    	 
    	// Si le cas n'est pas trouvé, alors on renvoie une équiprobabilité
    	if(total == 0)
    	{
    		System.out.println("Erreur! VDD.countingpondereOnPossibleDomain");
    		for(String s: possibles)
	        	m.put(s, 1.);
    		return m;
    	}
    		
    	dom=var.domain;
    	for(int j=0; j<dom; j++)
    	{
    		if(possibles.contains(var.valeurs.get(j)))
    		{
	        	uht.countingToMoinsUnUnderANode(var.pos);
	    		res=countingpondereOnValAllege(var, j);		//////////////////BUUUUUUUUG
	        	m.put(var.valeurs.get(j), (double)res/total);
    		}
    	}
    	uht.countingToMoinsUn();

    	return m;
    	
    }
    
	//prend en compte la ponderation
	//cas de l'historique. (SLDD multiplicatif uniquement)
    public double inference(){
    	double res=0;
    	if(first.actif && first.bottom==0){
    		first.fils.counting=1;
    		first.fils.inference=first.s.getvaldouble();
    	}
    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		double inference = inference(uht.getLast().get(i));
//    		System.out.println(inference);
    		res+=inference;
    	}
    	
    	uht.countingToMoinsUn();
    	return res;
    }
    
	public double inference(NodeDD n){	
		n.counting=0;
		Arc arc;
		for(int i=0; i<n.fathers.size(); i++){
			arc = n.fathers.get(i);
			if(arc.actif && arc.bottom==0){ //sinon on partirai plusieurs fois de chaque sommets
				if(arc.pere.counting == -1)
					inference(arc.pere);
//				n.counting+=arc.pere.counting;
//	    		System.out.println(arc.pere.inference * arc.s.getvaldouble());

				n.inference+=arc.pere.inference * arc.s.getvaldouble();
			}
			
		}
		return n.inference;
		
	}
	
	
	//calcule la proba pour pour le choix v de var.
	public double inferenceOnVal(Var var, int v){
    	double res=0;
    	if(first.actif && first.bottom==0){
    		first.fils.counting=1;
    		first.fils.inference=(double)first.s.getvaldouble();
    	}
    	
    	conditioner(var, v);
    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		res+=inference(uht.getLast().get(i));
    	}
    	
    	deconditioner(var);
    	uht.countingToMoinsUn();
    	
    	return res;
	}
	
	//identique a countingpondereOnVal sauf pour l'init et le deconditionnement
	// /!\ uht.countingToMoinsUn() ou uht.countingToMoinsUnUnderANode(var.pos) necessaire apres cette fonction
	public double inferenceOnValAllege(Var var, int v){
    	double res=0;
    	conditioner(var, v);
    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		res+=inference(uht.getLast().get(i));
    	}
    	
    	deconditioner(var);

    	return res;
	}
	
	//donne la probabilite de chacune des options en fonction de ce qui a deja ete conditionne
    public Map<String,Double> inferenceOnFullDomain(Var var){
    	Map<String, Double> m=new HashMap<String, Double>();

    	double res=0;
    	int dom;
    	if(first.actif && first.bottom==0){
    		first.fils.counting=1;
    		first.fils.inference=first.s.getvaldouble();
    	}

    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		inference(uht.getLast().get(i));
    	}

//    	System.out.println("total: "+total);
    	dom=var.domain;
    	for(int j=0; j<dom; j++){
        	uht.countingToMoinsUnUnderANode(var.pos);
    		res=inferenceOnValAllege(var, j);		//////////////////BUUUUUUUUG
        	m.put(var.valeurs.get(j), (double)res);///total);
//        	System.out.println(var.valeurs.get(j)+" "+res/total);
    	}
    	uht.countingToMoinsUn();

    	return m;
    	
    }
    
    public Map<String,Double> inferenceOnPossibleDomain(Var var, ArrayList<String> possibles){
    	Map<String, Double> m=new HashMap<String, Double>();

    	double res=0;
    	int dom;
    	if(first.actif && first.bottom==0){
    		first.fils.counting=1;
    		first.fils.inference=first.s.getvaldouble();
    	}

    	
    	for(int i=0; i<uht.getLast().size(); i++){
    		inference(uht.getLast().get(i));
    	}

//    	System.out.println("total: "+total);
    	dom=var.domain;
    	for(int j=0; j<dom; j++){
    		if(possibles.contains(var.valeurs.get(j)))
    		{
	        	uht.countingToMoinsUnUnderANode(var.pos);
	    		res=inferenceOnValAllege(var, j);		//////////////////BUUUUUUUUG
	        	m.put(var.valeurs.get(j), (double)res);///total);
	//        	System.out.println(var.valeurs.get(j)+" "+res/total);
    		}
    	}
    	uht.countingToMoinsUn();

    	return m;
    	
    }
    
	//opt
	public void conditioner(int var, int val){
		uht.conditioner(var, val);
	}
	
	//opt
	public void conditioner(Var variable, int val){
		uht.conditioner(variable.pos, val);
	}
	
	public void conditionerTrue(int var, int val){
		ArrayList<NodeDD> savelist;//=new ArrayList<NodeDD>();
		savelist=uht.get(var);

		for(int i=0; i<savelist.size(); i++){
			uht.removeFromTable(savelist.get(i));
			savelist.get(i).conditionerTrue(val);
			uht.ajoutNormaliseReduitPropage(savelist.get(i));
		}
	}
	
	public void conditionerTrue(Var variable, int val){
		int var=variable.pos;
		ArrayList<NodeDD> savelist=new ArrayList<NodeDD>();
		//for(int i=0; i<uht.size(var); i++){
			savelist=uht.get(var);
		//}
		for(int i=0; i<savelist.size(); i++){
			uht.removeFromTable(savelist.get(i));
			savelist.get(i).conditionerTrue(val);
			uht.ajoutNormaliseReduitPropage(savelist.get(i));
		}
	}
	
	public void conditionerExclureTrue(int var, int val){
		ArrayList<NodeDD> savelist;//=new ArrayList<NodeDD>();
		savelist=uht.get(var);

		for(int i=0; i<savelist.size(); i++){
			uht.removeFromTable(savelist.get(i));
			savelist.get(i).conditionerExclureTrue(val);
			uht.ajoutNormaliseReduitPropage(savelist.get(i));
		}
	}
	
	public void conditionerExclureTrue(Var variable, int val){
		int var=variable.pos;
		ArrayList<NodeDD> savelist=new ArrayList<NodeDD>();
		for(int i=0; i<uht.size(var); i++){
			savelist=uht.get(var);
		}
		for(int i=0; i<savelist.size(); i++){
			uht.removeFromTable(savelist.get(i));
			savelist.get(i).conditionerExclureTrue(val);
			uht.ajoutNormaliseReduitPropage(savelist.get(i));
		}
	}

	//opt
	//annnule le conditionnement de var
	public void deconditioner(int var){
		uht.deconditioner(var);
	}
	
	//opt
	//annnule le conditionnement de var
	public void deconditioner(Var variable){
		uht.deconditioner(variable.pos);
	}
	
	//opt
	public void deconditionerAll(){
		for(int j=0; j<variables.size(); j++){
			uht.deconditioner(j);
		}
	}
	
	public void reinitializeInState(Map<String, String> state) {
		for(int j=0; j<variables.size(); j++){
			Var v=variables.get(j-1);
			String name=v.name;
			if(state.containsKey(name))
				uht.conditioner(j, v.conv(state.get(name)));
			else
				uht.deconditioner(j);
		}
	}

	
	public void minMaxConsistance(){
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).inGraph)
				variables.get(i).consValTofalse();
		}
		//uht.maxminNull();
		
		//bug premier dernier a resoudre
		
		
		//if(first.s.printstr().compareTo("Sp")==0){
				min=new Sp(); 
				max=new Sp(); 
		//}
		//if(first.s.printstr().compareTo("St")==0){
		//		min=new St(); 
		//		max=new St();
		//}
		//if(first.s.printstr().compareTo("Spt")==0){
		//		min=new Spt(); 
		//		max=new Spt();
		//}
		min.rendreInaccessible();
		max.rendreInaccessible();
		
//		long start= System.currentTimeMillis();
//		long end;
		//uht.maxminNull();
		
		uht.minMaxConsistance();
				
//		end=System.currentTimeMillis();
//		System.out.println("------> :  " + (end-start) + "ms");
		
		min=uht.getLast().get(0).min.copie();
		max=first.s.copie();
		max.operation(first.fils.max);
		if(min.printstr().compareTo("Spt")==0){
			min=first.s.copie();
			min.operation(first.fils.min);
//			System.out.println(((Spt)first.s).q +" "+((Spt)first.s).f);		
//			System.out.println(((Spt)first.fils.min).q +" "+((Spt)first.fils.min).f);		
//			System.out.println(((Spt)first.fils.max).q +" "+((Spt)first.fils.max).f);		
		}
		
		//System.out.println("min="+min.getvaldouble());		
		//System.out.println("max="+max.getvaldouble());

		/*for(int i=0; i<variables.size(); i++){
			System.out.print(variables.get(i).name+" ");
			for(int j=0; j<variables.get(i).domain; j++)
				System.out.print(variables.get(i).consVal[j]);
			System.out.println();
		}*/
		
		
	}
	
	public void minMaxConsistanceMaj(int var, boolean cd){
		//for(int i=0; i<variables.size(); i++)
			variables.get(var).consValTofalse();
		
		min.rendreInaccessible();
		max.rendreInaccessible();
		
		uht.minMaxConsistanceMaj(var, cd);
				
		//GIC();
		//for(int i=var; i<=variables.size(); i++)
		//	uht.GIC(i);
		
//		end=System.currentTimeMillis();
//		System.out.println("------> :  " + (end-start) + "ms");
		
		min=uht.getLast().get(0).min.copie();
		max=first.s.copie();
		max.operation(first.fils.max);
		if(min.printstr().compareTo("Spt")==0){
			min=first.s.copie();
			min.operation(first.fils.min);
//			System.out.println(((Spt)first.s).q +" "+((Spt)first.s).f);		
//			System.out.println(((Spt)first.fils.min).q +" "+((Spt)first.fils.min).f);		
//			System.out.println(((Spt)first.fils.max).q +" "+((Spt)first.fils.max).f);		
		}
		
	}
	
	public void minMaxConsistanceMajopt(int var, boolean cd){

		min.rendreInaccessible();
		max.rendreInaccessible();
		
		uht.minMaxConsistanceMajopt(var, cd);
				
//		end=System.currentTimeMillis();
//		System.out.println("------> :  " + (end-start) + "ms");
		min=uht.getLast().get(0).min.copie();
		max=first.s.copie();
		max.operation(first.fils.max);
		if(min.printstr().compareTo("Spt")==0){
			min=first.s.copie();
			min.operation(first.fils.min);
//			System.out.println(((Spt)first.s).q +" "+((Spt)first.s).f);		
//			System.out.println(((Spt)first.fils.min).q +" "+((Spt)first.fils.min).f);		
//			System.out.println(((Spt)first.fils.max).q +" "+((Spt)first.fils.max).f);		
		}
		//if(cd)
		//	GIC();
		for(int i=0; i<variables.size(); i++)
			variables.get(i).consValTofalse();
		uht.consGraceAMinMax();
		
	}
	
	public Map<String, String> minCostConfiguration(){
		Map<String, String> m=new HashMap<String, String>();
		int ind=-1;
		int pos=-1;
		NodeDD n;
		
		n=uht.getLast().get(0);
		
		while(n!=null){
			ind=n.posMin;
			pos=n.fathers.get(ind).pos;
			n=n.fathers.get(ind).pere;
			if(n!=null)
				m.put(n.variable.name, n.variable.valeurs.get(pos));
			//	System.out.print(n.variable.name +"->" +n.variable.valeurs.get(pos)+" ");
		}
		
		return m;
	}
	
	public Map<String, String> maxCostConfiguration(){	
		Map<String, String> m=new HashMap<String, String>();	
		NodeDD n;	
			
		n=first.fils;	
		while(!n.isLeaf()){	
			m.put(n.variable.name, n.variable.valeurs.get(n.posMax));	
			//System.out.print(n.variable.name+"->"+n.variable.valeurs.get(n.posMax)+" ");	
			//ajouter entree n.variable.name, n.variable.valeurs.get(n.posMax)	
			n=n.kids.get(n.posMax).fils;	
		}		
			
		return m;	
	}
	
	public Map<String, Integer> minCosts(int var){
		Map<String, Integer> m=new HashMap<String, Integer>();
		ArrayList<NodeDD> liste;
		uht.minDomainVariable(var);
		liste=uht.get(var);
		int[] minDom=new int[this.variables.get(var).domain];
		for(int i=0; i<minDom.length; i++){
			minDom[i]=2147483647;
		}
		
		for(int i=0; i<liste.size(); i++){
			for(int j=0; j<liste.get(i).kids.size(); j++){
				if(liste.get(i).kids.get(j).bottom==0 && liste.get(i).kids.get(j).fils!=null){
					if(!liste.get(i).min.inaccessible() && !liste.get(i).kids.get(j).fils.min.inaccessible()){
						if(liste.get(i).min.getvaldouble()+liste.get(i).kids.get(j).s.getvaldouble()+liste.get(i).kids.get(j).fils.min.getvaldouble()<minDom[j])
							minDom[j]=(int) (liste.get(i).min.getvaldouble()+liste.get(i).kids.get(j).s.getvaldouble()+liste.get(i).kids.get(j).fils.min.getvaldouble());
					}
				}
			}
		}
			
		for(int i=0; i<minDom.length; i++){
			if(minDom[i]!=2147483647)
				m.put(variables.get(var).valeurs.get(i), minDom[i]);
		}
		
		uht.minMaxConsistance();

		return m;
	} 
	
	public Map<String, Integer> maxCosts(int var){
		Map<String, Integer> m=new HashMap<String, Integer>();
		ArrayList<NodeDD> liste;
		uht.maxDomainVariable(var);
		liste=uht.get(var);
		int[] maxDom=new int[this.variables.get(var).domain];
		for(int i=0; i<maxDom.length; i++){
			maxDom[i]=-1;
		}
		
		for(int i=0; i<liste.size(); i++){
			for(int j=0; j<liste.get(i).kids.size(); j++){
				if(liste.get(i).kids.get(j).bottom==0 && liste.get(i).kids.get(j).fils!=null){
					if(!liste.get(i).max.inaccessible() && !liste.get(i).kids.get(j).fils.max.inaccessible()){
						if(liste.get(i).max.getvaldouble()+liste.get(i).kids.get(j).s.getvaldouble()+liste.get(i).kids.get(j).fils.max.getvaldouble()>maxDom[j])
							maxDom[j]=(int) (liste.get(i).max.getvaldouble()+liste.get(i).kids.get(j).s.getvaldouble()+liste.get(i).kids.get(j).fils.max.getvaldouble());
					}
				}
			}
		}
			
		for(int i=0; i<maxDom.length; i++){
			if(maxDom[i]!=-1)
				m.put(variables.get(var).valeurs.get(i), maxDom[i]);
		}
//		for(int i=0; i<minDom.length; i++){
//			System.out.print(maxDom[i]+" ");
//		}
		
		
		uht.minMaxConsistance();

		return m;
	} 
	
	public void GIC(){
		for(int i=0; i<variables.size(); i++)
			variables.get(i).consValTofalse();
		uht.GIC();
	}
	
	//pour un cd
	public void GICup(){
		for(int i=0; i<variables.size(); i++)
			if(variables.get(i).consistenceSize()>1){
				variables.get(i).consValTofalse();
				uht.GIC(i);
			}
	}
	
	//pour un dcd
	public void GICdown(){
		for(int i=0; i<variables.size(); i++)
			if(!variables.get(i).consistenceFull()){
				variables.get(i).consValTofalse();
				uht.GIC(i);
			}
	}
	
	//operation addition recursive
    public void add(VDD a, VDD b, ArrayList<NodeDD> listP, int etage){
    	ArrayList<NodeDD> listF=new ArrayList<NodeDD>();
    	NodeDD template;
    	
    	System.out.println(a.uht.size()+" "+a.uht.size(0)+" "+a.uht.size(1)+ " "+a.uht.size(2));
    	System.out.println(b.uht.size()+" "+b.uht.size(0)+" "+b.uht.size(1)+ " "+b.uht.size(2));

    	
    	etage++;
    	if(etage<=variables.size()){
    		template=a.uht.get(etage).get(0);
 
	    	
	    	System.out.println("listPsize:"+listP.size());
	    	//uht.removeFromTable(template);
	    	
	    	System.out.println(listP.get(0).variable.name+" "+listP.get(0).variable.domain);
	    	for(int i=0; i<listP.size(); i++){
	    		for(int j=0; j<listP.get(0).variable.domain; j++){
	    			System.out.println("boucle:"+j);
	    			NodeDD node1, node2;
	    			node1=listP.get(i).copie.get(0);
	    			node2=listP.get(i).copie.get(1);
	    			if(node1.kids.get(j).bottom==0 && node2.kids.get(j).bottom==0){			//sinon : bottom
	    				NodeDD nouveau = null;
	    				System.out.println(listF.size());
	    				for(int k=0; k<listF.size(); k++){
	    					if(listF.get(k).copie.get(0)==node1.kids.get(j).fils && listF.get(k).copie.get(1)==node2.kids.get(j).fils){
	    						nouveau=listF.get(k);
	    						break;
	    					}
	    				}
	    				if(nouveau==null){
	    					nouveau=new NodeDD(template, new Arc(listP.get(i), last, j, false, new Sp()));
	    					listP.get(i).kids.get(j).s.initOperation(node1.kids.get(j).s, node2.kids.get(j).s);
	    					listF.add(nouveau);
	    					nouveau.copie.add(node1.kids.get(j).fils);
	    					nouveau.copie.add(node2.kids.get(j).fils);
	    				}else{
	    					new Arc(listP.get(i), nouveau, j, new Sp());
	    					listP.get(i).kids.get(j).s.initOperation(node1.kids.get(j).s, node2.kids.get(j).s);
	    				}
	    			}else{
	    				//bottom
	    			}
	    			
	    		}
	    		uht.ajoutSansNormaliser(listP.get(i));
	    		System.out.println("size:"+listP.size()+" uht:"+uht.size()+" F:"+listF.size());
	    	}
	    	add(a, b, listF, etage);
    	
       	}
    	else{
	    	for(int i=0; i<listP.size(); i++){
	    		for(int j=0; j<listP.get(0).variable.domain; j++){ 
	    			new Arc(listP.get(i), last, j, false, new Sp());
					listP.get(i).kids.get(j).s.initOperation(listP.get(i).copie.get(0).kids.get(j).s, listP.get(i).copie.get(1).kids.get(j).s);

	    		}
	    		uht.ajoutSansNormaliser(listP.get(i));
	    	}
    	}
    	
 	}
    
    //operation addition init
    public void add(VDD a, VDD b){
    		
    	variables=a.variables;
    	
//        protected NodeDD first;							//the one first node (si plusieurs, creer plusieurs 
//        protected Arc first;
//        static NodeDDlast last;
    	first=new Arc(new NodeDD(variables.get(0)), true);
        last=new NodeDDlast ();
        uht.ajoutSansNormaliser(last);
        
    	flagPlus=a.flagPlus;
    	flagMult=a.flagMult;
    	
    	this.first.s.initOperation(a.first.s, b.first.s);
    	this.first.fils.copie.add(a.first.fils);
    	this.first.fils.copie.add(b.first.fils);				//pas besoin de suppr ce noeud pour tout ca
    	
    	ArrayList<NodeDD> listP=new ArrayList<NodeDD>();
    	System.out.println();
    	listP.add(first.fils);
    	
    	this.add(a, b, listP, 0);
    	    	
    	uht.copieToNull();
    	uht.normaliser();   	
 	}
    
    public void testerIntegriteStructure(NodeDD n){
    	if(!n.isLeaf())
    	for(int i=0; i<n.kids.size(); i++){
    		if(n.kids.get(i).bottom==0){
    			NodeDD f;
    			f=n.kids.get(i).fils;
    			if(n.id!=f.fathers.get(f.fathers.indexOf(n.kids.get(i))).pere.id)
    				System.out.println("Warrning : ID fils pere");
    		}
    	}
    	if(n.id!=first.fils.id)
    	for(int i=0; i<n.fathers.size(); i++){
			NodeDD p;
			p=n.fathers.get(i).pere;
    		if(n.id!=p.kids.get(p.kids.indexOf(n.fathers.get(i))).fils.id)
    			System.out.println("Warrning : ID pere fisl");
    	}
    	
    	NodeDD trouve;
    	trouve=uht.recherche(n);
    	if(n.id!=trouve.id){
    		System.out.println("Warrning : ID");
    	}
    	
    	uht.removeFromTable(n);
    	trouve=uht.recherche(n);
    	if(trouve!=null){
    		System.out.println("Warrning : double");
    	}
    	uht.ajoutSansNormaliser(n);
    	
    }

    
    
    public void testerIntegriteStructureRecu(NodeDD n){
    	if(n.cpt==0){
    	
	    	NodeDD n2;
	    	
	    	for(int i=0; i<n.kids.size(); i++){
	    		n2=n.kids.get(i).fils;
	    		if(n2!=null){
	    			if(!n2.isLeaf()){
	    				testerIntegriteStructureRecu(n2);
	    			}
	    		}
	    	}
	    	testerIntegriteStructure(n);
	    	n.cpt=-1;
    	}
    	
    }
    
    public void testerIntegriteStructure(){
    	uht.detect();
    	testerIntegriteStructureRecu(first.fils);
    	uht.cptTo(0);
    	
    }
    
    
    public void countingtomoinsunR(NodeDD n){
    	if(n.counting!=-1){
    	
	    	NodeDD n2;
	    	
	    	for(int i=0; i<n.kids.size(); i++){
	    		n2=n.kids.get(i).fils;
	    		if(n2!=null){
	    			if(!n2.isLeaf()){
	    				countingtomoinsunR(n2);
	    			}
	    		}
	    	}
	    	n.counting=-1;
    	}
    	
    }
    
    public void countingtomoinsunR(){
    	countingtomoinsunR(first.fils);
    	
    }
    
    public boolean equivalenceRecu(NodeDD n, NodeDD ny){
    	boolean test;
    	if(n.cpt==0){		//pas encore passe par la
    		
    		if(ny.cpt!=0){
    			//System.out.println("err noeud");
    			return false;
    		}
	    	n.cpt=n.id;
	    	ny.cpt=n.id;

	    	NodeDD n2, ny2;
	    	
	    	
	    	for(int i=0; i<n.kids.size(); i++){
	    		n2=n.kids.get(i).fils;
	    		ny2=ny.kids.get(i).fils;

	    			
	    		if(n.kids.get(i).bottom>0 || !n.kids.get(i).actif){
	    			if(n.kids.get(i).bottom==0 && n.kids.get(i).actif){
		    			//System.out.println("err null");
		    			return false;
	    			}	
	    		}else{
		    		// verifie valuation
		    		if(!n.kids.get(i).s.equals(ny.kids.get(i).s)){
		    			//System.out.println("err valuation");
		    			return false;
		    		}
		    		
	    			if(!n2.isLeaf()){
	    				test=equivalenceRecu(n2, ny2);
	    				if(!test)
	    					return false;
	    			}
	    		}
	    	}
	    	
    	}else{
    		if(ny.cpt!=n.cpt){
    			//System.out.println("err noeud2");
    			return false;
    		}
    	}
    	return true;
    	
    }
    
    public boolean equivalence(VDD y){
    	//test des variables
    	boolean bool_variables=true, bool_structure=true, bool_valuation=true;
    	
    	if(variables.size()==y.variables.size()){
    		for(int i=0; i<variables.size(); i++){
    			if(variables.get(i).domain!=y.variables.get(i).domain){
    				//taille des domaines (ou ordre)
    				System.out.println("a");
    				bool_variables=false;
    			}
    		}
    	}else{
    		//nombre de variables
			System.out.println("b");
    		bool_variables=false;
    	}
    	
    	if(!first.s.equals(y.first.s)){
			System.out.println("c");
    		bool_valuation=false;
    	}
    	
    	bool_structure=equivalenceRecu(first.fils, y.first.fils);
    	
    	this.uht.cptTo(0);
    	y.uht.cptTo(0);
    	return(bool_variables && bool_valuation && bool_structure);
    	
    }
    
	
//accesseurs
    
	public UniqueHashTable getUHT(){
		return uht;
	}

//afficheurs
    
    public void toDot(String nameGraph, boolean afficheGraph){
    	
    	FileWriter fW;
//    	File f;
    	
    	String s;

		//fichier
    	if(nameGraph.endsWith(".dot"))
    		nameGraph=nameGraph.substring(0, nameGraph.length()-4);
    	
		String name_file= "./a/" +nameGraph+ ".dot";
		String name_pdf= "./a/" +nameGraph+ ".pdf";
		try{
			fW = new FileWriter(name_file);
		
			//entete comenté
			if(flagPlus)
				if(flagMult)
					s="//AADD\n";
				else
					s="//SLDDp\n";
			else
				if(flagMult)
					s="//SLDDt\n";
				else
					s="//ADD\n";
			fW.write(s);
			
			for(int i=0; i<variables.size(); i++){
				s="// "+i+" "+variables.get(i).name;
				for(int j=0; j<variables.get(i).domain; j++)
					s+=" " + variables.get(i).valeurs.get(j);
				s+="\n";
				fW.write(s);
			}
				
			
	    	//entete
	    	s="digraph "+nameGraph+" {\n";
	    	fW.write(s);
	    	
	    	//first arc
	    	s=first.toDot();
    		fW.write(s);
	    	
	    	//nodes
    		ArrayList<NodeDD> l;
    		for(int i=0; i<uht.nbVariables; i++){
    			l=uht.get(i);
		    	for(int j=0; j<l.size(); j++){
		    		//name label form
		    		s=l.get(j).toDot();		//true non definitif (binary only)
		    		fW.write(s);
		    	}
	    	}
    		
    		//last
			l=uht.getLast();
	    	for(int j=0; j<l.size(); j++){
	    		//name label form
	    		s=l.get(j).toDot();		//true non definitif (binary only)
	    		fW.write(s);
	    	}
    	
    		
    		/*for(int i=0; i<uht.nbVariables+1; i++)
		    	for(int j=0; j<uht.size(i); j++){
		    		//name label form
		    		s=uht.get(i).get(j).toDot();		//true non definitif (binary only)
		    		fW.write(s);
		    	}
	    	*/
	    	s="}\n";
	    	fW.write(s);
	    	
	    	fW.close(); 
    	
		}catch(java.io.IOException exc){System.out.println("pb de fichier: " + exc);}
    
		if(afficheGraph){
			try {	//creation pdf
				Runtime.getRuntime().exec("/usr/bin/dot dot -Tpdf " + name_file + " -o " + name_pdf);
			} catch (java.io.IOException exc) {System.out.println("pb de creation pdf: " + exc); }
	
			try {	//ouverture pdf
				Runtime.getRuntime().exec("/usr/bin/evince evince " + name_pdf);
			} catch (java.io.IOException exc) {System.out.println("pb d'ouverture pdf: " + exc); }
		}
    }
    
    
    
    public void affichageResultats(int arg_text, long start_time){
    	if(arg_text>=1){
	    	System.out.println("--");
			long end=System.currentTimeMillis();
			System.out.println("fin compilation :  " + (end-start_time)/1000+","+(end-start_time)%1000 + "s ("+(end-start_time)+"ms)");
			System.out.println(this.uht.size() + " noeuds et " + this.uht.sizeArcs() + " arcs");
			if(arg_text>=2)
			{
				long count = this.counting();
				if(count >= 0)
					System.out.println("nombre de modeles : "+count);
				else
					System.out.println("nombre de modeles : "+count+" (dépassement d'entier!)");
			}
    	}

    }
    
    public void transformation(String arg_formefinale, int arg_affich_text){
		if(flagPlus){		//sldd+ vers autre
			if(arg_formefinale.contains("AADD")){
				//System.out.println("SLDD+ : " + x.uht.size() + " nodes and " + x.uht.sizeArcs() + " edges");
				uht.copieToNull();
				slddToAadd();
				uht.copieToNull();
				//x[0].uht.testStructureUniforme();
				if(arg_affich_text>=1){
		    		System.out.println("--Transformation--");
					System.out.println("AADD : " + uht.size() + " nodes and " + uht.sizeArcs() + " edges");
				}
				//end=System.currentTimeMillis();
				//System.out.println("aadd :  " + (end-start)/1000+","+(end-start)%1000 + "s");
//				x[0].toDot("xaadd", true);
				


				
			}else{
				if(arg_formefinale.contains("ADD")){
					slddToAdd();
					if(arg_affich_text>=1){
						System.out.println("--Transformation--");
		    			System.out.println("ADD : " + uht.size() + " nodes and " + uht.sizeArcs() + " edges");
					}
					//x.addToSldd();
					//System.out.println("SLDD+ : " + x.uht.size() + " nodes and " + x.uht.sizeArcs() + " edges");
					//end=System.currentTimeMillis();
					//System.out.println("add :  " + (end-start)/1000+","+(end-start)%1000 + "s");
				}else{
					if(arg_formefinale.contains("SLDDt")){
				    	if(arg_affich_text>=1){
							System.out.println("--Transformation--");
				    		System.out.println("Attention : transformation SLDD+ -> SLDDx déconseillée");
				    		System.out.println("Passage par la forme AADD (pour passer par la forme ADD, encore plus deconeillée, plus d'infos données avec la commande -text=3)");
				    		if(arg_affich_text>=3){
					    		System.out.println("Vous tenez vraiment à passer par le langage ADD? il va falloir modifier le code.");
					    		System.out.println("\t ouvrez le fichier ./br4cp/VDD.java");
					    		System.out.println("\t lancez une recherche du mot \"shlagu"+"evuk\" dans le code (ca veut dire \"manger\" en troll, mais on s'en fout), cela vous ammenera a une ligne bien précise");
					    		System.out.println("\t sur cette même ligne, changer la condition du if \"true\" en \"false\"");
				    		}
				    		
				    	}
				    	
				    	/*if(false){						//"shlaguevuk"     hihi c'est bien ici 
							slddToAadd();
							if(arg_affich_text>=2)
								System.out.println("AADD : " + uht.size() + " nodes and " + uht.sizeArcs() + " edges");
							//end=System.currentTimeMillis();
							//System.out.println("add :  " + (end-start)/1000+","+(end-start)%1000 + "s");
							aaddToSlddMult();
							if(arg_affich_text>=1)
								System.out.println("SLDDt " + uht.size() + " (" + uht.sizeArcs() + ")");
							//end=System.currentTimeMillis();
							//System.out.println("slddt :  " + (end-start)/1000+","+(end-start)%1000 + "s");
				    	}else{*/
				    		if(arg_affich_text>=1)
					    		System.out.println("Passage forcé par la forme ADD");
				    		
							slddToAdd();
							if(arg_affich_text>=2)
								System.out.println("ADD : " + uht.size() + " nodes and " + uht.sizeArcs() + " edges");
							//end=System.currentTimeMillis();
							//System.out.println("add :  " + (end-start)/1000+","+(end-start)%1000 + "s");
							addToSlddMult();
							if(arg_affich_text>=1)
								System.out.println("SLDDt " + uht.size() + " (" + uht.sizeArcs() + ")");
							//end=System.currentTimeMillis();
							//System.out.println("slddt :  " + (end-start)/1000+","+(end-start)%1000 + "s");
//				    	}
					}
				}
			}
		}else{				//slddt vers autre
			if(arg_formefinale.contains("AADD")){
				uht.copieToNull();
				slddMultToAadd();
				uht.copieToNull();
				if(arg_affich_text>=1){
					System.out.println("--Transformation--");
					System.out.println("AADD : " + uht.size() + " nodes and " + uht.sizeArcs() + " edges");
				}
				
				
			}else{
				if(arg_formefinale.contains("ADD")){
					slddMultToAdd();
					if(arg_affich_text>=1){
						System.out.println("--Transformation--");
						System.out.println("ADD : " + uht.size() + " nodes and " + uht.sizeArcs() + " edges");
					}
					//System.out.println("add " + x[0].uht.size() + " (" + x[0].uht.sizeArcs() + ")");
					//end=System.currentTimeMillis();
					//System.out.println("add :  " + (end-start)/1000+","+(end-start)%1000 + "s");
				}else{
					if(arg_formefinale.contains("SLDDp")){
				    	if(arg_affich_text>=1){
							System.out.println("--Transformation--");
				    		System.out.println("Attention : transformation SLDDx -> SLDD+ VIVEMENT déconseillée");
				    		System.out.println("les valuations des SLDDx sont des réels alors que les valuations des SLDD+ sont des entiers naturels");
				    		System.out.println("Passage par la forme AADD (plus d'info sur cette transfo avec l'option -text=3)");
				    		if(arg_affich_text>=3){
					    		System.out.println("Si vous voulez passer par le langage ADD? il va falloir modifier le code.");
					    		System.out.println("\t ouvrez le fichier ./br4cp/VDD.java");
					    		System.out.println("\t lancez une recherche du mot \"hyppolite"+"_bergamote\" dans le code (c'est un personnage de tintin (: ), cela vous ammenera a une ligne bien précise");
					    		System.out.println("\t sur cette même ligne, changer la condition du if \"true\" en \"false\"");
					    		System.out.println("------------------");
					    		System.out.println("Si vous souhaitez augmenter le nombre de chiffrs significatifs lors d'un passage de SLDDx à SLDD+ (passage de réel à entier) :");
					    		System.out.println("\t ouvrez le fichier ./br4cp/VDD.java");
					    		System.out.println("\t lancez une recherche du mot \"le_facteur_n_es"+"t_pas_passe\"  dans le code, cela vous ammenera a une ligne bien précise");
					    		System.out.println("\t chagez la valeur de \"facteur\".");
					    		System.out.println("\t in est conseillé de mettre une puissance de 10. par exemple facteur=100 passera un prix en centimes.");
				    		}
				    		
				    	}
				    	int facteur=1;					//"le_facteur_n_est_pas_passe"     modifier la valeur de facteur pour ajouter un facteur lors de la convertion float vers int, pour avoir des entiers plus precis   
				    	
/*				    	if(false){						//"hyppolite_bergamote"     hihi c'est bien ici 
				    		
							slddMultToAadd();
							if(arg_affich_text>=2)
								System.out.println("AADD : " + uht.size() + " nodes and " + uht.sizeArcs() + " edges");
							//end=System.currentTimeMillis();
							//System.out.println("add :  " + (end-start)/1000+","+(end-start)%1000 + "s");
							aaddToSldd(facteur);
							if(arg_affich_text>=1)
								System.out.println("SLDDp " + uht.size() + " (" + uht.sizeArcs() + ")");
							//end=System.currentTimeMillis();
							//System.out.println("slddt :  " + (end-start)/1000+","+(end-start)%1000 + "s");
				    	}else{*/
				    		if(arg_affich_text>=1)
					    		System.out.println("Passage forcé par la forme ADD");
				    		
							slddMultToAdd();
							if(arg_affich_text>=2)
								System.out.println("ADD : " + uht.size() + " nodes and " + uht.sizeArcs() + " edges");
							//end=System.currentTimeMillis();
							//System.out.println("add :  " + (end-start)/1000+","+(end-start)%1000 + "s");
							addToSldd(facteur);
							if(arg_affich_text>=1)
								System.out.println("SLDDp " + uht.size() + " (" + uht.sizeArcs() + ")");
							//end=System.currentTimeMillis();
							//System.out.println("slddt :  " + (end-start)/1000+","+(end-start)%1000 + "s");
//				    	}
					}
				}
			}
		}
    }

    
    
    public void toXML(String nameGraph){
    	
    	FileWriter fW;
    	
    	String s;
    	
		String name_file= "./" + nameGraph + ".xml";

		//fichier
		try{
			fW = new FileWriter(name_file);
		
			//0 instance
			fW.write("<instance>\n");
			//1 presentation	/presentation
			s="\t<presentation format=\"XmlVdd\" type=\"slddplus\" name=\""+nameGraph+"\"/>\n";
			fW.write(s);
			//1 domains
			s="\t<domains nbDomains=\""+ this.variables.size() +"\">\n";
			fW.write(s);
			
			//2 domain
			for(int i=0; i<this.variables.size(); i++){
				s="\t\t<domain name=\"D"+ this.variables.get(i).name +"\" nbValues=\""+ this.variables.get(i).domain +"\">\n";
				fW.write(s);
				for(int j=0; j<this.variables.get(i).domain; j++){
					//3 val
					s="\t\t\t<val name=\""+ this.variables.get(i).valeurs.get(j) +"\"/>\n";
					fW.write(s);
				}
				fW.write("\t\t</domain>\n");
			}
			fW.write("\t</domains>\n");
			
			//1 variables
			s="\t<variables nbVariables=\""+ this.variables.size() +"\">\n";
			fW.write(s);
			for(int i=0; i<this.variables.size(); i++){
				//2 variable
				s="\t\t<variable name=\""+ this.variables.get(i).name +"\" domain=\"D"+ this.variables.get(i).name +"\"/>\n";
				fW.write(s);
			}
			fW.write("\t</variables>\n");
			
			
			//1 automate
			s="\t<automate nbNiv=\""+ this.variables.size() +"\" type=\"slddplus\" offset=\""+ first.s.toTxt() +"\" root=\"q"+first.fils.id+"\" sink=\"q"+last.id+"\" ordered=\"true\">\n";
			fW.write(s);
			
			for(int i=0; i<this.variables.size(); i++){
				//2 niveau
				s="\t\t<niveau variable=\""+ this.variables.get(i-1).name +"\" nbNoeuds=\""+uht.size(i)+"\" nbTransitions=\""+uht.sizeArcs(i)+"\">\n";
				fW.write(s);
				ArrayList<NodeDD> l=uht.get(i);
				for(int j=0; j<l.size(); j++){
					for(int k=0; k<l.get(j).kids.size(); k++){
						//3 trans
						if(l.get(j).kids.get(k).bottom==0){
							s="\t\t\t<trans inNode=\"q"+l.get(j).id+"\" value=\""+l.get(j).kids.get(k).pos+"\" outNode=\"q"+l.get(j).kids.get(k).fils.id+"\" phi=\""+l.get(j).kids.get(k).s.toTxt()+"\"/>\n";
							fW.write(s);
						}
					}
				}
				fW.write("\t\t</niveau>\n");
			}
			
			fW.write("\t</automate>\n");
			fW.write("</instance>\n");
			
		
	    	
	    	fW.close(); 
    	
		}catch(java.io.IOException exc){System.out.println("pb de fichier: " + exc);}
    
    }
    
    public VDD clone(){
    	NodeDD n;
  //  	Arc curr;
    	int index;
    	ArrayList<NodeDD> lp1, lf1, lp2, lf2;
    	lp1=new ArrayList<NodeDD>();
    	lf1=new ArrayList<NodeDD>();
    	lp2=new ArrayList<NodeDD>();
    	lf2=new ArrayList<NodeDD>();

    	n=new NodeDD(first.fils.variable, first.fils.id);
    	VDD newVDD=new VDD(new Arc(n, this.first.s.copie()), new UniqueHashTable(uht.nbVariables), this.variables);
    	//curr=newVDD.first;
    	newVDD.uht.ajoutSansNormaliser(newVDD.last);
    	
    	lp1.add(first.fils);
    	lp2.add(n);
    	newVDD.first.changerFils(n);
    	
    	
    	for(int i=0; i<variables.size(); i++){
    		lf1=uht.get(i);
    		for(int j=0; j<lf1.size(); j++)
    			lf2.add(new NodeDD(lf1.get(j).variable, lf1.get(j).id));
    		for(int j=0; j<lp1.size(); j++){
    			for(int k=0; k<lp1.get(j).kids.size(); k++){
    				if(lp1.get(j).kids.get(k).bottom!=0){
    					new Arc(lp2.get(j), null, k, true, lp1.get(j).kids.get(k).s.copie());
    				}else{
    					index=lf1.indexOf(lp1.get(j).kids.get(k).fils);
    					new Arc(lp2.get(j), lf2.get(index), k, false, lp1.get(j).kids.get(k).s.copie());
    				}
    			}
    			newVDD.uht.ajoutSansNormaliser(lp2.get(j));
    		}
    		
    		lp1.clear();
    		lp2.clear();
    		lp1=lf1;
    		lp2=lf2;
    		lf1=new ArrayList<NodeDD>();
    		lf2=new ArrayList<NodeDD>();
    		
    	}
    	
		
		for(int j=0; j<lp1.size(); j++){
			for(int k=0; k<lp1.get(j).kids.size(); k++){
				if(lp1.get(j).kids.get(k).bottom!=0){
					new Arc(lp2.get(j), null, k, true, lp1.get(j).kids.get(k).s.copie());
				}else{
					new Arc(lp2.get(j), newVDD.last, k, false, lp1.get(j).kids.get(k).s.copie());
				}
			}
			newVDD.uht.ajoutSansNormaliser(lp2.get(j));
		}
		
		return newVDD;
    }
    
    public Variance variance(TestIndependance methode, String name){
       	return new Variance(variables, this, methode, name);
    }
    
    //renvoie la var correspondant a la string s
    public Var getVar(String s){
    	for(int i=0; i<variables.size(); i++){
    		if(variables.get(i).name.trim().compareTo(s.trim())==0){
    			return variables.get(i);
    		}
    	}
    	System.out.print("Variable "+s+" introuvable");
    	return null;
    }

    /**
     * Inférence sur un SLDD*
     * @param v
     * @return
     */
	public Map<String, Double> calculeDistributionAPosteriori(Var v, ArrayList<String> historiqueOperations, Set<String> values)
	{
		Map<String, Double> m = new HashMap<String, Double>();
		double inference;
		for(String value: values)
//		for(String value: v.valeurs)
		{
			if(v.conv(value) == -1)
				continue;
			conditioner(v, v.conv(value));
			inference = inference();
			m.put(v.valeurs.get(v.conv(value)), inference);
			System.out.println(v.valeurs.get(v.conv(value))+": "+inference);
			deconditioner(v);
		}

    	return m;
	}
	
	public void updatePassage(Map<String, String> mapVarnameDom){
		NodeDD n=first.fils;
		int dom;
		first.passage1++;

		while(!n.isLeaf()) {
			dom=n.variable.conv(mapVarnameDom.get(n.variable.name));
			n.kids.get(dom).passage1++;
			n=n.kids.get(dom).fils;
		}
		
	}
	
	public ArrayList<Integer> getOnlyChildParents(boolean verbose) {
		ArrayList<Integer> listOnlyChild = new ArrayList<Integer>();
		
		ArrayList<NodeDD> list;
		boolean onlyChild;
		int idOnlyChild;
		
		
		for(int i=0; i<uht.nbVariables; i++){
			list=uht.get(i);
			onlyChild=list.size()>0;

			fullListLoop:
			for(NodeDD n:list) {
				idOnlyChild=-1;
				
				for(int j=0; j<n.kids.size(); j++) {
					if(n.kids.get(j).bottom==0 && n.kids.get(j).actif) {
						if(idOnlyChild==-1)
							idOnlyChild=n.kids.get(j).fils.id;
						else {
							if(idOnlyChild!=n.kids.get(j).fils.id) {
								onlyChild=false;
								break fullListLoop;
							}
						}
					}
				}
			}
			if(onlyChild) {
				listOnlyChild.add(i);
				
			}
			
		}
		
		return listOnlyChild;
	}
	
	public void forgetOnlyChildParents(int etage) {
		
		ArrayList<NodeDD> list;
		NodeDD child=null;
		NodeDD pere=null;
		
		list=(ArrayList<NodeDD>) uht.get(etage).clone();
		for(NodeDD n:list) {
			
			//looking for child
			for(int j=0; j<n.kids.size(); j++) {
				if(n.kids.get(j).bottom==0 && n.kids.get(j).actif) {
					child=n.kids.get(j).fils;
					break;
				}
			}
			
			//for every incoming edge
			while(n.fathers.size()>0 && etage!=0) {

				pere=n.fathers.get(0).pere;
				uht.removeFromTable(pere);
				
				n.fathers.get(0).changerFils(child);
				
				uht.ajoutSansNormaliser(pere);
			}
			//delete n
			uht.removeDefinitely(n);
		}
		

			
	}
	
	//non only child
	public ArrayList<Integer> getDecisionVariables(boolean verbose) {
		ArrayList<Integer> listDecisionVariables = new ArrayList<Integer>();
		
		ArrayList<NodeDD> list;
		boolean onlyChild;
		int idOnlyChild;
		
		
		for(int i=0; i<uht.nbVariables; i++){
			list=uht.get(i);
			onlyChild=true;

			fullListLoop:
			for(NodeDD n:list) {
				idOnlyChild=-1;
				
				for(int j=0; j<n.kids.size(); j++) {
					if(n.kids.get(j).bottom==0 && n.kids.get(j).actif) {
						if(idOnlyChild==-1)
							idOnlyChild=n.kids.get(j).fils.id;
						else {
							if(idOnlyChild!=n.kids.get(j).fils.id) {
								onlyChild=false;
								break fullListLoop;
							}
						}
					}
				}
			}
			if(!onlyChild) {
				if(verbose)
					System.out.println(variables.get(i).name);
				listDecisionVariables.add(i);
			}
			
		}
		
		return listDecisionVariables;
	}
	
	public void learnVariable() {
		ArrayList<Integer> listDecisionVariables = getDecisionVariables(false);
		for(int iVarDec=0; iVarDec<listDecisionVariables.size(); iVarDec++) {
			double entropie=0;
			
		}
	}
	
	public void entropie(int aApprendre, int decision) {
    	Var vDecision=uht.get(decision).get(0).variable;

    	for(int i=0; i<vDecision.domain; i++) {
    		conditioner(decision, i);
			minMaxConsistanceMaj(decision, true);
			toDot("s"+i, false);
			System.out.println("plop="+entropie(aApprendre));
			deconditioner(decision);
    	}
		
	}
	
	public double entropie(int aApprendre) {
		double entropie=0;
    	ArrayList<Integer> counts=new ArrayList<Integer>();
    	int nbSol=0;
    	Var v;
    	
		if(first.actif && first.bottom==0){
    		first.fils.counting=1;
    	}
    	for(int i=0; i<uht.getLast().size(); i++){
    		uht.getLast().get(i).counting=1;
    	}
    	
    	//preparation counting
    	ArrayList<NodeDD> allNodes=uht.get(aApprendre);
    	for(NodeDD node : allNodes){
    		counting(node);
    		for(Arc kid :node.kids) {
    			if(kid.bottom==0 && kid.actif)
    				countingFromBottom(kid.fils);
    		}
    	}
    	
    	//counting
    	v=allNodes.get(0).variable;
    	
    	for(int i=0; i<v.domain; i++) {
    		int count=0;
        	for(NodeDD node : allNodes){
        		if(node.kids.get(i).bottom==0 && node.kids.get(i).actif) {
        			count+=(node.counting*node.kids.get(i).fils.counting);
        		}
        	}
        	counts.add(count);
        	nbSol+=count;
        	System.out.println(v.name + "("+i+"/"+v.domain+") : " + count);
    	}
    	System.out.println(v.name + " : " + nbSol);

    	//calcul entropie
    	for(int i=0; i<v.domain; i++) {
    		double proba=((double)counts.get(i))/nbSol;
        	System.out.println(v.name + "("+i+"/"+v.domain+") = " + proba + " * Math.log("+proba+") / Math.log("+v.domain+"))");
        	if(proba>0) {
        		System.out.println(v.name + "("+i+"/"+v.domain+") : " + (proba * Math.log(proba) / Math.log(v.domain)));
        		entropie -= proba * Math.log(proba) / Math.log(v.domain);
        	}else {
        		System.out.println(v.name + "("+i+"/"+v.domain+") : " + 0);
        	}
        		
    	}
    	System.out.println("entropie="+entropie);
    	
    	uht.countingToMoinsUn();
    	
    	return entropie;
    	
	}
	
	

	
   /* public VDD learnUp(Var variable, int valeur, VDD newVDD){
    	if(newVDD==null) {
        	UniqueHashTable newUht = new UniqueHashTable(variables.size());
    		newVDD = new VDD(null, newUht, this.variables);
    	}
    	int variablePos=variable.pos;
    	NodeDD first=null;
    	NodeDD isNewFirst=null;
    	
    	boolean atLeastOneModel=false;
    	
    	ArrayList<NodeDD> nodes;
    	
    	ArrayList<NodeDD> allNewNodes=new ArrayList<NodeDD>();
    	ArrayList<Var> allNewVar=new ArrayList<Var>();

		conditioner(variablePos, valeur);
		minMaxConsistanceMaj(variablePos, true);
    	uht.countingToMoinsUn();
    	
    	    	
    	
    	
    	for(int i=0; i<uht.get(variablePos).size(); i++){
    		if(uht.get(variablePos).get(i).kidsdiffbottomActif()>0) {
    			uht.get(variablePos).get(i).counting=0;
    			atLeastOneModel=true;
    		}
    		else
    			uht.get(variablePos).get(i).counting=-1;
    	}
    	
    	allNewNodes.add(newVDD.last);
    	
    	
    	for(int i=variablePos-1; i>=0; i--){
    		nodes=uht.get(i);
        	for(int j=0; j<nodes.size(); j++){
        		isNewFirst=learnUp(nodes.get(j), newVDD, allNewNodes, allNewVar);
        		if(isNewFirst!=null)
        			first=isNewFirst;
        	}
    	}
    	deconditioner(variable);
    	minMaxConsistance();
    	
    	//make newVDD real
    	if(first==null) {
    		first=newVDD.last;
    	}

    	
    	//reorganise allNewVar by order
    	for(int i=0; i<allNewVar.size()-1; i++) {
    		if(allNewVar.get(i+1).pos<allNewVar.get(i).pos) {
    			allNewVar.add(i+1, allNewVar.remove(i));		//switch places
    			i-=2;
    			if(i==-2)
    				i=-1;
    		}
    	}
    	
    	newVDD.first = new Arc(first, true);
    	newVDD.variables=allNewVar;
    	newVDD.uht.ajoutSansNormaliser(newVDD.last);
    	
    	if(!atLeastOneModel) {
    		newVDD.first.bottom++;
    	}
    	
    	newVDD.toDot(variable.name+"_"+valeur, false);
    	return newVDD;
    }*/
    public VDD learnUp_all(Var variable, VDD newVDD){
    	if(newVDD==null) {
        	UniqueHashTable newUht = new UniqueHashTable(variables.size());
    		newVDD = new VDD(null, newUht, this.variables);
    	}
    	int variablePos=variable.pos;
    	NodeDD first=null;
    	NodeDD isNewFirst=null;
    	
    	boolean atLeastOneModel=false;
    	boolean atLeastOneModelForAll=false;

    	ArrayList<NodeDD> nodes;
    	
    	ArrayList<NodeDD> allNewNodes=new ArrayList<NodeDD>();
    	ArrayList<Var> allNewVar=new ArrayList<Var>();

    	allNewNodes.add(newVDD.last);
    	
    	//ajout du 1er node
    	NodeDD n1=new NodeDD(variable);
    	newVDD.first = new Arc(n1, true);
    	
    	for(int val=0; val<variable.domain; val++) {

    		atLeastOneModel=false;
    		conditioner(variablePos, val);
    		minMaxConsistanceMaj(variablePos, true);
    		uht.countingToMoinsUn();
    	
    	    	
    	
    	
	    	for(int i=0; i<uht.get(variablePos).size(); i++){
	    		if(uht.get(variablePos).get(i).kidsdiffbottomActif()>0) {
	    			uht.get(variablePos).get(i).counting=0;
	    			atLeastOneModel=true;
	    			atLeastOneModelForAll=true;
	    		}
	    		else
	    			uht.get(variablePos).get(i).counting=-1;
	    	}    	
	    	
	    	
	    	for(int i=variablePos-1; i>=0; i--){
	    		nodes=uht.get(i);
	        	for(int j=0; j<nodes.size(); j++){
	        		isNewFirst=learnUp(nodes.get(j), newVDD, allNewNodes, allNewVar);
	        		if(isNewFirst!=null)
	        			first=isNewFirst;
	        	}
	    	}
	    	deconditioner(variable);
	    	minMaxConsistance();
	    	
	    	
	    	//make newVDD real
	    	if(first==null) {
	    		first=newVDD.last;
	    	}
			new Arc(n1, first, val, new Sp());
			if(!atLeastOneModel) {
				n1.kids.get(val).bottom++;
			}
			
    	}
    	
    	//reorganise allNewVar by order
    	for(int i=0; i<allNewVar.size()-1; i++) {
    		if(allNewVar.get(i+1).pos<allNewVar.get(i).pos) {
    			allNewVar.add(i+1, allNewVar.remove(i));		//switch places
    			i-=2;
    			if(i==-2)
    				i=-1;
    		}
    	}
    	allNewVar.add(0, variable);
    	
    	newVDD.variables=allNewVar;
    	newVDD.uht.ajoutSansNormaliser(newVDD.last);
    	newVDD.uht.ajoutSansNormaliser(n1);

    	if(!atLeastOneModelForAll) {
    		newVDD.first.bottom++;
    	}
    	
    	newVDD.toDot(variable.name+"_all", false);
    	return newVDD;
    }  
    
    public VDD learnUp_all(Var variable, VDD newVDD, boolean onLastPos){
    	if(newVDD==null) {
        	UniqueHashTable newUht = new UniqueHashTable(variables.size());
    		newVDD = new VDD(null, newUht, this.variables);
    	}
    	int variablePos=variable.pos;
    	NodeDD first=null;
    	NodeDD isNewFirst=null;
    	
    	boolean atLeastOneModel=false;
    	
    	ArrayList<NodeDD> nodes;
    	
    	ArrayList<NodeDD> allNewNodes=new ArrayList<NodeDD>();
    	ArrayList<Var> allNewVar=new ArrayList<Var>();

    	allNewNodes.add(newVDD.last);
    	
    	//ajout du 1er/drrnier node
    	NodeDD n1=null;
    	ArrayList<NodeDD> nx=null;

    	if(!onLastPos) {
    		n1=new NodeDD(variable);
    		newVDD.first = new Arc(n1, true);
    	}
    	
    	NodeDD firstfirst=null;
    	for(int val=0; val<variable.domain; val++) {
    		if(onLastPos) {
    	    	allNewNodes.set(0, nx.get(val));
    		}
    		
    		conditioner(variablePos, val);
    		minMaxConsistanceMaj(variablePos, true);
    		uht.countingToMoinsUn();
    	
    	    	
    	
    	
	    	for(int i=0; i<uht.get(variablePos).size(); i++){
	    		if(uht.get(variablePos).get(i).kidsdiffbottomActif()>0) {
	    			uht.get(variablePos).get(i).counting=0;
	    			atLeastOneModel=true;
	    		}
	    		else
	    			uht.get(variablePos).get(i).counting=-1;
	    	}    	
	    	
	    	
	    	for(int i=variablePos-1; i>=0; i--){
	    		nodes=uht.get(i);
	        	for(int j=0; j<nodes.size(); j++){
	        		isNewFirst=learnUp(nodes.get(j), newVDD, allNewNodes, allNewVar);
	        		if(isNewFirst!=null)
	        			first=isNewFirst;
	        	}
	    	}
	    	deconditioner(variable);
	    	minMaxConsistance();
	    	
	    	//make newVDD real
	    	if(first==null) {
	    		first=newVDD.last;
	    	}
	    	
	    	if(!onLastPos) {
	    		new Arc(n1, first, val, new Sp());
	    	}else {
	    		if(firstfirst==null)
	    			firstfirst=first;
	    		else
	    			firstfirst.fusion(first);
	    	}

    	}
		if(onLastPos) {
	    	allNewNodes.set(0, newVDD.last);
		}
    	
    	//reorganise allNewVar by order
    	for(int i=0; i<allNewVar.size()-1; i++) {
    		if(allNewVar.get(i+1).pos<allNewVar.get(i).pos) {
    			allNewVar.add(i+1, allNewVar.remove(i));		//switch places
    			i-=2;
    			if(i==-2)
    				i=-1;
    		}
    	}
    	if(!onLastPos) {
    		allNewVar.add(0, variable);
    	}else {
    		allNewVar.add(variable);
    	}
    	
    	newVDD.variables=allNewVar;
    	newVDD.uht.ajoutSansNormaliser(newVDD.last);
    	if(!onLastPos) {
    		newVDD.uht.ajoutSansNormaliser(n1);
    	}else {
    		for(int i=0; i<variable.domain; i++)
    			newVDD.uht.ajoutSansNormaliser(nx.get(i));
    	}

    	if(!atLeastOneModel) {
    		newVDD.first.bottom++;
    	}
    	
    	newVDD.toDot(variable.name+"_all_upsideDown", false);
    	return newVDD;
    }  
    
	public NodeDD learnUp(NodeDD n, VDD newVDD, ArrayList<NodeDD> allNewNodes, ArrayList<Var> allNewVar){
		Arc a;
		long origine=-2; //-2 not found, -1 inactif, 0..x actif
		boolean allTheSame=true;
		
		for(int i=0; i<n.kids.size(); i++) {
			a=n.kids.get(i);
			if(a.bottom==0) {
				
				//find first diff bottom==0
				if(origine==-2) {
					if(a.actif)
						origine=a.fils.counting;
					else
						origine=-1;
				}
				else {
					if((a.actif && a.fils.counting!=origine) || (!a.actif && origine!=-1)) {
						allTheSame=false;
						break;
					}
				}
			}
				
		}
		
		if(allTheSame) {
			n.counting=origine;
		}else {
			int num;
			//add var
			if(allNewVar.indexOf(n.variable)==-1)
				allNewVar.add(n.variable);
			
			//add node
			NodeDD newNode = new NodeDD(n.variable);
			
			for(int i=0; i<n.kids.size(); i++) {
				Structure s=new Sp();

				//old arc
				a=n.kids.get(i);
				if(a.bottom==0 && a.actif) {					
					num=(int)a.fils.counting;
					//new arc
					if(num!=-1) {
						new Arc(newNode, allNewNodes.get(num), i, s);		//int donc SLDD+
					}else {
						new Arc(newNode, newVDD.last, i, s);		//int donc SLDD+
						newNode.kids.get(i).bottom++;
					}
				}else {
					new Arc(newNode, newVDD.last, i, s);		//int donc SLDD+
					newNode.kids.get(i).bottom++;
				}
			}
			
			//add newNode to uht
			int cptNode=newVDD.uht.get(n.variable.pos).size();
			newNode=newVDD.uht.ajoutSansNormaliser(newNode);
			if(newVDD.uht.get(n.variable.pos).size()>cptNode) {
				allNewNodes.add(newNode);
				n.counting=allNewNodes.size()-1;
			}else {
				n.counting=allNewNodes.indexOf(newNode);
			}

			
			
			return newNode;	
		}
		
		return null;
		
		
	}
	
	public ArrayList<NodeDD> mergeAllCase(int posInOrder) {
		Map<NodeDD, NodeDD> adresseMap = null;
		ArrayList<NodeDD> savelist=new ArrayList<NodeDD>();
		
		ArrayList<NodeDD> nodes;
		ArrayList<Arc> fathers;

		Arc arcX;
		NodeDD pereX;
		NodeDD frerotCurr;
		
		if(posInOrder<uht.nbVariables) {
			//node:pas en commun && father : variable en commun
			nodes=(ArrayList<NodeDD>) uht.get(posInOrder).clone();
		}else {
			/////////////////  cas last node //////////////
			nodes = uht.getLast();
			for(NodeDD node:nodes) {
				//remove and save
				
				fathers=(ArrayList<Arc>) node.fathers.clone();
				for(Arc father:fathers) {
					int pos=father.pos;
					pereX=father.pere.adresse;
					if(!pereX.isLeaf()) {
						arcX=pereX.kids.get(pos);
						//pas encore de duplication
						if(arcX.bottom>0) {
							father.bottom++;
							father.changerFils(uht.getLast().get(0));
							//father.activer(false);
						}
					}
				}
				
				
			}
			nodes=new ArrayList<NodeDD>();
			return nodes;
			/////////////////////////////////////////////
		}
		for(NodeDD node:nodes) {
			//remove and save
			
			fathers=(ArrayList<Arc>) node.fathers.clone();
			for(Arc father:fathers) {
				int pos=father.pos;
				pereX=father.pere.adresse;
				
				if(!pereX.isLeaf() && pereX.variable.equals(father.pere.variable)) {
					/////////////////////cas father commun ////////////////////////////
					arcX=pereX.kids.get(pos);
					//pas encore de duplication
					if(arcX.bottom>0) {
						father.bottom++;
						father.changerFils(uht.getLast().get(0));
						//father.activer(false);
					}else {
						//pas encore de duplication
						if(node.adresse==null) {
							node.adresse=arcX.fils;
						}else{		//duplication?
							if(arcX.fils!=node.adresse){
								
								////////////////////
								//init if needeed
								if(adresseMap==null)
									adresseMap=new HashMap<NodeDD, NodeDD>();
								
								frerotCurr=adresseMap.get(arcX.fils);
								if(frerotCurr==null) {
									
									frerotCurr=new NodeDD(node, father);
									frerotCurr.cpt=1;
									adresseMap.put(arcX.fils, frerotCurr);
									
									savelist.add(frerotCurr);
	
								}else {
									father.changerFils(frerotCurr);
								}
								frerotCurr.adresse=arcX.fils;
									
				    			
							}
							
						}
					}
					/////////////////////////////////////////////////
				}else {							
					//////////////cas father pas comun /////////////
					if(node.adresse==null) {
						node.adresse=pereX;
					}else{		//duplication?
						if(pereX!=node.adresse){
							
							////////////////////
							//init if needeed
							if(adresseMap==null)
								adresseMap=new HashMap<NodeDD, NodeDD>();
							
							frerotCurr=adresseMap.get(pereX);
							if(frerotCurr==null) {
								frerotCurr=new NodeDD(node, father);
								frerotCurr.cpt=1;
								adresseMap.put(pereX, frerotCurr);
								savelist.add(frerotCurr);
							}else {
								father.changerFils(frerotCurr);
							}
							frerotCurr.adresse=pereX;	
						}
						
					}
					////////////////////////////////////////////////
				}
			}
			
			if(node.fathers.size()==0){
    			//node.cpt=-1;
    			uht.removeDefinitely(node);
			}else {
				uht.removeFromTable(node);
				savelist.add(node);
			}
			
			if(adresseMap!=null)
				adresseMap.clear();
		}
		
		return savelist;
	}
	

	//todo add valued data
	public void merge(VDD X) {
		uht.copieToNull();
		
		ArrayList<NodeDD> nodes;
		ArrayList<ArrayList<NodeDD>> savelist = new ArrayList<ArrayList<NodeDD>>();

		if(X.variables.size()==0) {
			return;
		}
		
		NodeDD startNode=X.first.fils;
		Var startVar=X.variables.get(0);
		Var endVar=X.variables.get(X.variables.size()-1);
		
		//var 1
		
		nodes=uht.get(startVar.pos);
		
		for(NodeDD node:nodes) {
			node.adresse=startNode;
			uht.removeFromTable(node);
		}
		savelist.add(nodes);
		

		for(int i=startVar.pos+1; i<=endVar.pos; i++) {
				savelist.add(mergeAllCase(i));
		}
		
		//get first next node existing
		int next=endVar.pos+1;
		while(next<uht.nbVariables && uht.get(next).size()==0) {
			next++;
		}
		//finish
		savelist.add(mergeAllCase(next));
		
		
		for(int i=savelist.size()-1; i>=0; i--) {
			for(int j=0; j<savelist.get(i).size(); j++) {
				uht.ajoutNormaliseReduit(savelist.get(i).get(j));
			}
		}
		
		uht.copieToNull();

		uht.supprNeudNegatifs();
		
		if(startVar.pos>0)
			uht.normaliser();
		
	}

	public void merge(VDD X, int valeur) {
		uht.copieToNull();
		
		ArrayList<NodeDD> nodes;
		ArrayList<ArrayList<NodeDD>> savelist = new ArrayList<ArrayList<NodeDD>>();

		if(X.variables.size()==0) {
			return;
		}
		
		NodeDD startNode=X.first.fils.kids.get(valeur).fils;
		if(!startNode.isLeaf()) {								//if leaf, it's over
			Var startVar=startNode.variable;
			Var endVar=X.variables.get(X.variables.size()-1);
			
			//var 1
			
			nodes=uht.get(startVar.pos);
			
			for(NodeDD node:nodes) {
				node.adresse=startNode;
				uht.removeFromTable(node);
			}
			savelist.add(nodes);
			
	
			for(int i=startVar.pos+1; i<=endVar.pos; i++) {
					savelist.add(mergeAllCase(i));
			}
			
			//get first next node existing
			int next=endVar.pos+1;
			while(next<uht.nbVariables && uht.get(next).size()==0) {
				next++;
			}
			//finish
			savelist.add(mergeAllCase(next));
			
			
			for(int i=savelist.size()-1; i>=0; i--) {
				for(int j=0; j<savelist.get(i).size(); j++) {
					uht.ajoutNormaliseReduit(savelist.get(i).get(j));
				}
			}
			
			uht.copieToNull();
	
			uht.supprNeudNegatifs();
			
			if(startVar.pos>0)
				uht.normaliser();
		}else {
			if(X.first.fils.kids.get(valeur).bottom>0) {
				System.out.println("VDD : no solution");
				this.first.changerFils(last);
				first.bottom++;
			}
		}
		
	}
	
	public void mergeWithMissingVars(VDD X) {
		Var comonVar = X.first.fils.variable;	//variable sur laquelle on merge
		Var temp;
		
		ArrayList<Var> varToAdd = new ArrayList<Var>();
		ArrayList<Var> allVar = new ArrayList<Var>();
		//detecter variables a ajouter
		for(int i=1; i<X.variables.size(); i++) {
			if(!this.variables.contains(X.variables.get(i)))
				varToAdd.add(X.variables.get(i));
		}
		
		///// ajouter la structure "blanche" /////
		//allVarInOrder
		allVar.addAll(this.variables);
		allVar.addAll(varToAdd);
		allVar.remove(comonVar);
		//sort
		for(int i=0; i<allVar.size()-1;i++) {
			if(allVar.get(i).pos > allVar.get(i+1).pos) {
				temp=varToAdd.remove(i+1);
				allVar.add(i, temp);
				if(i>0)
					i-=2;
				else
					i-=1;
				
			}
		}
		allVar.add(0, comonVar);

		
		//create new vdd blanc		
		ArrayList<Integer> saveOrder=fakeOrder(allVar);
		
		UniqueHashTable uht=new UniqueHashTable(allVar.size());
		VDD newVdd=new VDD(allVar, uht, true);
		
		newVdd.merge(this);
		newVdd.merge(X);
		
		
		
		
		goBackToRightOrder(allVar, saveOrder);
	}
	
	//used by mergeWithMissingVars
	public ArrayList<Integer> fakeOrder(ArrayList<Var> vars){
		ArrayList<Integer> saveOrder=new ArrayList<Integer>();
		for(int i=0; i<vars.size(); i++) {
				saveOrder.add(vars.get(i).pos);
				vars.get(i).pos=i;
		}
		return saveOrder;
	}
	public void goBackToRightOrder(ArrayList<Var> vars, ArrayList<Integer> order){
		for(int i=0; i<vars.size(); i++) {
			vars.get(i).pos=order.get(i);
		}
	}
	
	
	public boolean modelChecking_r(NodeDD node){
		boolean res=false;
		//comone variable
		if(node.variable==node.adresse.variable) {
			NodeDD nodeX=node.adresse;
			for(int i=0; i<node.kids.size(); i++) {
				if(node.kids.get(i).actif && node.kids.get(i).bottom==0 && nodeX.kids.get(i).bottom==0) {
					//alreasdy been here?
					if(node.kids.get(i).fils.adresse!=nodeX.kids.get(i).fils) {
						//end
						if(nodeX.kids.get(i).fils.isLeaf())
							return true;
						
						node.kids.get(i).fils.adresse=nodeX.kids.get(i).fils;
						if(modelChecking_r(node.kids.get(i).fils))
							return true;
					}
				}
			}
		}else {
			for(int i=0; i<node.kids.size(); i++) {
				if(node.kids.get(i).actif && node.kids.get(i).bottom==0) {
					//alreasdy been here?
					if(node.kids.get(i).fils.adresse!=node.adresse) {
						//end
						node.kids.get(i).fils.adresse=node.adresse;
						if(modelChecking_r(node.kids.get(i).fils))
							return true;
					}
				}
			}
		}
		
		return false;
		
	}
	
	public boolean modelChecking_init(VDD X) {
		ArrayList<NodeDD> nodes;

		if(X.variables.size()==0) {
			if(X.first.bottom==0)
				return true;
			else
				return false;
		}
		
		NodeDD startNode=X.first.fils;
		Var startVar=X.variables.get(0);
		Var endVar=X.variables.get(X.variables.size()-1);
		
		nodes=uht.get(startVar.pos);
		
		for(NodeDD node:nodes) {
			node.adresse=startNode;
			
			if(modelChecking_r(node))
				return true;
		}
		return false;
		
	}
	
	public boolean modelChecking_init(VDD X, int valeur) {
		ArrayList<NodeDD> nodes;

		if(X.variables.size()==0) {
			if(X.first.bottom==0)
				return true;
			else
				return false;
		}
		
		if(X.first.fils.kids.get(valeur).bottom>=1 || !X.first.fils.kids.get(valeur).actif)
			return false;
		
		
		NodeDD startNode=X.first.fils.kids.get(valeur).fils;
		Var startVar=startNode.variable;
		Var endVar=X.variables.get(X.variables.size()-1);
		
		if(startNode.isLeaf())
			return true;
		
		nodes=uht.get(startVar.pos);
		
		for(NodeDD node:nodes) {
			node.adresse=startNode;
			
			if(modelChecking_r(node))
				return true;
		}
		return false;
		
	}
	
	public void setWeight(int weight) {
		Arc a;
		NodeDD n;
		
		a=first;
		a.s.operation(new Sp(weight));
		n=a.fils;

		while(!n.isLeaf()) {
			for(int i=0; i<n.kids.size(); i++) {
				if(n.variable.consVal[i])
					a=n.kids.get(i);
			}
			a.s.operation(new Sp(weight));
			n=a.fils;
		}

		
	}

}
