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

public interface HeuristiqueVariable {
	
	public ArrayList<Var> reordoner(int[][] contraintes, ArrayList<Var> listeVariables, Ordonnancement ord);
	
}


////////////////aide////////////// 
/*
formats : 
contraintes
	table comptenant toutes les variables impliques dans chacunes des variables
	table[i][j] -> i : permet de changer de contrainte; j -> permet de parcourir les variables dans la contrainte
	 /!\ taille des lignes variables
	table[i][j]=(int) : une variable
	les variables sont exprimés par leur position dans l'ordre initial. 

listeVariables
	Arrayliste de Var, l'ensemble des variables dans l'ordre avec lequel elles ont été introduite par les fichiers xml
	
ord
	contient quelques petits outils utils.
	
	//--------------------
	
Var : objet représentant les variables.
	var.name (String)
	var.pos (int) position actuelle dans l'ordre
	var.domain (int) taille du domaine
	var.valeurs (ArrayList<String>) nom des alternatives
	
	
int[] ord.nbContraintes : indique dans combien de contrainte chaque variable (désigné par son numero) est impliquée
 		necessite de lancer une fois ord.constNbContraintes(contraintes); pour le calcule de ces valeurs
int[][] ord.graphAdj : graph d'adjacence. ord.graphAdj[var1.pos][var2.pos]>0 si var1 et var2 sont impliqués dans une meme variable
 		necessite de lancer une fois ord.constGraphAdj(contraintes); pour le calcule de ces valeurs

sortie : 
	la liste des variables (ArrayList<Var>) ordonnée

*/