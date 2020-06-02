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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;

// 0 : leaf
// 1 -> n : vars


public class UniqueHashTable {
	protected Hashtable<NodeDD, NodeDD>[] uniqueHashTable;
	protected int nbVariables;
	protected Iterator<NodeDD> eN;
	protected Enumeration<NodeDD> enumeration;
	protected Iterator<NodeDD> iT;
	//protected ArrayList<Var> variables;
	
	// constructeur
	
	@SuppressWarnings("unchecked")
	public UniqueHashTable(int nbVar){
		int n=10;
		uniqueHashTable = (Hashtable<NodeDD, NodeDD>[]) new Hashtable[nbVar+1];
		for(int i=0; i<nbVar+1; i++)
			uniqueHashTable[i] = new Hashtable<NodeDD,NodeDD>(n);
		
		nbVariables=nbVar;
	}
	
	//permet d'attribuer un index a chacun des neuds
/*		public void giveIndex(){
			int k=0;
			for(int i=0; i<uniqueHashTable.length; i++){
				eN=uniqueHashTable[i].values().iterator();
				while(eN.hasNext())
					eN.next().id=k;
			}
		}*/
	
	//supprime les neuds dont le compteur est a -1
	public void supprNeudNegatifs(){
		NodeDD temp;
		
		for(int i=0; i<uniqueHashTable.length; i++){
			enumeration=uniqueHashTable[i].elements();
			while(enumeration.hasMoreElements()){
				temp=enumeration.nextElement();
				if(temp.cpt==-1){
					removeDefinitely(temp);
					//uniqueHashTable[i].remove(temp);
				}
			}
		}

	}
	
	//met les valeurs des neuds a 0 (si flagLeaf=false, pas les feuilles)
/*		public void valNodesToZero(boolean flagLeaf){
			int start;
			if(flagLeaf)
				start=0;
			else
				start=1;
			
			for(int i=start; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					uniqueTable[i].get(j).setVal(0);
				}
			}
		}*/
	
/*		public NodeDD ajoutNormaliseReduit(NodeDD n){
			NodeDD frere;
			boolean changement;
			
			changement=n.normalise();
				
			frere=recherche(n);
			if(frere==null){			//pas de frere
				add(n);
			}else{							//toi le frere que je n'ai jamais eu...
				frere.fusion(n);
			}
			
			if(n.cpt==-1){
				this.removeDefinitely(n);
				return frere;
			}
			
			return n;
		}*/


	
	//normalise le noeud
	//verifier si la verif est pas deja fait avec put
	//le n peut changer
	public NodeDD ajoutNormaliseReduitPropage(NodeDD n){				
		
		NodeDD frere=null;
		Structure save;
		boolean actif=false;
		boolean fusion=false;
		ArrayList<Arc> copieListePere=new ArrayList<Arc>();		//comme ca on peut le supprimer en paix du fils a supprimer						
		for(int i=0; i<n.kids.size(); i++){
			if(n.kids.get(i).bottom>0){
				if(n.kids.get(i).fils!=null){
					if(n.kids.get(i).fils.fathers.size()==1){	//on veille a ne pas laisser d'orphelins
						removeDefinitely(n.kids.get(i).fils);
					}							
					n.kids.get(i).changerFils(null);
				}
			}
			else{
				actif=true;
			}
		}

		copieListePere.addAll(n.fathers);
		
		if(actif){
			save=n.normaliseInf();
			
			frere=recherche(n);
			if(frere==null){			//pas de frere
				add(n);
			}else{							//toi le frere que je n'ai jamais eu...
//					frere.fusion(n);
				// Destruction de noeud
				fusion=true;
			}
			
			if(!save.isNeutre() || fusion){
				ArrayList<NodeDD> temp=new ArrayList<NodeDD>();
				for(int i=0; i<copieListePere.size(); i++){
					NodeDD check=this.recherche(copieListePere.get(i).pere);
					if(check!=null){
						if(check.id==copieListePere.get(i).pere.id){				//pour etre sur que on a pas pecho un autre noeud identique mais deja ajoute
							removeFromTable(copieListePere.get(i).pere);
							temp.add(copieListePere.get(i).pere);
						}
					}
					if(!save.isNeutre())
						copieListePere.get(i).s.normaliseSup(copieListePere.get(i).s, save);
				}
				if(fusion){
						frere.fusion(n);
				}
				
				for(int i=0; i<temp.size(); i++)
					this.ajoutNormaliseReduitPropage(temp.get(i));

			}
		}else{						//si pas actif		(tous les noeuds fils etaient a bottom
			n.cpt=-1;
    		
			ArrayList<NodeDD> temp=new ArrayList<NodeDD>();
			for(int i=0; i<copieListePere.size(); i++){

				NodeDD check=this.recherche(copieListePere.get(i).pere);
				if(check!=null){
					if(check.id==copieListePere.get(i).pere.id){				//pour etre sur que on a pas pecho un autre noeud identique mais deja ajoute
						removeFromTable(copieListePere.get(i).pere);
						temp.add(copieListePere.get(i).pere);
					}
				}
				copieListePere.get(i).bottom=1;
				copieListePere.get(i).changerFils(null);
			}
			for(int i=0; i<temp.size(); i++){
				this.ajoutNormaliseReduitPropage(temp.get(i));
			}
		}
		
		if(n.cpt==-1){
			//removeDefinitely(n);
			return frere;		//null si suprime, frere si fusione
		}else{				// le noeud existe toujours
			return n;
		}
	}
	
	public NodeDD normaliseReduitPropage(NodeDD n){	
		this.removeFromTable(n);
		return this.ajoutNormaliseReduitPropage(n);
	}
	
	public NodeDD ajoutSansNormaliser(NodeDD n){
		NodeDD frere=null;
		boolean fusion=false;
		ArrayList<Arc> copieListePere=new ArrayList<Arc>();		//comme ca on peut le supprimer en paix du fils a supprimer						

		//copieListePere=(ArrayList<Arc>) n.fathers.clone();
		for(int i=0; i<n.fathers.size(); i++)
			copieListePere.add(n.fathers.get(i));
			
		frere=recherche(n);
		if(frere==null){			//pas de frere
			add(n);
		}else{							//toi le frere que je n'ai jamais eu...
//				frere.fusion(n);
			fusion=true;
		}
			
			if(fusion){
				ArrayList<NodeDD> temp=new ArrayList<NodeDD>();
				for(int i=0; i<copieListePere.size(); i++){
					NodeDD check=this.recherche(copieListePere.get(i).pere);
					if(check!=null){
						if(check.id==copieListePere.get(i).pere.id){				//pour etre sur que on a pas pecho un autre noeud identique mais deja ajoute
							removeFromTable(copieListePere.get(i).pere);
							temp.add(copieListePere.get(i).pere);
						}
					}
				}
				if(fusion){
						frere.fusion(n);
				}
				
				for(int i=0; i<temp.size(); i++)
					this.ajoutSansNormaliser(temp.get(i));

			}
		
		
		if(n.cpt==-1){
			//removeDefinitely(n);
			return frere;		//null si suprime, frere si fusione
		}else{				// le noeud existe toujours
			return n;
		}
	}
	
