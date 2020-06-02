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

public class Arc {
	// Attributs

	protected NodeDD pere;
	protected NodeDD fils;
	protected int pos;			//la valeure de la variable du pere qui mene a cette arc
	protected Structure s;
//fils	protected double val;						//+
//fils	protected double coef;
	//protected Fraction fracMult;		//* (numerateur/denominateur)	pour AADD
	//protected Fraction fracAddi;		//* (numerateur/denominateur)	pour AADD
	protected boolean actif=true;
	
	protected int bottom=0;

	
		// constructeur
	
	//si pas de bottom, l'arc est actif
/*	public Arc(NodeDD pe, NodeDD f, int po){		//note, si on rentre un int la ou on demande un double, ca marche :)
		//init arc
		pere=pe;
		fils=f;
		pos=po;
		bottom=0;
		
		s=new S();

		this.attribuerPereFils();	
	}
	
	public Arc(NodeDD pe, NodeDD f, int po, boolean bo){		//note, si on rentre un int la ou on demande un double, ca marche :)
		//init arc
		pere=pe;
		fils=f;
		pos=po;
		if(bo)
			bottom=1;
		else
			bottom=0;
		
		s=new S();

		this.attribuerPereFils();	
	}*/
	
	//s=copie de str
	public Arc(NodeDD pe, NodeDD f, int po, boolean bo, Structure str){		//note, si on rentre un int la ou on demande un double, ca marche :)
		//init arc
		pere=pe;
		fils=f;
		pos=po;
		if(bo){
			bottom=1;
			fils=null;
		}
		else
			bottom=0;
		
		if(str.isabsorbant()){
			bottom++;
			System.out.println("plop");
		}
		//else
			s=str.copie();

		this.attribuerPereFils();	
	}
	
	//s=str
	public Arc(NodeDD pe, NodeDD f, int po, Structure str){		//note, si on rentre un int la ou on demande un double, ca marche :)
		//init arc
		pere=pe;
		fils=f;
		pos=po;
		bottom=0;
		
		if(str.isabsorbant()){
			bottom++;
			System.out.println("plop2");
		}
		//else
			s=str;

		this.attribuerPereFils();	
	}
	
/*	public Arc(NodeDD pe, NodeDD f, int po, boolean bo, int plus){		//note, si on rentre un int la ou on demande un double, ca marche :)
		//init arc
		pere=pe;
		fils=f;
		pos=po;
		if(bo)
			bottom=1;
		else
			bottom=0;
		
		s=new Sp(plus);

		this.attribuerPereFils();	
	}*/
	
/*	public Arc(NodeDD pe, NodeDD f, int po, boolean bo, double time){		//note, si on rentre un int la ou on demande un double, ca marche :)
		//init arc
		pere=pe;
		fils=f;
		pos=po;
		if(bo)
			bottom=1;
		else
			bottom=0;
		
		s=new St(time);

		this.attribuerPereFils();	
	}*/
	
/*	public Arc(NodeDD pe, NodeDD f, int po, boolean bo, double plus, double time){		//note, si on rentre un int la ou on demande un double, ca marche :)
		//init arc
		pere=pe;
		fils=f;
		pos=po;
		if(bo)
			bottom=1;
		else
			bottom=0;
		
		s=new Spt(plus, time);

		this.attribuerPereFils();	
	}*/
	
/*	public Arc(NodeDD pe, NodeDD f, int po, Structure str){		//note, si on rentre un int la ou on demande un double, ca marche :)
		//init arc
		pere=pe;
		fils=f;
		pos=po;
		bottom=0;
		
		s=str.copie();

		this.attribuerPereFils();	
	}*/
	
/*	public Arc(NodeDD pe, NodeDD f, int po, int plus){		//note, si on rentre un int la ou on demande un double, ca marche :)
		//init arc
		pere=pe;
		fils=f;
		pos=po;
		bottom=0;
		
		s=new Sp(plus);

		this.attribuerPereFils();	
	}*/
	
/*	public Arc(NodeDD pe, NodeDD f, int po, double time){		//note, si on rentre un int la ou on demande un double, ca marche :)
		//init arc
		pere=pe;
		fils=f;
		pos=po;
		bottom=0;
		
		s=new St(time);

		this.attribuerPereFils();	
	}
	
	public Arc(NodeDD pe, NodeDD f, int po, double plus, double time){		//note, si on rentre un int la ou on demande un double, ca marche :)
		//init arc
		pere=pe;
		fils=f;
		pos=po;
		bottom=0;
		
		s=new Spt(plus, time);

		this.attribuerPereFils();	
	}*/
	
