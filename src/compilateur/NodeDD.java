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

public class NodeDD{
	
	// Attributs

	protected int id;
	protected Var variable;				//var
	protected ArrayList<Arc> kids;		//kids.fils && kids.value && kids.mult	
	protected ArrayList<Arc> fathers;
	
	protected ArrayList<NodeDD> copie;
	protected ArrayList<Integer> indcopie;
	//protected ArrayList<Structure> structcopie;
	protected NodeDD adresse;
	
	protected int cpt;
	protected long counting=-1;
	protected int pondere=0;
	protected double inference=0;

	protected double memoir;
	protected Structure memoire;
	
	
//	private static final double PRECISION=0.00000001;
	
	protected int hashdur;
	protected Structure aRemonter;			// si on rebranche par la sauvegarde alors qu'il y a une valeur qui a deja ete remonte, on la rate!
	protected Structure min=null;
	protected Structure max=null;
	protected int posMin=-1;
	protected int posMax=-1;	//position des min et max
	
	static int idcpt=0;
		// constructeur
	
	public NodeDD(Var var){
		
		kids=new ArrayList<Arc>();
		fathers=new ArrayList<Arc>();
		
	    variable=var;
	    
	    cpt=0;
	    
	    copie=new ArrayList<NodeDD>();
	    indcopie=new ArrayList<Integer>();
	    
	    id=idcpt;
	    idcpt++;
	    	    
	    aRemonter=null;
	    adresse=null;
	}
	
	public NodeDD(Var var, int ident){
		
		kids=new ArrayList<Arc>();
		fathers=new ArrayList<Arc>();
		
	    variable=var;
	    
	    cpt=0;
	    
	    copie=new ArrayList<NodeDD>();
	    indcopie=new ArrayList<Integer>();
	    
	    id=ident;
	    	    
	    aRemonter=null;
	    adresse=null;
	}
	

	//copie d'un neud existant (le nouveau est issue de l'arc a)
	public NodeDD(NodeDD clone, Arc a){
		
		this.kids=new ArrayList<Arc>();
		this.fathers=new ArrayList<Arc>();
		
	    this.variable=clone.variable;
	    this.cpt=0;
		//arc pere
		a.changerFils(this);
		
		//arcs fils
		for(int i=0; i<clone.kids.size(); i++){
			clone.kids.get(i).copieNouveauPere(this);
		}
		
	    copie=new ArrayList<NodeDD>();
	    indcopie=new ArrayList<Integer>();

	    id=idcpt;
	    idcpt++;
	    
	    aRemonter=null;
	    adresse=null;
	 }
			
	//methodes
	
	//ajouter une relation pere fils, a la position pos du pere
/*	public void ajouterFils(NodeDD fils, int pos){
		new Arc(this, fils, pos);
	}*/
	
	//remplacer une relation pere fils, a la position pos du pere
/*	public void remplacerFils(NodeDD fils, int pos){
		ajouterFils(fils, pos);
	}*/
			//comparaison de 2 neuds (variable, valeure, valeurs des arcs, fils)
/*	public boolean compare(NodeDD comp){
				    
	    if(this.variable==comp.variable &&			//si ce sont les meme variable de meme valeure et qu'ils ont le meme nombre de fils (a priori c'est logique)
	    	(this.bottom==0) == (comp.bottom==0) &&					// les deux valent zero ou aucun des deux
	    	(this.bottom!=0 || this.value==comp.value)){			// si bottom!=0 et false, l'autre doit etre vrai
	    
   			    	for(int i=0; i<this.kids.size(); i++){
	    		diff2=this.kids.get(i).val - comp.kids.get(i).val;
	    		if(this.kids.get(i).val!= 0)
	    			diff2=diff2/this.kids.get(i).val;
//	    		else
//	    			diff2=0;
	    		diff3=this.kids.get(i).coef - comp.kids.get(i).coef;
	    		if (this.kids.get(i).coef!= 0)
	    			diff3=diff3/this.kids.get(i).coef;
//	    		else
//	    			diff3=0;
	    		
	    		if ((this.kids.get(i).bottom==0) != (comp.kids.get(i).bottom==0) ||
	    			 this.kids.get(i).fils != comp.kids.get(i).fils ||
	    			 ( this.kids.get(i).bottom==0 && (Math.abs(diff2)>PRECISION || Math.abs(diff3)>PRECISION))){
	    				
	    				break;				//les fils ne sont pas les memes
	    		}
	    		if(i==(this.kids.size()-1)){
	    			//System.out.println("hoy");
	    			if(this.hashCode()!=comp.hashCode())
	    				System.out.println("coin !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	    			return true;
	    		}
	    	}
	    }
		if(this.hashCode()==comp.hashCode())
		    System.out.println(this.hashCode() + " " + comp.hashCode());
			//System.out.println("poueeeeeeeeeeettttt !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	    return false;   
	}*/
	
