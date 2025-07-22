// src/main/java/org/example/common/model/Group.java
package org.example.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 群组模型（客户端和服务端共用）
 */
public class Group implements Serializable {
    private static final long serialVersionUID = 1L;

    private String groupId;      // 群组唯一ID
    private String name;         // 群组名称（客户端显示用）
    private String ownerId;      // 群主ID
    private String description;  // 群组描述
    private Date createdAt;      // 创建时间
    private int memberCount;     // 成员数量（客户端显示用）

    // 构造方法
    public Group(String name, String ownerId, String description) {
        this.name = name;
        this.ownerId = ownerId;
        this.description = description;
        this.createdAt = new Date();
    }

    public Group() {}

    // Getter和Setter
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
}