	public NodeDD ajoutNormaliseReduit(NodeDD n){				
		
		NodeDD frere=null;
		Structure save;
		boolean actif=false;
		boolean fusion=false;
		ArrayList<Arc> copieListePere=new ArrayList<Arc>();		//comme ca on peut le supprimer en paix du fils a supprimer						
		for(int i=0; i<n.kids.size(); i++){
			if(n.kids.get(i).bottom>0){
				if(n.kids.get(i).fils!=null){
					if(n.kids.get(i).fils.fathers.size()==1){	//on veille a ne pas laisser d'orphelins
						removeDefinitely(n.kids.get(i).fils);
					}							
					n.kids.get(i).changerFils(null);
				}
			}
			else
				actif=true;
		}
		for(int i=0; i<n.fathers.size(); i++)
			copieListePere.add(n.fathers.get(i));
		
		if(actif){
			save=n.normaliseInf();
			
			frere=recherche(n);
			if(frere==null){			//pas de frere
				add(n);
			}else{							//toi le frere que je n'ai jamais eu...
//					frere.fusion(n);
				fusion=true;
			}
			
			if(!save.isNeutre() || fusion){
				ArrayList<NodeDD> temp=new ArrayList<NodeDD>();
				for(int i=0; i<copieListePere.size(); i++){
					NodeDD check=this.recherche(copieListePere.get(i).pere);
					if(check!=null){
						if(check.id==copieListePere.get(i).pere.id){				//pour etre sur que on a pas pecho un autre noeud identique mais deja ajoute
							removeFromTable(copieListePere.get(i).pere);
							temp.add(copieListePere.get(i).pere);
						}
					}
					copieListePere.get(i).s.normaliseSup(copieListePere.get(i).s, save);
				}
				if(fusion){
						frere.fusion(n);
				}
				
				for(int i=0; i<temp.size(); i++)
					this.ajoutSansNormaliser(temp.get(i));

			}
		}else{						//si pas actif		(tous les noeuds fils etaient a bottom
			n.cpt=-1;
    		
			ArrayList<NodeDD> temp=new ArrayList<NodeDD>();
			for(int i=0; i<copieListePere.size(); i++){

				NodeDD check=this.recherche(copieListePere.get(i).pere);
				if(check!=null){
					if(check.id==copieListePere.get(i).pere.id){				//pour etre sur que on a pas pecho un autre noeud identique mais deja ajoute
						removeFromTable(copieListePere.get(i).pere);
						temp.add(copieListePere.get(i).pere);
					}
				}
				copieListePere.get(i).bottom=1;
				copieListePere.get(i).changerFils(null);
			}
			for(int i=0; i<temp.size(); i++){
				this.ajoutSansNormaliser(temp.get(i));
			}
		}
		
		if(n.cpt==-1){
			//removeDefinitely(n);
			return frere;		//null si suprime, frere si fusione
		}else{				// le noeud existe toujours
			return n;
		}
	}
	
	public NodeDD normaliseReduit(NodeDD n){	
		this.removeFromTable(n);
		return this.ajoutNormaliseReduit(n);
	}
	
	//opt
	public NodeDD removeFromTable(NodeDD n){
//    		NodeDD check;
		int position;
		
		if (n.variable!=null)
			position=n.variable.pos;
		else
			position=0;
		
//			eN=uniqueHashTable[2].values().iterator();
//			while(eN.hasNext())
//				System.out.println(eN.next().id);

		//check=(NodeDD)uniqueHashTable[position].get(n);
		//if(check.id==n.id)
//			check=(NodeDD)uniqueHashTable[position].get(n);
//			if(check.id==n.id)
			uniqueHashTable[position].remove(n);

//			System.out.println("remove " + position + " " + n.id);
		
		
		return n;
	}
	
/*		public NodeDD modifUHT1(NodeDD n){
			return removeFromTable(n);
		}
		
		//le n peut changer
		public NodeDD modifUHT2(NodeDD n){
			return ajout(n);
		}*/
	
	
	
	//accesseurs
	private void add(NodeDD n){
		if(n.variable!=null)
			uniqueHashTable[n.variable.pos].put(n, n);
		else
			uniqueHashTable[0].put(n, n);
	}
	
	
	public int size(){
		int size=0;
		eN=uniqueHashTable[0].values().iterator();
		NodeDDlast n=(NodeDDlast) eN.next();
		size+=n.count();								// on compte le premier a part pour le cas du add
		for(int i=1; i<uniqueHashTable.length; i++)
			size+=uniqueHashTable[i].size();
		return size;
	}
	
	public int sizeArcs(){
		int size=1;

		for(int i=1; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext())
				size+=eN.next().kids.size();
		}

		return size;
	}
	
	
	public int sizeArcsDiffBottom(){
		int size=1;

		for(int i=1; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext())
				size+=eN.next().kidsdiffbottom();
		}

		return size;
	}
	
	public int size(int var){
		return uniqueHashTable[var].size();
	}
	
	public int sizeArcs(int var){
		int size=0;

		eN=uniqueHashTable[var].values().iterator();
		while(eN.hasNext())
			size+=eN.next().kidsdiffbottom();
		

		return size;
	}
	
	public void deconditioner(int var){
//			NodeDD n;
		eN=uniqueHashTable[var].values().iterator();
		while(eN.hasNext()){
			eN.next().deconditioner();
		}
	}
	
	public void conditioner(int var, int val){
//			NodeDD n;
		eN=uniqueHashTable[var].values().iterator();
		while(eN.hasNext()){
			eN.next().conditioner(val);
		}
	}
	
	
