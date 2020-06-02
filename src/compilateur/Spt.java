package compilateur;

import java.util.ArrayList;

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

//Spt double
public class Spt extends Structure {

	public double q;
	public double f;
	
	public Spt(double plus, double time){
		super();
		q=plus;
		f=time;
	}
	
	public Spt(){
		super();
		q=0;
		f=1;
	}
	
	public Structure copie(){
		Spt s=new Spt(this.q, this.f);
		return s;
	}
	
	public void operation(Structure str){
		q=(q+(f*((Spt)str).q));
		f=(f*((Spt)str).f);
	}
	
	public void initOperation(Structure str, Structure str2){
		System.out.println("la fonction initOperation n'est pas realisable avec une structure de type Spt");
	}
	
	public Spt normaliseInf(ArrayList<Structure> liste){
		double min=((Spt)liste.get(0)).q;
		double max=((Spt)liste.get(0)).q + ((Spt)liste.get(0)).f;
		for(int i=1; i<liste.size(); i++){
			if(((Spt)liste.get(i)).q<min){
				min=((Spt)liste.get(i)).q;
			}
			if((((Spt)liste.get(i)).q+((Spt)liste.get(i)).f)>max){
				max=((Spt)liste.get(i)).q+((Spt)liste.get(i)).f;
			}
		}
		max-=min;
		if(max!=0){
			for(int i=0; i<liste.size(); i++){
				((Spt)liste.get(i)).q-=min;
				((Spt)liste.get(i)).q=((Spt)liste.get(i)).q/max;
				((Spt)liste.get(i)).f=((Spt)liste.get(i)).f/max;
			}
		}else{
			for(int i=0; i<liste.size(); i++){
				((Spt)liste.get(i)).q=1;
				((Spt)liste.get(i)).f=0;
			}
		}
		
		return new Spt(min, max);
	}
	
	public void normaliseSup(ArrayList<Structure>liste, Structure remonte){
		for(int i=0; i<liste.size(); i++){
			((Spt)liste.get(i)).q+=((Spt)remonte).q*((Spt)liste.get(i)).f;
			((Spt)liste.get(i)).f*=((Spt)remonte).f;
		}
	}
	
	public void normaliseSup(Structure str, Structure remonte){
		((Spt)str).q+=((Spt)remonte).q*((Spt)str).f;
		((Spt)str).f*=((Spt)remonte).f;
	}
	
	public boolean isNeutre(){
		return (q==0 && f==1);
	}
	
	// c'est un fake, pour ouvrir la route. tout est calculé dans le max (on ne peut calculer que du min/mpax en partant du bas
	//on sait que le pere (comp 2) n'est pas a -1
	public boolean min(Structure comp1, Structure comp2){
		this.q=1;		// 1/1 = max
		return false;
	}
	

	//min et max
	//1=max/min 2=arc
	public boolean max(Structure comp1, Structure comp2){
		if(comp2!=null){
			if(q!=-1){
				if( this.q < (((Spt)comp2).q+( ((Spt)comp2).f * ((Spt)comp1).q )) ){
					this.q = (((Spt)comp2).q+( ((Spt)comp2).f * ((Spt)comp1).q ));
				}
			}else{
				//System.out.println("1 "+((Spt)comp2).q+" "+( ((Spt)comp2).f +" "+ ((Spt)comp1).q ));
				this.q=(((Spt)comp2).q+( ((Spt)comp2).f * ((Spt)comp1).q ));
			}
		}else{
			//this.q=((Spt)comp1).q.add(((Spt)comp1).f);
			this.q=((Spt)comp1).q;
		}
		
		return false;
	}
	
	
	//seul fonction exclusive a spt
	public void minvrai(Structure comp1, Structure comp2){
		if(comp2!=null){
			if( this.q > (((Spt)comp2).q+( ((Spt)comp2).f * ((Spt)comp1).q )) ){
				this.q = (((Spt)comp2).q+( ((Spt)comp2).f * ((Spt)comp1).q ));
			}
		}else{
			//this.q=((Spt)comp1).q.add(((Spt)comp1).f);
			this.q=((Spt)comp1).q;
		}
	}
	

