package com.gomeplus.amp.ad.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 消息Model
 * 
 * @author lifei01
 *
 */
@Entity
@Table(name = "ams_message")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Message implements Serializable {
	
	private static final long serialVersionUID = 5300748096003720109L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "message_id", unique = true, nullable = false)
	private Integer messageId;

	@Column(name = "title", nullable = false)
	private String title;
	
	@Column(name = "content", nullable = false)
	private String content;
	
	@Column(name = "type", nullable = false)
	private Integer type;
	
	@Column(name = "user_Id", nullable = false)
	private Integer userId;

	@Column(name = "create_time", nullable = false)
	private Date createTime;
	
	@Column(name = "read_Time", nullable = false)
	private Date readTime;
	
	@Column(name = "is_read", nullable = false)
	private Integer isRead;

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getReadTime() {
		return readTime;
	}

	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}

	public Integer getIsRead() {
		return isRead;
	}

	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((readTime == null) ? 0 : readTime.hashCode());
		result = prime * result + ((isRead == null) ? 0 : isRead.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (messageId == null) {
			if (other.messageId != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!readTime.equals(other.readTime))
			return false;
		if (isRead == null) {
			if (other.isRead != null)
				return false;
		} 
		return true;
	}

	public enum Type {
		SystemAnnouncement(1), CapitalChanges(2), bill(3);

		private Integer value;

		private Type(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}
	
	public enum IsRead {
		Unread(1), Read(2);

		private Integer value;

		private IsRead(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}
}
