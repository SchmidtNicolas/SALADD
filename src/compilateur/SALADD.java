package compilateur;

	/*   (C) Copyright 2014, Schmidt Nicolas
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



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.io.*;

import compilateur.heuristique_contraintes.HeuristiqueContraintes;
import compilateur.heuristique_contraintes.HeuristiqueContraintesBCF;
import compilateur.heuristique_contraintes.HeuristiqueContraintesDomaineMaxDomaineMaxEcartMaxHardFirst;
import compilateur.heuristique_contraintes.HeuristiqueContraintesDurete;
import compilateur.heuristique_contraintes.HeuristiqueContraintesInversion;
import compilateur.heuristique_contraintes.HeuristiqueContraintesRandom;
import compilateur.heuristique_contraintes.HeuristiqueContraintesRien;
import compilateur.heuristique_variable.HeuristiqueVariable;
import compilateur.heuristique_variable.HeuristiqueVariableBW;
import compilateur.heuristique_variable.HeuristiqueVariableForce;
import compilateur.heuristique_variable.HeuristiqueVariableMCF;
import compilateur.heuristique_variable.HeuristiqueVariableMCSinv;
import compilateur.heuristique_variable.HeuristiqueVariableMCSinvPlusUn;
import compilateur.heuristique_variable.HeuristiqueVariableOrdreChoisi;
import compilateur.heuristique_variable.HeuristiqueVariableOrdreRandom;
import compilateur.test_independance.TestEcartMax;
import compilateur.test_independance.TestIndependance;

	
public class SALADD {		
	
	private VDD x;//testVDD;
	private boolean isHistorique;
	private String inX;
	
	private MethodeOubli methode=null;
	
	private HashMap<String, String> historiqueOperations;	// key:variable - valeur:valeur

	/** 
	 * Constructeur
	 */
	public SALADD(){
		x=null;
		historiqueOperations=new HashMap<String, String>();
		inX="";

		isHistorique=false;
	}
	
	/**
	 * Compilation du fichier de contraintes file_name
	 * 
	 * heuristiques d'ordonnancement des variables : -1=aléatoire; 0=ordre naturel; 1=MCF; 2=BW; 3=MCS; 4=MCS+1; 5=Force
	 * heuristiques d'ordonnancement des contraintes : -1=aléatoire; 0=ordre naturel; 1=BCF; 2=tri par difficulté; 3=tri par dureté
	 * 
	 * @param file_name : chemin/nom du fichier a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : heuristique d'ordonnancement des variables a utiliser (valeur conseillée : '3' ou '4')
	 * @param arg_heuristique_cons : heuristique d'ordonnancement des cointraintes a utiliser (valeur conseillée : '2')
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilation(String file_name, boolean arg_plus, int arg_heuristique, int arg_heuristique_cons, int arg_affich_text){
		ArrayList<String> s=new ArrayList<String>();
		s.add(file_name);
		compilation(s, arg_plus, arg_heuristique, arg_heuristique_cons, arg_affich_text);
	}
	/**
	 * Compilation du fichier de contraintes file_name avec votre heuristique d'ordonnancement de variables perso
	 * Votre heuristique personnelle doit implémenter la classe "HeuristiqueVariable" 
	 * 
	 * heuristiques d'ordonnancement des variables : -1=aléatoire; 0=ordre naturel; 1=MCF; 2=BW; 3=MCS; 4=MCS+1; 5=Force
	 * heuristiques d'ordonnancement des contraintes : -1=aléatoire; 0=ordre naturel; 1=BCF; 2=tri par difficulté; 3=tri par dureté
	 * 
	 * @param file_name : chemin/nom du fichier a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : votre heuristique personnelle
	 * @param arg_heuristique_cons : heuristique d'ordonnancement des cointraintes a utiliser (valeur conseillée : '2')
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilation(String file_name, boolean arg_plus, HeuristiqueVariable arg_heuristique, int arg_heuristique_cons, int arg_affich_text){
		ArrayList<String> s=new ArrayList<String>();
		s.add(file_name);
		compilation(s, arg_plus, arg_heuristique, arg_heuristique_cons, arg_affich_text);
	}
	/**
	 * Compilation du fichier de contraintes file_name avec votre heuristique d'ordonnancement de contraintes perso
	 * Votre heuristique personnelle doit implémenter la classe "HeuristiqueContraintes" 
	 *
	 * heuristiques d'ordonnancement des variables : -1=aléatoire; 0=ordre naturel; 1=MCF; 2=BW; 3=MCS; 4=MCS+1; 5=Force
	 * 
	 * @param file_name : chemin/nom du fichier a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : heuristique d'ordonnancement des variables a utiliser (valeur conseillée : '3' ou '4')
	 * @param arg_heuristique_cons : votre heuristique personnelle
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilation(String file_name, boolean arg_plus, int arg_heuristique, HeuristiqueContraintes arg_heuristique_cons, int arg_affich_text){
		ArrayList<String> s=new ArrayList<String>();
		s.add(file_name);
		compilation(s, arg_plus, arg_heuristique, arg_heuristique_cons, arg_affich_text);
	}
	/**
	 * Compilation du fichier de contraintes file_name avec votre heuristique d'ordonnancement de variables et de contraintes
	 * Votre heuristique personnelle doit implémenter la classe "HeuristiqueVariable" 
	 * Votre heuristique personnelle doit implémenter la classe "HeuristiqueContraintes" 
	 * 
	 * @param file_name : chemin/nom du fichier a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : votre heuristique personnelle
	 * @param arg_heuristique_cons : votre heuristique personnelle
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilation(String file_name, boolean arg_plus, HeuristiqueVariable arg_heuristique, HeuristiqueContraintes arg_heuristique_cons, int arg_affich_text){
		ArrayList<String> s=new ArrayList<String>();
		s.add(file_name);
		compilation(s, arg_plus, arg_heuristique, arg_heuristique_cons, arg_affich_text);
	}
	/**
	 * Compilation du (ou des) fichier(s) de contraintes file_names
	 * 
	 * heuristiques d'ordonnancement des variables : -1=aléatoire; 0=ordre naturel; 1=MCF; 2=BW; 3=MCS; 4=MCS+1; 5=Force
	 * heuristiques d'ordonnancement des contraintes : -1=aléatoire; 0=ordre naturel; 1=BCF; 2=tri par difficulté; 3=tri par dureté
	 * 
	 * @param file_names : chemin/nom des fichiers a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : heuristique d'ordonnancement des variables a utiliser (valeur conseillée : '3' ou '4')
	 * @param arg_heuristique_cons : heuristique d'ordonnancement des cointraintes a utiliser (valeur conseillée : '2')
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilation(ArrayList<String> file_names, boolean arg_plus, int arg_heuristique, int arg_heuristique_cons, int arg_affich_text){
		HeuristiqueVariable[] heuristiquesVariables = {
				new HeuristiqueVariableOrdreRandom(),
				new HeuristiqueVariableOrdreChoisi(),
				new HeuristiqueVariableMCF(),
				new HeuristiqueVariableBW(),
				new HeuristiqueVariableMCSinv(),
				new HeuristiqueVariableMCSinvPlusUn(),
				new HeuristiqueVariableForce()};
		HeuristiqueContraintes[] heuristiquesContraintes = {
				new HeuristiqueContraintesInversion(), 
				new HeuristiqueContraintesRandom(),
				new HeuristiqueContraintesRien(),
				new HeuristiqueContraintesBCF(),
				new HeuristiqueContraintesDomaineMaxDomaineMaxEcartMaxHardFirst(),
				new HeuristiqueContraintesDurete()};
		compilation(file_names, arg_plus, heuristiquesVariables[arg_heuristique+1], heuristiquesContraintes[arg_heuristique_cons+2], arg_affich_text);

	}
	/**
	 * Compilation du (ou des) fichier(s) de contraintes file_names avec votre heuristique d'ordonnancement de variables perso
	 * Votre heuristique personnelle doit implémenter la classe "HeuristiqueVariable" 
	 *
	 * heuristiques d'ordonnancement des contraintes : -1=aléatoire; 0=ordre naturel; 1=BCF; 2=tri par difficulté; 3=tri par dureté
	 * 
	 * @param file_names : chemin/nom des fichiers a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : votre heuristique personnelle
	 * @param arg_heuristique_cons : heuristique d'ordonnancement des cointraintes a utiliser (valeur conseillée : '2')
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilation(ArrayList<String> file_names, boolean arg_plus, HeuristiqueVariable arg_heuristique, int arg_heuristique_cons, int arg_affich_text){
		HeuristiqueContraintes[] heuristiquesContraintes = {
				new HeuristiqueContraintesInversion(), 
				new HeuristiqueContraintesRandom(),
				new HeuristiqueContraintesRien(),
				new HeuristiqueContraintesBCF(),
				new HeuristiqueContraintesDomaineMaxDomaineMaxEcartMaxHardFirst(),
				new HeuristiqueContraintesDurete()};
		compilation(file_names, arg_plus, arg_heuristique, heuristiquesContraintes[arg_heuristique_cons+2], arg_affich_text);

	}
	/**
	 * Compilation du (ou des) fichier(s) de contraintes file_names avec votre heuristique d'ordonnancement de contraintes perso
	 * Votre heuristique personnelle doit implémenter la classe "HeuristiqueContraintes" 
	 *
	 * heuristiques d'ordonnancement des variables : -1=aléatoire; 0=ordre naturel; 1=MCF; 2=BW; 3=MCS; 4=MCS+1; 5=Force
	 * 
	 * @param file_names : chemin/nom des fichiers a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : heuristique d'ordonnancement des variables a utiliser (valeur conseillée : '3' ou '4')
	 * @param arg_heuristique_cons : votre heuristique personnelle
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilation(ArrayList<String> file_names, boolean arg_plus, int arg_heuristique, HeuristiqueContraintes arg_heuristique_cons, int arg_affich_text){
		HeuristiqueVariable[] heuristiquesVariables = {
				new HeuristiqueVariableOrdreRandom(),
				new HeuristiqueVariableOrdreChoisi(),
				new HeuristiqueVariableMCF(),
				new HeuristiqueVariableBW(),
				new HeuristiqueVariableMCSinv(),
				new HeuristiqueVariableMCSinvPlusUn(),
				new HeuristiqueVariableForce()};
		compilation(file_names, arg_plus, heuristiquesVariables[arg_heuristique+1], arg_heuristique_cons, arg_affich_text);
	}


	/**
	 * Compilation du (ou des) fichier(s) de contraintes file_names avec votre heuristique d'ordonnancement de variables et de contraintes
	 * Votre heuristique personnelle doit implémenter la classe "HeuristiqueVariable" 
	 * Votre heuristique personnelle doit implémenter la classe "HeuristiqueContraintes" 
	 * 
	 * @param file_names : chemin/nom des fichiers a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : votre heuristique personnelle
	 * @param arg_heuristique_cons : votre heuristique personnelle
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilation(ArrayList<String> file_names, boolean arg_plus, HeuristiqueVariable arg_heuristique, HeuristiqueContraintes arg_heuristique_cons, int arg_affich_text){
		
		isHistorique=false;
		
		long start= System.currentTimeMillis();
		long end;
		
		Ordonnancement ord;			
		ord = new Ordonnancement();
		LecteurXML xml=new LecteurXML();
		if(arg_plus){
			if(file_names.get(0).contains(".xml"))
				xml.lecture(file_names.get(0));
			else if(file_names.get(0).contains(".cnf"))
				xml.lectureCNF(file_names.get(0));
			else if(file_names.get(0).contains(".cons"))
				xml.readContraintes(file_names.get(0));
			else
				xml.lecture(file_names.get(0));

		}else{	
			xml.lectureBIFpifi(file_names.get(0), arg_plus);
		}

		for(int i=1; i<file_names.size(); i++){
			xml.lectureSuite(file_names.get(i));
		}
		
		ord.addVarialbes(xml.getVariables());
		if(xml.getNbVariables()!=ord.size())
			System.out.println("bug nb variables");
		
		
		ord.reordoner(xml.getInvolvedVariablesEntree(), arg_heuristique, false);			//<---
		xml.actualiseVariables();
		xml.compactConstraint();
		
		UniqueHashTable uht=new UniqueHashTable(ord.size());
		x =new VDD(ord.getVariables(), uht, arg_plus);

		uht.ellagage(xml);

			
		x.flagMult=(!arg_plus);											//<---
		x.flagPlus=arg_plus;											//<---
			
	
		int contraintes[][];
		String contraintesS[][];
		Structure Poids[];
		Structure defaultCost;
		boolean softConstraint;
		boolean conflictsConstraint;
		
		System.out.println();
		xml.reorganiseContraintes(arg_heuristique_cons);
		
		if(arg_affich_text==3){
			ord.afficherOrdre();
			xml.afficheOrdreContraintes();
		}
		
	
		for(int i1=0; i1<xml.nbConstraints; i1++){
			int i=xml.equiv(i1);
		
			contraintesS=xml.getConstraintS(i);
			
			
			
			if(contraintesS!=null){
				Poids=xml.getPoid(i);
				if(contraintesS.length!=0){
					defaultCost=xml.getDefaultCost(i);
					softConstraint=xml.getSoftConstraint(i);
					conflictsConstraint=xml.getConflictsConstraint(i);
				

					contraintes=new int[contraintesS.length][contraintesS[0].length];
								
				//traduction en valeur de 0 a n (au lieu de strings)
					for(int j=0; j<contraintes.length; j++){
						for(int k=1; k<contraintes[j].length; k++){
							// tag2020
							if(contraintesS[j][k].startsWith("!"))
								contraintes[j][k]=-ord.getVariables().get(k-1).conv(contraintesS[j][k].substring(1))-2;
							else
								contraintes[j][k]=ord.getVariables().get(k-1).conv(contraintesS[j][k]);
						
						}
					}

			
					x.valeurChemin(contraintes, Poids, defaultCost, softConstraint, conflictsConstraint);


					
					//uht.detect();
					if(arg_affich_text>=2){
						end=System.currentTimeMillis();
						System.out.println(i1+":sldd"+(i+1)+"/"+xml.nbConstraints+"  nbnoeuds:" + x.uht.size() + " (" + x.uht.sizeArcs() + ")   " + (end-start)/1000+","+(end-start)%1000 + "s");
					}
				}
			}
//			System.gc();
		}
		x.affichageResultats(arg_affich_text, start);
	}
	
	/**
	 * Suppression des noeuds begayants (redondants, inutiles)
	 * Ces noeuds sont pourtant necessaires pour la quasi totalité des fonctions proposées par cette bibliothèque
	 * 
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 2 (beaucoup de texte)
	 */
	public void suppressionNoeudsBegayants(int arg_affich_text){
		if(arg_affich_text>=2)
    		System.out.println(this.x.uht.size() + " noeuds et " + this.x.uht.sizeArcs() + " arcs avant suppression");
		this.x.uht.rechercheNoeudInutile();
    	if(arg_affich_text>=1){
    		System.out.println(this.x.uht.size() + " noeuds et " + this.x.uht.sizeArcs() + " arcs apres suppression des noeuds begayants");
    		System.out.println("vous ne pourrez plus utiliser correctement les fonctions de configuration de produits");
    	}

	}
	
	/**
	 * Suppression des noeuds begayants (redondants, inutiles)
	 * Ces noeuds sont pourtant necessaires pour la quasi totalité des fonctions proposées par cette bibliothèque
	 */
	public void suppressionNoeudsBegayants(){
		this.x.uht.rechercheNoeudInutile();
	}

	/**
	 * Compilation d'un fichier d'historique en vue de la recomandation
	 * 
	 * @param file_name : chemin/nom du fichier d'historique a compiler (extention incluse)
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilationDHistorique(String file_name, int arg_affich_text)
	{
		ArrayList<String> file_names = new ArrayList<String>();
		file_names.add(file_name);
		compilationDHistorique(file_names, arg_affich_text);
	}

	/**
	 * Compilation du (ou des) fichier(s) d'historique en vue de la recomandation
	 * Attention : Si plusieurs fichiers, ceux ci doivent porter sur un meme ensemble de variables
	 * 
	 * @param file_names : chemin/nom des fichiers a compiler (extention incluse)
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilationDHistorique(ArrayList<String> file_names, int arg_affich_text){

		isHistorique=true;
		
		long start= System.currentTimeMillis();
		long end;
		
		Ordonnancement ord;			
		ord = new Ordonnancement();
		LecteurXML xml=new LecteurXML();
		
/*		for(int i=0; i<file_name.size(); i++)
		{
			String v = file_name.get(i);
			if(v.charAt(v.length()-4) != '.')
				v = v+".xml";
			else if(!v.endsWith(".xml"))
				v = v.substring(0, v.length()-4)+".xml";
			file_name.set(i, v);
		}
		
		xml.lecture(file_name.get(0));
		for(int i=1; i<file_name.size(); i++){
			xml.lectureSuite(file_name.get(i));
		}*/
		xml.lecture(file_names.get(0));

		for(int i=1; i<file_names.size(); i++){
			xml.lectureSuite(file_names.get(i));
		}
		
		
