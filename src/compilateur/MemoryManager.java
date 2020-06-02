package compilateur;

import java.util.ArrayList;

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

public class MemoryManager {

	private int indiceFirstAvailable;
	private ArrayList<VarPoidsId> objects = new ArrayList<VarPoidsId>();
	private static MemoryManager instance;
	
	private MemoryManager()
	{
	}
	
	public static MemoryManager getMemoryManager()
	{
		if(instance == null)
			instance = new MemoryManager();
		return instance;
	}

	public VarPoidsId getObject()
	{
		VarPoidsId out;
		try {
			out = objects.get(indiceFirstAvailable);
			if(!out.available)
				System.out.println("Intégrité rompue dans le MemoryManager: object déjà utilisé");
			out.available = false;

		}
		catch(IndexOutOfBoundsException e)
		{
			out = new VarPoidsId(indiceFirstAvailable);
			objects.add(out);
		}
		indiceFirstAvailable++;
		return out;
	}
	
	public void destroyObject(VarPoidsId object)
	{
		if(object.available)
			System.out.println("Intégrité rompue dans le MemoryManager: objet à détruire déjà détruit");
		indiceFirstAvailable--;
		VarPoidsId tmp = objects.get(indiceFirstAvailable);
		objects.set(indiceFirstAvailable, object);
		tmp.memoryId = object.memoryId;
		objects.set(object.memoryId, tmp);
		object.memoryId = indiceFirstAvailable;
		object.clear();
		object.available = true;
//		System.out.println(indiceFirstAvailable);
	}
	
}
