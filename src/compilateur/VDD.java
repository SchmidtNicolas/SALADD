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
    //operation de deux VDD
/*    public VDD(VDD a, VDD b, UniqueHashTable u){
		boolean plus=true;
    	uht=u;
		variables=a.variables;
		last=new NodeDDlast();
		Structure s;
		
		NodeDD tete=new NodeDD(variables.get(0));
		first=new Arc(tete, plus);						//todo
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
			uht.add(precedant);	
			precedant=suivant;
		}
		
		
		for(int j=0; j<variables.get(variables.size()-1).domain; j++){		//on ajoute les feuilles au dernier
			if(plus)
				s=new Sp();
			else
				s=new St();
			new Arc(precedant, last, j, s);
		}
		uht.add(precedant);
		uht.add(last);
    	
    	this.first.s.initOperation(a.first.s, b.first.s);
    	this.first.fils.copie.add(a.first.fils);
    	this.first.fils.copie.add(b.first.fils);				//pas besoin de suppr ce noeud pour tout ca
    	
    	
    	this.add(a, b, 1);
    	
    	uht.copieToNull();
    	uht.normaliser();   	
 	}*/
    
    
    
    
//methodes
 

    
    //risque d'explosion !!!!
    //recursif descend les valeurs sur les feuilles finales
    ////////////obsolete !!!!!//////////////////
/*    public void combDown(NodeDD curr){			//peigne vers le bas

    	curr.cpt=0;												//neud utile, on le garde
    	
    	for(int i=0; i<curr.kids.size(); i++){
    		if ((curr.value + curr.kids.get(i).getVal())>0){		//si une valeure a descendre 
    			NodeDD nouveau = new NodeDD(curr.kids.get(i).fils, curr.kids.get(i), (curr.value + curr.kids.get(i).getVal()) );	// on en cree un nouveau, avec la nouvelle valeure en plus. le nouveau remplace l'ancien dans l'arc
    			
    			//test si deja existant
    			int existeDeja=ut.recherche(nouveau);
    			if(existeDeja!=-1){			//alors il exite deja
    				nouveau=ut.get(nouveau.variable, existeDeja);
    				curr.kids.get(i).changerFils(nouveau);
    			}else{
    				ut.add(nouveau);
    			}
    		}											
    		
			curr.kids.get(i).setVal(0);					//plus de valeur sur l'arc
			
			//if (!curr.kids.get(i).fils.isLeaf()){
			//	descendreValeurs(curr.kids.get(i).fils);		// la suite !
			}
    	for(int i=0; i<curr.kids.size(); i++){
    		if (!curr.kids.get(i).fils.isLeaf()){
    			combDown(curr.kids.get(i).fils);
    		}else{
    			curr.kids.get(i).fils.cpt=0;
    		}
    	}
    		
    	curr.value=0;
    	
    }*/
    
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
    
    

/*    public void addToSlddMult(){
    	flagPlus=false;
    	flagMult=true;
    	
    	//remonter les valeurs finales sur les arcs
    	for(int i=0; i<ut.get(0).size(); i++){
    		for(int j=0; j<ut.get(0, i).fathers.size(); j++){
    			ut.get(0, i).fathers.get(j).coef=ut.get(0, i).value;
    		}
			ut.get(0, i).value=1;
    	}
    	
    	comb();
    }
    */
    //recursif introduit des coefficients multiplicateurs. valeurs normalisés
