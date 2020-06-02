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

public class S extends Structure{
	
	public Structure last;

	public S(){
		super();
	}
	
	public S(Structure l){	
		super();
		last=l.copie();
	}
	
	public Structure copie(){
		S s=new S(this.last);
		return s;
	}
	
	public void operation(S str){
		//nadau
	}
	
	public void initOperation(Structure str, Structure str2){
		//vive le bearn libre
	}
	
	public S normaliseInf(ArrayList<Structure> liste){
		return new S();
	}
	
	public void normaliseSup(ArrayList<S> liste, S remonte){
		//yalalheeeeee
	}
	
	public void normaliseSup(S str, Spt remonte){
		//yalalheeeeee
	}
	
	public boolean isNeutre(){
		return true;
	}
	
	public boolean min(Structure comp1, Structure comp2){
		System.out.println("@Structure : non applicable");
		return false;
	}
	
	public boolean max(Structure comp1, Structure comp2){
		System.out.println("@Structure : non applicable");
		return false;
	}
	
	public boolean isabsorbant(){
		return false;
	}
	
	public void toNeutre(){
	}
	
	
	public String toDot(){
//		if(last!=null)
//			return last.toDot();			pour avoir le return, il faut faire ((S)s).last.toDot()
		return"";
	}
	
	public String toTxt(){
		if(last!=null)
			return last.toTxt();
		return"";
	}
	
	public int hashCode(){
		if(this.last==null)
			return 0;
		else
			return this.last.hashCode();
	}
	
	public boolean equals(Structure comp){
		if(this.last==null && ((S)comp).last==null)
			return true;
		else{
			return this.last.equals(((S)comp).last);
		}
	}
	
	public String printstr(){
		return "S";
	}
	
	public boolean inaccessible(){
		System.out.println("@Structure : cas a revoir");
		return false;
	}
	
	public void rendreInaccessible(){
		System.out.println("@Structure : cas a revoir");
	}
	
	public double getvaldouble(){
		if(last!=null)
			return last.getvaldouble();
		return 0;
	}

	@Override
	public void operation(Structure str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void normaliseSup(ArrayList<Structure> liste, Structure remonte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void normaliseSup(Structure str, Structure remonte) {
		// TODO Auto-generated method stub
		
	}
	
}
