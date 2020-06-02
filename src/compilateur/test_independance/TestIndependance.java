package compilateur.test_independance;

import java.util.ArrayList;

import compilateur.VDD;
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

public interface TestIndependance {

	/**
	 * Renvoie un tableau de statistiques
	 * @param v
	 * @param graph
	 * @return
	 */
	public double[][] getIndependancy(ArrayList<Var> v, VDD graph);
	
	/**
	 * Doit renvoyer vrai si le couple de variables avec la statistique "valeur1" est plus
	 * indépendant que celui avec la statistique "valeur 2"
	 * @param valeur1
	 * @param valeur2
	 * @return
	 */
	public boolean estPlusIndependantQue(double valeur1, double valeur2);
	
}
