package compilateur.test_independance;

import java.util.ArrayList;

import compilateur.VDD;
import compilateur.Var;


/*   (C) Copyright 2015, Gimenez Pierre-François
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

public class TestVariancePonderee implements TestIndependance {

	@Override
	public double[][] getIndependancy(ArrayList<Var> v, VDD graph) {
		double[][] variance = new double[v.size()][v.size()];
		int count=graph.countingpondere();	
		
		double[] probabilite;
		double probaTemp;
		Var var1, var2;
		int dom1, dom2, count2;
		double[] probaCond;
		
		for(int j=0; j<v.size(); j++)
		{
			var2=v.get(j);
			System.out.println();
			System.out.println(var2.name);
			dom2=var2.domain;
			probaCond = new double[dom2];
			probabilite=new double[dom2];
			
			//calcul des probab initiales
			for(int k=0; k<dom2; k++){
				probabilite[k]=graph.countingpondereOnVal(var2, k);
				probabilite[k]=probabilite[k]/count;
			}				

			for(int i=0; i<v.size(); i++)
			{
				var1=v.get(i);					
				dom1=var1.domain;
				int dom1min = 0;
	
				//calcul des probab initiales
				for(int k=0; k<dom2; k++){
					probaCond[k] = 0;
				}				
				//calcul des proba au cas par cas
				for(int l=0; l<dom1; l++){
					graph.conditioner(var1, l);
					count2=graph.countingpondere();
//					System.out.println(var1.valeurs.get(l)+" "+count2);
					if(count2 > 0)
						for(int k=0; k<dom2; k++){
							probaTemp=graph.countingpondereOnVal(var2, k);
							probaTemp=probaTemp/count2;
							probaCond[k] += (probaTemp-probabilite[k])*(probaTemp-probabilite[k]);
						}
					else
						dom1min++;
					graph.deconditioner(var1);
				}
				
				double distance = 0;
				
				for(int k=0; k<dom2; k++)
				{
//					System.out.println(probaCond[k]);
					// dom1/(dom1-1) provient de l'estimateur sans biais de la variance
					distance += probabilite[k] * (dom1-dom1min)/(dom1-dom1min-1) * Math.sqrt(probaCond[k]);
				}
				
				variance[i][j]=distance;
//				System.out.println("dom: "+(dom1-dom1min-1));
				System.out.print(var1.name+"="+(double)(Math.round(variance[i][j]*100))/100+" ");

			}
		}
		return variance;
	}
	
	public boolean estPlusIndependantQue(double valeur1, double valeur2)
	{
		return valeur1 < valeur2;
	}
	
}
