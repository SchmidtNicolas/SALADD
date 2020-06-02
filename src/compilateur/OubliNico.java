package compilateur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compilateur.test_independance.TestIndependance;


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

/**
 * Méthode d'oubli dans laquelle on oublie les variables les plus indépendantes jusqu'à atteindre un certain seuil
 * @author pgimenez
 *
 */

public class OubliNico implements MethodeOubli {

	private int nbOubli;
	private Variance variance = null;
	private TestIndependance test;
	
	public OubliNico(TestIndependance test)
	{
		this.test = test;
	}
	
	@Override
	public void learn(SALADD saladd, String prefix_file_name)
	{
		variance = saladd.calculerVarianceHistorique(test, prefix_file_name);
	}
	
	@Override
	public Map<String, Double> recommandation(Var v, HashMap<String, String> historiqueOperations, VDD vdd, ArrayList<String> possibles)
	{
//		int dfcorr = 1;
				
/*		for(int i = 0; i < historiqueOperations.size(); i += 2)
		{
			Var connue = vdd.getVar(historiqueOperations.get(i));
			dfcorr *= connue.domain;
		}*/
		
		nbOubli = 0;
		Map<String, Double> m;
		int seuil=50*(possibles.size()-1);
		//System.out.println("avant : "+uht.size());
    	ArrayList<Var> dejavu=new ArrayList<Var>();
    	ArrayList<String> dejavuVal=new ArrayList<String>();
    	
    	while(vdd.countingpondere()<seuil){
    		boolean first = true;
    		double min=-1, curr;
    		Var varmin=null, varcurr;
    		String val="";
    		for(String s: historiqueOperations.keySet())
    		{
    			varcurr=vdd.getVar(s);
    			if(!dejavu.contains(varcurr)){
	    			curr=variance.get(v, varcurr);
//    				curr = testg2.computeInd(v, varcurr, vdd, dfcorr);
//    				vdd.conditioner(varcurr, varcurr.conv(historiqueOperations.get(i+1)));
    				if(first || test.estPlusIndependantQue(curr,min)){
	    				first = false;
	    				min=curr;
	    				varmin=varcurr;
	    				val=historiqueOperations.get(s);
	    			}
	    		}
    		}
    		nbOubli++;
    		dejavu.add(varmin);
    		dejavuVal.add(val);
    		vdd.deconditioner(varmin);
    	}
    	
//    	System.out.println(nbOubli+" oublis");
		m=vdd.countingpondereOnPossibleDomain(v, possibles);
    	for(int i=0; i<dejavu.size(); i++){
        	vdd.conditioner(dejavu.get(i), dejavu.get(i).conv(dejavuVal.get(i)));
    	}

    	return m;
	}

	@Override
	public int getNbOublis() {
		return nbOubli;
	}
	
}