/*		public NodeDD getprout(int var, int i){
			return uniqueTable[var].get(i);
		}*/
	
	
	//opt
	// /!\ un element peut bouger de place apres modification !!!!!!
	public ArrayList<NodeDD> get(int var){
		ArrayList<NodeDD> liste=new ArrayList<NodeDD>();
		
		eN=uniqueHashTable[var].values().iterator();
		while(eN.hasNext())
			liste.add(eN.next());
		return liste;
	}
			
	//un, zero, meme fonction
/*		public NodeDD getneutre(){
			NodeDD zero=new NodeDD(null,0);
			if(uniqueHashTable[0].containsKey(zero)){
				return (NodeDD)uniqueHashTable[0].get(zero);
			}else{
				uniqueHashTable[0].put(zero, zero);
			}
			return zero;
		}*/
	

	public void removeDefinitely(NodeDD n){
		
		int id=n.id;
		NodeDD no=n;
		NodeDD v;
		
		int position;
		if (n.variable!=null)
			position=n.variable.pos;
		else
			position=0;
		
		v=(NodeDD)uniqueHashTable[position].remove(n);
		if(v==null){
			System.out.println("@ uht : erreur noeud a enlever introuvable - pos"+position+";name:"+n.variable.name+" id:"+n.id);
			int z=1;
			ArrayList<String> s=new ArrayList<String>();
			s.get(z);
			
//				eN=uniqueHashTable[position].keys();
//				while(eN.hasNext())
//					System.out.println(eN.next().hashCode());
//				for(int i=0; i<uniqueHashTable[position].size(); i++){
//					System.out.println("->"+this.get(position).get(i).id +" " + this.get(position).get(i).equals(n));
//					System.out.println(n.hashCode() + " " + this.get(position).get(i).hashCode());
//				}
		}else{
			if(v.id!=id){
				System.out.println("@uht, wrong suppr : "+v.id+" instead of "+id);
				System.out.println("id:"+v.id+" size:"+v.kids.size()+" 1:"+v.kids.get(0).bottom+" 2:"+v.kids.get(1).bottom+" 3:"+v.kids.get(2).bottom+" 4:"+v.kids.get(3).bottom+" 5:"+v.kids.get(4).bottom+" 6:"+v.kids.get(5).bottom+" 7:"+v.kids.get(6).bottom+" 8:"+v.kids.get(7).bottom);
				System.out.println("id:"+no.id+" size:"+no.kids.size()+" 1:"+no.kids.get(0).bottom+" 2:"+no.kids.get(1).bottom+" 3:"+no.kids.get(2).bottom+" 4:"+no.kids.get(3).bottom+" 5:"+no.kids.get(4).bottom+" 6:"+no.kids.get(5).bottom+" 7:"+no.kids.get(6).bottom+" 8:"+no.kids.get(7).bottom);
			}
		}
		
		while(n.kids.size()!=0){
			if(n.kids.get(n.kids.size()-1).fils!=null){
				if(n.kids.get(n.kids.size()-1).fils.fathers.size()==1){	//on veille a ne pas laisser d'orphelins
					removeDefinitely(n.kids.get(n.kids.size()-1).fils);
				}
			}
			n.kids.get(n.kids.size()-1).remove();		//on supprime tous les arcs fils
		}
	}
	
/*		//au cas ou il soit toujours pointe, on laisse la nouvelle adresse
		public void removeDefinitely(NodeDD n, NodeDD adresse){	
			int position;
			if (n.variable!=null)
				position=n.variable.pos;
			else
				position=0;
			
			uniqueHashTable[position].remove(n);
			
			while(n.kids.size()!=0){
				if(n.kids.get(0).fils.fathers.size()==1){	//on veille a ne pas laisser d'orphelins
					removeDefinitely(n.kids.get(0).fils);
				}							
				n.kids.get(0).remove();		//on supprime tous les arcs fils
			}
			
			if(adresse!=null){
				n.cpt=-2;
				while(adresse.cpt==-2)
					adresse=adresse.copie.get(0);
				n.copie.add(adresse);
			}
		}*/
	
	//renvoie le noeud identique a n (null si aucun)
	public NodeDD recherche(NodeDD n){
		int pos;
		if(n==null)					//et hop
			return null;
		
		
		if(n.variable!=null)	//si c'est pas une feuille, on recupere l'id
			pos=n.variable.pos;
		else					// si c'est une feuille, on cherche dans les zeros
			pos=0;
		
		// get renvoie "null" si l'objet n'est pas dans la table
		
//			if(uniqueHashTable[pos].containsKey(n)){   //si elle existe deja
			return uniqueHashTable[pos].get(n);
//			}

		
//			return null;
	}
	
	//initialise tous les cpt a une valeure x
	public void cptTo(int x){
		for(int i=0; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext())
				eN.next().cpt=x;
		}
	}
	
	//initialise tous les counting a -1
	public void countingToMoinsUn(){
		NodeDD temp;
		for(int i=0; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				temp=eN.next();
				temp.counting=-1;
				temp.pondere=0;
				temp.inference=0;
			}
		}
	}
	
	//initialise tous les counting a -1
	public void countingToMoinsUnUnderANode(int start){
		NodeDD temp;
		
		eN=uniqueHashTable[0].values().iterator();
		while(eN.hasNext()){
			temp=eN.next();
			temp.counting=-1;
			temp.pondere=0;
			temp.inference=0;

		}
		
	
		for(int i=start+1; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				temp=eN.next();
				temp.counting=-1;
				temp.pondere=0;
				temp.inference=0;
			}
		}
	}
	
	//initialise tous les cptMult a une valeure x
	public void memoireTo(Structure x){
		for(int i=0; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext())
				eN.next().memoire=x;	
		}
	}
	
	//initialise tous les valeurs de chaques neuds a x (en general 0)
