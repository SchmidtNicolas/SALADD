package compilateur.heuristique_variable;

import java.util.ArrayList;

import compilateur.Ordonnancement;
import compilateur.Var;


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

public class HeuristiqueVariableOrdreChoisi implements HeuristiqueVariable{

	public ArrayList<Var> reordoner(int[][] contraintes, ArrayList<Var> listeVariables, Ordonnancement ord)
	{
		ArrayList<Var> liste=new ArrayList<Var>();
		
		for(int i=0; i<listeVariables.size(); i++)
			liste.add(listeVariables.get(i));

		return liste;

	}
	
}