	//comparaison de 2 neuds (variable, valeure, valeurs des arcs, fils)
	//plus besoin maintenant qu'on est en float
	/*public boolean compare2(NodeDD comp){
		
	    if(this.value==comp.value &&				//si ce snt des feuilles de meme valeure
	       this.isLeaf() && comp.isLeaf())
	    		return true;
	    
	    
	    if(this.variable==comp.variable &&			//si ce sont les meme variable de meme valeure et q'ils ont le meme nombre de fils (a priori c'est logique)
		    	       this.kids.size() == comp.kids.size() &&
	    	       (this.bottom==0) == (comp.bottom==0)){			// les deux valent zero, ou aucun des deux
	    
	    	for(int i=0; i<this.kids.size(); i++){
	    		if(this.kids.get(i).getFracAddi().getResultat() != comp.kids.get(i).getFracAddi().getResultat() || 	//valeure des arcs
	    		   this.kids.get(i).getFracMult().getResultat() != comp.kids.get(i).getFracMult().getResultat() ||  //on verifie aussi le multiplie
	    		   (this.kids.get(i).bottom==0) != (comp.kids.get(i).bottom==0) || //les meme elements absorbants
	    		   this.kids.get(i).fils != comp.kids.get(i).fils)
	    				break;				//les fils ne sont pas les memes
	    		if(i==(this.kids.size()-1))
	    				return true;
	    	}
		    	
	    }
	    return false;   
	}*/
	
	//fusionne same et this (same=-1 => on peut le suppr)
	public void fusion(NodeDD same){
		
		if(this!=same){
			while(same.fathers.size() != 0){
		        same.fathers.get(0).changerFils(this);		//on remplace le kid
		    }
		    while(same.kids.size() != 0){					//on supprime tous les arcs sortants
		    	same.kids.get(same.kids.size()-1).remove();
		    }
		    
		    //GROS BUG : on ne peut pas copier toutes les copies, sinon on pourrait arriver dans un cas qui n'etait pas accessible a la base parthis (mais qui l'etait a la base par same)
		    //on ne garde que l'info qu'il pointe vers lui meme (donc que ce truc a deja ete traite) mais on modifie ca en : je pointe vers moi meme
		    /*for(int i=0; i<same.copie.size(); i++){
		    	if(same.copie.get(i).id==same.id){
		    		this.copie.add(this);
		    		this.indcopie.add(same.indcopie.get(i));
		    	}
		    }*/
		    
		    //v1 on differencie copie et adress, on ne garde que l'adress
		    //rien
		    
		    
		    //v2 on differencie copie et adress, on ne garde que l'adress et la copie si elle pointe sur elle meme
		    //pas teste
		    
		    
		    ////ca marche parsau'il n'y a qu'un seul noeud actif a  la fois !! on ne garde que le dernier!
		    ////non en fait ca marchait pas du tout parce que il fusione avec un noeud ou on devrait rien avoir a remonter, alors il faut qu'il passe par le bon noeud
		    //this.aRemonter=same.aRemonter;
		    
		    
		    same.copie.clear();
		    same.adresse=this;		//on laisse une adresse
		    //todo : suprimer same
		    //edit : non surtout pas il faut le suprimer des vector avant
		    same.cpt=-1;
		}
	}
	
	public void fusionSansCopie(NodeDD same){
		
		if(this!=same){
			while(same.fathers.size() != 0){
		        same.fathers.get(0).changerFils(this);		//on remplace le kid
		    }
		    while(same.kids.size() != 0){					//on supprime tous les arcs sortants
		    	same.kids.get(same.kids.size()-1).remove();
		    }
		    same.cpt=-1;
		}
	}
	