	public boolean isabsorbant(){
		return false;
	}
	
	public void toNeutre(){
		q=0;
		f=1;
	}
	
	public String toDot(){
		return "\"<" + Double.toString(q) + "," + Double.toString(f) +">\"";
	}
	
	public String toTxt(){
		return "\"<" + Double.toString(q) + "," + Double.toString(f) +">\"";
	}
	
	public int hashCode(){
		int valf, valq, hash;
		valf=(int)Math.round((float)(f*10000));
		valq=(int)Math.round((float)(q*10000));
		
		hash=valf+Integer.rotateLeft(valq, 2);
		return hash;
	}

	
	public boolean equals(Structure comp){
		//double errf, errq;
		//errf=Math.abs(this.f-((Spt)comp).f);
		//errq=Math.abs(this.q-((Spt)comp).q);
		//if(errf+errq<0.001)
		//	return true;
		//else
		//	return false;
		if(this.f==((Spt)comp).f && this.q==((Spt)comp).q)
			return true;
		else
			return false;
	}
	
	public String printstr(){
		return "Spt";
	}
	
	public double getVal(){
		System.out.println("@ Spt::getVal, attention info incomplette");
		return q+f;
	}
	
	public boolean inaccessible(){
		return q==-1;
	}
	
	public void rendreInaccessible(){
		q=-1;
	}
	
	//val que de q
	public double getvaldouble(){
		return q;
	}
	
	public void multQetF(double a){
		q=q*a;
		f=f*a;
	}
	
	public void addQparF(Structure s){
		q+=((Spt)s).q/((Spt)s).f;
	}
	
}

