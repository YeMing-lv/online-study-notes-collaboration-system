package com.osnc.main.pojo.vo;

import com.osnc.main.pojo.dto.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private String id;

    private String username;

    private String name;

    private String email;

    private String avatar;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


    public UserVO(User user) {
        this.id = user.getId().toString();
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.status = user.getStatus();
        this.createTime = user.getCreateTime();
        this.updateTime = user.getUpdateTime();
    }

}