	//realise le 'ou' entre deux noeuds (normalement les premiers) mais c'est un faux ou. c'est juste que les arcs a bottom disparaissen si ils le peuvent
	public void fusionFauxOu(NodeDD n){
		for(int i=0; i<this.kids.size(); i++){
			if(this.kids.get(i).bottom==0){
				this.kids.get(i).s.operation(this.fathers.get(0).s);
			}
			if(this.kids.get(i).bottom>0 && n.kids.get(i).bottom==0){
				n.kids.get(i).s.operation(n.fathers.get(0).s);
				this.kids.get(i).bottom=0;
				this.kids.get(i).changerFils(n.kids.get(i).fils);
				this.kids.get(i).s=n.kids.get(i).s;
			}
			
		}
		n.fathers.get(0).s.toNeutre();
		this.fathers.get(0).s.toNeutre();
		while(n.kids.size()>0){
			n.kids.get(n.kids.size()-1).remove();
		}
	}
	
	
	//courcircuite le neud courrant. les pères vont direct au (seul) fils
/*	public void courtcircuit(){
		int bottom= this.kids.get(0).bottom;		//on doit tout envoyer vers bottom?
		
		if(this.kids.get(0).bottom!=0){												//oui
			for(int i=(this.fathers.size()-1); i>=0; i--){
				this.fathers.get(i).bottom=this.kids.get(0).bottom;
		    	this.fathers.get(i).changerFils(this.kids.get(0).fils);		// on courcircuit le fils
			}
		}else{														//non
			for(int i=(this.fathers.size()-1); i>=0; i--){
		    	this.fathers.get(i).changerFils(this.kids.get(0).fils);		// on courcircuit le fils
			}
		}
	    
	    for(int i=(this.kids.size()-1); i>=0; i--){
	    	this.kids.get(i).remove();	
	    }
	    
	    
	    this.cpt=-1;   //on peut le virer
	}*/
	
	//note : la procedure de normalisation passe par la uniquehashtable
	//le noeud doit avoir ete retire de la uht
	//il doit exister au moins un noeud diff de bottom
	//renvoie la structure a remonter
	public Structure normaliseInf(){
				
		ArrayList<Structure> liste=new ArrayList<Structure>();
		Structure save;
		for(int i=0; i<kids.size(); i++){
			if(kids.get(i).bottom==0){ //&& kids.get(i).actif){
				liste.add(kids.get(i).s);
			}
		}
		
		if(liste.size()>0)
			save=liste.get(0).normaliseInf(liste);
		else 
			save=null;	
		
		if(aRemonter==null){
			aRemonter=save;
		}else{
			aRemonter.operation(save);			//si on avait deja remonte quelque chose, ca s'ajoute
			//en fait on s'en fout parsque ca n'arrive que dans une zone au dessus de nos contraintes, donc on l'utilisera jamais
		}
		return save;
	} 
		
	public void conditioner(int val){
		if(val<this.variable.domain){
			for(int i=0; i<kids.size(); i++){
				if(i!=val)
					kids.get(i).activer(false);
			}
		}else{
			System.out.println("err @ nodeDD.conditioner : out of range");
		}
	}
	
	public void conditionerTrue(int val){
		if(val<this.variable.domain){
			for(int i=0; i<kids.size(); i++){
				if(i!=val){
					kids.get(i).bottom++;
				}
			}
		}else{
			System.out.println("err @ nodeDD.conditioner : out of range");
		}
	}
	
	public void conditionerExclure(int val){
		if(val<this.variable.domain){
			for(int i=0; i<kids.size(); i++){
				if(i==val)
					kids.get(i).activer(false);
			}
		}else{
			System.out.println("err @ nodeDD.conditioner : out of range");
		}
	}
	
	public void conditionerExclureTrue(int val){
		if(val<this.variable.domain){
			for(int i=0; i<kids.size(); i++){
				if(i==val)
					kids.get(i).bottom++;
			}
		}else{
			System.out.println("err @ nodeDD.conditioner : out of range");
		}
	}
	
	//annule le conditionnement d'un noeud
	public void deconditioner(){
		for(int i=0; i<kids.size(); i++){
			kids.get(i).activer(true);
		}
	}
	
/*	public void calcMin(double minPred, Structure s){
			if(min!=-1){
				if(this.min>minPred + s.getvaldouble()){
					this.min=minPred + s.getvaldouble();
				}
			}
			else{
				min=minPred + s.getvaldouble();
			}
	}
	public void calcMax(double maxSucc, Structure s){
		if(max!=-1){
			if(this.max<maxSucc + s.getvaldouble()){
				this.max=maxSucc + s.getvaldouble();
			}
		}
		else{
			max=maxSucc + s.getvaldouble();
		}
	}*/
	
