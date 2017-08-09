package com.gomeplus.amp.ad.security.shiro.model;

import java.io.Serializable;

/**
 * 自定义Principal对象，Subject携带更多信息
 * 
 * @author DèngBīn
 */
public class Principal implements Serializable {
	private static final long serialVersionUID = 1L;
	public Integer userId;
	public Long extUserId;
	public String name;
	public Integer advertiserId;
	public Integer dspAdvertiserId;
	public Integer adAccountId;
	public Integer dspAdAccountId;
	public Integer rebateAccountId;
	public Integer dspRebateAccountId;

	public Principal(Integer userId, String name) {
		this.userId = userId;
		this.extUserId = 0L;
		this.name = name;
		this.advertiserId = 0;
		this.dspAdvertiserId = 0;
		this.adAccountId = 0;
		this.dspAdAccountId = 0;
		this.rebateAccountId = 0;
		this.dspRebateAccountId = 0;
	}

	public Principal(Integer userId, Long extUserId, String name, Integer advertiserId,
		Integer dspAdvertiserId, Integer adAccountId, Integer dspAdAccountId,
		Integer rebateAccountId, Integer dspRebateAccountId) {
		this.userId = userId;
		this.extUserId = extUserId;
		this.name = name;
		this.advertiserId = advertiserId;
		this.dspAdvertiserId = dspAdvertiserId;
		this.adAccountId = adAccountId;
		this.dspAdAccountId = dspAdAccountId;
		this.rebateAccountId = rebateAccountId;
		this.dspRebateAccountId = dspRebateAccountId;
	}

	public Integer getUserId(){
		return userId;
	}

	public String getName() {
		return name;
	}

	public Integer getAdvertiserId() {
		return advertiserId;
	}

	public Integer getDspAdvertiserId() {
		return dspAdvertiserId;
	}

	public Integer getAdAccountId() {
		return adAccountId;
	}

	public Integer getDspAdAccountId() {
		return dspAdAccountId;
	}

	public Integer getRebateAccountId() {
		return rebateAccountId;
	}

	public Integer getDspRebateAccountId() {
		return dspRebateAccountId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setDspAdvertiserId(Integer dspAdvertiserId) {
		this.dspAdvertiserId = dspAdvertiserId;
	}

	/**
	 * 默认的<shiro:principal/>输出 显示账号名
	 * @return 账号名
	 */
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		Principal other = (Principal) object;
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		return true;
	}
}
