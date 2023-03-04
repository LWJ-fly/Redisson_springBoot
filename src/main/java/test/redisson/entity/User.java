package test.redisson.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述：用户实体类
 * @author 技匠
 * @date 2023-02-17 17:00:22
 * 版权所有 Copyright www.wenmeng.online
 */
@Data
public class User implements Serializable {
    private String name;
    private String img;
    private Integer age;
    
    public User() {
    }
    
    public User(String name, String img, Integer age) {
        this.name = name;
        this.img = img;
        this.age = age;
    }
}