	public void minMaxConsistance1(){
		//init des min et max
		if(this.fathers.size()==0)
			System.out.println("fathersize:"+this.fathers.size()+"  "+this.variable.name+":"+this.id);
		//if(min==null){
			if(this.fathers.get(0).s.printstr().compareTo("Sp")==0){
				min=new Sp();
				max=new Sp();
			}
			if(this.fathers.get(0).s.printstr().compareTo("St")==0){
				min=new St();
				max=new St();
			}
			if(this.fathers.get(0).s.printstr().compareTo("Spt")==0){
				min=new Spt();
				max=new Spt();
			}
//		}
		
		min.rendreInaccessible();
		max.rendreInaccessible();
		
		for(int i=0; i<this.fathers.size(); i++){
			if(fathers.get(i).bottom==0 && fathers.get(i).actif){
				if(fathers.get(i).pere!=null){
					if(!fathers.get(i).pere.min.inaccessible()){
						if(min.min(fathers.get(i).pere.min, fathers.get(i).s)){		//si le min a change, on note le chemin
							posMin=i;
						}
							
					}
				}else{
					if(min.inaccessible()){
						min=fathers.get(i).s.copie();
						posMin=i;
					}else{
						if(min.getvaldouble()>fathers.get(i).s.getvaldouble()){
							min=fathers.get(i).s.copie();
							posMin=i;
						}
					}
				}
			}
		}
//		if(!min.inaccessible()){
//			for(int i=0; i<this.kids.size(); i++){
//				if(kids.get(i).bottom==0 && kids.get(i).actif)
//					this.variable.consVal[i]=true;
//			}
//		}
	}
	
	public boolean minMaxConsistance1Maj(boolean CD){
		Structure savemin=min.copie();
		
		//if(!CD || (!min.inaccessible() && !max.inaccessible() )){	//pour ne pas traiter un noeud que l'on sait inaccessible
		if(!CD || !min.inaccessible()){
		
		
			min.rendreInaccessible();
			
			for(int i=0; i<this.fathers.size(); i++){
				if(fathers.get(i).bottom==0 && fathers.get(i).actif){
					if(fathers.get(i).pere!=null){
						if(!fathers.get(i).pere.min.inaccessible()){
							if(min.min(fathers.get(i).pere.min, fathers.get(i).s))
								posMin=i;
						}
					}else{
						if(min.inaccessible()){
							min=fathers.get(i).s.copie();
							posMin=i;
						}else{
							if(min.getvaldouble()>fathers.get(i).s.getvaldouble()){
								min=fathers.get(i).s.copie();
								posMin=i;
							}
						}
					}
				}
			}
			
			if(!min.inaccessible() && !max.inaccessible()){		//alors le noeud est full accessible
				for(int i=0; i<this.kids.size(); i++){
					if(kids.get(i).bottom==0 && kids.get(i).actif)
						if(!kids.get(i).fils.max.inaccessible())
						this.variable.consVal[i]=true;
				}
			}
		
		}
		
		if(min.equals(savemin)){	//ya pas eu de changement, rien a propager
			return false;
		}else{
			return true;
		}
		

	}
	
	
	//posMin pas a jours
	public ArrayList<Arc> minMaxConsistance1Majopt(){
		int savemin=(int)min.getvaldouble();
		min.rendreInaccessible();
		
		
		for(int i=0; i<this.fathers.size(); i++){
			if(fathers.get(i).bottom==0 && fathers.get(i).actif){
				if(fathers.get(i).pere!=null){
					if(!fathers.get(i).pere.min.inaccessible()){
						min.min(fathers.get(i).pere.min, fathers.get(i).s);
					}
				}else{
					if(min.inaccessible())
						min=fathers.get(i).s.copie();
					else{
						if(min.getvaldouble()>fathers.get(i).s.getvaldouble())
							min=fathers.get(i).s.copie();

					}
				}
			}
		}
		

//		if(!isLeaf()){
			if((min.getvaldouble()==-1) != (savemin==-1)){		//si changement d'accessiblite
				return kids;
			}
			if(savemin!=(int)min.getvaldouble()){
				return kids;
			}
//		}
		
		return null;

	}
	