/*		public void valueTo(int x){
			for(int i=0; i<uniqueHashTable.length; i++){
				eN=uniqueHashTable[i].values().iterator();
				while(eN.hasNext())
					eN.next().value=x;
			}
		}*/
	
	//copie prend null (fait apres ajouter une contrainte)
	//on en profite pour faire null sur la structure a remonter (voir @Arc::operationValuerARemonter)
	public void copieToNull(){
		for(int i=0; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				eN.next().copieToNull();
			}
		}
	}
	
	public void maxminNull(){
		NodeDD temp;
		for(int i=0; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				temp=eN.next();
				temp.max=null;
				temp.min=null;
			}
		}
	}
	
	
	//la consistance necessite 2 parcours, un vers le bas, un vers le haut. on en profite pour mettre a jours "gratuitement" min et max, en plus ils servent d'indicateur.
	public void minMaxConsistance(){
//			NodeDD temp;
//			Structure min, max;
		for(int i=1; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				eN.next().minMaxConsistance1();
			}
		}
		
		this.get(0).get(0).minMaxConsistance1();
		//this.get(0).get(0).min.toNeutre();
		this.get(0).get(0).max.toNeutre();
		
		
		for(int i=uniqueHashTable.length-1; i>0; i--){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				eN.next().minMaxConsistance2();
			}
		}
	}
	
	//CD = true : conditionning
	//CD = false : deconditionning
	public void minMaxConsistanceMaj(int var, boolean cd){
		NodeDD temp;
//			Structure min, max;
		boolean next=true;
		//for(int i=1; i>uniqueHashTable.length; i--){
		
		
		eN=uniqueHashTable[var].values().iterator();
		while(eN.hasNext()){
			eN.next().minMaxConsistance1Maj(cd);
		}
		
		for(int i=var+1; i<uniqueHashTable.length; i++){
			if(next){
				//convtofalse
				//uniqueHashTable[i].
				
				next=false;
				eN=uniqueHashTable[i].values().iterator();
				temp=eN.next();
				temp.variable.consValTofalse();
				next=temp.minMaxConsistance1Maj(cd);
				
				while(eN.hasNext()){
					if(next)
						eN.next().minMaxConsistance1Maj(cd);
					else
						next=eN.next().minMaxConsistance1Maj(cd);
				}
			}
		}
		
		this.get(0).get(0).minMaxConsistance1Maj(cd);
		//this.get(0).get(0).max.toNeutre();
		this.get(var).get(0).variable.consValTofalse();
		
		next=true;
		
		eN=uniqueHashTable[var].values().iterator();
		while(eN.hasNext()){
				eN.next().minMaxConsistance2Maj(cd);
		}
		
		for(int i=var-1; i>0; i--){
		//for(int i=uniqueHashTable.length-1; i>0; i--){
			if(next){
				next=false;
				eN=uniqueHashTable[i].values().iterator();
				temp=eN.next();
				temp.variable.consValTofalse();
				next=temp.minMaxConsistance2Maj(cd);

				while(eN.hasNext()){
					if(next)
						eN.next().minMaxConsistance2Maj(cd);
					else
						next=eN.next().minMaxConsistance2Maj(cd);
				}
			}
		}
		
		
	}
	
	//la consistance necessite 2 parcours, un vers le bas, un vers le haut. on en profite pour mettre a jours "gratuitement" min et max, en plus ils servent d'indicateur.
	//mise a jours, on garde le haut du min et le bas du max. (2x plus rapide normalement
	public void minMaxConsistanceMajopt(int var, boolean cd){
		ArrayList<Arc> prehash;
		HashSet<NodeDD> tempHash1=new HashSet<NodeDD>();
		HashSet<NodeDD> tempHash2=new HashSet<NodeDD>();
		//Hashtable<NodeDD, NodeDD> tempHash1=new Hashtable<NodeDD, NodeDD>();
		//Hashtable<NodeDD, NodeDD> tempHash2=new Hashtable<NodeDD, NodeDD>();
		NodeDD temp;
//			Structure min, max;
		ArrayList<Integer> aUpdater=new ArrayList<Integer>(); 
		////
		eN=uniqueHashTable[var].values().iterator();
		while(eN.hasNext()){
			eN.next().minMaxConsistance1Maj(cd);
		}
		
		eN=uniqueHashTable[var+1].values().iterator();
		temp=eN.next();
		temp.variable.consValTofalse();
		aUpdater.add(temp.variable.pos);
		prehash=temp.minMaxConsistance1Majopt();
		if(prehash!=null){
			for(int i=0; i<prehash.size(); i++){
				if(prehash.get(i).fils!=null)
					tempHash1.add(prehash.get(i).fils);
			}
		}
		while(eN.hasNext()){
			prehash=eN.next().minMaxConsistance1Majopt();
			if(prehash!=null){
				for(int i=0; i<prehash.size(); i++){
					if(prehash.get(i).fils!=null)
						tempHash1.add(prehash.get(i).fils);
				}
			}
		}
		
		while(tempHash1.size()!=0){
			iT=tempHash1.iterator();//.values().iterator();
			temp=iT.next();
			if(!temp.isLeaf()){
				temp.variable.consValTofalse();
				aUpdater.add(temp.variable.pos);
				prehash=temp.minMaxConsistance1Majopt();
				if(prehash!=null){
					for(int i=0; i<prehash.size(); i++){
						if(prehash.get(i).fils!=null){
							tempHash2.add(prehash.get(i).fils);
						}
					}
				}
				while(iT.hasNext()){
					prehash=iT.next().minMaxConsistance1Majopt();
					if(prehash!=null){
						for(int i=0; i<prehash.size(); i++){
							if(prehash.get(i).fils!=null){
								tempHash2.add(prehash.get(i).fils);
							}
						}
					}
				}
			}
			tempHash1=tempHash2;
			tempHash2=new HashSet<NodeDD>();
		}
		
		this.get(0).get(0).minMaxConsistance1Maj(cd);
		
		////
		eN=uniqueHashTable[var].values().iterator();
		while(eN.hasNext()){
			temp=eN.next();
			temp.minMaxConsistance2Maj(cd);
			temp.majConsistance();
			
		}
		
		if(var>1){
			eN=uniqueHashTable[var-1].values().iterator();
			temp=eN.next();
			temp.variable.consValTofalse();
			aUpdater.add(temp.variable.pos);
			prehash=temp.minMaxConsistance2Majopt();
			if(prehash!=null){
				for(int i=0; i<prehash.size(); i++){
					if(prehash.get(i).pere!=null)
						tempHash1.add(prehash.get(i).pere);
				}
			}
			while(eN.hasNext()){
				prehash=eN.next().minMaxConsistance2Majopt();
				if(prehash!=null){
					for(int i=0; i<prehash.size(); i++){
						if(prehash.get(i).pere!=null)
							tempHash1.add(prehash.get(i).pere);
					}
				}
			}
		}
		
		while(tempHash1.size()!=0){
			iT=tempHash1.iterator();//.values().iterator();
			temp=iT.next();
			temp.variable.consValTofalse();
			aUpdater.add(temp.variable.pos);
			prehash=temp.minMaxConsistance2Majopt();
			if(prehash!=null){
				for(int i=0; i<prehash.size(); i++){
					if(prehash.get(i).pere!=null)
						tempHash2.add(prehash.get(i).pere);
				}
			}
			while(iT.hasNext()){
				prehash=iT.next().minMaxConsistance2Majopt();
				if(prehash!=null){
					for(int i=0; i<prehash.size(); i++){
						if(prehash.get(i).pere!=null)
							tempHash2.add(prehash.get(i).pere);
					}
				}
			}
			tempHash1=tempHash2;
			tempHash2=new HashSet<NodeDD>();
		}
		
		for(int i=0; i<aUpdater.size(); i++){
			eN=uniqueHashTable[aUpdater.get(i)].values().iterator();
			while(eN.hasNext()){
				eN.next().majConsistance();
			}
		}

	}
	public void minDomainVariable(int var){
//			NodeDD temp;
//			Structure min, max;
		
		for(int i=uniqueHashTable.length-1; i>var; i--){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				eN.next().minPartieBasse();
			}
		}
	}

	public void maxDomainVariable(int var){
//			NodeDD temp;
//			Structure min, max;
		
		for(int i=1; i<=var; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				eN.next().maxPartieHaute();
			}
		}
	}
	
	public void consGraceAMinMax(){
		for(int i=1; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				eN.next().majConsistance();
			}
		}
	}
	
    public void dimminutionduproblemetempasupprimer(){
		//String s="v3v6v28v15v14v18v55v8v11v16v12_0_Optionv25v20_0_Optionv27v27_0_Packv19v5v19_0_Optionv118v49";
		String s2="v30v35v38v40v55v0v45v18v14v34_1_Seriev118v5v19v11v8v13v25v20_0v27v11_4v24_1";

    	NodeDD temp;
    	boolean useless;
    	cptTo(0);
		for(int i=1; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				temp=eN.next();
	        	useless=true;
	        	
	            if(s2.contains(temp.variable.name)){
	            	System.out.println("garde="+temp.variable.name);
	            	useless=false;
	            }else{
	            	System.out.println("vire="+temp.variable.name);
	            }
	            if(useless){
	            
	            	this.courtcircuit(temp);        //ce neud est court circuité, et supprimé
	            	//uniqueTable[i].remove(j); //plouf
	            	//j--;
	            }
			}
        }
		//supprNeudNegatifs();
    }
	
    public void rechercheNoeudInutile(){
		NodeDD temp;
    	boolean useless;
    	cptTo(0);
		for(int i=1; i<uniqueHashTable.length; i++){
			enumeration=uniqueHashTable[i].elements();
			while(enumeration.hasMoreElements()){
				temp=enumeration.nextElement();
	        	useless=true;
	        	
	            for(int k=1; k<temp.kids.size(); k++){
	            	if( !temp.kids.get(k).equals(temp.kids.get(0))){		         
	            		useless=false;												//ce neud sert a qqch
	            		break;														//innutile d'aller plus loin
	            	}
	            }
	            if(useless){
	            
	            	this.courtcircuit(temp);        //ce neud est court circuité, et supprimé
	            	//uniqueTable[i].remove(j); //plouf
	            	//j--;
	            }
			}
        }
		//supprNeudNegatifs();
    }
    
    
    public void courtcircuit(NodeDD n){
//			int bottom= n.kids.get(0).bottom;		//on doit tout envoyer vers bottom?
		this.removeFromTable(n);
		NodeDD p;
		
		if(n.kids.get(0).bottom!=0){												//oui
			for(int i=(n.fathers.size()-1); i>=0; i--){
				p=n.fathers.get(i).pere;
				this.removeFromTable(p);
				

				boolean ok=false;
				for(int j=0; j<n.kids.size(); j++){
					if(n.kids.get(j).bottom==0){
						n.fathers.get(i).bottom=n.kids.get(j).bottom;
						n.fathers.get(i).changerFils(n.kids.get(j).fils);		// on courcircuit le fils
						ok=true;
						break;
					}
				}
				if(!ok){
					n.fathers.get(i).bottom=n.kids.get(0).bottom;
					n.fathers.get(i).changerFils(n.kids.get(0).fils);
				}
		    	
		    	this.add(p);
			}
		}else{														//non
			
			for(int i=(n.fathers.size()-1); i>=0; i--){
				p=n.fathers.get(i).pere;
				if(p!=null)
					this.removeFromTable(p);
				
		    	n.fathers.get(i).changerFils(n.kids.get(0).fils);		// on courcircuit le fils
		    	if(p!=null)
		    		this.add(p);
			}
		}
	    
	    for(int i=(n.kids.size()-1); i>=0; i--){
	    	n.kids.get(i).remove();	
	    }
	    
	    
	    //n.cpt=-1;   //on peut le virer
	}
    
    
