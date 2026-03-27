package com.muzi;

import com.muzi.healthsys.entity.User;
import com.muzi.healthsys.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class HealthApplicationTests {

    @Resource
    private UserMapper userMapper;
    @Test
    void testMapper() {
        List<User> users = userMapper.selectList(null);
        users.forEach(user -> System.out.println(user + "\n"));
    }

}

