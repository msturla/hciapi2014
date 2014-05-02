package ar.edu.itba.hciapi.model;

import java.util.List;

/**
 * Immutable class representing a possible attribute that a product 
 * can have.
 *
 */
public class ProductAttribute {
	
	private long id;
	private String name;
	private List<String> values;
	
	// empty-arg constructor required by Gson
	ProductAttribute() { };
	
	public ProductAttribute(long id, String name, List<String> values) {
		this.id = id;
		this.name = name;
		this.values = values;
	}
	
	public String getName() {
		return name;
	}
	
	public long getId() {
		return id;
	}
	
	public List<String> getValues() {
		return values;
	}
	
}
