package net.msoetebier.nadia;

import java.util.HashMap;
import org.eclipse.ui.part.ViewPart;

public class Singleton extends HashMap<String, ViewPart>{

	private static final long serialVersionUID = -8360732184495326760L;
	private static Singleton instance;
	  
	private Singleton() {}
	
	public static Singleton getInstance() {
		if(instance==null){
			instance=new Singleton();
		}
		return instance;
    }
}
