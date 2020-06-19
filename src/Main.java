import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compilateur.*;


public class Main {

//exemple 1 compilation
	//compilation de un fichier unique
	public static void main(String[] args) {
		
		SALADD s=new SALADD();
		
		ArrayList<String> files=new ArrayList<>();
		files.add("small.xml");
		//files.add("bigPrices.xml");
		//files.add("Benchmarcks\\cnf\\Handmade\\ais\\ais12d.cons");
		
		//big.xml et bigPrices.xml; nature additive; heuristique numero 4; heuristique de contraintes numero 2; affichage de texte niveau 2 sur 3
		s.compilation(files, true, 4, 2, 2);
		
		s.suppressionNoeudsBegayants();
		
		s.save("smallMaster.dot");
				
		System.out.println(s.nb_edges());
		System.out.println(s.nb_nodes());

		
	}
	
	
//exemple 2 compilations 
	//compilation de plusieurs fichier, avec sauvegarde (.dot et .xml) de la forme minimale (suppressionNoeudsBegayants)
	// -- 
	//chargement d'un fixhier compilé (s2) puis comparaison d'équivalence

	public static void main2(String[] args) {
		
		
		SALADD s=new SALADD();

		s.compilation("big.xml", true, 3, 0, 2);
		
		
		
		/*
		SALADD s=new SALADD();
		
		ArrayList<String> files=new ArrayList<>();
		files.add("medium.xml");
		//files.add("mediumPrices.xml");
		
		//big.xml et bigPrices.xml; nature additive; heuristique numero 4; heuristique de contraintes numero 2; affichage de texte niveau 2 sur 3
		s.compilation(files, true, 4, 2, 2);
		
		s.suppressionNoeudsBegayants();
		
		s.save("sauvegarde.dot");
		s.saveToXml("sauvegarde.xml");
		
		System.out.println("fichier sauvegardé");
		System.out.println("--");

		
		SALADD s2=new SALADD();
		
		s2.chargement("sauvegarde.dot", 1);
		
		
		System.out.println(s2.equivalence(s));*/
		
	}
	
	
//exemple 3 configuration de produits, une configuration �  la fois

	public static void main3(String[] args) {
			
		SALADD s=new SALADD();
			
		ArrayList<String> files=new ArrayList<>();
		files.add("big.xml");
		files.add("bigPrices.xml");

		//compile et sauvegarde le problème si jamais compilé avant, le charge sinon.
		//si déj�  en memoire, le problème est juste réinitialisé.
		s.readProblem(files);
		
		s.propagation();
		System.out.println("cout minimal : "+s.minCost());
		
		System.out.println("variables non affectées : "+s.getFreeVariables());
		//selection variable "v73"
		System.out.println("v73 : "+s.getCurrentDomainOf("v73"));
		//selection alternative "1"
		s.assignAndPropagate("v73", "1");
		System.out.println("assignation de v73 a 1");
		System.out.println("cout minimal : "+s.minCost());

		System.out.println("variables non affectées : "+s.getFreeVariables());
		//selection variable "v27"
		System.out.println("v27 : "+s.getCurrentDomainOf("v27") +" - " + s.minCosts("v27"));	// on demande aussi le cout minimals associé a chaques alternatives (minCosts)
		//selection alternative "0"
		s.assignAndPropagate("v27", "0");
		System.out.println("assignation de v27 a 0");
		System.out.println("cout minimal : "+s.minCost());
		
		System.out.println("variables non affectées : "+s.getFreeVariables());
		//selection variable "v95"
		System.out.println("v95 : "+s.getCurrentDomainOf("v95"));	
		//selection alternative "1"
		s.assignAndPropagate("v95", "1");
		System.out.println("assignation de v95 a 1");
		System.out.println("cout minimal : "+s.minCost());

		//retour sur le choix v73
		System.out.println("variables non affectées : "+s.getFreeVariables());
		s.unassignAndRestore("v73");
		System.out.println("restauration de v73");
		System.out.println("v73 : "+s.getCurrentDomainOf("v73")+" - " + s.minCosts("v73"));
		System.out.println("variables non affectées : "+s.getFreeVariables());
		System.out.println("cout minimal : "+s.minCost());

		
		//réinitialisation
		s.readProblem(files);
		s.propagation();
		
		System.out.println("cout minimal : "+s.minCost());
		//...

	}
	
	
	
