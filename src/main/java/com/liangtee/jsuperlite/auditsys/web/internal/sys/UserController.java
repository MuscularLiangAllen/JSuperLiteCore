package com.liangtee.jsuperlite.auditsys.web.internal.sys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.liangtee.jsuperlite.auditsys.Annotation.AccessControl;
import com.liangtee.jsuperlite.auditsys.Annotation.PermissionValidate;
import com.liangtee.jsuperlite.auditsys.model.Organization;
import com.liangtee.jsuperlite.auditsys.model.Role;
import com.liangtee.jsuperlite.auditsys.model.User;
import com.liangtee.jsuperlite.auditsys.service.OrganizationService;
import com.liangtee.jsuperlite.auditsys.service.UserService;
import com.liangtee.jsuperlite.auditsys.service.base.QueryHelper;
import com.liangtee.jsuperlite.auditsys.utils.InjectionFilter;
import com.liangtee.jsuperlite.auditsys.utils.MD5Encoder;
import com.liangtee.jsuperlite.auditsys.values.UserConfs;
import com.liangtee.jsuperlite.auditsys.values.json.ReturnMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/5/18.
 * All rights Reserved
 */

@Controller
@RequestMapping("/sys/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private QueryHelper queryHelper;

    @PermissionValidate
    @RequestMapping(path = "show", method = RequestMethod.GET)
    public String show(HttpServletRequest request, Model model) {

        Map<Integer, List<User>> userMap = userService.getAll();
        model.addAttribute("userMap", userMap);

        List<Role> roleList = queryHelper.findAll(Role.class, "1 = ?", 1);
        model.addAttribute("roleList", roleList);

        return "content_pages/sys-users";
    }

    @PermissionValidate
    @RequestMapping(path = "add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String add(@RequestParam(value = "userName", required = true) String userName,
                      @RequestParam(value = "passwd", required = true) String passwd,
                      @RequestParam(value = "orgID", required = true) Integer orgID,
                      @RequestParam(value = "selectedRoleID", required = true) Integer selectedRoleID,
                      @RequestParam(value = "userTel", required = true) String userTel,
                      @RequestParam(value = "userMail", required = true) String userMail,
                      @RequestParam(value = "isActive", required = true) boolean isActive) {

        if(InjectionFilter.filter(userName, passwd, userTel, userMail))
            return JSON.toJSONString(new ReturnMessage("(警告)拒绝访问"));

        if(userService.isExist("USER_NAME = ? AND DEPT_ID = ?", userName, orgID))
            return JSON.toJSONString(new ReturnMessage("用户名已存在"));

        return userService.add(userName, MD5Encoder.get2RoundsHash(passwd, "MD5"), selectedRoleID, orgID, userMail, userTel, isActive) != null ?
                JSON.toJSONString(new ReturnMessage("添加用户成功")):JSON.toJSONString(new ReturnMessage("添加用户成功"));

    }

    @PermissionValidate
    @RequestMapping(path = "update", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String update(@RequestParam(value = "userID", required = true) Long userID,
                                    @RequestParam(value = "userName", required = true) String userName,
                                    @RequestParam(value = "passwd", required = true) String passwd,
                                    @RequestParam(value = "orgID", required = true) Integer orgID,
                                    @RequestParam(value = "roleCode", required = true) Integer roleCode,
                                    @RequestParam(value = "userTel", required = true) String userTel,
                                    @RequestParam(value = "userMail", required = true) String userMail,
                                    @RequestParam(value = "isActive", required = true) boolean isActive) {

        if(InjectionFilter.filter(userName, passwd, userTel, userMail))
            return JSON.toJSONString(new ReturnMessage("(警告)拒绝访问"));

        if(userService.isExist("USER_NAME = ? AND DEPT_ID = ?", userName, orgID))
            return JSON.toJSONString(new ReturnMessage("用户名已存在"));

        return userService.update(userID, userName, passwd, orgID, roleCode, userMail, userTel, isActive) != null ?
                JSON.toJSONString(new ReturnMessage("修改用户信息成功")):JSON.toJSONString(new ReturnMessage("修改用户信息失败"));
    }

    @PermissionValidate
    @RequestMapping(path = "get", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String get(@RequestParam(value = "userID", required = true) Long userID) {

        User user = userService.get(userID);

        return JSON.toJSONString(user);

    }

    @PermissionValidate
    @RequestMapping(path = "delete", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String delete(@RequestParam(value = "UID", required = true) Long UID,
                  HttpServletRequest request) {

        String message = "";

        User currentUser = (User) request.getSession().getAttribute("user");
        User deletedUser = userService.get(UID);

        Role currentUserRole = queryHelper.findAll(Role.class, "id = ?", currentUser.getRoleID()).get(0);
        Role deletedUserRole = queryHelper.findAll(Role.class, "id = ?", deletedUser.getRoleID()).get(0);

        if(deletedUserRole.getGrantedMenuIDs().equalsIgnoreCase(Role.GRANTED_ALL) ||
                currentUserRole.getGrantedMenuIDs().split(",").length <= deletedUserRole.getGrantedMenuIDs().split(",").length) {
            return JSON.toJSONString(new ReturnMessage("无法删除同级或更高级用户"));
        }

        return userService.delete("UID = ?", UID) == true ? JSON.toJSONString(new ReturnMessage("删除成功")) :
                JSON.toJSONString(new ReturnMessage("删除失败"));

    }

    @PermissionValidate
    @RequestMapping(path = "user-list-json", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String getUserJsonList(HttpServletRequest request) {

        Map<Integer, List<User>> userMap = userService.getAll();
        List<Organization> orgList = (List<Organization>) request.getAttribute("orgList");

        List<Node> zTreeNodeList = new ArrayList<Node>();

        orgList.forEach(o -> {zTreeNodeList.add(new Node(o.getID(), o.getBelongTo(), o.getOrgName()));
            if(userMap.containsKey(o.getID())) {
                userMap.get(o.getID()).forEach(u->zTreeNodeList.add(new Node(u.getUID(), u.getDeptID(), u.getName())));
            }
        });

        System.out.println(JSON.toJSONString(zTreeNodeList, SerializerFeature.DisableCircularReferenceDetect));

        return JSON.toJSONString(zTreeNodeList, SerializerFeature.DisableCircularReferenceDetect);
    }


    class Node {
        @JSONField(name="id")
        long ID;
        @JSONField(name="pId")
        long pID;
        @JSONField(name="name")
        String name;

        public Node() {}

        public Node(long ID, long pID, String name) {
            this.ID = ID;
            this.pID = pID;
            this.name = name;
        }

        public long getID() {
            return ID;
        }

        public void setID(long ID) {
            this.ID = ID;
        }

        public long getpID() {
            return pID;
        }

        public void setpID(long pID) {
            this.pID = pID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