	public Arc(NodeDD f, boolean plus){
		pere=null;
		fils=f;
		pos=0;
		if(plus)
			s=new Sp();
		else
			s=new St();

		fils.fathers.add(this);
	}	
	
	public Arc(NodeDD f, boolean plus, double val){
		pere=null;
		fils=f;
		pos=0;
		if(plus)
			s=new Sp((int)val);
		else{
			s=new St(val);
			if(val==0){
				bottom++;
				System.out.println("plop3");
			}
		}

		fils.fathers.add(this);
	}
	
	public Arc(NodeDD f, Structure str){
		pere=null;
		fils=f;
		pos=0;

		s=str.copie();
		
		fils.fathers.add(this);
	}	
	
/*	public Arc(NodeDD f, int plus){
		pere=null;
		fils=f;
		pos=0;
		s=new Sp(plus);

		fils.fathers.add(this);
	}*/
	
/*	public Arc(NodeDD f, double time){
		pere=null;
		fils=f;
		pos=0;
		s=new St(time);
		
		fils.fathers.add(this);
	}*/
	
	
	//destructeur
		public void remove(){
			if(pere!=null)			//cas first
				pere.kids.remove(pos);
			
			if(fils!=null){	
				fils.fathers.remove(fils.fathers.indexOf(this));
			}
		}
	
	
	//methodes
	
	protected void attribuerPereFils(){
		//2 cas de figure.
		
		if(pere.kids.size()>pos){			// 1 :l'arc existait deja (ex, si pos=1, si size=2, alors les valeurs 0 et 1 sont deja attribues
			pere.kids.get(pos).remove();	// supp propre de l'arc
			pere.kids.add(pos, this);		// supp chez le pere
			if(fils!=null)
				fils.fathers.add(this);
		}
		else{							//2 : l'arc n'existait pas
			for(int i=pere.kids.size(); i<pos; i++){	// et en ten plus il faut en creer des faux avant 
				System.out.println("@Arc : cas impossible?");
				//new Arc(pere, pere, i);	//on cree un faux arc
			}
			pere.kids.add(this);
			if(fils!=null)
				fils.fathers.add(this);
			
		}
	}
	
	//changer le fils d'un arc.
	public void changerFils(NodeDD f){
		if(this.fils!=f){
			if(fils!=null)
				fils.fathers.remove(fils.fathers.indexOf(this));	//on le supprime de la liste de l'ancien fils
			
			fils=f;												//on met le nouveau
			if(fils!=null)
				fils.fathers.add(this);								//on le dit au nouveau
		}
	}
	
	//opt
	//ici on ne s'enleve pas de la liste du fils. gain de temps enorme lors du chargement du .dot
/*	public void changerFilsRapide(NodeDD f){
		if(this.fils!=f){
			fils=f;											//on met le nouveau
			fils.fathers.add(this);								//on le dit au nouveau
		}
	}*/
	
	//remplace un arc vers bottom par cet arc. l'ancien pere se recupere un bottom
	//en fait c'est un switch
/*	public void changerPere(Arc a){
		NodeDD save;
		int pos;
		save=this.pere;
		pos=this.pos;
		
		this.pere=a.pere;
		this.pere.kids.set(pos, this);
		
		a.pere=save;
		save.kids.set(pos, a);
	}*/
	
	//copie l'arc this en lui donnant un nouveau pere (l'arc original existe toujours)
	public void copieNouveauPere(NodeDD p){
		new Arc(p, this.fils, this.pos, (this.bottom!=0), this.s);
	}


/*	public double antiaproximation(double c){
		int i=0;
		long comp=100000000;
		long temp=0;
		//comp=comp*temp;
		if(c>0){
			while(c<comp){
				c=c*10;
				i++;
			}
			temp=Math.round(c);
			c=(double)temp*(Math.pow(10, -i));
		}
		return c;
		
	}*/
	
	
	//accesseurs
	public Structure getS() {
		return s;
	}

