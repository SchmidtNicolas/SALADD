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

public class Sp extends Structure {

	private int val;
	
	public Sp(int v){
		super();
		val=v;
	}
	
	public Sp(){
		super();
		val=0;
	}
	
	public Structure copie(){
		Sp s=new Sp(this.val);
		return s;
	}
	
	public void operation(Structure str){
		val+=((Sp)str).val;
	}
	
	public void initOperation(Structure str, Structure str2){
		val=((Sp)str).val+((Sp)str2).val;
	}
	
	public Sp normaliseInf(ArrayList<Structure> liste){
		int min=((Sp)liste.get(0)).val;
		
		for(int i=1; i<liste.size(); i++){
			if(((Sp)liste.get(i)).val<min){
				min=((Sp)liste.get(i)).val;
			}
		}
		for(int i=0; i<liste.size(); i++){
			((Sp)liste.get(i)).val-=min;
		}
		
		return new Sp(min);
	}
	
	public void normaliseSup(ArrayList<Structure>liste, Structure re){
		Sp remonte=(Sp)re;
		
		for(int i=0; i<liste.size(); i++)
			((Sp)liste.get(i)).val+=remonte.val;
	}
	
	public void normaliseSup(Structure str, Structure remonte){
		((Sp)str).val+=((Sp)remonte).val;
	}
	
	public boolean isNeutre(){
		return val==0;
	}
	
	//return true si this a change
	public boolean min(Structure comp1, Structure comp2){
		if(comp2!=null){
			if(val!=-1){
				if(this.val>((Sp)comp1).val + ((Sp)comp2).val){
					this.val=((Sp)comp1).val + ((Sp)comp2).val;
					return true;
				}
			}
			else{
				this.val=((Sp)comp1).val + ((Sp)comp2).val;
				return true;
			}
		}else{
			this.val=((Sp)comp1).val;
			return true;
		}
		return false;
	}
	
	public boolean max(Structure comp1, Structure comp2){
		if(comp2!=null){
			if(val!=-1){
				if(this.val<((Sp)comp1).val + ((Sp)comp2).val){
					this.val=((Sp)comp1).val + ((Sp)comp2).val;
					return true;
				}
			}else{
				this.val=((Sp)comp1).val + ((Sp)comp2).val;
				return true;
			}
		}else{
			this.val=((Sp)comp1).val;
			return true;
		}
		return false;
	}
	
	public boolean isabsorbant(){
		return false;
	}
	
	public void toNeutre(){
		val=0;
	}
	
	public String toDot(){
		if(val==0)
			return "";
		else
			return ""+val;
	}
	
	public String toTxt(){
		return ""+val;
	}
	
	public int hashCode(){
		return val;
	}
	
	public boolean equals(Structure comp){
		if (this.val==((Sp)comp).val)
			return true;
		else
			return false;
	}
	
	public String printstr(){
		return "Sp";
	}
	
	public int getVal(){
		return val;
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