	//exemple 4 configuration de produits, plusieurs configurations �  la fois

		public static void main4(String[] args) {
				
			SALADD s=new SALADD();
				
			ArrayList<String> files=new ArrayList<>();
			files.add("big.xml");
			files.add("bigPrices.xml");

			//compile et sauvegarde le problème si jamais compilé avant, le charge sinon.
			//si déj�  en memoire, le problème est juste réinitialisé.
			s.readProblem(files);
			
			
			Map<String, String> configurationClient1, configurationClient2;
			configurationClient1 = new HashMap<String, String>();
			configurationClient2 = new HashMap<String, String>();
			
			configurationClient1.put("v73", "1");
			configurationClient1.put("v27", "0");
			configurationClient1.put("v95", "1");
			
			s.reinitializeInState(configurationClient1);
			s.propagation();
			System.out.println("client1");
			System.out.println("variables non affectées : "+s.getFreeVariables());
			System.out.println("v10 : "+s.getCurrentDomainOf("v10"));

			s.reinitializeInState(configurationClient2);
			s.propagation();
			System.out.println("client2");
			System.out.println("variables non affectées : "+s.getFreeVariables());
			System.out.println("v26_5_Serie : "+s.getCurrentDomainOf("v26_5_Serie"));
			
			configurationClient1.put("v10", "0");
			s.reinitializeInState(configurationClient1);
			s.propagation();
			System.out.println("client1");
			System.out.println("variables non affectées : "+s.getFreeVariables());
			System.out.println("v72 : "+s.getCurrentDomainOf("v72")+" - " + s.minCosts("v72"));		// on demande aussi le cout minimals associé a chaques alternatives (minCosts)
			
			configurationClient1.put("v26_5_Serie", "1");
			s.reinitializeInState(configurationClient2);
			s.propagation();
			System.out.println("client2");
			System.out.println("variables non affectées : "+s.getFreeVariables());
			System.out.println("v42 : "+s.getCurrentDomainOf("v42")+" - " + s.minCosts("v42"));		// on demande aussi le cout minimals associé a chaques alternatives (minCosts)
			
			//...
			
		}
	
	//exemple 5 recommandation

		public static void main5(String[] args) {
				
			SALADD contraintes=new SALADD();
			SALADD historiques=new SALADD();

			
			ArrayList<String> files=new ArrayList<>();
			files.add("small.xml");
			files.add("smallPrices.xml");
			
			contraintes.readProblem(files);
			
			historiques.compilationDHistorique("smallHistory.xml", 2);
			

			contraintes.propagation();
			historiques.propagation();
			
			
			System.out.println("variables non affectées : "+contraintes.getFreeVariables());
			//selection variable "v33"
			System.out.println("v33 : "+contraintes.getCurrentDomainOf("v33"));
			System.out.println("v33 : "+historiques.recomandation("v33", "small", null));
			//selection alternative "0"
			contraintes.assignAndPropagate("v33", "0");
			historiques.assignAndPropagate("v33", "0");
			System.out.println("assignation de v33 a 0");
			System.out.println("cout minimal : "+contraintes.minCost());
			
			System.out.println("variables non affectées : "+contraintes.getFreeVariables());
			//selection variable "v8"
			System.out.println("v8 : "+contraintes.getCurrentDomainOf("v8"));
			System.out.println("v8 : "+historiques.recomandation("v8", "small", null));
			//selection alternative "0"
			contraintes.assignAndPropagate("v8", "0");
			System.out.println("assignation de v8 a 0");
			System.out.println("cout minimal : "+contraintes.minCost());

			System.out.println("variables non affectées : "+contraintes.getFreeVariables());
			//selection variable "v8"
			System.out.println("v93 : "+contraintes.getCurrentDomainOf("v93"));
			System.out.println("v93 : "+historiques.recomandation("v93", "small", null));
			//selection alternative "0"
			contraintes.assignAndPropagate("v93", "14");
			System.out.println("assignation de v93 a 14");
			System.out.println("cout minimal : "+contraintes.minCost());

			
			//réinitialisation
			contraintes.reinitialisation();
			historiques.reinitialisation();
			
		}
}
