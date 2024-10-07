package model.entities;

import java.util.Date;

public class Product {

	private Integer id;
	private String name;
	private Double price;
	private Date manufacturing;
	private String made;
	
	private Category category;
	
	public Product() {
	}

	public Product(Integer id, String name, Double price, Date manufacturing, String made, Category category) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.manufacturing = manufacturing;
		this.made = made;
		this.category = category;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Date getManufacturing() {
		return manufacturing;
	}

	public void setManufacturing(Date manufacturing) {
		this.manufacturing = manufacturing;
	}

	public String getMade() {
		return made;
	}

	public void setMade(String made) {
		this.made = made;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", price=" + price + ", manufacturing=" + manufacturing
				+ ", made=" + made + ", category=" + category + "]";
	}
	
	
	
}