/*	    public void cutBottom(){
			NodeDD temp;
	    	for(int i=1; i<uniqueHashTable.length; i++){
				eN=uniqueHashTable[i].values().iterator();
				while(eN.hasNext()){
					temp=eN.next();
	    			for(int k=0; k<temp.kids.size(); k++){
	    				if(temp.kids.get(k).bottom > 0){
	    					//on supprime le fils si besoin
	    					if(temp.kids.get(k).fils.fathers.size()==1){	//on veille a ne pas laisser d'orphelins
	    						remove(temp.kids.get(k).fils);
	    					}	
	    					temp.kids.get(k).changerFils(getzero());
	    					temp.kids.get(k).s.setVal(0);
	    				}
	    			}
	    		}
	    	}
	    }*/

/*		public void test(int x){
			int a=0, b=0;
			for(int i=0; i<uniqueTable.size(); i++){
				a+=uniqueTable.get(i).fathers.size();
				b+=uniqueTable.get(i).kids.size();
			}
			if(a!=b){
				//erreur
				uniqueTable.get(x).cpt=100;
			}
		}*/
    
	public void compterNeudsSansPere(){
		int cpt=0;
		for(int i=0; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				if (eN.next().fathers.size()==0)
					cpt++;						
			}
		}
		if (cpt!=0)
			System.out.println("/!\\ neuds solitaires : " + cpt);

	}

	/*public int countingpondere(){
		int cpt=0;
		NodeDD n=null;
		for(int j=1; j<uniqueHashTable.length; j++){
			eN=uniqueHashTable[j].values().iterator();
			while(eN.hasNext()){
				n=eN.next();
				n.counting=0;
				n.pondere=0;
				for(int i=0; i<n.fathers.size(); i++){
					if(n.fathers.get(i).bottom==0 && n.fathers.get(i).actif){
						if(n.fathers.get(i).pere!=null){
							n.counting+=n.fathers.get(i).pere.counting;
							n.pondere+=n.fathers.get(i).pere.pondere + n.fathers.get(i).s.getvaldouble()*n.fathers.get(i).pere.counting;
						}else{
							n.counting=1;
							n.pondere=(int) n.fathers.get(i).s.getvaldouble();
						}
					}
				}
			}
		}
		eN=uniqueHashTable[0].values().iterator();
		while(eN.hasNext()){
			n=eN.next();
			n.counting=0;
			n.pondere=0;
			for(int i=0; i<n.fathers.size(); i++){
				if(n.fathers.get(i).bottom==0 && n.fathers.get(i).actif){
					n.counting+=n.fathers.get(i).pere.counting;
					n.pondere+=n.fathers.get(i).pere.pondere + n.fathers.get(i).s.getvaldouble()*n.fathers.get(i).pere.counting;
				}
			}
		}
		return n.pondere;

	}*/
	
	public void SpToSpt(){
		
		ArrayList<NodeDD> list;
		for(int i=0; i<uniqueHashTable.length; i++){
			list=new ArrayList<NodeDD>();
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}
			for(int j=0; j<list.size(); j++){
				removeFromTable(list.get(j));
				list.get(j).SpToSpt();
				ajoutSansNormaliser(list.get(j));
			}
		}
	}
	
	public void StToSpt(){
		
		ArrayList<NodeDD> list;
		for(int i=0; i<uniqueHashTable.length; i++){
			list=new ArrayList<NodeDD>();
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}
			for(int j=0; j<list.size(); j++){
				removeFromTable(list.get(j));
				list.get(j).StToSpt();
				ajoutSansNormaliser(list.get(j));
			}
		}
	}
	
	public void SpToS(){
		ArrayList<NodeDD> list;
		for(int i=1; i<uniqueHashTable.length; i++){
			list=new ArrayList<NodeDD>();
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}
			for(int j=0; j<list.size(); j++){
//					System.out.print(size());
				removeFromTable(list.get(j));
				//ajoutSansNormaliser(list.get(j));
//					System.out.println(" "+size());
			}
			for(int j=0; j<list.size(); j++){
				list.get(j).SpToS();
				ajoutSansNormaliser(list.get(j));
//					System.out.println(" "+size());
			}
		}
	}
	
	//on applique un facteur eventuellement pour rendre la conversion plus precise
	public void SToSp(int facteur){
		ArrayList<NodeDD> list;
		for(int i=0; i<uniqueHashTable.length; i++){
			list=new ArrayList<NodeDD>();
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}
			for(int j=0; j<list.size(); j++){
				removeFromTable(list.get(j));
			}
			for(int j=0; j<list.size(); j++){
				list.get(j).SToSp(facteur);
				ajoutSansNormaliser(list.get(j));
			}
		}
	}
	
	public void StToS(){
		ArrayList<NodeDD> list;
		for(int i=0; i<uniqueHashTable.length; i++){
			list=new ArrayList<NodeDD>();
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}
			for(int j=0; j<list.size(); j++){
				removeFromTable(list.get(j));
			}
			for(int j=0; j<list.size(); j++){
				list.get(j).StToS();
				ajoutSansNormaliser(list.get(j));
			}
		}
	}

	
	public void SToSt(){
		ArrayList<NodeDD> list;
		for(int i=0; i<uniqueHashTable.length; i++){
			list=new ArrayList<NodeDD>();
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}
			for(int j=0; j<list.size(); j++){
				removeFromTable(list.get(j));
				list.get(j).SToSt();
				ajoutSansNormaliser(list.get(j));
			}
		}
	}
	
	//on applique un facteur eventuellement pour rendre la conversion plus precise
	public void SptToSp(int facteur){
		ArrayList<NodeDD> list;
		for(int i=0; i<uniqueHashTable.length; i++){
			testStructureUniforme();
			list=new ArrayList<NodeDD>();
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}
			for(int j=0; j<list.size(); j++){
				removeFromTable(list.get(j));
			}
			for(int j=0; j<list.size(); j++){
				list.get(j).SptToSp(facteur);
				ajoutSansNormaliser(list.get(j));
			}
		}
	}
	
	
	public void SptToSt(){
		ArrayList<NodeDD> list;
		for(int i=0; i<uniqueHashTable.length; i++){
			list=new ArrayList<NodeDD>();
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}

			for(int j=0; j<list.size(); j++){
				removeFromTable(list.get(j));
			}
			for(int j=0; j<list.size(); j++){
				list.get(j).SptToSt();
				ajoutSansNormaliser(list.get(j));
			}
		}
	}
	
	public void combDown(VDD x){
		x.combDownToADD(x.first);
		
		ArrayList<NodeDD> list;
		ArrayList<Arc> listArc;
		for(int i=1; i<uniqueHashTable.length; i++){
			list=new ArrayList<NodeDD>();
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}
			listArc=new ArrayList<Arc>();
			for(int j=0; j<list.size(); j++){
				for(int k=0; k<list.get(j).kids.size(); k++)
					listArc.add(list.get(j).kids.get(k));
			}
			for(int j=0; j<listArc.size(); j++){
				if(listArc.get(j).fils!=null)			//on vire les bottom
					x.combDownToADD(listArc.get(j));
			}
		}
	}
	
	public void combDownSptToSp(VDD x){
		x.combDownSptToSp(x.first);
		
		ArrayList<NodeDD> list;
		ArrayList<Arc> listArc;
		for(int i=1; i<uniqueHashTable.length; i++){
			x.toDot("a"+i, false);

			list=new ArrayList<NodeDD>();
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}
			
			listArc=new ArrayList<Arc>();
			for(int j=0; j<list.size(); j++){
				for(int k=0; k<list.get(j).kids.size(); k++)
					listArc.add(list.get(j).kids.get(k));
			}
			for(int j=0; j<listArc.size(); j++){
				if(listArc.get(j).fils!=null)
					x.combDownSptToSp(listArc.get(j));
			}
		}
	}
	
	public void combDownSptToSt(VDD x){
		x.combDownSptToSt(x.first);
		
		ArrayList<NodeDD> list;
		ArrayList<Arc> listArc;
		for(int i=1; i<uniqueHashTable.length; i++){ 
			
			list=new ArrayList<NodeDD>();
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}
			
			listArc=new ArrayList<Arc>();
			for(int j=0; j<list.size(); j++){
				removeFromTable(list.get(j));
				for(int k=0; k<list.get(j).kids.size(); k++){
					listArc.add(list.get(j).kids.get(k));
					
				}
			}
			for(int j=0; j<listArc.size(); j++){
				if(listArc.get(j).fils!=null)
					x.combDownSptToSt(listArc.get(j));
			}
			for(int j=0; j<list.size(); j++){
				ajoutSansNormaliser(list.get(j));
			}
		}
	}
    	
	
	//ligne commente : peut etre amelioré pour sldd* -> aadd
	//date de l'epoque ou on utilisait des fractions. inutile maintenant !
	/*public void modeAaddON(){
		for(int i=0; i<uniqueTable.length; i++){
			for(int j=0; j<uniqueTable[i].size(); j++){
				for(int k=0; k<uniqueTable[i].get(j).kids.size(); k++){
					uniqueTable[i].get(j).kids.get(k).fracAddi.n=uniqueTable[i].get(j).kids.get(k).val;	
					uniqueTable[i].get(j).kids.get(k).val=0;
						//uniqueTable[i].get(j).kids.get(k).fracAddi.add(uniqueTable[i].get(j).kids.get(k).fracMult);
						uniqueTable[i].get(j).kids.get(k).fracMult.n=0;			//on met les arcs du bas a zero
					}
				}
			}
		}
	}*/
	