	public void maxPartieHaute(){	
		if(!min.inaccessible() && !max.inaccessible()){

			max.rendreInaccessible();
			
			for(int i=0; i<this.fathers.size(); i++){
				if(fathers.get(i).bottom==0 && fathers.get(i).actif){
					if(fathers.get(i).pere!=null){
						if(!fathers.get(i).pere.min.inaccessible()){
							max.max(fathers.get(i).pere.max, fathers.get(i).s);
						}
					}else{
						if(max.inaccessible()){
							max=fathers.get(i).s.copie();
						}else{
							if(max.getvaldouble()<fathers.get(i).s.getvaldouble()){
								max=fathers.get(i).s.copie();
							}
						}
					}
				}
			}
		}
	}
	
	
	//bug pour le premier (puit)
	public void minMaxConsistance2(){
		
		
		for(int i=0; i<this.kids.size(); i++){
//			if(this.variable.name.compareTo("v1")==0){
	//			for(int k)
		//	}
			if(kids.get(i).actif && kids.get(i).bottom==0){
				if(!kids.get(i).fils.isLeaf()){
					if(!kids.get(i).fils.max.inaccessible()){
						if(max.max(kids.get(i).fils.max, kids.get(i).s)){
							posMax=i;;
						}
						if(!min.inaccessible()){
							if(min.printstr().compareTo("Spt")==0){		//faire le min aussi
								if(min.getvaldouble()>kids.get(i).s.getvaldouble())
									((Spt)min).minvrai(kids.get(i).fils.min, kids.get(i).s);
							}
						}
					}
				}else{

					if(max.inaccessible()){
						max=kids.get(i).s.copie();
						posMax=i;
					}else{
						if(max.getvaldouble()<kids.get(i).s.getvaldouble()){
							max=kids.get(i).s.copie();
							posMax=i;	
						}
					}
					if(!min.inaccessible()){
						if(min.printstr().compareTo("Spt")==0)		//faire le min aussi
							if(min.getvaldouble()>kids.get(i).s.getvaldouble())
								((Spt)min).minvrai(kids.get(i).s, null);
					}
				}
			}
		}
		if(!min.inaccessible() && !max.inaccessible()){		//alors le noeud est full accessible
			for(int i=0; i<this.kids.size(); i++){
				if(kids.get(i).bottom==0 && kids.get(i).actif)
					if(!kids.get(i).fils.max.inaccessible() && !kids.get(i).fils.min.inaccessible())
					this.variable.consVal[i]=true;
			}
		}
		//System.out.println("min="+min+ " max=" + max);
	}
	
	//bug pour le premier (puit)
	public boolean minMaxConsistance2Maj(boolean CD){
		Structure savemax=max.copie();
		
		//if(!CD || (!min.inaccessible() && !max.inaccessible() )){			//pour ne pas traiter un noeud que l'on sait inaccessible
		if(!CD || !max.inaccessible() ){
		
			
			
			max.rendreInaccessible();
			
			for(int i=0; i<this.kids.size(); i++){
	//			if(this.variable.name.compareTo("v1")==0){
		//			for(int k)
			//	}
				if(kids.get(i).actif && kids.get(i).bottom==0){
					if(!kids.get(i).fils.isLeaf()){
						if(!kids.get(i).fils.max.inaccessible()){
							if(max.max(kids.get(i).fils.max, kids.get(i).s)){
								posMax=i;
							}
							if(!min.inaccessible()){
								if(min.printstr().compareTo("Spt")==0){		//faire le min aussi
									if(min.getvaldouble()>kids.get(i).s.getvaldouble())
										((Spt)min).minvrai(kids.get(i).fils.min, kids.get(i).s);
								}
							}
						}
					}else{
	
						if(max.inaccessible()){
							max=kids.get(i).s.copie();
							posMax=i;
						}else{
							if(max.getvaldouble()<kids.get(i).s.getvaldouble()){
								max=kids.get(i).s.copie();
								posMax=i;
							}
						}
						if(!min.inaccessible()){
							if(min.printstr().compareTo("Spt")==0)		//faire le min aussi
								if(min.getvaldouble()>kids.get(i).s.getvaldouble())
									((Spt)min).minvrai(kids.get(i).s, null);
						}
					}
				}
			}
			if(!min.inaccessible() && !max.inaccessible()){		//alors le noeud est full accessible
				for(int i=0; i<this.kids.size(); i++){
					if(kids.get(i).bottom==0 && kids.get(i).actif)
						if(!kids.get(i).fils.max.inaccessible())
						this.variable.consVal[i]=true;
				}
			}
		
		}
		
		if(max.equals(savemax)){	//ya pas eu de changement, rien a propager
			return false;
		}else{
			return true;
		}
		
	}
	
