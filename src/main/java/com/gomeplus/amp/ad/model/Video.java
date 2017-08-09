package com.gomeplus.amp.ad.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 视频model
 * @author zhangqian
 *
 */
@Entity
@Table(name = "ams_video")
public class Video implements Serializable {

		
		private static final long serialVersionUID = -9168031092267724160L;
		@Id
		@Column(name = "video_id", unique = true, nullable = false)
		private Long videoId;
		@Column(name = "title", nullable = false)
		private String title;
		@Column(name = "file_name", nullable = false)
		private String fileName;
		@Column(name = "description", nullable = false)
		private String description;
		@Column(name = "image", nullable = false)
		private String image;
		@Column(name = "length", nullable = false)
		private Integer length;
		@Column(name = "ratio", nullable = false)
		private Float ratio;	
		@Column(name = "hash", nullable = false)
		private String hashcolumn;
		@Column(name = "appname", nullable = false)
		private String appname;
		@Column(name = "convert_status", nullable = false)
		private Integer convertStatus;
		@Column(name = "distribute_status", nullable = false)
		private Integer distributeStatus;
		@Column(name = "approve_status", nullable = false)
		private Integer approveStatus;
		@Column(name = "is_approve", nullable = false)
		private Integer isApprove;
		@Column(name = "is_published", nullable = false)
		private Integer isPublished;
		@Column(name = "ip", nullable = false)
		private String ip;
		@Column(name = "user_id", nullable = false)
		private String userId;
		@Column(name = "page_status", nullable = false)
		private Integer pageStatus;
		@Column(name = "status", nullable = false)
		private Integer status;
		@Column(name = "upload_time", nullable = false)
		private Date uploadTime;
		@Column(name = "create_time", nullable = false)
		private Date createTime;
		@Column(name = "update_time", nullable = false)
		private Date updateTime;
		
		public enum ConvertStatus {
			FAIL(-1), WAIT(0), SUCCESS(1), PROCESS(2), EXCEPTION(3);

			private Integer value;

			private ConvertStatus(Integer value) {
				this.value = value;
			}

			public Integer getValue() {
				return value;
			}
		}
		
		public enum DistributeStatus {
			FAIL(-1), WAIT(0), SUCCESS(1), PROCESS(2), EXCEPTION(3);

			private Integer value;

			private DistributeStatus(Integer value) {
				this.value = value;
			}

			public Integer getValue() {
				return value;
			}
		}
		
		public enum ApproveStatus {
			FAIL(-1), WAIT(0), SUCCESS(1);

			private Integer value;

			private ApproveStatus(Integer value) {
				this.value = value;
			}

			public Integer getValue() {
				return value;
			}
		}
		
		public enum IsApprove {
			UNDO(0), DONE(1);

			private Integer value;

			private IsApprove(Integer value) {
				this.value = value;
			}

			public Integer getValue() {
				return value;
			}
		}
		
		public enum IsPublished {
			UNDO(0), DONE(1);

			private Integer value;

			private IsPublished(Integer value) {
				this.value = value;
			}

			public Integer getValue() {
				return value;
			}
		}
		
		public enum PageStatus {
			CLOSE(0), OPEN(1);

			private Integer value;

			private PageStatus(Integer value) {
				this.value = value;
			}

			public Integer getValue() {
				return value;
			}
		}
		
		public enum Status {
			DELETE(0), NORMAL(1);

			private Integer value;

			private Status(Integer value) {
				this.value = value;
			}

			public Integer getValue() {
				return value;
			}
		}

		
		public Long getVideoId() {
			return videoId;
		}

		public void setVideoId(Long videoId) {
			this.videoId = videoId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public Integer getLength() {
			return length;
		}

		public void setLength(Integer length) {
			this.length = length;
		}

		public Float getRatio() {
			return ratio;
		}

		public void setRatio(Float ratio) {
			this.ratio = ratio;
		}

		public String getHashcolumn() {
			return hashcolumn;
		}

		public void setHashcolumn(String hashcolumn) {
			this.hashcolumn = hashcolumn;
		}

		public String getAppname() {
			return appname;
		}

		public void setAppname(String appname) {
			this.appname = appname;
		}

		public Integer getConvertStatus() {
			return convertStatus;
		}

		public void setConvertStatus(Integer convertStatus) {
			this.convertStatus = convertStatus;
		}

		public Integer getDistributeStatus() {
			return distributeStatus;
		}

		public void setDistributeStatus(Integer distributeStatus) {
			this.distributeStatus = distributeStatus;
		}

		public Integer getApproveStatus() {
			return approveStatus;
		}

		public void setApproveStatus(Integer approveStatus) {
			this.approveStatus = approveStatus;
		}

		public Integer getIsApprove() {
			return isApprove;
		}

		public void setIsApprove(Integer isApprove) {
			this.isApprove = isApprove;
		}

		public Integer getIsPublished() {
			return isPublished;
		}

		public void setIsPublished(Integer isPublished) {
			this.isPublished = isPublished;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public Integer getPageStatus() {
			return pageStatus;
		}

		public void setPageStatus(Integer pageStatus) {
			this.pageStatus = pageStatus;
		}

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public Date getUploadTime() {
			return uploadTime;
		}

		public void setUploadTime(Date uploadTime) {
			this.uploadTime = uploadTime;
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

		public Video(){
			
		}
		
		public Video(Long videoId, String title, String fileName, String description, String image, Integer length,
				Float ratio, String hashcolumn, String appname, Integer convertStatus, Integer distributeStatus,
				Integer approveStatus, Integer isApprove, Integer isPublished, String ip, String userId, Integer pageStatus,
				Integer status, Date uploadTime, Date createTime, Date updateTime) {
			super();
			this.videoId = videoId;
			this.title = title;
			this.fileName = fileName;
			this.description = description;
			this.image = image;
			this.length = length;
			this.ratio = ratio;
			this.hashcolumn = hashcolumn;
			this.appname = appname;
			this.convertStatus = convertStatus;
			this.distributeStatus = distributeStatus;
			this.approveStatus = approveStatus;
			this.isApprove = isApprove;
			this.isPublished = isPublished;
			this.ip = ip;
			this.userId = userId;
			this.pageStatus = pageStatus;
			this.status = status;
			this.uploadTime = uploadTime;
			this.createTime = createTime;
			this.updateTime = updateTime;
		}

		@Override
		public String toString() {
			return "Video [videoId=" + videoId + ", title=" + title + ", fileName=" + fileName + ", description="
					+ description + ", image=" + image + ", length=" + length + ", ratio=" + ratio + ", hashcolumn="
					+ hashcolumn + ", appname=" + appname + ", convertStatus=" + convertStatus + ", distributeStatus="
					+ distributeStatus + ", approveStatus=" + approveStatus + ", isApprove=" + isApprove + ", isPublished="
					+ isPublished + ", ip=" + ip + ", userId=" + userId + ", pageStatus=" + pageStatus + ", status="
					+ status + ", uploadTime=" + uploadTime + ", createTime=" + createTime + ", updateTime=" + updateTime
					+ "]";
		}
}