/*		public void printValFeuillesAdd(){
			for(int i=0; i<this.uniqueTable[0].size(); i++)
				System.out.print(this.uniqueTable[0].get(i).value + " ");
			System.out.println();
		}*/
	
	/*public void aproximationAdd(){
		for(int i=0; i<this.uniqueTable[0].size(); i++){
			
			double c=this.uniqueTable[0].get(i).value;
			int j=0;
			long comp =1000000000;
			long temp=1000000000;
			//comp=comp*temp;
			if(c>0){
				while(c<comp){
					c=c*10;
					j++;
				}
				temp=Math.round(c);
				c=(double)temp*(Math.pow(10, -j));
			}
			this.uniqueTable[0].get(i).value=c;
		}

		
	}*/
	
	//fusion de deux ut (de bas en haut pour l'optimisation
	//todo : que les variables soient les memes au départ
	public void fusion(UniqueHashTable[] uhtab){
		ArrayList<NodeDD> tempArr=new ArrayList<NodeDD>();
		NodeDD temp;
		//0			
		
		for(int i=1; i<uhtab.length; i++){
			
//				long start= System.nanoTime();

			
			eN=uhtab[i].uniqueHashTable[0].values().iterator();
			temp=eN.next();
			
			this.get(0).get(0).fusionSansCopie(temp);

			for(int j=uhtab[i].uniqueHashTable.length-1; j>1; j--){
				tempArr.clear();
				eN=uhtab[i].uniqueHashTable[j].values().iterator();

				while(eN.hasNext()){
					tempArr.add(eN.next());
				}
				for(int k=0; k<tempArr.size(); k++){
					if(uniqueHashTable[j].containsKey(tempArr.get(k))){
						temp=(NodeDD)uniqueHashTable[j].get(tempArr.get(k));
						temp.fusionSansCopie(tempArr.get(k));
					}else{
						this.add(tempArr.get(k));
					}
				}
			}


			temp=this.get(1).get(0);
			this.removeFromTable(temp);
			temp.fusionFauxOu(uhtab[i].get(1).get(0));
			add(temp);
			//System.out.println("regroupage:"+i+"/"+uhtab.length+":"+ (double)((System.nanoTime()-start) /1000000)/1000+ "s");
		}
	}
	

	public NodeDD getWithIndex(int ind){
		NodeDD temp;

		for(int i=0; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				temp=eN.next();
				if (temp.id==ind)
					return temp;
			}
		}
		System.out.println("@ut err, pas de neud trouve a cet index");
		return null;
	}
	