	//bug pour le premier (puit)
	//posMax pas a jours
		public ArrayList<Arc> minMaxConsistance2Majopt(){
			int savemax=(int)max.getvaldouble();
			
			max.rendreInaccessible();
			
			for(int i=0; i<this.kids.size(); i++){
//				if(this.variable.name.compareTo("v1")==0){
		//			for(int k)
			//	}
				if(kids.get(i).actif && kids.get(i).bottom==0){
					if(!kids.get(i).fils.isLeaf()){
						if(!kids.get(i).fils.max.inaccessible()){
							max.max(kids.get(i).fils.max, kids.get(i).s);
							if(!min.inaccessible()){
								if(min.printstr().compareTo("Spt")==0){		//faire le min aussi
									if(min.getvaldouble()>kids.get(i).s.getvaldouble())
										((Spt)min).minvrai(kids.get(i).fils.min, kids.get(i).s);
								}
							}
						}
					}else{

						if(max.inaccessible()){
							max=kids.get(i).s.copie();
						}else{
							if(max.getvaldouble()<kids.get(i).s.getvaldouble())
									max=kids.get(i).s.copie();
						}
						if(!min.inaccessible()){
							if(min.printstr().compareTo("Spt")==0)		//faire le min aussi
								if(min.getvaldouble()>kids.get(i).s.getvaldouble())
									((Spt)min).minvrai(kids.get(i).s, null);
						}
					}
				}
			}
			
			if((max.getvaldouble()==-1) != (savemax==-1)){		//si changement d'accessiblite
				return fathers;
			}
			if(savemax!=(int)max.getvaldouble())
				return fathers;
			
			return null;
		}
		
		//bug pour le premier (puit)
		public void minPartieBasse(){
			if(!min.inaccessible() && !max.inaccessible()){
				min.rendreInaccessible();
				
				for(int i=0; i<this.kids.size(); i++){
					if(kids.get(i).actif && kids.get(i).bottom==0){
						if(!kids.get(i).fils.isLeaf()){
							if(!kids.get(i).fils.max.inaccessible()){
								min.min(kids.get(i).fils.min, kids.get(i).s);
							}
						}else{
	
							if(min.inaccessible()){
								min=kids.get(i).s.copie();
							}else{
								if(min.getvaldouble()>kids.get(i).s.getvaldouble()){
									min=kids.get(i).s.copie();
								}
							}
						}
					}
				}
			}
		}
	
		//vu qu'on a des condition pour la mise a jours
		public void majConsistance(){
			if(!min.inaccessible() && !max.inaccessible()){		//alors le noeud est full accessible
				for(int i=0; i<this.kids.size(); i++){
					this.variable.consVal[i]=false;
					if(kids.get(i).bottom==0 && kids.get(i).actif)
						if(!kids.get(i).fils.max.inaccessible())
							this.variable.consVal[i]=true;
					
				}
			}
		}
		
		
	//renvoie true si la variable est consistante et on peut arreter
	public boolean GIC(){
		
		for(int i=0; i<this.kids.size(); i++){
			if(kids.get(i).bottom==0 && kids.get(i).actif)
				this.variable.consVal[i]=true;
		}
		
		
		return this.variable.consistenceFull();
	}
	
//operateurs
	
	//accesseurs
	
	// returrn vrai si ce neud est une feuille  (pas de fils)
	// alt: this.kids.size()==0
	public boolean isLeaf(){
		return false;
	}		
	
	//return true si tous les arcs peres pointent vers un seul et meme pere. si on supprime ce noeud, il devient orphelin !
	public boolean isMonoPere(){
		for(int i=1; i<fathers.size(); i++){
			if(fathers.get(i).pere!=fathers.get(0).pere)
				return false;
		}
		return true;
	}
	
	public ArrayList<NodeDD> getFilsSansDoublons(){
		ArrayList<NodeDD> listeFils=new ArrayList<NodeDD>();
		for(int i=0; i<this.kids.size(); i++){
			if(!listeFils.contains(this.kids.get(i).fils))
				listeFils.add(this.kids.get(i).fils);
		}
		return listeFils;
	}
	
	//afficheurs
	
