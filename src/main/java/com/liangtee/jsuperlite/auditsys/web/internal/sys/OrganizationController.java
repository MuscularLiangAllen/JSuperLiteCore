package com.liangtee.jsuperlite.core.web.internal.sys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.liangtee.jsuperlite.core.Annotation.AccessControl;
import com.liangtee.jsuperlite.core.Annotation.PermissionValidate;
import com.liangtee.jsuperlite.core.model.Organization;
import com.liangtee.jsuperlite.core.service.OrganizationService;
import com.liangtee.jsuperlite.core.utils.InjectionFilter;
import com.liangtee.jsuperlite.core.values.OrgConfs;
import com.liangtee.jsuperlite.core.values.UserConfs;
import com.liangtee.jsuperlite.core.values.json.ReturnMessage;
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

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/5/21.
 * All rights Reserved
 */

@Controller
@RequestMapping("/sys/org")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @PermissionValidate
    @RequestMapping(path = "show", method = RequestMethod.GET)
    public String show(HttpServletRequest request, Model model) {

        List<Organization> orgList = organizationService.getAll();
        model.addAttribute("orgList", orgList);

//        model.addAttribute("pageName", "sys-org");

//        return "internal/sys-org";
        return "content_pages/sys-org";
    }

    @PermissionValidate
    @RequestMapping(path = "list", method = RequestMethod.GET)
    public @ResponseBody String list(HttpServletRequest request, Model model) {

        return JSON.toJSONString(organizationService.getAll(), SerializerFeature.DisableCircularReferenceDetect);

    }

    @PermissionValidate
    @RequestMapping(path = "add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String add(@RequestParam(value = "parentOrgID", required = true) int parentOrgID,
                                    @RequestParam(value = "orgname", required = true) String orgname,
                                    @RequestParam(value = "orgtypeID", required = true) int orgtypeID,
                                    @RequestParam(value = "orgtel", required = true) String orgtel,
                                    @RequestParam(value = "orgloc", required = true) String orgloc,
                                    @RequestParam(value = "orgdesc", required = true) String orgdesc
                                    ) {

        String message = "";

        if(InjectionFilter.filter(orgname, orgtel, orgloc, orgdesc)) {
            return JSON.toJSONString(new ReturnMessage("(警告)拒绝访问"), SerializerFeature.DisableCircularReferenceDetect);
        }

        if(organizationService.isExist("BELONG_TO_ID = ? && ORG_NAME = ? && IS_HIDDEN ='0'",
                Integer.toString(parentOrgID), orgname) && parentOrgID != OrgConfs.NO_PARENT_ORG) {
            return JSON.toJSONString(new ReturnMessage("组织机构名称已存在"), SerializerFeature.DisableCircularReferenceDetect);
        }

        int hierarchicalLevel = organizationService.countHierarchicalLevel(parentOrgID);

        if(hierarchicalLevel > 4) {
            return JSON.toJSONString(new ReturnMessage("组织机构层级最大为4"), SerializerFeature.DisableCircularReferenceDetect);
        }

        Organization organization = new Organization(parentOrgID, hierarchicalLevel, orgname, orgtypeID, orgtel);

        organization.setLocation(orgloc);
        organization.setOrgDesc(orgdesc);

        return organizationService.add(organization) != null ? JSON.toJSONString(new ReturnMessage("添加组织机构成功"),
                SerializerFeature.DisableCircularReferenceDetect) : JSON.toJSONString(new ReturnMessage("添加组织机构失败"),
                SerializerFeature.DisableCircularReferenceDetect);
    }

    @PermissionValidate
    @RequestMapping(path = "update", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String update(@RequestParam(value = "orgID", required = true) int orgID,
                                    @RequestParam(value = "orgname", required = true) String orgname,
                                    @RequestParam(value = "orgtypeID", required = true) int orgtypeID,
                                    @RequestParam(value = "orgtel", required = true) String orgtel,
                                    @RequestParam(value = "orgloc", required = true) String orgloc,
                                    @RequestParam(value = "orgdesc", required = true) String orgdesc) {

        List<Organization> orgList = organizationService.findAll("ID = ?", orgID);

        if(orgList == null) return JSON.toJSONString(new ReturnMessage("修改组织机构失败"));
        Organization updated = orgList.get(0);
        updated.setOrgName(orgname);
        updated.setTel(orgtel);
        updated.setLocation(orgloc);
        updated.setOrgDesc(orgdesc);

        if(organizationService.update(updated)) return JSON.toJSONString(new ReturnMessage("修改组织机构成功"));
        return JSON.toJSONString(new ReturnMessage("修改组织机构失败"));

    }

    @PermissionValidate
    @RequestMapping(path = "get", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String get(@RequestParam(value = "orgID", required = true) int orgID) {

        List<Organization> orgList = organizationService.findAll("ID = ?", orgID);
        if(orgList != null) {
            String result = JSON.toJSONString(orgList.get(0));
            System.out.println(result);
            return result;
        }

        return null;
    }

    @PermissionValidate
    @RequestMapping(path = "delete", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String delete(@RequestParam(value = "orgID", required = true) int orgID) {

        organizationService.delete(orgID);

        return JSON.toJSONString(new ReturnMessage("删除组织机构成功"), SerializerFeature.DisableCircularReferenceDetect);
    }

    @PermissionValidate
    @RequestMapping(path = "org-list-json", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String getOrgJsonList(HttpServletRequest request) {

        List<Organization> orgList = (List<Organization>) request.getAttribute("orgList");
        List<OrganizationController.Node> zTreeNodeList = new ArrayList<OrganizationController.Node>();

        orgList.forEach(o -> {
            zTreeNodeList.add(new OrganizationController.Node(o.getID(), o.getBelongTo(), o.getOrgName()));
        });

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

