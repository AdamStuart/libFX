package model.bio;

import java.util.ArrayList;
import java.util.List;

import services.bridgedb.BridgeDbIdMapper;
import services.bridgedb.MappingSource;
import util.StringUtil;

public class GeneList extends ArrayList<Gene> {

	/**
	 * 	GeneList is a first class data structure that can be searched,
	 *  compared, iterated, or manipulated
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Species species;
	public Species getSpecies() { return species;	}

	public GeneList()
	{
		super();
	}
	
	public GeneList(Species sp)
	{
		super();
		species = sp;
	}
	
	public GeneList(GeneList orig)
	{
		super();
		addAll(orig);
		species = orig.getSpecies();
	}
	
	public GeneList(List<Gene> orig, Species sp)
	{
		super();
		addAll(orig);
		species = sp;
	}
	//--------------------------------------------------------------------
	public boolean add(Gene g)
	{
		if ( find(g.getName()) == null) 
			return super.add(g);
		return false;
	}
	
	public GeneList intersection(GeneList other)
	{
		GeneList intersection = new GeneList();
		for (Gene g : this)
			if (other.find(g.getName()) != null)
				intersection.add(g);
		return intersection;
	}
	
	public GeneList union(GeneList other)
	{
		GeneList union = new GeneList(this);
		for (Gene g : this)
			if (other.find(g.getName()) == null)
				union.add(g);
		return union;
	}
	static public Gene findInList(List<Gene> list, String nameOrId)
	{
		if (nameOrId == null) return null;
		String name = nameOrId.trim();
		for (Gene g : list)
		{
			if (name.equalsIgnoreCase(g.getName())) return g;
			if (name.equalsIgnoreCase(g.getId())) return g;
		}
		return null;
	}
	public Gene find(String nameOrId)
	{
		if (nameOrId == null) return null;
		String name = nameOrId.trim();
		for (Gene g : this)
		{
			if (name.equalsIgnoreCase(g.getName())) return g;
			if (name.equalsIgnoreCase(g.getId())) return g;
		}
		return null;
	}
	public Gene find(Gene g)	{		return find(g.getName());	}

	//--------------------------------------------------------------------

	static String TAB = "\t";
	static String NL = "\n";
	public static String BDB = "http://webservice.bridgedb.org/";
	public void fillIdlist()
	{
		if (species == null) 
			species = Species.Human;
		StringBuilder str = new StringBuilder();
		for (Gene g : this)
		{
			if (StringUtil.hasText(g.getIdlist())) continue;
			String name = g.getName();
			MappingSource sys = MappingSource.guessSource(species, name);
			str.append(name + TAB + sys.system() + NL);
		}
		try
		{
			List<String> output = BridgeDbIdMapper.post(BDB, species.common(), "xrefsBatch", "", str.toString());
			for (String line : output)
			{
				String [] flds = line.split("\t");
				String name = flds[0];
				String allrefs = flds[2];
				for (Gene g : this)
				{
					if (!g.getName().equals(name)) continue;
					System.out.println("setting ids for " + name );	
					g.setIdlist(allrefs);
					g.setEnsembl(BridgeDbIdMapper.getEnsembl(allrefs));
				}
			}
		}
		catch(Exception ex) 
		{ 
			System.err.println(ex.getMessage());	
		}
	}

}