	//index x=? val | valArc var (index)  
/*	public String toString(){
		String s="";
		
	    if(this.index<10) s+=" ";
	    
	    s+=this.index+" x"+this.variable.name+"="+this.value;
	    if(!this.isLeaf()){
	        s+=" " + this.kids.get(0).getVal() +" " + this.kids.get(0).fils.variable.name + "(" + this.kids.get(0).fils.index + ")\n";
	        for(int i=1; i<this.kids.size(); i++){
	        	s+="       ";
	        	s+=" " + this.kids.get(i).getVal() +" " + this.kids.get(i).fils.variable.name + "(" + this.kids.get(i).fils.index + ")\n";
	        }
	    }
	    else{
	        s+="\n";
	    }
	    return s;
	}*/
	
/*	public String toStringDown(){
		String s="";
		
	    if(this.index<10) s+=" ";
		    
	    s+=this.index+" x"+this.variable.name+"="+this.value;
	    if(this.fathers.size()>0){
	        s+=" " + this.fathers.get(0).getVal() +" " + this.fathers.get(0).pere.variable.name + "(" + this.fathers.get(0).pere.index + ")\n";
	        for(int i=1; i<this.fathers.size(); i++){
	        	s+="       ";
	        	s+=" " + this.fathers.get(i).getVal() + " " + this.fathers.get(i).pere.variable.name + "(" + this.fathers.get(i).pere.index + ")\n";
	        }
	    }
	    else{
	        s+="\n";
	    }
	    return s;
	}*/
	
	public void SpToSpt(){
		Spt nouv;
		for(int i=0; i<this.kids.size(); i++){
			if(this.kids.get(i).fils!=null){
				if(!this.kids.get(i).fils.isLeaf()){
					nouv=new Spt(((Sp)kids.get(i).s).getVal(), 1);
					this.kids.get(i).s=nouv;
				}else{
					nouv=new Spt(((Sp)kids.get(i).s).getVal(), 0);
					this.kids.get(i).s=nouv;
				}
			}else{
				nouv=new Spt(((Sp)kids.get(i).s).getVal(), 0);
				this.kids.get(i).s=nouv;
			}
		}
	}
	
	public void StToSpt(){
		Spt nouv;
		for(int i=0; i<this.kids.size(); i++){
			if(this.kids.get(i).fils!=null){
				if(!this.kids.get(i).fils.isLeaf()){					//pas fin
					nouv=new Spt(0, ((St)kids.get(i).s).getvaldouble());
					this.kids.get(i).s=nouv;
				}else{													//vrai fin
					nouv=new Spt(((St)kids.get(i).s).getvaldouble(), 0);
					this.kids.get(i).s=nouv;
				}
			}else{														//bottom
				nouv=new Spt(((St)kids.get(i).s).getvaldouble(), 0);
				this.kids.get(i).s=nouv;
			}
		}
	}
	
	public void SpToS(){
		S nouv;
		for(int i=0; i<this.kids.size(); i++){
			if(this.kids.get(i).fils!=null){			//pas fin
				if(!this.kids.get(i).fils.isLeaf()){
					nouv=new S();
				}else{									//vrai fin
					nouv=new S(this.kids.get(i).s.copie());
				}
			}else{									//bottom     arc qui pointent vers la fin
				nouv=new S();
			}
			this.kids.get(i).s=nouv;	
		}
	}
	
	//on applique un facteur eventuellement pour rendre la conversion plus precise
	public void SToSp(int facteur){
		Sp nouv;
		for(int i=0; i<this.kids.size(); i++){
			if(this.kids.get(i).fils!=null){
				if(!this.kids.get(i).fils.isLeaf()){
					nouv=new Sp(0);
				}else{									//arc qui pointent vers la fin
					if( ((S)this.kids.get(i).s).last.printstr().compareTo("Sp") == 0 ){
						nouv= (Sp) ((S)this.kids.get(i).s).last.copie();				//int -> que des entiers
					}else{
						nouv=new Sp((int) (((S)this.kids.get(i).s).last.getvaldouble()*facteur));					//int -> que des entiers
						
						if(nouv.getvaldouble()!=((S)this.kids.get(i).s).last.getvaldouble()*facteur)
							System.out.println("erreur d'arrondis au passage de ADD en SLDD+ (passage de reel a entier naturel) : "+nouv.getvaldouble()+"->"+((S)this.kids.get(i).s).last.getvaldouble() +"*"+facteur);
					}

				}
			}else{
				nouv=new Sp(0);
			}
			this.kids.get(i).s=nouv;

		}
	}
	
	public void StToS(){
		S nouv;
		for(int i=0; i<this.kids.size(); i++){
			if(this.kids.get(i).fils!=null){			//pas fin
				if(!this.kids.get(i).fils.isLeaf()){
					nouv=new S();
				}else{									//vrai fin
					nouv=new S(this.kids.get(i).s.copie());
				}
			}else{									//bottom     arc qui pointent vers la fin
				nouv=new S();
			}
			this.kids.get(i).s=nouv;	
		}
	}

	
	
