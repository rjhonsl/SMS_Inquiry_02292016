package com.santeh.inboxlistproject.util;

public class NewMessage_Contacts {
	public String contactID, contactName, contactNumber, contactType;
	private long id;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	//for handling TYPE
	public String getcontactID() {
		return contactID;
	}
	public void setcontactID(String id) {
		this.contactID = id;
	}
	//
	public String getcontactName() {
		return contactName;
	}
	public void setcontactName(String name) {
		this.contactName = name;
	}
	
	public String getcontactNumber() {
		return contactNumber;
	}
	public void setcontactNumber(String number) {
		this.contactNumber = number;
	}
	
	public String getcontactType() {
		return contactType;
	}
	public void setcontactType(String type) {
		this.contactType = type;
	}
}
