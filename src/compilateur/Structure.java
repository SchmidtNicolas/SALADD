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

public abstract class Structure {
	
	public Structure(){
	}
	
	public abstract Structure copie();
	
	public abstract void operation(Structure str);
	
	//initialisation de la valeur a partir de l'operation de deux structures (util pour la fonction add (times)
	public abstract void initOperation(Structure str, Structure str2);
	
	public abstract Structure normaliseInf(ArrayList<Structure> liste);
	
	public abstract void normaliseSup(ArrayList<Structure>liste, Structure remonte);
	
	public abstract void normaliseSup(Structure str, Structure remonte);
	
	public abstract boolean isNeutre();
	
	public abstract boolean min(Structure comp1, Structure comp2);
	
	public abstract boolean max(Structure comp1, Structure comp2);
	
	public abstract boolean isabsorbant();
	
	public abstract void toNeutre();
	
	public abstract String toDot();
	
	public abstract String toTxt();
	
	public abstract int hashCode();
	
	public abstract boolean equals(Structure comp);
	
	public abstract String printstr();
	
	public abstract boolean inaccessible();
	
	public abstract void rendreInaccessible();
	
	public abstract double getvaldouble();
}
