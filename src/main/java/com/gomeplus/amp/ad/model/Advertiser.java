package com.gomeplus.amp.ad.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * 广告主 model
 * @author DèngBīn
 */
@Entity
@Table(name = "ams_advertiser")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Advertiser implements Serializable {

	private static final long serialVersionUID = -1878555511304306084L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "advertiser_id", unique = true, nullable = false)
	private Integer advertiserId;

	// 投放系统广告主ID
	@Column(name = "dsp_advertiser_id", nullable = false)
	private Integer dspAdvertiserId;

	/* ------------营业执照信息(副本)------------- */
	// 公司性质
	@Column(name = "company_nature", nullable = false)
	private Integer companyNature;

	// 公司名称
	@Column(name = "company_name", nullable = false)
	private String companyName;

	// 注册号
	@Column(name = "biz_license_number", nullable = false)
	private String bizLicenseNumber;

	// 法人代表姓名
	@Column(name = "legal_repr_name", nullable = false)
	private String legalReprName;

	// 法人代表身份证号
	@Column(name = "legal_repr_id_number", nullable = false)
	private String legalReprIdNumber;

	// 请上传法人身份证正面
	@Column(name = "legal_repr_id_image", nullable = false)
	private String legalReprIdImage;

	// 请上传法人身份证背面
	@Column(name = "legal_repr_id_back_image", nullable = false)
	private String legalReprIdBackImage;

	// 营业执照详细地址不能为空
	@Column(name = "biz_license_address", nullable = false)
	private String bizLicenseAddress;

	// 成立日期
	@Column(name = "company_found_date", nullable = false)
	private Date companyFoundDate;

	// 营业开始日期
	@Column(name = "biz_start_date", nullable = false)
	private Date bizStartDate;

	// 营业结束日期
	@Column(name = "biz_end_date", nullable = false)
	private Date bizEndDate;

	// 注册资本
	@Column(name = "reg_capital", nullable = false)
	private String regCapital;

	// 经营范围
	@Column(name = "biz_scope", nullable = false)
	private String bizScope;

	// 营业执照副本
	@Column(name = "biz_license_image", nullable = false)
	private String bizLicenseImage;

	// 公司详细地址
	@Column(name = "company_address", nullable = false)
	private String companyAddress;

	// 公司电话
	@Column(name = "company_phone", nullable = false)
	private String companyPhone;

	// 公司紧急联系人
	@Column(name = "emer_contact", nullable = false)
	private String emerContact;

	// 公司紧急联系人
	@Column(name = "emer_contact_phone", nullable = false)
	private String emerContactPhone;

	// 公司邮箱
	@Column(name = "company_email", nullable = false)
	private String companyEmail;


	/* ------------组织机构代码证------------- */
	// 组织机构代码
	@Column(name = "org_code", nullable = false)
	private String orgCode;

	// 上传组织机构代码副本
	@Column(name = "org_code_image", nullable = false)
	private String orgCodeImage;


	/* ------------税务登记证------------- */
	// 税号不能为空
	@Column(name = "tax_number", nullable = false)
	private String taxNumber;

	// 纳税人类型
	@Column(name = "taxpayer_type", nullable = false)
	private Integer taxpayerType;

	// 纳税类型税码
	@Column(name = "tax_code", nullable = false)
	private Integer taxCode;

	// 上传税务登记证
	@Column(name = "tax_reg_cert_image", nullable = false)
	private String taxRegCertImage;

	// 一般纳税人资格
	@Column(name = "taxpayer_qualification_image", nullable = false)
	private String taxpayerQualificationImage;


	/* ------------开户银行许可证------------- */
	// 银行开户名
	@Column(name = "bank_account_name", nullable = false)
	private String bankAccountName;

	// 公司银行账号
	@Column(name = "bank_account", nullable = false)
	private String bankAccount;

	// 开户银行支行名称
	@Column(name = "bank_branch_name", nullable = false)
	private String bankBranchName;

	// 开户银行支行联行号
	@Column(name = "bank_branch_line_number", nullable = false)
	private String bankBranchLineNumber;

	// 开户银行支行地址
	@Column(name = "bank_branch_address", nullable = false)
	private String bankBranchAddress;

	// 银行开户许可证
	@Column(name = "bank_account_permission_image", nullable = false)
	private String bankAccountPermissionImage;

	// 用户ID
	@Column(name = "user_id", nullable = false)
	private Integer userId;

	// 审核状态
	@Column(name = "approve_status", nullable = false)
	private Integer approveStatus;

	// 备注
	@Column(name = "remark", nullable = false)
	private String remark;

	// 状态
	@Column(name = "status", nullable = false)
	private Integer status;

	// 创建时间
	@Column(name = "create_time", nullable = false)
	private Date createTime;

	// 修改时间
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	// SAP客户编码
	@Column(name = "customer_no", nullable = false)
	private String customerNo;
	
	//广告主审核拒绝原因
	@Column(name = "reject_reasons", nullable = false)
	private String rejectReasons;
	
	public String getRejectReasons() {
		return rejectReasons;
	}

	public void setRejectReasons(String rejectReasons) {
		this.rejectReasons = rejectReasons;
	}

	public enum Status {
		DELETE(-1), OFFLINE(0), ONLINE(1);

		private Integer value;

		private Status(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	public enum ApproveStatus {
		FAIL(-1), PENDING(0), PASS(1);

		private Integer value;

		private ApproveStatus(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}


	public Integer getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(Integer advertiserId) {
		this.advertiserId = advertiserId;
	}

	public Integer getDspAdvertiserId() {
		return dspAdvertiserId;
	}

	public void setDspAdvertiserId(Integer dspAdvertiserId) {
		this.dspAdvertiserId = dspAdvertiserId;
	}

	public Integer getCompanyNature() {
		return companyNature;
	}

	public void setCompanyNature(Integer companyNature) {
		this.companyNature = companyNature;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getBizLicenseNumber() {
		return bizLicenseNumber;
	}

	public void setBizLicenseNumber(String bizLicenseNumber) {
		this.bizLicenseNumber = bizLicenseNumber;
	}

	public String getLegalReprName() {
		return legalReprName;
	}

	public void setLegalReprName(String legalReprName) {
		this.legalReprName = legalReprName;
	}

	public String getLegalReprIdNumber() {
		return legalReprIdNumber;
	}

	public void setLegalReprIdNumber(String legalReprIdNumber) {
		this.legalReprIdNumber = legalReprIdNumber;
	}

	public String getLegalReprIdImage() {
		return legalReprIdImage;
	}

	public void setLegalReprIdImage(String legalReprIdImage) {
		this.legalReprIdImage = legalReprIdImage;
	}

	public String getLegalReprIdBackImage() {
		return legalReprIdBackImage;
	}

	public void setLegalReprIdBackImage(String legalReprIdBackImage) {
		this.legalReprIdBackImage = legalReprIdBackImage;
	}

	public String getBizLicenseAddress() {
		return bizLicenseAddress;
	}

	public void setBizLicenseAddress(String bizLicenseAddress) {
		this.bizLicenseAddress = bizLicenseAddress;
	}

	public Date getCompanyFoundDate() {
		return companyFoundDate;
	}

	public void setCompanyFoundDate(Date companyFoundDate) {
		this.companyFoundDate = companyFoundDate;
	}

	public Date getBizStartDate() {
		return bizStartDate;
	}

	public void setBizStartDate(Date bizStartDate) {
		this.bizStartDate = bizStartDate;
	}

	public Date getBizEndDate() {
		return bizEndDate;
	}

	public void setBizEndDate(Date bizEndDate) {
		this.bizEndDate = bizEndDate;
	}

	public String getRegCapital() {
		return regCapital;
	}

	public void setRegCapital(String regCapital) {
		this.regCapital = regCapital;
	}

	public String getBizScope() {
		return bizScope;
	}

	public void setBizScope(String bizScope) {
		this.bizScope = bizScope;
	}

	public String getBizLicenseImage() {
		return bizLicenseImage;
	}

	public void setBizLicenseImage(String bizLicenseImage) {
		this.bizLicenseImage = bizLicenseImage;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCompanyPhone() {
		return companyPhone;
	}

	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}

	public String getEmerContact() {
		return emerContact;
	}

	public void setEmerContact(String emerContact) {
		this.emerContact = emerContact;
	}

	public String getEmerContactPhone() {
		return emerContactPhone;
	}

	public void setEmerContactPhone(String emerContactPhone) {
		this.emerContactPhone = emerContactPhone;
	}

	public String getCompanyEmail() {
		return companyEmail;
	}

	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgCodeImage() {
		return orgCodeImage;
	}

	public void setOrgCodeImage(String orgCodeImage) {
		this.orgCodeImage = orgCodeImage;
	}

	public String getTaxNumber() {
		return taxNumber;
	}

	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	public Integer getTaxpayerType() {
		return taxpayerType;
	}

	public void setTaxpayerType(Integer taxpayerType) {
		this.taxpayerType = taxpayerType;
	}

	public Integer getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(Integer taxCode) {
		this.taxCode = taxCode;
	}

	public String getTaxRegCertImage() {
		return taxRegCertImage;
	}

	public void setTaxRegCertImage(String taxRegCertImage) {
		this.taxRegCertImage = taxRegCertImage;
	}

	public String getTaxpayerQualificationImage() {
		return taxpayerQualificationImage;
	}

	public void setTaxpayerQualificationImage(String taxpayerQualificationImage) {
		this.taxpayerQualificationImage = taxpayerQualificationImage;
	}

	public String getBankAccountName() {
		return bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBankBranchName() {
		return bankBranchName;
	}

	public void setBankBranchName(String bankBranchName) {
		this.bankBranchName = bankBranchName;
	}

	public String getBankBranchLineNumber() {
		return bankBranchLineNumber;
	}

	public void setBankBranchLineNumber(String bankBranchLineNumber) {
		this.bankBranchLineNumber = bankBranchLineNumber;
	}

	public String getBankBranchAddress() {
		return bankBranchAddress;
	}

	public void setBankBranchAddress(String bankBranchAddress) {
		this.bankBranchAddress = bankBranchAddress;
	}

	public String getBankAccountPermissionImage() {
		return bankAccountPermissionImage;
	}

	public void setBankAccountPermissionImage(String bankAccountPermissionImage) {
		this.bankAccountPermissionImage = bankAccountPermissionImage;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(Integer approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((advertiserId == null) ? 0 : advertiserId.hashCode());
		result = prime * result + ((approveStatus == null) ? 0 : approveStatus.hashCode());
		result = prime * result + ((bankAccount == null) ? 0 : bankAccount.hashCode());
		result = prime * result + ((bankAccountName == null) ? 0 : bankAccountName.hashCode());
		result = prime * result + ((bankAccountPermissionImage == null) ? 0 : bankAccountPermissionImage.hashCode());
		result = prime * result + ((bankBranchAddress == null) ? 0 : bankBranchAddress.hashCode());
		result = prime * result + ((bankBranchLineNumber == null) ? 0 : bankBranchLineNumber.hashCode());
		result = prime * result + ((bankBranchName == null) ? 0 : bankBranchName.hashCode());
		result = prime * result + ((bizEndDate == null) ? 0 : bizEndDate.hashCode());
		result = prime * result + ((bizLicenseAddress == null) ? 0 : bizLicenseAddress.hashCode());
		result = prime * result + ((bizLicenseImage == null) ? 0 : bizLicenseImage.hashCode());
		result = prime * result + ((bizLicenseNumber == null) ? 0 : bizLicenseNumber.hashCode());
		result = prime * result + ((bizScope == null) ? 0 : bizScope.hashCode());
		result = prime * result + ((bizStartDate == null) ? 0 : bizStartDate.hashCode());
		result = prime * result + ((companyAddress == null) ? 0 : companyAddress.hashCode());
		result = prime * result + ((companyEmail == null) ? 0 : companyEmail.hashCode());
		result = prime * result + ((companyFoundDate == null) ? 0 : companyFoundDate.hashCode());
		result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
		result = prime * result + ((companyNature == null) ? 0 : companyNature.hashCode());
		result = prime * result + ((companyPhone == null) ? 0 : companyPhone.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((dspAdvertiserId == null) ? 0 : dspAdvertiserId.hashCode());
		result = prime * result + ((emerContact == null) ? 0 : emerContact.hashCode());
		result = prime * result + ((emerContactPhone == null) ? 0 : emerContactPhone.hashCode());
		result = prime * result + ((legalReprIdBackImage == null) ? 0 : legalReprIdBackImage.hashCode());
		result = prime * result + ((legalReprIdImage == null) ? 0 : legalReprIdImage.hashCode());
		result = prime * result + ((legalReprIdNumber == null) ? 0 : legalReprIdNumber.hashCode());
		result = prime * result + ((legalReprName == null) ? 0 : legalReprName.hashCode());
		result = prime * result + ((orgCode == null) ? 0 : orgCode.hashCode());
		result = prime * result + ((orgCodeImage == null) ? 0 : orgCodeImage.hashCode());
		result = prime * result + ((regCapital == null) ? 0 : regCapital.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((taxCode == null) ? 0 : taxCode.hashCode());
		result = prime * result + ((taxNumber == null) ? 0 : taxNumber.hashCode());
		result = prime * result + ((taxRegCertImage == null) ? 0 : taxRegCertImage.hashCode());
		result = prime * result + ((taxpayerQualificationImage == null) ? 0 : taxpayerQualificationImage.hashCode());
		result = prime * result + ((taxpayerType == null) ? 0 : taxpayerType.hashCode());
		result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((customerNo == null) ? 0 : customerNo.hashCode());
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
		Advertiser other = (Advertiser) obj;
		if (advertiserId == null) {
			if (other.advertiserId != null) {
				return false;
			}
		} else if (!advertiserId.equals(other.advertiserId)) {
			return false;
		}
		if (approveStatus == null) {
			if (other.approveStatus != null) {
				return false;
			}
		} else if (!approveStatus.equals(other.approveStatus)) {
			return false;
		}
		if (bankAccount == null) {
			if (other.bankAccount != null) {
				return false;
			}
		} else if (!bankAccount.equals(other.bankAccount)) {
			return false;
		}
		if (bankAccountName == null) {
			if (other.bankAccountName != null) {
				return false;
			}
		} else if (!bankAccountName.equals(other.bankAccountName)) {
			return false;
		}
		if (bankAccountPermissionImage == null) {
			if (other.bankAccountPermissionImage != null) {
				return false;
			}
		} else if (!bankAccountPermissionImage.equals(other.bankAccountPermissionImage)) {
			return false;
		}
		if (bankBranchAddress == null) {
			if (other.bankBranchAddress != null) {
				return false;
			}
		} else if (!bankBranchAddress.equals(other.bankBranchAddress)) {
			return false;
		}
		if (bankBranchLineNumber == null) {
			if (other.bankBranchLineNumber != null) {
				return false;
			}
		} else if (!bankBranchLineNumber.equals(other.bankBranchLineNumber)) {
			return false;
		}
		if (bankBranchName == null) {
			if (other.bankBranchName != null) {
				return false;
			}
		} else if (!bankBranchName.equals(other.bankBranchName)) {
			return false;
		}
		if (bizEndDate == null) {
			if (other.bizEndDate != null) {
				return false;
			}
		} else if (!bizEndDate.equals(other.bizEndDate)) {
			return false;
		}
		if (bizLicenseAddress == null) {
			if (other.bizLicenseAddress != null) {
				return false;
			}
		} else if (!bizLicenseAddress.equals(other.bizLicenseAddress)) {
			return false;
		}
		if (bizLicenseImage == null) {
			if (other.bizLicenseImage != null) {
				return false;
			}
		} else if (!bizLicenseImage.equals(other.bizLicenseImage)) {
			return false;
		}
		if (bizLicenseNumber == null) {
			if (other.bizLicenseNumber != null) {
				return false;
			}
		} else if (!bizLicenseNumber.equals(other.bizLicenseNumber)) {
			return false;
		}
		if (bizScope == null) {
			if (other.bizScope != null) {
				return false;
			}
		} else if (!bizScope.equals(other.bizScope)) {
			return false;
		}
		if (bizStartDate == null) {
			if (other.bizStartDate != null) {
				return false;
			}
		} else if (!bizStartDate.equals(other.bizStartDate)) {
			return false;
		}
		if (companyAddress == null) {
			if (other.companyAddress != null) {
				return false;
			}
		} else if (!companyAddress.equals(other.companyAddress)) {
			return false;
		}
		if (companyEmail == null) {
			if (other.companyEmail != null) {
				return false;
			}
		} else if (!companyEmail.equals(other.companyEmail)) {
			return false;
		}
		if (companyFoundDate == null) {
			if (other.companyFoundDate != null) {
				return false;
			}
		} else if (!companyFoundDate.equals(other.companyFoundDate)) {
			return false;
		}
		if (companyName == null) {
			if (other.companyName != null) {
				return false;
			}
		} else if (!companyName.equals(other.companyName)) {
			return false;
		}
		if (companyNature == null) {
			if (other.companyNature != null) {
				return false;
			}
		} else if (!companyNature.equals(other.companyNature)) {
			return false;
		}
		if (companyPhone == null) {
			if (other.companyPhone != null) {
				return false;
			}
		} else if (!companyPhone.equals(other.companyPhone)) {
			return false;
		}
		if (createTime == null) {
			if (other.createTime != null) {
				return false;
			}
		} else if (!createTime.equals(other.createTime)) {
			return false;
		}
		if (dspAdvertiserId == null) {
			if (other.dspAdvertiserId != null) {
				return false;
			}
		} else if (!dspAdvertiserId.equals(other.dspAdvertiserId)) {
			return false;
		}
		if (emerContact == null) {
			if (other.emerContact != null) {
				return false;
			}
		} else if (!emerContact.equals(other.emerContact)) {
			return false;
		}
		if (emerContactPhone == null) {
			if (other.emerContactPhone != null) {
				return false;
			}
		} else if (!emerContactPhone.equals(other.emerContactPhone)) {
			return false;
		}
		if (legalReprIdBackImage == null) {
			if (other.legalReprIdBackImage != null) {
				return false;
			}
		} else if (!legalReprIdBackImage.equals(other.legalReprIdBackImage)) {
			return false;
		}
		if (legalReprIdImage == null) {
			if (other.legalReprIdImage != null) {
				return false;
			}
		} else if (!legalReprIdImage.equals(other.legalReprIdImage)) {
			return false;
		}
		if (legalReprIdNumber == null) {
			if (other.legalReprIdNumber != null) {
				return false;
			}
		} else if (!legalReprIdNumber.equals(other.legalReprIdNumber)) {
			return false;
		}
		if (legalReprName == null) {
			if (other.legalReprName != null) {
				return false;
			}
		} else if (!legalReprName.equals(other.legalReprName)) {
			return false;
		}
		if (orgCode == null) {
			if (other.orgCode != null) {
				return false;
			}
		} else if (!orgCode.equals(other.orgCode)) {
			return false;
		}
		if (orgCodeImage == null) {
			if (other.orgCodeImage != null) {
				return false;
			}
		} else if (!orgCodeImage.equals(other.orgCodeImage)) {
			return false;
		}
		if (regCapital == null) {
			if (other.regCapital != null) {
				return false;
			}
		} else if (!regCapital.equals(other.regCapital)) {
			return false;
		}
		if (remark == null) {
			if (other.remark != null) {
				return false;
			}
		} else if (!remark.equals(other.remark)) {
			return false;
		}
		if (status == null) {
			if (other.status != null) {
				return false;
			}
		} else if (!status.equals(other.status)) {
			return false;
		}
		if (taxCode == null) {
			if (other.taxCode != null) {
				return false;
			}
		} else if (!taxCode.equals(other.taxCode)) {
			return false;
		}
		if (taxNumber == null) {
			if (other.taxNumber != null) {
				return false;
			}
		} else if (!taxNumber.equals(other.taxNumber)) {
			return false;
		}
		if (taxRegCertImage == null) {
			if (other.taxRegCertImage != null) {
				return false;
			}
		} else if (!taxRegCertImage.equals(other.taxRegCertImage)) {
			return false;
		}
		if (taxpayerQualificationImage == null) {
			if (other.taxpayerQualificationImage != null) {
				return false;
			}
		} else if (!taxpayerQualificationImage.equals(other.taxpayerQualificationImage)) {
			return false;
		}
		if (taxpayerType == null) {
			if (other.taxpayerType != null) {
				return false;
			}
		} else if (!taxpayerType.equals(other.taxpayerType)) {
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
		if (customerNo == null) {
			if (other.customerNo != null) {
				return false;
			}
		} else if (!customerNo.equals(other.customerNo)) {
			return false;
		}
		return true;
	}
}