	public void SToSt(){
		St nouv;
		for(int i=0; i<this.kids.size(); i++){
			if(this.kids.get(i).fils!=null){
				if(!this.kids.get(i).fils.isLeaf()){
					nouv=new St(1);
				}else{									//arc qui pointent vers la fin
					if( ((S)this.kids.get(i).s).last.printstr().compareTo("St") == 0 ){
						nouv= (St) ((S)this.kids.get(i).s).last.copie();				//int -> que des entiers
					}else{
						nouv=new St(((S)this.kids.get(i).s).last.getvaldouble());					//int -> que des entiers
					}

				}
			}else{
				nouv=new St(1);
			}
			this.kids.get(i).s=nouv;			
		}
	}
	
	//on applique un facteur eventuellement pour rendre la conversion plus precise
	public void SptToSp(int facteur){
		Sp nouv;
		for(int i=0; i<this.kids.size(); i++){
			nouv=new Sp((int) Math.round(((Spt)this.kids.get(i).s).getvaldouble()*facteur));
			if( nouv.getvaldouble() != ((Spt)this.kids.get(i).s).getvaldouble()*facteur )
				System.out.println("erreur d'arrondis au passage de AADD en SLDD+ (passage de reel a entier naturel) : "+nouv.getvaldouble()+"->"+((S)this.kids.get(i).s).last.getvaldouble() +"*"+facteur);

			this.kids.get(i).s=nouv;
		}
	}
	
	public void SptToSt(){
		St nouv;
		for(int i=0; i<this.kids.size(); i++){
			if(this.kids.get(i).fils!=null){			//pas fin
				if(!this.kids.get(i).fils.isLeaf()){
					if(this.kids.get(i).s.printstr().compareTo("Spt")==0){
						nouv=new St(((Spt)this.kids.get(i).s).f);
					}else{
						nouv=(St) this.kids.get(i).s.copie();
					}
				}else{									//vrai fin
					nouv=new St(((Spt)this.kids.get(i).s).q);
				}
			}else{									//bottom     arc qui pointent vers la fin
				nouv=new St();
			}
			this.kids.get(i).s=nouv;
			
		}
	}
		
	public String toDot(){
   		String s;
		
		//name label form
   		s="n"+this.id + " [label=";
    	if(this.isLeaf())
    		s+="0, shape=box";
    	else
    		s+=this.variable.name;//+"_"+this.id;//+"_"+this.kidsdiffbottom();
    	//for(int i=0; i<this.indcopie.size(); i++)
    	//	s+="_"+this.indcopie.get(i);
    	
   		s+="];\n";
    		if(!this.isLeaf()){
    			for(int i=(this.kids.size()-1); i>=0; i--)
    				s+=this.kids.get(i).toDot();
   			//for(int i=(this.fathers.size()-1); i>=0; i--)
   			//	s+=this.fathers.get(i).toDot2();

   		}
   		//for(int i=(this.fathers.size()-1); i>=0; i--)
    		//	s+=this.fathers.get(i).toDot(binary, true);
   		
   		return s;
	}
	
	//comparaison de 2 neuds (variable, valeure, valeurs des arcs, fils)
	public int hashCode(){
		int hash=0;		
		
		for(int i=0; i<this.kids.size(); i++){
			hash+=this.kids.get(i).hashCode();
			hash=Integer.rotateLeft(hash, 3);
		}
		//System.out.println("----->"+hash);
		hashdur=hash;
		return hash;
	}
	
	public boolean equals(Object comp){
		
		
		if(this.variable!=((NodeDD)comp).variable){
			return false;
		}
		
		if((this.cpt==-1) != (((NodeDD)comp).cpt==-1))
			return false;
		
		for(int i=0; i<this.kids.size(); i++){
			if(!this.kids.get(i).equals(((NodeDD)comp).kids.get(i))){
				return false;
			}
		}
		return true;
	}
	
	public int kidsdiffbottom(){
		int cpt=0;
		for(int i=0; i<kids.size(); i++)
			if(kids.get(i).bottom==0 && kids.get(i).actif)
				cpt++;
		return cpt;
	}
	
	public void copieToNull()
	{
		copie.clear();
		indcopie.clear();
		aRemonter=null;
		adresse=null;
	}
	
}
