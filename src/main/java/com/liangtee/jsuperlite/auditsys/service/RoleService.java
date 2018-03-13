package com.liangtee.jsuperlite.core.service;

import com.liangtee.jsuperlite.core.model.Role;
import com.liangtee.jsuperlite.core.repository.RoleRepository;
import com.liangtee.jsuperlite.core.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


/**
 * Created by Allen on 2018/1/31.
 */

@Component("roleService")
public class RoleService extends BaseService<Role, Integer> {

    private RoleRepository roleRepository;

    @Autowired
    public RoleService(JdbcTemplate jdbcTemplate, RoleRepository roleRepository) {
        super(jdbcTemplate);
        this.roleRepository = roleRepository;
    }

    public Role add(Role role) {return roleRepository.save(role);}

    public Role findOne(int id) {return roleRepository.findOne(id);}

}
