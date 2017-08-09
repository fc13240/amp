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
 * 页面模板 model
 * @author DèngBīn
 *
 */
@Entity
@Table(name = "ams_webpage_template")
@DynamicUpdate(true)
@DynamicInsert(true)
public class PageTemplate implements Serializable  {

	private static final long serialVersionUID = -1445370553939163016L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "webpage_template_id", unique = true, nullable = false)
	private Integer pageTemplateId;

	// 模版标题
	@Column(name = "title", nullable = false)
	private String title;

	// 平台 1APP 2WAP 3PC
	@Column(name = "platform", nullable = false)
	private Integer platform;

	// 模版名称
	@Column(name = "name", nullable = false)
	private String name;

	// 模版预览
	@Column(name = "preview", nullable = false)
	private String preview;

	// 状态(默认值0) -1删除 0下线 1上线
	@Column(name = "status", nullable = false)
	private Integer status;

	// 添加页面模版的账户ID(默认值0)
	@Column(name = "account_id", nullable = false)
	private Integer accountId;

    // 添加页面模版的账户名(默认值"")
    @Column(name = "account_name", nullable = false)
    private String accountName;

	// 创建时间(默认1970-01-01 09:00:00)
	@Column(name = "create_time", nullable = false)
	private Date createTime;

	// 修改时间(默认1970-01-01 09:00:00)
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	@Column(name = "description", nullable = false)
	private String description;

	public enum Platform {
		APP(1), WAP(2), PC(3);

		private Integer value;

		private Platform(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
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

	public Integer getPageTemplateId() {
		return pageTemplateId;
	}

	public void setPageTemplateId(Integer pageTemplateId) {
		this.pageTemplateId = pageTemplateId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	//模板类型
	//当前没有管理模板的系统，暂用模板id来替代模板类型
	//现在数据库中ams_webpage_template表中5条数据；  1-有腔调_自建活动页  2-探索_店铺  3-探索_视频  4-探索_清单  5-探索_精选  6-有腔调_话题
	//(当前精选(话题)并没有自建页，所以该条记录只用作占位)
	public enum WebpageTemplateType {
		MEIMEI_ACTIVITY(1), EXPLORE_SHOP(2), EXPLORE_VIDEO(3), EXPLORE_DETAILED_LIST(4), EXPLORE_SELECTION(5);

		private Integer value;

		private WebpageTemplateType(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
		result = prime * result + ((accountName == null) ? 0 : accountName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((pageTemplateId == null) ? 0 : pageTemplateId.hashCode());
		result = prime * result + ((platform == null) ? 0 : platform.hashCode());
		result = prime * result + ((preview == null) ? 0 : preview.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
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
		PageTemplate other = (PageTemplate) obj;
		if (accountId == null) {
			if (other.accountId != null) {
				return false;
			}
		} else if (!accountId.equals(other.accountId)) {
			return false;
		}
        if (accountName == null) {
            if (other.accountName != null) {
                return false;
            }
        } else if (!accountName.equals(other.accountName)) {
            return false;
        }
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (createTime == null) {
			if (other.createTime != null) {
				return false;
			}
		} else if (!createTime.equals(other.createTime)) {
			return false;
		}
		if (pageTemplateId == null) {
			if (other.pageTemplateId != null) {
				return false;
			}
		} else if (!pageTemplateId.equals(other.pageTemplateId)) {
			return false;
		}
		if (platform == null) {
			if (other.platform != null) {
				return false;
			}
		} else if (!platform.equals(other.platform)) {
			return false;
		}
		if (preview == null) {
			if (other.preview != null) {
				return false;
			}
		} else if (!preview.equals(other.preview)) {
			return false;
		}
		if (status == null) {
			if (other.status != null) {
				return false;
			}
		} else if (!status.equals(other.status)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		if (updateTime == null) {
			if (other.updateTime != null) {
				return false;
			}
		} else if (!updateTime.equals(other.updateTime)) {
			return false;
		}
		return true;
	}
}
