package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.*;

import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import com.liangtee.jsuperlite.auditsys.values.UserConfs;

/**
 * author: liangtee 梁天一
 *
 * all rights reserved
 *
 */

@Entity
@Table(name = "T_USER")
public class User {

	@Id
	@Column(name = "UID")
	private long UID;

	@Column(name = "USER_NAME", nullable = false, unique = true)
	private String name;

	@Column(name = "PASSWD", nullable = false, unique = false)
	private String passwd;

	@Column(name = "EMAIL", unique = true)
	private String EMail;

	@Column(name = "PHONE_NUMBER", nullable = false)
	private String phoneNumber;

	@Column(name = "role_id", nullable = false)
	private int roleID;

	@Column(name = "role_name", nullable = false)
	private String roleName;

	@Column(name = "DEPT_ID",  unique = false)
	private int deptID;

	@Column(name = "CREATE_TIME", unique = false)
	private String createTime;

	@Column(name = "LAST_ACTIVE_TIME", unique = false)
	private String lastActiveTime;
	
	/**
	 * The account is disabled if the property "disabled" = 1
	 */
	@Column(name = "IS_ACTIVE", unique = false)
	private boolean isActive;

	protected User() {
		// TODO Auto-generated constructor stub
	}
	
	public User (String name, String passwd, int roleID, String roleName, int deptID, String email, String phoneNumber, boolean isActive) {
		this.UID = Long.parseLong(TimeFormater.format("yyMMddHHmmss"));
		this.name = name;
		this.roleID = roleID;
		this.roleName = roleName;
		this.passwd = passwd;
		this.deptID = deptID;
		this.EMail = email;
		this.phoneNumber = phoneNumber;
		this.isActive = isActive;
		this.createTime = TimeFormater.format("yyyy/MM/dd");
	}

	public User (Long userID, String name, String passwd, int roleID, String roleName, int deptID, String email, String phoneNumber, boolean isActive) {
		this.UID = userID;
		this.name = name;
		this.passwd = passwd;
		this.roleID = roleID;
		this.roleName = roleName;
		this.deptID = deptID;
		this.EMail = email;
		this.phoneNumber = phoneNumber;
		this.isActive = isActive;
		this.createTime = TimeFormater.format("yyyy/MM/dd");
	}

	public long getUID() {
		return UID;
	}

	public void setUID(long UID) {
		this.UID = UID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getEMail() {
		return EMail;
	}

	public void setEMail(String eMail) {
		EMail = eMail;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getDeptID() {
		return deptID;
	}

	public void setDeptID(int deptID) {
		this.deptID = deptID;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getLastActiveTime() {
		return lastActiveTime;
	}

	public void setLastActiveTime(String lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public int getRoleID() {
		return roleID;
	}

	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
