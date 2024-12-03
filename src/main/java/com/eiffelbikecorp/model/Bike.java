package com.eiffelbikecorp.model;

import java.util.ArrayList;
import java.util.List;

public class Bike {
    private String id;
    private String condition;
    private float price;
    private boolean isAvailable;
    private static WaitingList waitingList = new WaitingList();
    private  float grade ; 
    private  int numberOfGrades= 1 ;
    private boolean sold= false;
    private List<String> comments = new ArrayList<>(); // we are keeping comments annonyme
    // Constructor
    
    public Bike() {
       
    }
    
    
    public Bike(String id, float grade, boolean isAvailable, float price) {
        this.id = id;
        this.grade = grade;
        this.price=price;
        if (grade>4) {this.condition = "perfect";}
    	else if(grade <4 && grade>2.5) {this.condition = "good";}
    	else {this.condition = "bad";}
        this.isAvailable = isAvailable;
        this.setSold(false);
    }
    

    
    // Getters and Setters
    
    
    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
    
    
    
    public WaitingList getwaitingList() {
        return waitingList;
    }
    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition() {
    	float note= getGrade();
    	if (note>4) {this.condition = "perfect";}
    	else if(note <=4 && note>2.5) {this.condition = "good";}
    	else {this.condition = "bad";}
    	
    	
        
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    // Grades
    
    public float getGrade() {
        return grade;
    }
    
    public void setGrade(float grade) {
        this.grade = grade;
    }
    
    public void updateGrade(float grade_1) {
    	float grade_2 = this.grade*this.numberOfGrades;
        ++this.numberOfGrades;
        System.out.println("DEBUG: number of grades = " + numberOfGrades);
        grade_2+=grade_1;
        this.grade=grade_2/this.numberOfGrades;
        System.out.println("DEBUG: new this.grade = " + this.grade);
        
    }
    
    // Comments
    public List<String> getComments() {
        return comments;
    }

    public void addComment(String message) {
    	comments.add(message);
    }


	public boolean isSold() {
		return sold;
	}


	public void setSold(boolean solde) {
		this.sold = solde;
	}
    
	public float getNumberOfGrades() {
        return numberOfGrades;
    }
    
    
}