/*    public void recurSlddToAadd(NodeDD curr){
    	curr.cpt=1;
    	double max=-1000000;
    	double min= 1000000;
    	double range=0;
    	
    	if(!curr.isLeaf()){
			
			for(int i=0; i<curr.kids.size(); i++){
				if(curr.kids.get(i).fils.cpt==0)
					recurSlddToAadd(curr.kids.get(i).fils);
			}

			for(int i=0; i<curr.kids.size(); i++){
				if (curr.kids.get(i).bottom==0 &&
					curr.kids.get(i).getVal()+curr.kids.get(i).getCoef()>max)  {
						max=curr.kids.get(i).getVal() + curr.kids.get(i).getCoef();		// max prend la plus grande somme v/d,n/d
				}
				
				if (curr.kids.get(i).bottom==0 &&
					curr.kids.get(i).getVal()<min ){
							min=curr.kids.get(i).getVal();
					   }
			}
			
			if(max!=-1000000 && min!=1000000){
				range=max-min;
				
				for(int i=0; i<curr.kids.size(); i++){
					curr.kids.get(i).addVal(-min);				//on soustrait le min a tous
					curr.kids.get(i).divVal(range);		//on divise les deux par range
					curr.kids.get(i).divCoef(range);
				}
				
				min*=range;		//on va tout additionner par (min*range), donc la c'est fait !!
				for(int i=0; i<curr.fathers.size(); i++){
					curr.fathers.get(i).addVal(min);			//on soustrait le min a tous
					curr.fathers.get(i).multCoef(range);
				}
				
				
			}else{
				System.out.println("err@VDD : neud dont tous les fils valent 0");
			}

    	}
    	
    }*/
    
    //reduction (ne supprime pas les neuds innutiles
/*    public void contract(){
    	//toremove
    	//ut.cptTo(0);
    	
    	//on on arrondi si on est en ADD
    	if(!flagMult && !flagPlus){
    		if(testplus_testtime()){	//si on est en mode +
    			ut.addToInt();    			
    		}
    	}
    	
        ArrayList<NodeDD> liste = new ArrayList<NodeDD>();
        NodeDD curr, comp;
        //init
        for(int i=0; i<ut.size(0); i++)
        	liste.add(ut.get(0, i));


        //debut
        for(int var=ut.nbVariables; var>0; var--){

        	for(int i=0; i<liste.size(); i++){
                curr=liste.get(i);
                for(int j=i+1; j<liste.size(); j++){    //test si similaires
                    comp=liste.get(j);
     
                    if(curr.compare(comp)){     //si equivalents
                        curr.fusion(comp);
                        ut.remove(comp);	//on supprime de tabNodes
                        liste.remove(j);									//puis de liste (optionel?)
                        j--;                   //sinon on sautera le test du suivant :(
                    }
                    
                }
            }
            
            //mise a jours de la liste
            liste.clear();
            for(int i=0; i<ut.size(var); i++)
            	liste.add(ut.get(var, i));
        }
        //rechercheNoeudInutile();    //bien fait cette bande de PARASITES !!!!

        //on suprime tous les neuds inutiles
        ut.supprNeudNegatifs();
        ut.cptTo(0);
    }*/
    
/*    //reduction (ne supprime pas les neuds innutiles
    //(2) s'assure qu'il n'y a pas une copie qui pointe vers lui
    public void contract2(int start){
    	//toremove
    	ut.cptTo(0);
    	
    	//on on arrondi si on est en ADD
    	if(!flagMult && !flagPlus){
    		if(testplus_testtime()){	//si on est en mode +
    			ut.addToInt();    			
    		}
    	}
    	
        ArrayList<NodeDD> liste = new ArrayList<NodeDD>();
        NodeDD curr, comp;
        //init
        for(int i=0; i<ut.size(0); i++)
        	liste.add(ut.get(0, i));


        //debut
        for(int var=start; var>0; var--){

        	for(int i=0; i<liste.size(); i++){
                curr=liste.get(i);
                for(int j=i+1; j<liste.size(); j++){    //test si similaires
                    comp=liste.get(j);
     
                    if(curr.compare(comp)){     //si equivalents
                        curr.fusion(comp);
                        comp.cpt=1;
                        for(int k=0; k<ut.size(var); k++){
                        	for(int l=0; l<ut.get(var, k).copie.size(); l++){
                        		if(ut.get(var, k).copie.get(l).cpt==1){
                        			ut.get(var, k).copie.set(l, curr);
                        			System.out.println("plop");
                        		}
                        	}
                        }
                        ut.cptTo(0);
                        ut.remove(comp);	//on supprime de tabNodes
                        liste.remove(j);									//puis de liste (optionel?)
                        j--;                   //sinon on sautera le test du suivant :(
                    }
                    
                }
            }
            
            //mise a jours de la liste
            liste.clear();
            for(int i=0; i<ut.size(var); i++)
            	liste.add(ut.get(var, i));
        }
        //rechercheNoeudInutile();    //bien fait cette bande de PARASITES !!!!

        //on suprime tous les neuds inutiles
        ut.supprNeudNegatifs();
        //ut.cptTo(0);
    }*/
    