//			xml.month(12,12);
		ord.addVarialbes(xml.getVariables());
//			ord.supprmonth();
		
		if(xml.getNbVariables()!=ord.size())
			System.out.println("bug nb variables");
		
		
//			ord.reordoner(xml.getInvolvedVariablesEntree(), 0, false);			//<---
		xml.actualiseVariables();
//			xml.compactConstraint();
		
		UniqueHashTable uht=new UniqueHashTable(ord.size());
		x =new VDD(ord.getVariables(), uht, true);

			
		x.flagMult=false;											//<---
		x.flagPlus=true;											//<---
			
	
		int contraintes[][];
		String contraintesS[][];
		Structure Poids[];
		Structure defaultCost;
		boolean softConstraint;
		boolean conflictsConstraint;
		
//			xml.reorganiseContraintes(0);
		
	
		for(int i1=0; i1<xml.nbConstraints; i1++){
			int i=xml.equiv(i1);
		
			contraintesS=xml.getConstraintS(i);
			if(contraintesS!=null){
				Poids=xml.getPoid(i);
				if(contraintesS.length!=0){
					defaultCost=xml.getDefaultCost(i);
					softConstraint=xml.getSoftConstraint(i);
					conflictsConstraint=xml.getConflictsConstraint(i);
				

					contraintes=new int[contraintesS.length][contraintesS[0].length];
								
				//traduction en valeur de 0 a n (au lieu de strings)
					for(int j=0; j<contraintes.length; j++){
						for(int k=1; k<contraintes[j].length; k++){
							contraintes[j][k]=ord.getVariables().get(k-1).conv(contraintesS[j][k]);
						}
					}
									
					x.valeurChemin(contraintes, Poids, defaultCost, softConstraint, conflictsConstraint);
					
					//uht.detect();
					if(arg_affich_text>=2){
						end=System.currentTimeMillis();
						System.out.println("nbnoeuds:" + x.uht.size() + " (" + x.uht.sizeArcs() + ")   " + (end-start)/1000+","+(end-start)%1000 + "s");
					}	
					
				}	
			}
		}
		x.affichageResultats(arg_affich_text, start);

	}
	
	
	protected void procedureCompilation(ArrayList<String> FichiersACompiler, boolean arg_plus, int arg_heuristique, int arg_heuristique_cons, String arg_formefinale, String arg_FichierSortie, boolean flag_fichierSortie, boolean flag_beg, int arg_affich_text){
		
		long start= System.currentTimeMillis();
//			long end;
		

		compilation(FichiersACompiler, arg_plus, arg_heuristique, arg_heuristique_cons, arg_affich_text);
		
		x.toDot("b", false);
		//affiche les resultats, es supprim les noeuds beg si besoin
		
		x.affichageResultats(arg_affich_text, start);
		if(!flag_beg){
			this.x.uht.rechercheNoeudInutile();
	    	if(arg_affich_text>=1)
	    		System.out.println(this.x.uht.size() + " noeuds et " + this.x.uht.sizeArcs() + " arcs apres suppression des noeuds begayants (option noskip non activee)");
		}

		
		x.transformation(arg_formefinale, arg_affich_text);


		if(flag_fichierSortie){
			x.toDot(arg_FichierSortie, false);
		}
		
		
	}
	
	/**
	 * chargement d'un fichier dot representant un diagram de decision
	 * 
	 * @param file_name : chemin/nom du fichier d'historique a compiler (extention incluse)
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void chargement(String file_name, int arg_affich_text){
		LecteurDot l=new LecteurDot(file_name);
		x=l.getVDD();
		
		if(arg_affich_text>=1)
			System.out.println("chargement " + x.uht.size() + " (" + x.uht.sizeArcs() + ")");
	}


	protected VDD procedureChargement(String arg_FichierACharger, String arg_formefinale, String arg_FichierSortie, boolean flag_fichierSortie, boolean flag_beg, int arg_affich_text){

		///////////////lecture fichier////////////////
		chargement(arg_FichierACharger, arg_affich_text);

		
		x.transformation(arg_formefinale, arg_affich_text);

		return x;
	}
	
	/**
	 * restaure le diagramme dans sa situation initiale
	 */
	public void reinitialisation(){
		x.deconditionerAll();
		if(isHistorique)
			historiqueOperations.clear();
	}
	/**
	 * transformation du SLDD courant en un autre langage
	 * 
	 * @param arg_formefinale : valeurs possibles : {"AADD", "SLDDp", "SLDDt", "ADD"} (avec SLDDp et SLDDt un SLDD de nature respectivement additive et multiplicative).
	 */
	public void transformation(String arg_formefinale){
		x.transformation(arg_formefinale, 0);
	}
	
	/**
	 * donne le nombre de noeuds dans le diagramme
	 * 
	 * @return nombre de noeuds dans le diagramme
	 */
	public int nb_nodes(){
		return x.uht.size();
	}
	
	/**
	 * donne le nombre d'arcs dans le diagramme
	 * 
	 * @return nombre d'arcs dans le diagramme
	 */
	public int nb_edges(){
		return x.uht.sizeArcs();
	}
	
	/**
	 * donne le nombre de modeles du probleme
	 * /!\ si nombre négatif : dépassement d'entier
	 * 
	 * @return nombre de modeles
	 */
	public long nb_models(){
		return x.counting();
	}
	
	/**
	 * donne le nombre d'echantillons dans l'historique correspondant a la configuration en cours
	 * 
	 * @return nombre de modeles
	 */
	public int nb_echantillonsHistorique(){
		if(isHistorique=true)
			return x.countingpondere();
		else
			System.out.println("la fonction nb_echantillonsHistorique() ne conscerne que le traitement des historiques");
		return -1;
	}
	
	/**
	 * calcule la variance existant entre les differentes variables de d'un historique
	 * 
	 * @param methode : methode de calcule de variance utilise. a faire vous meme
	 * @param prefix_file_name : nom de lecture / sauvegarde (suivant l'existance) du fichier de sauvegarde de la variance
	 */
	public Variance calculerVarianceHistorique(TestIndependance methode, String prefix_file_name){
		if(isHistorique==true){
			return x.variance(methode, prefix_file_name);
		}else{
			System.out.println("la fonction calculerVariance() ne conscerne que le traitement des historiques");
			return null;
		}
	}
	
	/**
	 * calcule la variance existant entre les differentes variables de d'un historique. la methode utilisée est fixée
	 * normalement, c'est fonction est innutile car le calcul de variance est géré automatiquement par la fonction recommandation
	 * 
	 * @param prefix_file_name : nom de lecture / sauvegarde (suivant l'existance) du fichier de sauvegarde de la variance
	 */
	public Variance calculerVarianceHistorique(String prefix_file_name){
		if(isHistorique==true){
			return x.variance(new TestEcartMax(), prefix_file_name);
		}else{
			System.out.println("la fonction calculerVariance() ne conscerne que le traitement des historiques");
			return null;
		}
	}
	
	/**
	 * enregistre le diagramme au format .dot
	 * notez que le format .dot peut etre lu par la bibliotheque graphitz afin d'afficher le diagramme. commande : $ dot -Tpdf file_name.dot -o file_name.pdf
	 * 
	 * @param file_name : chemin/nom du fichier de sauvegarde
	 */
	public void save(String file_name){
		x.toDot(file_name, false);
	}

	/**
	 * enregistre le diagramme au format .dot, puis affiche le graph dans un fichier pdf
	 * il est vivement déconseillé d'utiliser cette fonction sur un diagramme de plusieurs miliers de noeuds
	 * 
	 * @param file_name : chemin/nom du fichier de sauvegarde
	 */
	public void saveToPdf(String file_name){
		x.toDot(file_name, true);
	}

	
	/**
	 * enregistre le diagramme au format .xml
	 * 
	 * @param file_name : chemin/nom du fichier de sauvegarde
	 */
	public void saveToXml(String file_name){
		x.toXML(file_name);
	}

	
	/**
	 * recomandation sur une variable
	 * 
	 * @param var : nom de la variable a recomander
	 * @param methodeOubli : Methode d'oubli à utiliser (cette methode doit implementer l'interface MethodeOubli).
	 * @param possibles : liste des alternatives que l'on considère lors de la recommandation. si possible=null, alors on considère toutes les valeurs
	 * @return un association valeur->probabilite pour la recomandation
	 */
	public Map<String, Double> recomandation(String var, MethodeOubli methodeOubli, ArrayList<String> possibles){
		if(isHistorique){
			Var v=x.getVar(var);
			return methodeOubli.recommandation(v, historiqueOperations, x, possibles);
		}else{
			System.out.println("la fonction recomandation() ne conscerne que le traitement des historiques");
			return null;
		}
	}
	
	/**
	 * recomandation sur une variable
	 * 
	 * @param var : nom de la variable a recomander
	 * @param prefix_file_name : nom du fichier d'enregistrement de la variance. doit etre le meme tout au long de la recommandation
	 * @param possibles : liste des alternatives que l'on considère lors de la recommandation. si possible=null, alors on considère toutes les valeurs
	 * @return un association valeur->probabilite pour la recomandation
	 */
	public Map<String, Double> recomandation(String var, String prefix_file_name, ArrayList <String> possibles){
		if(methode==null){
			methode=new OubliNico(new TestEcartMax());
			methode.learn(this, prefix_file_name);
		}
		
		if(possibles == null)
		{
			possibles = new ArrayList<String>();
			possibles.addAll(x.getVar(var).valeurs);
		}
		
		if(isHistorique){
			Var v=x.getVar(var);
			return methode.recommandation(v, historiqueOperations, x, possibles);
		}else{
			System.out.println("la fonction recomandation() ne conscerne que le traitement des historiques");
			return null;
		}
	}

	
	/**
	 * Calcul d'inférence, à utiliser sur un SLDDx appris d'un réseau bayésien
	 * Les valeurs renvoyées sont des probabilités à une constante multiplicative près.
	 * 
	 * @param var
	 * @param possibles : liste des alternatives que l'on considère lors de la recommandation. si possible=null, alors on considère toutes les valeurs
	 * @return une associassion valeurs probabilité
	 */
	public Map<String, Double> calculeDistributionAPosteriori(String var, ArrayList<String> possibles){
		Var v=x.getVar(var);
//		return x.calculeDistributionAPosteriori(v, historiqueOperations, values);
		if(possibles!=null)
			return x.inferenceOnPossibleDomain(v, possibles);
		else
			return x.inferenceOnFullDomain(v);
	}
	
	/**
	 * donne l'ensemble des variables du diagramme
	 * 
	 * @return ArrayList<Var> , une arrayList de l'ensemble des variables Var
	 */
	public ArrayList<Var> getAllVar()
	{
		return x.variables;
	}
	
	/**
	 * donne l'objet Var, la variable portant le nom "var"
	 * 
	 * @return Var , une variables 'Var'
	 */
	public Var getVar(String var)
	{
		return x.getVar(var);
	}
	
	/**
	 * donne vrai si les deux diagrammes this et s sont équivalent. 
	 * Cette fonction ne marche pas si l'un des diagrammes a encore ses noeuds begayants alors que l'autre non.
	 * 
	 * @param s : un autre diagramme de type SALADD
	 * @return true si this est equivalent à s
	 */
	public boolean equivalence(SALADD s){
		return this.x.equivalence(s.x);
	}
	
	/**
	 * réinitialise et conditionne le diagramme dans une configuration précise
	 * utile pour que plusieurs utilisateur utilisent le configurateur simultanément
	 * Cette fonction doit être suivie de la fonction propagation()
	 * 
	 * @param state : une Map<String, String> associant à chaque variable (K) que l'on souhaite instancier une valeur (V) à instancier.
	 * @return true si this est equivalent à s
	 */	
	public void reinitializeInState(Map<String, String> state){
		x.reinitializeInState(state);
	}
	
    //////////////
    // Protocol //
    //////////////
    


	/**
     * procédure automatisée de chargement de probleme.
	 * Si le problème n'a jamais été compilé, il est compilé est sauvegardé.
	 * Si le problème a déjà été compilé, mais n'est pas chargé en mémoire, il le charge.
	 * Si le problème est déjà chargé en mémoire, il le réinitialise
	 * 
	 * @param problemName : chemin/nom du fichier a compiler (extention incluse)
	 */
    public void readProblem(String problemName){
    	ArrayList<String> list=new ArrayList<String>();
    		list.add(problemName);
    	readProblem(list);
    }

    /**
     * procédure automatisée de chargement de probleme.
	 * Si le problème n'a jamais été compilé, il est compilé est sauvegardé.
	 * Si le problème a déjà été compilé, mais n'est pas chargé en mémoire, il le charge.
	 * Si le problème est déjà chargé en mémoire, il le réinitialise
     * 
     * @param problemName : chemin/nom des fichiers a compiler (extention incluse)
     */
    public void readProblem(ArrayList<String> problemName){
    	String filename=""; 
    	for(int i=0; i<problemName.size(); i++)		
    		filename+=problemName.get(i)+"_";

    			
    	if(x==null || inX.compareTo(filename)!=0){
    			File f=new File(filename+"_compiled.dot");
    			if(f.canRead()){
    				System.out.println("lecture du fichier compilé \""+filename+"_compiled.dot\"");
    				this.chargement(filename+"_compiled", 0);
    				inX=filename;
    				}else{
    					System.out.println("compilation (attention, cette operation peut prendre plusieurs minutes)");																				//sinon heuristique 5
    					procedureCompilation(problemName, true,  3, 2, "", (filename+"_compiled"), true, true, 0);
    					inX=filename;
    				}
    			}else{
    			//	System.out.println("Réinitialisation du problème");
    				x.deconditionerAll();
    			}
    	}
    
    	/**
    	 * propagation complette.
    	 * cette methode doit être appelée apres les fonctions readProblem() compilation() reinitializeInState() et reinitialisation()
    	 */
		public void propagation(){
			x.minMaxConsistance();
		}

		//public void assignAndPropagate(String var, String val){
    	protected void assignAndPropagateNoMaj(String var, String val){
    		if(!isPresentInCurrentDomain(var, val))
    			System.out.println(val+" non presente dans "+var+". aucune operation effectue...");
    		else{
	    		Var v=x.getVar(var);
				x.conditioner(v, v.conv(val));
				x.minMaxConsistance();
    		}
    	}
    	
    	/**
    	 * Assign a specific value to a variable.
    	 * 
    	 * prerequis getCurrentDomainOf(var).contains(val)
    	 * 
    	 * @param var
    	 * @param val
    	 */
    	public void assignAndPropagate(String var, String val){
//    		System.out.println(var+" "+val+"------"+isPresentInCurrentDomain(var, val));
    		if(!isPresentInCurrentDomain(var, val) && !isHistorique)
    		{
    			System.out.println(val+" non presente dans "+var+". aucune operation effectue.");
    			int z = 0;
    			z = 1/z;
    		}
    		else{
	    		Var v=x.getVar(var);
				x.conditioner(v, v.conv(val));
				x.minMaxConsistanceMaj(v.pos, true);
    		}
  
    		
    		if(isHistorique){
    			historiqueOperations.put(var, val);
    		}
    	}
    	
    	protected void assignAndPropagateOpt(String var, String val){
    		if(!isPresentInCurrentDomain(var, val))
    			System.out.println(val+" non presente dans "+var+". aucune operation effectue..");
    		else{
	    		Var v=x.getVar(var);
				x.conditioner(v, v.conv(val));
				x.minMaxConsistanceMajopt(v.pos, true);
				
				//x.GICup();
    		}
    	}
    	
    	protected void unassignAndRestoreNoMaj(String var){
    		Var v=x.getVar(var);
    		x.deconditioner(v);
    		x.minMaxConsistance();
    	}
    	
    	/**
    	 * Unassign a specific variable
    	 * 
    	 * @param var
    	 */
    	public void unassignAndRestore(String var){
    		Var v=x.getVar(var);
    		x.deconditioner(v);
    		x.minMaxConsistanceMaj(v.pos, false);
    		
    		if(isHistorique){
    			historiqueOperations.remove(var);
    		}
    	}
    	
    	protected void unassignAndRestoreOpt(String var){
    		Var v=x.getVar(var);
    		x.deconditioner(v);
    		x.minMaxConsistanceMajopt(v.pos, false);
    	}

    	/**
    	 * Get the minimal price of the configurations compatible with the current
    	 * choices.
    	 * 
    	 * @return the cost of the configuration
    	 */
    	public int minCost(){
    		return (int)x.min.getvaldouble();
    	}

    	/**
    	 * Provide a full configuration of minimal cost.
    	 * 
    	 * @return a full assignment var->value of minimal cost (given by {@link #minCost()}
    	 */
    	public Map<String, String> minCostConfiguration(){
    		return x.minCostConfiguration();
    	}

    	/**
    	 * Get the maximal price of the configurations compatible with the current
    	 * choices.
    	 * 
    	 * @return the cost of the configuration
    	 */
    	public int maxCost(){
    		return (int)x.max.getvaldouble();
    	}    	
    	/**
    	 * Provide a full configuration of maximal cost.
    	 * 
    	 * @return a full assignment var->value of maximal cost (given by {@link #maxCost()}
    	 */
    	public Map<String, String> maxCostConfiguration(){
    		return x.maxCostConfiguration();
    	}

    	/**
    	 * getSizeOfCurrentDomain(var) == getCurrentDomainOf(var).size()
    	 */
    	public int getSizeOfCurrentDomainOf(String var){
    		return x.getVar(var).consistenceSize();    		
    	}
    	
    	/**
    	 * getSizeOfDomain(var) == getDomainOf(var).size()
    	 */
    	public int getSizeOfDomainOf(String var){
    		return x.getVar(var).getDomainSize();
    	}


    	/**
    	 * isCurrentInCurrentDomain(var,val)==getCurrentDomainOf(var).contains(val)
    	 *      
    	 * @param var
    	 * @param val
    	 * @return true si la valeur val appartient au domain courant de la variable var
    	 */
    	public boolean isPresentInCurrentDomain(String var, String val){
    		Var v=x.getVar(var);
    		if(v.conv(val) == -1)
    			return false;
    		return v.consVal[v.conv(val)];
    	}

    	public Set<String> getCurrentDomainOf(String var){
    		Set<String> s=new HashSet<String>();
    		Var v=x.getVar(var);
    		for(int i=0; i<v.domain; i++){
    			if(v.consVal[i])
    				s.add(v.valeurs.get(i));
    			
    		}
    		return s;
    	}
    	
    	public Set<String> getDomainOf(String var){
    		Set<String> s=new HashSet<String>();
    		if(x!=null){
	    		Var v=x.getVar(var);
	    		for(int i=0; i<v.domain; i++){
	    			s.add(v.valeurs.get(i));
	    		}
    		}
    		return s;
    	}

    	/**
    	 * Retrieve for each valid value of the variable the minimal cost of the
    	 * configuration.
    	 * 
    	 * @param var
    	 *            a variable id
    	 * @return a map value->mincost
    	 */
    	public Map<String, Integer> minCosts(String var){
    		Var v=x.getVar(var);
    		Map<String, Integer> m;
    		m=x.minCosts(v.pos);
    		x.minMaxConsistanceMaj(v.pos, true);
    		return m;
    	} 

    	/**
    	 * Retrieve for each valid value of the variable the maximal cost of the
    	 * configuration.
    	 * 
    	 * @param var
    	 *            a variable id
    	 * @return a map value->maxcost
    	 */
    	public Map<String, Integer> maxCosts(String var){
    		Var v=x.getVar(var);
    		Map<String, Integer> m;
    		m=x.maxCosts(v.pos);
    		x.minMaxConsistanceMaj(v.pos, true);
    		return m;
    	} 

    	/**
    	 * Get all unassigned variables.
    	 * 
    	 * @return a set of non assigned variables.
    	 */
    	public Set<String> getFreeVariables(){
    		Set<String> s=new HashSet<String>();
    		for(int i=0; i<x.variables.size(); i++){
    			if(x.variables.get(i).consistenceSize()>1)
    				s.add(x.variables.get(i).name);
    		}
    		return s;
    	}

    	/**
    	 * Check that there is no more choice for the user.
    	 * 
    	 * @return true iff there is exactly one value left per variable.
    	 */
    	public boolean isConfigurationComplete(){
    		return getFreeVariables().size()==0;	
    	}

    	/**
    	 * Check there there is at least one value in each domain. Note that
    	 * depending of the level of consistency used, the configuration may of may
    	 * not be finally consistent.
    	 * 
    	 * @return true iff there is at least one value left per variable.
    	 */
    	public boolean isPossiblyConsistent(){
    		System.out.println("m&m : "+x.max.getvaldouble()+" "+x.min.getvaldouble());
    		return x.min.getvaldouble()!=-1;
    	}
	
    	protected void infos(String var){
    		Var v=x.getVar(var);
    		x.countingpondereOnFullDomain(v);
    	}
   
}