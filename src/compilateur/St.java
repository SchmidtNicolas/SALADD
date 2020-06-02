package compilateur;

import java.math.BigDecimal;
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

public class St extends Structure {

	private double val;
	
	public St(double v){
		super();
		val=v;
	}
	
	public St(){
		super();
		val=1;
	}
	
	public Structure copie(){
		St s=new St(this.val);
		return s;
	}
	
	public void operation(Structure str){
		val*=((St)str).val;
	}
	
	public void initOperation(Structure str, Structure str2){
		val=((St)str).val*((St)str2).val;
	}
	
	public St normaliseInf(ArrayList<Structure> liste){
		double max=((St)liste.get(0)).val;
		
		for(int i=1; i<liste.size(); i++){
			if(((St)liste.get(i)).val>max){
				max=((St)liste.get(i)).val;
			}
		}
		for(int i=0; i<liste.size(); i++){
			((St)liste.get(i)).val=((St)liste.get(i)).val/max;
		}
		
		return new St(max);
	}
	
	public void normaliseSup(ArrayList<Structure>liste, Structure re){
		St remonte=(St)re;
		
		for(int i=0; i<liste.size(); i++)
			((St)liste.get(i)).val*=remonte.val;
	}
	
	public void normaliseSup(Structure str, Structure remonte){
		((St)str).val*=((St)remonte).val;
	}
	
	public boolean isNeutre(){
		return val==1;
	}
	
	//return true si min a change
	public boolean min(Structure comp1, Structure comp2){
		if(comp2!=null){
			if(val!=-1){
				if(this.val>((St)comp1).val * ((St)comp2).val){
					this.val=((St)comp1).val * ((St)comp2).val;
					return true;
				}
			}else{
				this.val=((St)comp1).val * ((St)comp2).val;
				return true;
			}
		}else{
			this.val=((St)comp1).val;
			return true;
		}
		return false;
	}
	
	public boolean max(Structure comp1, Structure comp2){
		if(comp2!=null){
			if(val!=-1){
				if(this.val<((St)comp1).val * ((St)comp2).val){
					this.val=((St)comp1).val * ((St)comp2).val;
					return true;
				}	
			}else{
				this.val=((St)comp1).val * ((St)comp2).val;
				return true;
			}
		}else{
			this.val=((St)comp1).val;
			return true;
		}
		return false;
	}
	
	public boolean isabsorbant(){
		return val==0;
	}
	
	public void toNeutre(){
		val=1;
	}
	
	public String toDot(){
		if(val==1)
			return "";
		else
			return (new BigDecimal(val)).toPlainString();
//			return ""+Double.toString(val);
	}
	
	public String toTxt(){
		return ""+Double.toString(val);
	}
	
	public boolean equals(St comp){
		System.out.println("c");
			return true;
	}
	
	
	public int hashCode(){
		int hash;
		hash=(int) Math.round(val*100000);
		return hash;
	}
	

	public boolean equals(Structure comp){
		
		double diff=Math.abs((this.val-((St)comp).val));
		if (diff<0.000001)
			return true;
		else
			return false;
	}
	
	public String printstr(){
		return "St";
	}
	
	public boolean inaccessible(){
		return val==-1;
	}
	
	public void rendreInaccessible(){
		val=-1;
	}
	
	public double getvaldouble(){
		return val;
	}
}