	public void setS(Structure str) {
		if(str.isabsorbant())
			bottom++;
		//else
			this.s = str;
	}
	
	public void operationS(Structure str) {
		
		if(str.isabsorbant())
			bottom++;
		else
			this.s.operation(str);
	}
	
	
	//dans certain ca la normalisation du noeud(fils) est deja faite alors que l'on lui ajoute cette arc apres, il faut donc quand meme remonter la valeur susnommee !
	public void operationValuerARemonter(){
		if(this.fils.aRemonter!=null &&  !this.fils.aRemonter.isNeutre()){
			this.s.operation(this.fils.aRemonter);
		}
	}
	
	//dans certain ca la normalisation du noeud(fils) est deja faite alors que l'on lui ajoute cette arc apres, il faut donc quand meme remonter la valeur susnommee !
	//vesrion corrigee du bug, cette valeur peut varier en fonction du tuple que l'on avait a remonter et que l'on a remonté. donc on prend la valeur qui a ete remontee sur le bon tuple
	public void operationValuerARemonter(NodeDD n){
		if(n.aRemonter!=null &&  !n.aRemonter.isNeutre()){
			this.s.operation(n.aRemonter);
		}
	}
	
/*	public void divVal(double p) {
		coef=antiaproximation(coef);
		if(p!=0)
			this.val = this.val/p;
		else{
			if(val!=0)
				System.out.println("err div Arc  : p =0 et val=" + val);
		}
		val=antiaproximation(val);

	}*/

	//permet de desactiver un arc sans le supprimer
	// true -> arc actif ; false -> arc inactif
	public void activer(boolean a){
		this.actif=a;
	}
	
	//afficheurs			//n'affiche pas les aadds
	public String toDot(){
		String s="";
		
		//if(!this.fils.isLeaf() || this.s.printstr().compareTo("S")!=0){		//toujours sauf qui pointe vers la feuille d'un add
						
			if(bottom==0){
				if(pere!=null){			//si c'est pas le premier arc
					if(this.s.printstr().compareTo("S")==0 && this.fils.isLeaf()){		//si on est au dernier noeud d'un add
						s+="n" + pere.id + "_" + ((S)this.s).last.toDot() + " -> n" + fils.id + " [pos="+pos;
					}else{																//cas normal
						s+="n" + pere.id + " -> n" + fils.id + " [pos="+pos;
					}
					if(!this.s.isNeutre())
						s+=", label=" + this.s.toDot();
					if(pos==0)
						s+= ", style=dotted";
					if(pos==(this.pere.variable.domain-1))
						s+= ", style=dashed";
					s+="];\n";
				}else{					// si c'est le premier arc
					s+="nada -> n" + fils.id + " [";
					if(!this.s.isNeutre())
						s+="label=" + this.s.toDot();
					s+="];\n";
					s+="nada [label=\" \",shape=plaintext];\n";
				}
			}	
		//}
		return s;
	}
	
	
	public String toDot2(){
		String s="";
		
		if(bottom==0){
			if(pere!=null){			//si c'est pas le premier arc
				s+="n" + pere.id + " -> n" + fils.id + " [pos="+pos;

				
				s+= ", style=dashed";
				s+="];\n";
			}else{					// si c'est le premier arc
				s+="nada -> n" + fils.id;
				if(!this.s.isNeutre())
					s+=" [label=" + this.s.toDot() + "]";
				s+="\n";
				s+="nada [label=\" \",shape=plaintext];\n";
			}
		}	
		return s;
	}
	public int hashCode(){
		int hash=0;
		if(bottom==0){
			hash+=this.fils.id;
			hash+=this.s.hashCode();
		}
		return hash;
	}
	
	public boolean equals(Arc comp){		
		if((this.bottom==0) == (comp.bottom==0)){
			if(this.bottom!=0){			//les deux sont a bottom
				return true;
			}
			else{						//cas normal
				if (this.fils.id==comp.fils.id &&
					this.s.equals(comp.s))
					return true;
			}
		}
		return false;
	}

}