/*    public void ajoutDefaultCost(double[] constraint, double defaultCost, boolean softConstraint){
    	// get last variable
    	int lastvariable=0;
    	if( !softConstraint ||
    		(( defaultCost!=0 && !flagOperateurPrincipalMultiplication) ||
    	   // ( defaultCost!=1 && flagOperateurPrincipalMultiplication) ) ){
    		( false ) ) ){
    		for(int i=(constraint.length-1); i>0; i--){
    			if(constraint[i]!=-1){
    				lastvariable=i;
    				break;
    			}
    		}
    	
	    	ArrayList<NodeDD> liste;
	    	liste=uht.get(lastvariable);
	    	
	    	for(int i=0; i<liste.size(); i++){
	    		for(int j=0; j<liste.get(i).kids.size(); j++){
	    			if(!softConstraint)
	    				liste.get(i).kids.get(j).bottom+=1;			//on increment le bottom, si il etait deja a 1, il reviendra pas a zero
	    			else{
	    				if(!flagOperateurPrincipalMultiplication)	//cas de l'adition en operateur principale du fichier d'entree
	    					liste.get(i).kids.get(j).addVal(defaultCost);
	    				else{										//cas de la multiplication...
	    					liste.get(i).kids.get(j).multCoef(defaultCost);
	    				}
	    			}
	    		}
	    	}
    	
    	}
    }*/
    
    
    //recursif (voir l'autre fonction du meme nom)
    public void valeurCheminRecursif(Arc arc, VarPoidsId data, boolean softConstraint, boolean conflictsConstraint, Structure defaultCost){    	  	
    	boolean end=true;
    	
    	boolean dejavu=false;
    	NodeDD temp=arc.fils;

    	if(!arc.fils.isLeaf()){
    		//for(int i=arc.fils.variable.pos+1; i<var.get(0).length; i++){
	    	for(int i=arc.fils.variable.pos; i<data.triplet.get(0).var.length; i++){
	    		if(data.triplet.get(0).var[i]!=-1){
	    			end=false;
	    			break;
	    		}
	    	}
    	}
    	
    	if(data.triplet.size()==0){
    		System.out.println("inutile");
    	}
    	
    	//on detecte si cette séquences est déjà passé par là
    	if(!end){
    		if(temp.cpt!=1){	
    			
		    	for(int i=0; i<temp.copie.size(); i++){
		    		if (temp.indcopie.get(i)==data.triplet.get(0).id){		//si le 0 y est, tous les autres doivent suivre  &&  sauf si il a ete supprime
		    			if(temp.copie.get(i).cpt!=-1){
		    				arc.changerFils(temp.copie.get(i));
		    				arc.operationValuerARemonter(temp.copie.get(i));
		    			}else{
		    				if(temp.copie.get(i).adresse!=null){
		    					arc.changerFils(temp.copie.get(i).adresse); 
		    					arc.operationValuerARemonter(temp.copie.get(i));
		    				}else
		    					//on pointe vers un truc a bottom en fait
		    					arc.bottom=1;
		    			}
		    			if(temp.fathers.size()==0){
		        			temp.cpt=-1;
		    			}
		    			dejavu=true;
		    			//arc.operationValuerARemonter();
		    			break;
		    		}
		    	}
	    	}else{
	    		dejavu=true;
	    	}
    	}
    	
    	if(!dejavu){
       		//on selectionne pour la suite
	        		
	        if(!end){            //si pas fini
	        	boolean condition;
	            if(arc.pere!=null)
	            condition=data.triplet.get(0).var[arc.pere.variable.pos]==-1;
	            else
	            condition=true;
	                       
	            if(arc.fils.fathers.size()>1 &&
	            		!(arc.fils.isMonoPere() &&  condition)){    //dans ces cas on cree un nouveau sommet	

	        		// Création de noeud
	        		NodeDD nouv=new NodeDD(arc.fils, arc);
	        		nouv.cpt=1;
	        		temp.copie.add(nouv);
	        		temp.indcopie.add(data.triplet.get(0).id);
	        	}else{					//on en cree pas
					arc.fils.cpt=1;
	        		uht.removeFromTable(arc.fils);							//on l'enleve le temps des changements
	        		temp.copie.add(temp);
	        		temp.indcopie.add(data.triplet.get(0).id);
	        	}
    	
        		if(data.triplet.get(0).var[temp.variable.pos]!=-1){						//cas ou la variable est instenciee
        	    	VarPoidsId nextData = memorymanager.getObject();

            		for(int i=0; i<temp.kids.size(); i++){
            			if(arc.fils.kids.get(i).bottom==0){		//on verifie que le fils n'est pas une feuille
            				nextData.clear();
            				for(int j=0; j<data.triplet.size(); j++){
            					if(data.triplet.get(j).var[temp.variable.pos]==i){		//on garde ceux qu'on va mettre ensemble
            						nextData.triplet.add(data.triplet.get(j));
//           						var.remove(j);
//            						id.remove(j);
//            						j--;
            					}
            				}
            				//ici on developpe ce groupe la
            				if (!nextData.triplet.isEmpty()){
            					valeurCheminRecursif(arc.fils.kids.get(i), nextData, softConstraint, conflictsConstraint, defaultCost);
            				}else{
            			    	if( !softConstraint && !conflictsConstraint){
            			    		arc.fils.kids.get(i).bottom++;
            			    	}else{
            			    		if(softConstraint && !defaultCost.isNeutre() && !flagPlus){// ||
            			    			arc.fils.kids.get(i).operationS(defaultCost);
            			    		}
            					}
            				}

            			}
            		}
                	
                	memorymanager.destroyObject(nextData);
    			}else{
    				VarPoidsId nextData = memorymanager.getObject();
//    				ArrayList<NodeDD> aajouter=new
    				for(int i=0; i<arc.fils.kids.size(); i++){
        				if(arc.fils.kids.get(i).bottom==0){		//on verifie que le fils n'est pas une feuille
        					//copie de var->varnext
        					if(i == arc.fils.kids.size()-1)
            					valeurCheminRecursif(arc.fils.kids.get(i), data, softConstraint, conflictsConstraint, defaultCost);
        					else
        					{
        						nextData.clear();
	        					nextData.triplet.addAll(data.triplet);
	        					valeurCheminRecursif(arc.fils.kids.get(i), nextData, softConstraint, conflictsConstraint, defaultCost);
        					}
        				}
    				}
    		    	
    		    	memorymanager.destroyObject(nextData);
    			}
    		}else{				//si fini
    	    	
        		if(softConstraint){		 			//contrainte valuee
        			for(int i=0; i<data.triplet.size(); i++){
 //       				if(poid.get(i).isabsorbant())
 //       					System.out.println("botom");
        				
        				arc.operationS(data.triplet.get(i).poid);
        				//if(i>=1)
        				//	System.out.println("@VDD : coucou");
        			}

        		//	for(int j=0; j<var.size(); j++)
        		//			arc.fils.kids.get((int)var.get(j)[arc.fils.variable.pos]).s.operationSLDD(var.get(j)[0]);		//var[0]==poid
        		}
        		if(conflictsConstraint){
        			arc.bottom++;
        		}
        		
         	}
        	
        	if(!arc.fils.isLeaf())
        		uht.ajoutNormaliseReduitPropage(arc.fils);

        }
    }
    
    //permet de rentrer un poid specifique a un chemin
    // [ poid0, contrainte0..]
    // [ poid1, contrainte1..]
    public void valeurChemin(int[][] var, Structure[] poids, Structure defaultCost, boolean softConstraint, boolean conflictsConstraint){
    //cout par defaut//
    	
    	int firstC=-1;
    	for(int i=1; i<var[0].length; i++){
    		if(var[0][i]!=-1){
    			firstC=i;
    			break;
    		}
    	}
    	
		VarPoidsId data = memorymanager.getObject();
    	
    	//transpho des donnes en arraylist
    	for(int i=0; i<var.length; i++){
    		data.triplet.add(new VarPoidIdElement(i, var[i], poids[i]));
    	}
 
		
    	if(data.triplet.size()>0){
    		//sauvegarde des departs (on ne peut pas lire une hashtable qu'on modifie)
    		//NodeDD[] tableNode=new NodeDD[uht.size(firstC)];
    		ArrayList<NodeDD> tableNode;
    		tableNode=uht.get(firstC);
   

    		
    		//on supprime les fathers pour pouvoir modifier les fils tranquil
    		//NodeDD[] fathers;
    		ArrayList<NodeDD> fathers;
    		//if(firstC!=1){
    		//	fathers=new NodeDD[uht.size(firstC-1)];
    		//}else{
    		//	fathers=new NodeDD[0];
    		//}
    		if(firstC!=1){
    			fathers=uht.get(firstC-1);
    		}else{
    			fathers=new ArrayList<NodeDD>();
    		}
 
    		for(int i=0; i<fathers.size(); i++){
    			uht.removeFromTable(fathers.get(i));
    		}

    		//opp
    			//on doit creer une liste de depart car les noeuds sont suceptibles de change de place dans le parcours de la hashtable
    		ArrayList<Arc> arcsDepart=new ArrayList<Arc>();
    		for(int i=0; i<tableNode.size(); i++){
    			for(int j=0; j<tableNode.get(i).fathers.size(); j++){
    				arcsDepart.add(tableNode.get(i).fathers.get(j));
    			}
    		}
    		
    		for(int i=0; i<arcsDepart.size(); i++){
    			if(arcsDepart.get(i).fils!=null)  //sans ca ca bug des fois. (small.xml h=-1 hcon=-1)
    				valeurCheminRecursif(arcsDepart.get(i), data, softConstraint, conflictsConstraint, defaultCost);		//on prend un arc au pif de tous les neuds de v1 de la contrainte
    		}
    		
    		//on remet les peres
    		for(int i=0; i<fathers.size(); i++){
    			uht.ajoutNormaliseReduitPropage(fathers.get(i));
    		}
    		
    		//for(int i=0; i<fathers.size(); i++){
    		//	uht.ajoutNormaliseReduit(fathers.get(i));
    		//}
    		//if(fathers.size()>0)
    		//uht.normaliser(fathers.get(0).variable.pos);
    	}
    	
    	
    	//on retablis les noeuds finaux non parcourus
/*    	if(flagDefaultCost){
    		for(int i=0; i<liste.size(); i++){        		
    			if(liste.get(i).cpt!=1){
    				uht.ajoutNormaliseReduitPropage(liste.get(i));
    			}
    		}
    	}*/
    	
    	uht.supprNeudNegatifs();
    	uht.copieToNull();			//+ a remonter to null
    	uht.cptTo(0);

		memorymanager.destroyObject(data);
		
    }

