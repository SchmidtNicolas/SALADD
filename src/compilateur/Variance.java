package compilateur;

import java.util.ArrayList;

import compilateur.test_independance.TestIndependance;


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

public class Variance {

	private ArrayList<Var> variables;
//	private int count;
	private double[][] variance;
	
	public Variance(ArrayList<Var> v, VDD graph, TestIndependance test, String name){
		variables=v;
		
		variance = (double[][]) DataSaver.charger(name+"_variance_m"+test.getClass().getSimpleName()+".txt");
		
		if(variance == null)
		{
			variance = test.getIndependancy(v, graph);
			DataSaver.sauvegarder(variance, name+"_variance_m"+test.getClass().getSimpleName()+".txt");
		}
		
    } 
	
	public void printNbModalites()
	{
		for(int i=0; i<variables.size(); i++)
			System.out.println(i+": "+variables.get(i).domain);
	}
	
	/**
	 * Affiche, pour chaque variable, les numéros des autres variables de la plus indépendante à la plus dépendante
	 * @param test
	 */
	public void printOrder(TestIndependance test)
	{
		for(int i=0; i<variables.size(); i++)
		{
			boolean[] done = new boolean[variables.size()];
			for(int j=0; j<variables.size(); j++)
				done[j] = false;
			for(int j=0; j<variables.size()-1; j++)
			{
				double max = Double.NaN;
				int indicemax = -1;
				for(int k=0; k<variables.size(); k++)
				{
					if(i == k)
						continue;
					if(!done[k] && (Double.isNaN(max) || test.estPlusIndependantQue(variance[k][i], max)))
					{
						max = variance[k][i];
						indicemax = k;
					}
				}
				done[indicemax] = true;
				System.out.print(indicemax+" ("+max+") ");
			}
			System.out.println();
		}
	}
	
	public void string(){
		for(int i=0; i<variables.size(); i++){
			for(int j=0; j<variables.size(); j++){
				System.out.print(variance[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Récupère l'indice d'indépendance entre v1 et v2.
	 * v1 est la variable d'intérêt à recommander,
	 * v2 est une autre variable déjà fixée
	 * @param v1
	 * @param v2
	 * @return
	 */
	public double get(Var v1, Var v2){
		int index1, index2;
//		double val1, val2;
		index1=variables.indexOf(v1);
		index2=variables.indexOf(v2);
		return variance[index2][index1];
/*		val1=variance[index1][index2];
		val2=variance[index2][index1];
		if (val1>val2)
			return val1;
		return val2;*/
	}
	/*
	//name : small medium big?
	public void save(String name){
		String file=name+"_variance_m"+meth+".txt";
		
		String ligne;
		
	   	FileWriter fW;

		try{
			fW = new FileWriter(file);
		
			for(int i=0; i<variance.length; i++){
				ligne="";
				for(int j=0; j<variance[i].length; j++){
					ligne+=""+variance[i][j]+" ";
				}
				fW.write(ligne+"\n");
			}
			fW.close();

	}catch (Exception e){
		System.out.println(e.toString());
	}
	}
	
	public boolean load(String name){
		String file=name+"_variance_m"+meth+".txt";
		
		String ligne;
		
		FileReader fR;
		InputStream ips;
		InputStreamReader ipsr=null;
		BufferedReader br=null;
		
		File f=new File(file);
		
		String numbers[];
		int i=0;
		
		if(f.canRead()){

			try{
			fR = new FileReader(file);
		
			ips=new FileInputStream(file); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);
			
			while((ligne=br.readLine())!=null){
			
				numbers=ligne.split(" ");
				for(int j=0; j<variance[i].length; j++){
					variance[i][j]=Double.parseDouble(numbers[j]);
				}
				i++;
			}
			fR.close();
			br.close();

			}catch (Exception e){
				System.out.println(e.toString());
			}
		}else
			return false;
		
		return true;
	}
	*/		

}
