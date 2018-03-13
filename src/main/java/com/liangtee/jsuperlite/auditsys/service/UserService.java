package com.liangtee.jsuperlite.core.service;

import com.liangtee.jsuperlite.core.model.Role;
import com.liangtee.jsuperlite.core.model.User;
import com.liangtee.jsuperlite.core.repository.UserRepository;
import com.liangtee.jsuperlite.core.service.base.BaseService;
import com.liangtee.jsuperlite.core.service.base.QueryHelper;
import com.liangtee.jsuperlite.core.utils.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by Allen on 2017/4/18.
 */

@Component("userService")
@Transactional
public class UserService extends BaseService<User, Long> {

    private UserRepository userRepository;

    @Autowired
    private QueryHelper queryHelper;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate, UserRepository userRepository) {
        super(jdbcTemplate);
        this.userRepository = userRepository;
    }

    public User get(String userName, String MD5EncodedPasswd) {
        User user = userRepository.findUserByName(userName);
        return user.getPasswd().equals(MD5EncodedPasswd) ? user : null;
    }

    public User get(long UID) {
        return userRepository.findOne(UID);
    }

    public User add(String userName, String MD5EncodedPasswd, int roleID, int deptID, String email, String phoneNumber, boolean isActive) {
        if(super.isExist("USER_NAME = ?", userName)) return null;
        String roleName = queryHelper.findAll(Role.class, "id = ?", roleID).get(0).getName();
        return userRepository.save(new User(userName, MD5EncodedPasswd, roleID, roleName, deptID, email, phoneNumber, isActive));
    }

    public User update(Long userID, String userName, String passwd, int roleID, int deptID, String email, String phoneNumber, boolean isActive) {
        User user = userRepository.findOne(userID);
        String roleName = queryHelper.findAll(Role.class, "id = ?", roleID).get(0).getName();
        if(user.getPasswd().equalsIgnoreCase(passwd))
            return userRepository.save(new User(userID, userName, passwd, roleID, roleName, deptID, email, phoneNumber, isActive));
        return userRepository.save(new User(userID, userName, MD5Encoder.get2RoundsHash(passwd, "MD5"), roleID, roleName, deptID, email, phoneNumber, isActive));
    }

    public Map<Integer, List<User>> getAll() {

        Map<Integer, List<User>> userMap = new HashMap<Integer, List<User>>();

        Iterator<User> iter = userRepository.findAll().iterator();

        while (iter.hasNext()) {
            User user = iter.next();
            if(userMap.containsKey(user.getDeptID())) userMap.get(user.getDeptID()).add(user);
            else {
                List<User> list = new ArrayList<User>();
                list.add(user);
                userMap.put(user.getDeptID(), list);
            }
        }

        return userMap;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<User>();
        userRepository.findAll().forEach(user -> list.add(user));
        return list;
    }

    public List<User> findByDeptID(int deptID) {
        return userRepository.findUserByDeptID(deptID);
    }

}
