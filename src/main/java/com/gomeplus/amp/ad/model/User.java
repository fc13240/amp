package com.gomeplus.amp.ad.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * 用户 model
 * @author DèngBīn
 */
@Entity
@Table(name = "ams_user")
@DynamicUpdate(true)
@DynamicInsert(true)
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "user_id", unique = true, nullable = false)
	private Integer userId;
	@Column(name = "ext_user_id", nullable = false)
	private Long extUserId;
	@Column(name = "login_name", nullable = false)
	private String loginName;
	@Column(name = "nickname", nullable = false)
	private String nickname;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "email", nullable = false)
	private String email;
	@Column(name = "mobile", length = 20)
	private String mobile;
	@Column(name = "gender", nullable = false)
	private Integer gender;
	@Column(name = "address", nullable = false)
	private String address;
	@Column(name = "avatar", nullable = false)
	private String avatar;
	@Column(name = "is_internal", nullable = false)
	private Integer isInternal;
	@Column(name = "type", nullable = false)
	private Integer type;
	@Column(name = "status", nullable = false)
	private Integer status;

	@Column(name = "is_test", nullable = false)
	private Integer isTest;
	@Column(name = "test_start_time", nullable = false)
	private Date testStartTime;
	@Column(name = "test_end_time", nullable = false)
	private Date testEndTime;

	@Column(name = "register_time", nullable = false)
	private Date registerTime;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;
	@Column(name = "last_login_time", nullable = false)
	private Date lastLoginTime;
	// 外部用户店铺id
	@Column(name = "ext_shop_id", nullable = false)
	private Integer extShopId;
	// 登录来源 gomeOnline, gomePop, gomePlus
	@Column(name = "login_from", nullable = false)
	private String loginFrom;

	@Transient
	private Account adAccount = new Account();
	@Transient
	private Account rebateAccount = new Account();
	@Transient
	private Advertiser advertiser = new Advertiser();

	public User() {
	}

	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public User(Integer userId) {
		this.userId = userId;
	}

	public enum Status {
		DELETE(-1), OFFLINE(0), NORMAL(1);
		private Integer value;

		private Status(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	// 用户类型: COMMON(0) 普通用户; MERCHANT(1) 商家用户;
	public enum Type {
		COMMON(0), MERCHANT(1);
		private Integer value;

		private Type(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	// 登录来源: ONLINE("GOME_ONLINE") 国美在线; POP("GOME_POP") 商家后台; PLUS("GOME_PLUS") 国美+;
	public enum Gome {
		ONLINE("GOME_ONLINE"), POP("GOME_POP"), PLUS("GOME_PLUS");
		private String value;

		private Gome(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Long getExtUserId() {
		return extUserId;
	}
	public void setExtUserId(Long extUserId) {
		this.extUserId = extUserId;
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getIsInternal() {
		return isInternal;
	}
	public void setIsInternal(Integer isInternal) {
		this.isInternal = isInternal;
	}

	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

	public Integer getExtShopId() {
		return extShopId;
	}

	public void setExtShopId(Integer extShopId) {
		this.extShopId = extShopId;
	}

	public String getLoginFrom() {
		return loginFrom;
	}

	public void setLoginFrom(String loginFrom) {
		this.loginFrom = loginFrom;
	}

	@Transient
	public Account getAdAccount() {
		return adAccount;
	}

	@Transient
	public void setAdAccount(Account adAccount) {
		this.adAccount = adAccount;
	}

	@Transient
	public Account getRebateAccount() {
		return rebateAccount;
	}

	@Transient
	public void setRebateAccount(Account rebateAccount) {
		this.rebateAccount = rebateAccount;
	}

	@Transient
	public Advertiser getAdvertiser() {
		return advertiser;
	}

	@Transient
	public void setAdvertiser(Advertiser advertiser) {
		this.advertiser = advertiser;
	}

	public Integer getIsTest() {
		return isTest;
	}

	public void setIsTest(Integer isTest) {
		this.isTest = isTest;
	}

	public Date getTestStartTime() {
		return testStartTime;
	}

	public void setTestStartTime(Date testStartTime) {
		this.testStartTime = testStartTime;
	}

	public Date getTestEndTime() {
		return testEndTime;
	}

	public void setTestEndTime(Date testEndTime) {
		this.testEndTime = testEndTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((extUserId == null) ? 0 : extUserId.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((lastLoginTime == null) ? 0 : lastLoginTime.hashCode());
		result = prime * result + ((mobile == null) ? 0 : mobile.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((loginName == null) ? 0 : loginName.hashCode());
		result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
		result = prime * result + ((avatar == null) ? 0 : avatar.hashCode());
		result = prime * result + ((registerTime == null) ? 0 : registerTime.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		if (address == null) {
			if (other.address != null) {
				return false;
			}
		} else if (!address.equals(other.address)) {
			return false;
		}
		if (createTime == null) {
			if (other.createTime != null) {
				return false;
			}
		} else if (!createTime.equals(other.createTime)) {
			return false;
		}
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!email.equals(other.email)) {
			return false;
		}
		if (extUserId == null) {
			if (other.extUserId != null) {
				return false;
			}
		} else if (!extUserId.equals(other.extUserId)) {
			return false;
		}
		if (gender == null) {
			if (other.gender != null) {
				return false;
			}
		} else if (!gender.equals(other.gender)) {
			return false;
		}
		if (lastLoginTime == null) {
			if (other.lastLoginTime != null) {
				return false;
			}
		} else if (!lastLoginTime.equals(other.lastLoginTime)) {
			return false;
		}
		if (mobile == null) {
			if (other.mobile != null) {
				return false;
			}
		} else if (!mobile.equals(other.mobile)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (status == null) {
			if (other.status != null) {
				return false;
			}
		} else if (!status.equals(other.status)) {
			return false;
		}
		if (updateTime == null) {
			if (other.updateTime != null) {
				return false;
			}
		} else if (!updateTime.equals(other.updateTime)) {
			return false;
		}
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		if (loginName == null) {
			if (other.loginName != null) {
				return false;
			}
		} else if (!loginName.equals(other.loginName)) {
			return false;
		}
		if (nickname == null) {
			if (other.nickname != null) {
				return false;
			}
		} else if (!nickname.equals(other.nickname)) {
			return false;
		}
		if (avatar == null) {
			if (other.avatar != null) {
				return false;
			}
		} else if (!avatar.equals(other.avatar)) {
			return false;
		}
		if (registerTime == null) {
			if (other.registerTime != null) {
				return false;
			}
		} else if (!registerTime.equals(other.registerTime)) {
			return false;
		}
		return true;
	}

}