///////////////////////////////////////////////////////////////////////////////
//Spt fraction
/*
public class Spt extends Structure {

	Frac q;
	Frac f;
	
	public Spt(double plus, double time){
		super();
		//q=new Frac((long)plus, 1);
		//f=new Frac((long)time, 1);
		q=new Frac((long)(plus*10), 10);
		f=new Frac((long)(time*10), 10);
	}
	
	public Spt(long qc, long qd, long fc, long fd){
		super();
		q=new Frac(qc, qd);
		f=new Frac(fc, fd);
	}
	
	public Spt(){
		super();
		q=new Frac(0, 1);
		f=new Frac(1, 1);
	}
	
	
	
	public Structure copie(){
		Spt s=new Spt(q.n, q.d, f.n, f.d);
		return s;
	}
	
	public void operation(Structure str){
		//q=q+(f*str.q)
		//q
		q=q.add(f.mult( ((Spt)str).q ));
		//qc=fc*((Spt)str).qc*qd+qc*fd*((Spt)str).qd;
		//qd=fd*((Spt)str).qd*qd;
		
		//q=(q+(f*((Spt)str).q));
		
		//f=f*str.f
		//fc=(fc*((Spt)str).fc);
		//fd=(fd*((Spt)str).fd);
		f=f.mult( ((Spt)str).f );

	}
	
	public void initOperation(Structure str, Structure str2){
		System.out.println("la fonction initOperation n'est pas realisable avec une structure de type Spt");
	}
	
	public Spt normaliseInf(ArrayList<Structure> liste){
		Frac min=((Spt)liste.get(0)).q.copie();
		Frac max=((Spt)liste.get(0)).q.add(((Spt)liste.get(0)).f);
		for(int i=1; i<liste.size(); i++){
			if(((Spt)liste.get(i)).q.val()<min.val()){
				min=((Spt)liste.get(i)).q.copie();
			}
			if((((Spt)liste.get(i)).q.add(((Spt)liste.get(i)).f)).val()>max.val()){
				max=((Spt)liste.get(i)).q.add(((Spt)liste.get(i)).f);
			}
		}
		max=max.sous(min);
		for(int i=0; i<liste.size(); i++){
			((Spt)liste.get(i)).q=((Spt)liste.get(i)).q.sous(min);
			((Spt)liste.get(i)).q=((Spt)liste.get(i)).q.div(max);
			((Spt)liste.get(i)).f=((Spt)liste.get(i)).f.div(max);
		}
		
		return new Spt(min.n, min.d, max.n, max.d);
	}
	
	public void normaliseSup(ArrayList<Structure>liste, Structure remonte){
		for(int i=0; i<liste.size(); i++){
			((Spt)liste.get(i)).q=((Spt)liste.get(i)).q.add(((Spt)remonte).q.mult(((Spt)liste.get(i)).f));
			//((Spt)liste.get(i)).q+=((Spt)remonte).q*((Spt)liste.get(i)).f;
			((Spt)liste.get(i)).f=((Spt)liste.get(i)).f.mult(((Spt)remonte).f);
		}
	}
	
	public void normaliseSup(Structure str, Structure remonte){
		((Spt)str).q=((Spt)str).q.add(((Spt)remonte).q.mult(((Spt)str).f));
		((Spt)str).f=((Spt)str).f.mult(((Spt)remonte).f);
	}
	
	public boolean isNeutre(){
		return (q.n==0 && f.val()==1);
	}
	
	// c'est un fake, pour ouvrir la route. tout est calculé dans le max (on ne peut calculer que du min/mpax en partant du bas
	//on sait que le pere (comp 2) n'est pas a -1
	public boolean min(Structure comp1, Structure comp2){
		this.q.n=1;		// 1/1 = max
		return false;
	}
	
	//min et max
	//1=max/min 2=arc
	public boolean max(Structure comp1, Structure comp2){
		if(comp2!=null){
			if(q.n!=-1){
				this.q = this.q.max( ((Spt)comp2).q.add( ((Spt)comp2).f.mult( ((Spt)comp1).q )));
			}else{
				this.q=((Spt)comp2).q.add( ((Spt)comp2).f.mult( ((Spt)comp1).q ));
			}
		}else{
			//this.q=((Spt)comp1).q.add(((Spt)comp1).f);
			this.q=((Spt)comp1).q.copie();
		}
		System.out.println("return boolean de Spt.min a revoir");
		return false;
	}
	
	//seul fonction exclusive a spt
	public boolean minvrai(Structure comp1, Structure comp2){
		if(comp2!=null){
			this.q = this.q.min( ((Spt)comp2).q.add( ((Spt)comp2).f.mult( ((Spt)comp1).q )));
		}else{
			//this.q=((Spt)comp1).q.add(((Spt)comp1).f);
			this.q=((Spt)comp1).q.copie();
		}
		System.out.println("return boolean de Spt.min a revoir");
		return false;
	}
	

	public boolean isabsorbant(){
		return false;
	}
	
	public void toNeutre(){
		q.n=0;
		q.d=1;
		f.n=1;
		f.d=1;
	}
	
	public String toDot(){
		return "\"<" + q.n+"/"+q.d + "," + f.n+"/"+f.d +">\"";
	}
	
	public String toTxt(){
		return "\"<" + Double.toString(q.val()) + "," + Double.toString(f.val()) +">\"";
	}
	
	public int hashCode(){
		int valf, valq, hash;
		valf=(int)(f.n+f.d);
		valq=(int)(q.n+q.d)*100;
		
		hash=valf+Integer.rotateLeft(valq, 2);
		return hash;
	}
	
	public boolean equals(Structure comp){
		if( q.equal(((Spt)comp).q) && f.equal(((Spt)comp).f) )
			return true;
		else
			return false;
	}
	
	public String printstr(){
		return "Spt";
	}
	
	public double getVal(){
		System.out.println("@ Spt::getVal, attention info incomplette");
		return q.val()+f.val();
	}
	
	public boolean inaccessible(){
		return q.val()==-1;
	}
	
	public void rendreInaccessible(){
		q.n=-1;
		q.d=1;
		f.n=1;
		f.d=1;
	}
	
	//val que de q
	public double getvaldouble(){
		return q.val();
	}
	
}*/
