package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="USER")
public class User {
	@jakarta.persistence.Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@NotBlank(message="Name should not be Blank")
	@Size(min=2,max=25,message="minimum 2 and maximum 25 character allowed")
	private String name;
	@Column(unique = true)
	@Email(regexp="^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",message="Invalid Email id")
	private String email;
	//@Pattern(regexp="^(?=[^\\d_].*?\\d)\\w(\\w|[!@#$%]){7,20}",message="The password must have lenth between 8 to 20 character .The password also can not start with a digit, underscore or special character and must contain at least one numeric digit.")
	private String password;
	private String role;
	private boolean enabled;
	private String imageurl;
	@Column(length = 500)
	@Size(min=50,max=500,message="Write about yourself between 50 to 500 character")
	private String about;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true )      		//Cascading means --> if i delete user all the contact in the user will also get deleted automatically 
	private List<Contact> contact =new ArrayList<>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getImageurl() {
		return imageurl;
	}
	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	
	
	
	public List<Contact> getContact() {
		return contact;
	}
	public void setContact(List<Contact> contact) {
		this.contact = contact;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
				+ ", enabled=" + enabled + ", imageurl=" + imageurl + ", about=" + about + ", contact=" + contact + "]";
	}
	
	
	
	
	
	

}
