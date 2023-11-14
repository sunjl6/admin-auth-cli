package cn.sunjl.dozer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    private Integer id;
    private String name;
    private int age;
    private String add;
}
