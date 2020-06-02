package compilateur;

/*   (C) Copyright 2015, Pierre-François Gimenez
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Classe statique qui gere les sauvegardes et chargements.
 * @author Stud, pf
 *
 */

public class DataSaver {

    private DataSaver()
    {}
    
    public static void supprimer(String filename)
    {
        // System.out.println("Suppression de " + filename);
        try
        {
            (new java.io.File(filename)).delete();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static <T> void sauvegarder(T obj, String filename)
    {
    	try {
			java.io.File fichier_creation;
			FileOutputStream fichier;
			ObjectOutputStream oos;
			
			fichier_creation = new java.io.File(filename);
			fichier_creation.createNewFile();
			fichier = new FileOutputStream(filename);
			oos = new ObjectOutputStream(fichier);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		}
		catch(Exception e)
		{
			System.out.println("Erreur lors de la création ou de l'écriture du fichier. Vérifiez que vous avez les droits et que le dossier existe bien.");
			e.printStackTrace();
		}
    }
    
    public static Object charger(String filename)
    {
		try {
			FileInputStream fichier = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fichier);
			Object obj = ois.readObject();
			ois.close();
			return obj;
		}
		catch(Exception e)
		{
			System.out.println("Fichier "+filename+" introuvable");
			return null;
		}
    }
}