//operateurs
    
    //operation addition, fction recursive
/*	public void add(NodeDD curr, NodeDD n, Arc a){
		if(curr.isLeaf()){							//cas feuille
			a.changerFils(n);
			a.addVal(curr.value);
			
			if(curr.fathers.size()==0){		//neud orphelin, a supprimer
				curr.cpt=-1;				
			}
		}else{
			if(curr.cpt!=0){			//arc non deja vu!
										//cas pas feuille
				if(curr.cpt==curr.fathers.size() || curr.fathers.size()==0){	//pas de souci (cas normal) || premier de liste		//a supprimer des que arc premier ajoute
					curr.cpt=0;
					for(int i=0; i<curr.kids.size(); i++)
						this.add(curr.kids.get(i).fils, n, curr.kids.get(i));		//on continue sur chaque fils
					
				}else{								//cas pas normal
					if(curr.copie==null){			//mais on y est pas encor passe
						NodeDD x=new NodeDD(curr, a, 0);		//copie conforme
						ut.add(x);
						curr.copie=x;								//on part pas sans laisser d'adresse
						
						a.changerFils(curr.copie);
						curr.cpt--;
						
						for(int i=0; i<x.kids.size(); i++)
							this.add(x.kids.get(i).fils, n, x.kids.get(i));		//on continue sur chaque fils de x
					}else{
						a.changerFils(curr.copie);
						curr.cpt--;
					}
					
				}
			}
		}
	}
    */
	
	//compte le nombre de passage dans chaques neud
	//resultat dans les cpt
    public long counting(){
    	long res=0;
    	if(first.actif && first.bottom==0){
    		first.fils.counting=1;
    	}
    	
    	for(int i=0; i<uht.get(0).size(); i++){
    		res+=counting(uht.get(0).get(i));
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
	
	//prend en compte la ponderation
	//cas de l'historique. (SLDD additif uniquement)
    public int countingpondere(){
    	int res=0;
    	if(first.actif && first.bottom==0){
    		first.fils.counting=1;
    		first.fils.pondere=(int)first.s.getvaldouble();
    	}
    	
    	for(int i=0; i<uht.get(0).size(); i++){
    		res+=countingpondere(uht.get(0).get(i));
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
    	
    	for(int i=0; i<uht.get(0).size(); i++){
    		res+=countingpondere(uht.get(0).get(i));
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
    	
    	for(int i=0; i<uht.get(0).size(); i++){
    		res+=countingpondere(uht.get(0).get(i));
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
    	
    	for(int i=0; i<uht.get(0).size(); i++){
    		total+=countingpondere(uht.get(0).get(i));
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
    	
    	for(int i=0; i<uht.get(0).size(); i++){
    		total+=countingpondere(uht.get(0).get(i));
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
    	
    	for(int i=0; i<uht.get(0).size(); i++){
    		double inference = inference(uht.get(0).get(i));
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
    	
    	for(int i=0; i<uht.get(0).size(); i++){
    		res+=inference(uht.get(0).get(i));
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
    	
    	for(int i=0; i<uht.get(0).size(); i++){
    		res+=inference(uht.get(0).get(i));
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

    	
    	for(int i=0; i<uht.get(0).size(); i++){
    		inference(uht.get(0).get(i));
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

    	
    	for(int i=0; i<uht.get(0).size(); i++){
    		inference(uht.get(0).get(i));
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
		for(int i=0; i<uht.size(var); i++){
			savelist=uht.get(var);
		}
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
		for(int j=1; j<=variables.size(); j++){
			uht.deconditioner(j);
		}
	}
	
	public void reinitializeInState(Map<String, String> state) {
		for(int j=1; j<=variables.size(); j++){
			Var v=variables.get(j-1);
			String name=v.name;
			if(state.containsKey(name))
				uht.conditioner(j, v.conv(state.get(name)));
			else
				uht.deconditioner(j);
		}
	}

	/*
	//ameliorable
	public double mostchoosen(int var){
		
		
		double max=-1;
		int indmax=-1;
		double val;
		for(int i=0; i<variables.get(var-1).domain; i++){
			this.deconditioner(var);
			this.conditioner(var, i);
			
			//val=this.countingpondere();
			val=this.countingpondere();
			if(val>max){
				max=val;
				indmax=i;
			}
			System.out.println("i:" + i + "  val:" + val);
		}
		deconditioner(var);
		
		
		return 0;
	}*/
	
	public void minMaxConsistance(){
		for(int i=0; i<variables.size(); i++)
			variables.get(i).consValTofalse();
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
		
		min=uht.get(0).get(0).min.copie();
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
			variables.get(var-1).consValTofalse();
		
		min.rendreInaccessible();
		max.rendreInaccessible();
		
		uht.minMaxConsistanceMaj(var, cd);
				
		//GIC();
		//for(int i=var; i<=variables.size(); i++)
		//	uht.GIC(i);
		
//		end=System.currentTimeMillis();
//		System.out.println("------> :  " + (end-start) + "ms");
		
		min=uht.get(0).get(0).min.copie();
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
		min=uht.get(0).get(0).min.copie();
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
		
		n=uht.get(0).get(0);
		
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
		int[] minDom=new int[this.variables.get(var-1).domain];
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
				m.put(variables.get(var-1).valeurs.get(i), minDom[i]);
			//else
				//m.put(variables.get(var-1).valeurs.get(i), -1);
		}
//		for(int i=0; i<minDom.length; i++){
//			System.out.print(maxDom[i]+" ");
//		}
		
		uht.minMaxConsistance();

		return m;
	} 
	
	public Map<String, Integer> maxCosts(int var){
		Map<String, Integer> m=new HashMap<String, Integer>();
		ArrayList<NodeDD> liste;
		uht.maxDomainVariable(var);
		liste=uht.get(var);
		int[] maxDom=new int[this.variables.get(var-1).domain];
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
				m.put(variables.get(var-1).valeurs.get(i), maxDom[i]);
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
				uht.GIC(i+1);
			}
	}
	
	//pour un dcd
	public void GICdown(){
		for(int i=0; i<variables.size(); i++)
			if(!variables.get(i).consistenceFull()){
				variables.get(i).consValTofalse();
				uht.GIC(i+1);
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
    	
    	this.add(a, b, listP, 1);
    	    	
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
    
	//fusion de deux VDD dont la premiere variable est concatennée
/*	public void fusion (VDD vdd2){
		for(int i=0; i<first.fils.variable.domain; i++){
			if(first.fils.kids.get(i).bottom>0)	{					//si il a été cuté
				if(vdd2.first.fils.kids.get(i).bottom==0){			//mais pas sur vdd2
					vdd2.first.fils.kids.get(i).changerPere(this.first.fils.kids.get(i));
				}
			}
		}
	}*/
	
	//test si notre ADD doit plutot etre transphorme en SLDD+ ou SLDD*
	//true -> SLDD+
	//false -> SLDD*
/*	public boolean testplus_testtime(){
		double moy=0;
		int size=ut.get(0).size();
		for(int i=0; i<size; i++){
			moy+=(ut.get(0).get(i).value/size);
		}
		if(moy>1)
			return true;
		else
			return false;
	}*/
	
//accesseurs
    
	public UniqueHashTable getUHT(){
		return uht;
	}

//afficheurs
	/*
    void plot(LddNode*, int);       // affichage de l'arbre depuis le neud (recursif)
    void plot();                    // affichage de l'arbre depuis la racine

    void plotVector();              // affichage du detail de l'ensemble des neuds

    void addToDot(string); // affichage en dot (passage par fichier)
    void slddToDot(string);
    */
    
/*    public String toString(){
    	ut.giveIndex();
    	
    	String s="";	
	 
	    	//nodes
    	for(int i=0; i<ut.nbVariables+1; i++)
	    	for(int j=0; j<ut.size(i); j++){
	    		//name label form
	    		s+=ut.get(i, j).toString();		//true non definitif (binary only)
	    	}
	    	
	    	return s;
    }*/
    
    public void toDot(String nameGraph, boolean afficheGraph){
    	
    	FileWriter fW;
//    	File f;
    	
    	String s;

		//fichier
    	if(nameGraph.endsWith(".dot"))
    		nameGraph=nameGraph.substring(0, nameGraph.length()-4);
    	
		String name_file= "./" + nameGraph + ".dot";
		String name_pdf= "./" + nameGraph + ".pdf";
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
    		for(int i=0; i<uht.nbVariables+1; i++){
    			l=uht.get(i);
		    	for(int j=0; j<l.size(); j++){
		    		//name label form
		    		s=l.get(j).toDot();		//true non definitif (binary only)
		    		fW.write(s);
		    	}
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
    
    public void toDotRecuIntro(String nameGraph, boolean afficheGraph){
    	
    	FileWriter fW;
//    	File f;
    	
    	String s;

		//fichier
    	if(nameGraph.endsWith(".dot"))
    		nameGraph=nameGraph.substring(0, nameGraph.length()-4);
    	
		String name_file= "./" + nameGraph + ".dot";
		String name_pdf= "./" + nameGraph + ".pdf";
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
	    	
    		s=last.toDot();
    		fW.write(s);

	    	//nodes
    		if(first.fils!=null)
    			toDotRecu(first.fils, fW);
    		
    		ArrayList<NodeDD> l;
    		l=uht.get(variables.size());
    		for(int j=0; j<l.size(); j++){
    			toDotRecu(l.get(j), fW);
    		}
    		
    		
    		this.countingtomoinsunR();
        	
    		//ArrayList<NodeDD> l;
    		//for(int i=0; i<uht.nbVariables+1; i++){
    		//	l=uht.get(i);
		  //  	for(int j=0; j<l.size(); j++){
		  //  		//name label form
		  //  		s=l.get(j).toDot();		//true non definitif (binary only)
		   // 		fW.write(s);
		   // 	}
	    	//}
    	
    		
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
    	uht.cptTo(0);

		if(afficheGraph){
			try {	//creation pdf
				Runtime.getRuntime().exec("/usr/bin/dot dot -Tpdf " + name_file + " -o " + name_pdf);
			} catch (java.io.IOException exc) {System.out.println("pb de creation pdf: " + exc); }
	
			try {	//ouverture pdf
				Runtime.getRuntime().exec("/usr/bin/evince evince " + name_pdf);
			} catch (java.io.IOException exc) {System.out.println("pb d'ouverture pdf: " + exc); }
		}
    }
    
    public void toDotRecu(NodeDD n, FileWriter fW){
    	String s;
    	if(n.counting!=500){
        	
	    	NodeDD n2;
	    	
	    	for(int i=0; i<n.kids.size(); i++){
	    		n2=n.kids.get(i).fils;
	    		//System.out.println(n2.id+" "+n2.kids.size());
	    		if(n2!=null){
	    			if(!n2.isLeaf()){
	    				toDotRecu(n2, fW);
	    			}
	    		}
	    	}
	    	s=n.toDot();
	    	
	    	n.counting=500;
	    	try {
				fW.write(s);
			} catch (Exception e) {
				System.out.println("aie");// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

    }
    
    public void toDotRecu2(NodeDD n, FileWriter fW){
    	String s;
    	if(n.counting!=500){
        	
	    	NodeDD n2;
	    	
	    	for(int i=0; i<n.fathers.size(); i++){
	    		n2=n.fathers.get(i).pere;
	    		if(n2!=null){
	    			toDotRecu2(n2, fW);
	    		}
	    	}
	    	s=n.toDot();
	    	n.counting=500;
	    	try {
				fW.write(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			
			for(int i=1; i<this.variables.size()+1; i++){
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
    	
    	
    	for(int i=2; i<=variables.size(); i++){
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


    
}