/*		public void addToInt(){
			NodeDD temp;
			
			eN=uniqueHashTable[0].values().iterator();
			while(eN.hasNext()){
				temp=eN.next();
				temp.setVal((int)Math.round(temp.getVal()));
			}
		}*/
	
	public void normaliser(){	
	/*	//preconditions
		eN=uniqueHashTable[1].values().iterator();
		if(eN.next().kids.get(0).s.printstr().compareTo("Spt")==0){
			System.out.println("Spt : init a 0");
			eN=uniqueHashTable[uniqueHashTable.length-1].values().iterator();

		}*/
		
		ArrayList<NodeDD> list=new ArrayList<NodeDD>();
		
		for(int i=(uniqueHashTable.length-1); i>0; i--){
			
			list.clear();
			
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}
			for(int j=0; j<list.size(); j++)
				this.normaliseReduit(list.get(j));

		}
	}
	
	//on normalise tout au dessus de var (var inclue)
	public void normaliser(int var){	
	/*	//preconditions
		eN=uniqueHashTable[1].values().iterator();
		if(eN.next().kids.get(0).s.printstr().compareTo("Spt")==0){
			System.out.println("Spt : init a 0");
			eN=uniqueHashTable[uniqueHashTable.length-1].values().iterator();

		}*/
		
		
		for(int i=(var); i>0; i--){

			if(i==1)
				System.out.println();
			
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				this.normaliseReduit(eN.next());
			}
		}
	}
	
	
	public void detail(){
//			int cpt=0;
		NodeDD n;
		for(int i=1; i<uniqueHashTable.length; i++){
			System.out.println(i+"---------------------"+uniqueHashTable[i].size());
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				n=eN.next();
				System.out.println(n.kids.get(0).s.toString() + " " + n.cpt + " " + n.variable.name+" id:"+n.id+" ksize:"+n.kids.size()+" hash:"+n.hashCode());//+" kbott:"+n.kids.get(0).bottom);
										
			}
		}
	}
	
	public void detect(){
		for(int i=0; i<uniqueHashTable.length; i++)
			detect(i);
	}
	
	public void detect(int pos){
		NodeDD n;
		eN=uniqueHashTable[pos].values().iterator();
		while(eN.hasNext()){
			n=eN.next();
			if(this.recherche(n)==null){
				System.out.println("@uht/detect : pos="+pos+" nID="+n.id+"<----------------------------");
			}
		}
	}
	
	//test le type de structure de tout arc pour s'assurer que tous sont de meme type.
	public void testStructureUniforme(int i){
		int Sp=0, St=0, S=0, Spt=0, Structure=0;
		String str;
		NodeDD n;

			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				n=eN.next();
				
				for(int j=0; j<n.kids.size(); j++){
					str=n.kids.get(j).s.printstr();
					
					if(str.compareTo("Structure")==0)
						Structure++;
					if(str.compareTo("Sp")==0)
						Sp++;
					if(str.compareTo("St")==0)
						St++;
					if(str.compareTo("Spt")==0)
						Spt++;
					if(str.compareTo("S")==0)
						S++;
					}
				}
			
		System.out.println(i+":Structure=" + Structure+" Sp=" + Sp+" St=" + St+" S=" +S+" Spt=" +Spt );
	}
	
	//test le type de structure de tout arc pour s'assurer que tous sont de meme type.
	public void testStructureUniforme(){
		int Sp=0, St=0, S=0, Spt=0, Structure=0;
		String str;
		NodeDD n;

		//init
		int i;
		for(i=1; i<uniqueHashTable.length; i++){
			if(!uniqueHashTable[i].isEmpty())
				break;
		}
		eN=uniqueHashTable[i].values().iterator();
			
		n=eN.next();
		if(n.fathers.size()==1){
			str=n.fathers.get(0).s.printstr();
			if(str.compareTo("Structure")==0)
				Structure++;
			if(str.compareTo("Sp")==0)
				Sp++;
			if(str.compareTo("St")==0)
				St++;
			if(str.compareTo("Spt")==0)
				Spt++;
			if(str.compareTo("S")==0)
				S++;
		}
		
		//rest
		for(i=1; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				n=eN.next();
				
				for(int j=0; j<n.kids.size(); j++){
					str=n.kids.get(j).s.printstr();
					
					if(str.compareTo("Structure")==0)
						Structure++;
					if(str.compareTo("Sp")==0)
						Sp++;
					if(str.compareTo("St")==0)
						St++;
					if(str.compareTo("Spt")==0)
						Spt++;
					if(str.compareTo("S")==0)
						S++;
					}
				}
			}
		System.out.println("Structure=" + Structure+" Sp=" + Sp+" St=" + St+" S=" +S+" Spt=" +Spt );
	}
	
	//test le type de structure de tout arc pour s'assurer que tous sont de meme type.
	public void testStructureUniformefathers(){
		int Sp=0, St=0, S=0, Spt=0, Structure=0;
		String str;
		NodeDD n;

		//rest
		for(int i=0; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				n=eN.next();
				
				for(int j=0; j<n.fathers.size(); j++){
					str=n.fathers.get(j).s.printstr();
					
					if(str.compareTo("Structure")==0)
						Structure++;
					if(str.compareTo("Sp")==0)
						Sp++;
					if(str.compareTo("St")==0)
						St++;
					if(str.compareTo("Spt")==0)
						Spt++;
					if(str.compareTo("S")==0)
						S++;
					}
				}
			}
		System.out.println("StructureF=" + Structure+" Sp=" + Sp+" St=" + St+" S=" +S+" Spt=" +Spt );
	}
	
	public void testIDNoeudEnDouble(){
		ArrayList<NodeDD> list;
		boolean test =false;
		for(int i=0; i<uniqueHashTable.length; i++){
			list=new ArrayList<NodeDD>();
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				list.add(eN.next());
			}
			for(int j=0; j<list.size(); j++){
				for(int k=j+1; k<list.size(); k++){
					if(list.get(j).id==list.get(k).id){
						test=true;
					}
				}
			}
		}
		if(test)
			System.out.println("id en double");
	}
	
	//permet de supprimer des varialbes qui ne seront pas utilise au cours de la compilation. on les rajoutera plus tard eventuellement.
	//cette fonction doit etre lancee juste apres la creation du sldd blanc
	//on regarde les contraintes de s a e (incluses, les contraintes commencent a 1)
	public void ellagage(LecteurXML xml, int s, int e){
		Var v;
		for (int i=0; i<nbVariables; i++){
			v=this.get(i+1).get(0).variable;
			if(!xml.isVariableUtile(v, s, e)){
				this.courtcircuit(this.get(i+1).get(0));
			}
		}
	}
	public void ellagage(LecteurXML xml){
		Var v;
		for (int i=0; i<nbVariables; i++){
			v=this.get(i+1).get(0).variable;
			if(!xml.isVariableUtile(v)){
				this.courtcircuit(this.get(i+1).get(0));
			}
		}
	}
	
	
	public void reintroductionNoeuds(ArrayList<Var> vars, boolean flag_plus, boolean flag_mult){
		ArrayList<NodeDD> listNode=null;
		NodeDD savefils;
		
		int diff;
		for (int i=1; i<=nbVariables; i++){
			listNode=(this.get(i));
			
			for(int j=0; j<listNode.size(); j++){
				for(int k=0; k<listNode.get(j).kids.size(); k++){
					if(listNode.get(j).kids.get(k).bottom==0){
						if(!listNode.get(j).kids.get(k).fils.isLeaf())
							diff=listNode.get(j).kids.get(k).fils.variable.pos-listNode.get(j).variable.pos;
						else{
							diff=nbVariables+1-listNode.get(j).variable.pos;
							System.out.println("diff="+diff);
						}
						if(diff<1)
							System.out.println("@uht : ordre non respecte");
						if(diff>1){
							System.out.println("@uht : rajout");
							//on doit rajouter un noeud intermediaire
							savefils=listNode.get(j).kids.get(k).fils;

							this.removeFromTable(listNode.get(j));
							
							NodeDD nouv=new NodeDD(vars.get(i));
							listNode.get(j).kids.get(k).changerFils(nouv);
							for(int l=0; l<nouv.variable.domain; l++){
								if(flag_plus)
									if(flag_mult)		//aadd
										new Arc(nouv, savefils, l, new Spt());
									else				//sldd+
										new Arc(nouv, savefils, l, new Sp());
								else
									if(flag_mult)		//sldd*
										new Arc(nouv, savefils, l, new St());
									else
										new Arc(nouv, savefils, l, new S());
							}
							
							this.add(nouv);
							this.add(listNode.get(j));
						}
					}
				}
			}
		}
	}
	
	public void GIC(){
		for(int i=1; i<uniqueHashTable.length; i++){
			eN=uniqueHashTable[i].values().iterator();
			while(eN.hasNext()){
				if(eN.next().GIC())
					break;
			}
		}
    }
	
	public void GIC(int i){
		eN=uniqueHashTable[i].values().iterator();
		while(eN.hasNext()){
			if(eN.next().GIC())
				break;
		}
    }
	
}