/*********************************/
/*
package br4cp;

import java.util.ArrayList;


public class St extends Structure {

	Frac val;
	
	public St(double v){
		super();
		val=new Frac(Math.round(v*1000000), 1000000);
	}
	
	public St(Frac v){
		super();
		val=new Frac(v.n, v.d);
	}
	
	public St(){
		super();
		val=new Frac(1, 1);
	}
	
	public Structure copie(){
		St s=new St(this.val);
		return s;
	}
	
	public void operation(Structure str){
		val=val.mult(((St)str).val);
	}
	
	public void initOperation(Structure str, Structure str2){
		val=((St)str).val.mult(((St)str2).val);
	}
	
	public St normaliseInf(ArrayList<Structure> liste){
		Frac max=((St)liste.get(0)).val;
		
		for(int i=1; i<liste.size(); i++){
			max=max.max(((St)liste.get(i)).val);
		}
		for(int i=0; i<liste.size(); i++){
			((St)liste.get(i)).val=((St)liste.get(i)).val.div(max);
		}
		
		return new St(max);
	}
	
	public void normaliseSup(ArrayList<Structure>liste, Structure re){
		St remonte=(St)re;
		
		for(int i=0; i<liste.size(); i++)
			((St)liste.get(i)).val=((St)liste.get(i)).val.mult(remonte.val);
	}
	
	public void normaliseSup(Structure str, Structure remonte){
		((St)str).val=((St)str).val.mult(((St)remonte).val);
	}
	
	public boolean isNeutre(){
		return val.val()==1;
	}
	
	//return true si min a change
	public boolean min(Structure comp1, Structure comp2){
		if(comp2!=null){
			if(val.val()!=-1){
				if(this.val.val()>((St)comp1).val.mult(((St)comp2).val).val()){
					this.val=((St)comp1).val.mult(((St)comp2).val);
					return true;
				}
			}else{
				this.val=((St)comp1).val.mult(((St)comp2).val);
				return true;
			}
		}else{
			this.val=((St)comp1).val;
			return true;
		}
		return false;
	}
	
	public boolean max(Structure comp1, Structure comp2){
		if(comp2!=null){
			if(val.val()!=-1){
				if(this.val.val()<((St)comp1).val.mult(((St)comp2).val).val()){
					this.val=((St)comp1).val.mult(((St)comp2).val);
					return true;
				}	
			}else{
				this.val=((St)comp1).val.mult(((St)comp2).val);
				return true;
			}
		}else{
			this.val=((St)comp1).val;
			return true;
		}
		return false;
	}
	
	public boolean isabsorbant(){
		return val.val()==0;
	}
	
	public void toNeutre(){
		val.toOne();
	}
	
	public String toDot(){
		if(val.val()==1)
			return "";
		else
			return ""+val.ts();
	}
	
	public String toTxt(){
		return ""+val.ts();
	}
	
	public boolean equals(St comp){
		if(val.val()==comp.val.val())
			return true;
		else
			return false;
	}
	
	
	public int hashCode(){
		int hash;
		hash=(int) Math.round(val.val()*10000000);
		return hash;
	}
	

	public boolean equals(Structure comp){
		
		if(val.val()==((St)comp).val.val())
			return true;
		else
			return false;
	}
	
	public String printstr(){
		return "St";
	}
	
	public boolean inaccessible(){
		return val.val()==-1;
	}
	
	public void rendreInaccessible(){
		val.toOneMoins();
	}
	
	public double getvaldouble(){
		return val.val();
	}
}*/
/*******/
/*

package br4cp;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.BigInteger;


public class St extends Structure {

	BigDecimal val;
	
	public St(double v){
		super();
		val=new BigDecimal(v);
	}
	
	public St(BigDecimal v){
		super();
		val=new BigDecimal(v.toString());
	}
	
	public St(){
		super();
		val=new BigDecimal(1);
	}
	
	public Structure copie(){
		St s=new St(this.val);
		return s;
	}
	
	public void operation(Structure str){
		val=val.multiply(((St)str).val);
	}
	
	public void initOperation(Structure str, Structure str2){
		val=((St)str).val.multiply(((St)str2).val);
	}
	
	public St normaliseInf(ArrayList<Structure> liste){
		BigDecimal max=((St)liste.get(0)).val;
		
		for(int i=1; i<liste.size(); i++){
			max=max.max(((St)liste.get(i)).val);
		}
		for(int i=0; i<liste.size(); i++){
			((St)liste.get(i)).val=((St)liste.get(i)).val.divide(max);
		}
		
		return new St(max);
	}
	
	public void normaliseSup(ArrayList<Structure>liste, Structure re){
		St remonte=(St)re;
		
		for(int i=0; i<liste.size(); i++)
			((St)liste.get(i)).val=((St)liste.get(i)).val.multiply(remonte.val);
	}
	
	public void normaliseSup(Structure str, Structure remonte){
		((St)str).val=((St)str).val.multiply(((St)remonte).val);
	}
	
	public boolean isNeutre(){
		return val.equals(BigDecimal.ONE);
	}
	
	//return true si min a change
	public boolean min(Structure comp1, Structure comp2){
		if(comp2!=null){
			if(!val.equals(BigInteger.ONE.negate())){
				if(this.val.compareTo(((St)comp1).val.multiply(((St)comp2).val))>0){
					this.val=((St)comp1).val.multiply(((St)comp2).val);
					return true;
				}
			}else{
				this.val=((St)comp1).val.multiply(((St)comp2).val);
				return true;
			}
		}else{
			this.val=((St)comp1).val;
			return true;
		}
		return false;
	}
	
	public boolean max(Structure comp1, Structure comp2){
		if(comp2!=null){
			if(val.compareTo(BigDecimal.ONE.negate())!=0){
				if(this.val.compareTo(((St)comp1).val.multiply(((St)comp2).val))<0){
					this.val=((St)comp1).val.multiply(((St)comp2).val);
					return true;
				}	
			}else{
				this.val=((St)comp1).val.multiply(((St)comp2).val);
				return true;
			}
		}else{
			this.val=((St)comp1).val;
			return true;
		}
		return false;
	}
	
	public boolean isabsorbant(){
		return val.compareTo(BigDecimal.ZERO)==0;
	}
	
	public void toNeutre(){
		val=new BigDecimal("1");
	}
	
	public String toDot(){
		if(val.compareTo(BigDecimal.ONE)==0)
			return "";
		else
			return ""+val.toString();
	}
	
	public String toTxt(){
		return ""+val.toString();
	}
	
	public boolean equals(St comp){
		if(val.compareTo(comp.val)==0)
			return true;
		else
			return false;
	}
	
	
	public int hashCode(){
		int hash;
		hash=(int) Math.round(val.doubleValue()*10000000);
		return hash;
	}
	

	public boolean equals(Structure comp){
		
		if(val.compareTo(((St)comp).val)==0)
			return true;
		else
			return false;
	}
	
	public String printstr(){
		return "St";
	}
	
	public boolean inaccessible(){
		return val.compareTo(BigDecimal.ONE.negate())==0;
	}
	
	public void rendreInaccessible(){
		val=new BigDecimal("1");
		val=val.negate();
	}
	
	public double getvaldouble(){
		return val.doubleValue();
	}
}*/