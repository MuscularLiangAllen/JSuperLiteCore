package com.liangtee.jsuperlite.core.web.internal.sys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.liangtee.jsuperlite.core.Annotation.AccessControl;
import com.liangtee.jsuperlite.core.Annotation.PermissionValidate;
import com.liangtee.jsuperlite.core.model.FileInfo;
import com.liangtee.jsuperlite.core.model.TmpFile;
import com.liangtee.jsuperlite.core.model.User;
import com.liangtee.jsuperlite.core.service.FileService;
import com.liangtee.jsuperlite.core.service.base.PageModel;
import com.liangtee.jsuperlite.core.utils.*;
import com.liangtee.jsuperlite.core.values.UserConfs;
import com.liangtee.jsuperlite.core.values.json.ReturnMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/5/29.
 * All rights Reserved
 */


@Controller
@RequestMapping("/sys/files")
public class FileMGMTController extends BaseController {

    static final String FOLDER_TYPE = "folder";

    static final String NO_PARENT_FOLDER = "root";

    @Autowired
    FileService fileService;

    @PermissionValidate
    @RequestMapping(path = "show", method = RequestMethod.GET)
    public String show(@RequestParam(name="pageNumber", required = false) Integer pageNumber, HttpServletRequest request, Model model) {

        User user = (User) request.getSession().getAttribute("user");

        List<FileInfo> fileInfoList = fileService.findFilesByUser(user.getUID(), new PageModel(pageNumber == null ? 1 : pageNumber));

        model.addAttribute("totalCounts", fileService.count(FileInfo.class, "PARENT_FOLDER_ID = ?", FileInfo.NO_PARENT_FOLDER));
        model.addAttribute("fileInfoList", fileInfoList);
        model.addAttribute("currentPage", pageNumber == null ? 1 : pageNumber);

        return "content_pages/sys-files";
    }

    @PermissionValidate
    @RequestMapping(path = "search", method = RequestMethod.POST)
    public @ResponseBody String search(@RequestParam(name="keyword", required = true) String keyword, HttpServletRequest request, Model model) {

        if(InjectionFilter.filter(keyword)) return JSON.toJSONString(new ReturnMessage("输入不合法"));

        List<FileInfo> fileInfoList = fileService.search(keyword);

        return JSON.toJSONString(fileInfoList, SerializerFeature.DisableCircularReferenceDetect);
    }

    @PermissionValidate
    @RequestMapping(path = "add", method = RequestMethod.POST)
    public @ResponseBody String add(@RequestParam(value = "filename", required = true) String fileName,
                                    @RequestParam(value = "filetype", required = true) String fileType,
                                    @RequestParam(value = "parentfolderID", required = true) String parentFolderID,
                                    @RequestParam(value = "filedesc", required = true) String fileDesc,
                                    @RequestParam(value = "grantedusers", required = true) String grantedUsers,
                                    @RequestParam(value = "tmpFileID", required = true) String tmpFileID,
                                    HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");

        fileService.createFile(user, fileName, fileType.equals("folder") ? FileInfo.FOLDER_TYPE : FileInfo.FILE_TYPE, parentFolderID, fileDesc, grantedUsers, tmpFileID, "", FileInfo.EDITABLE);

        return JSON.toJSONString(new ReturnMessage("创建文件成功"), SerializerFeature.DisableCircularReferenceDetect);
    }

    @PermissionValidate
    @RequestMapping(path = "update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestParam(value = "fileID", required = true) String fileID,
                                    @RequestParam(value = "filename", required = true) String fileName,
                                    @RequestParam(value = "filedesc", required = true) String fileDesc,
                                    @RequestParam(value = "grantedusers", required = true) String grantedUsers,
                                    HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");

        FileInfo file = fileService.get(fileID);
        if(file.getEditable() == FileInfo.NON_EDITABLE) return JSON.toJSONString(new ReturnMessage("该文件夹不可修改"));

        file.setFileName(fileName);
        file.setFileDesc(fileDesc);

        return fileService.update(file, grantedUsers) == true ? JSON.toJSONString(new ReturnMessage("修改文件成功")) :
                JSON.toJSONString(new ReturnMessage("修改文件失败"));
    }

    @PermissionValidate
    @RequestMapping(path = "get", method = RequestMethod.POST)
    public @ResponseBody String get(@RequestParam(value = "fileID", required = true) String fileID,
                                    HttpServletRequest request) {
       List<FileInfo> result = fileService.findAll(FileInfo.class, "ID = ?", fileID);
       return result != null ? JSON.toJSONString(result.get(0)) : JSON.toJSONString(new ReturnMessage("获取文件失败"));
    }


    @PermissionValidate
    @RequestMapping(path = "delete", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String delete(@RequestParam(value = "fileID", required = true) String fileID) {

        FileInfo deletedFile = fileService.get(fileID);
//        if(deletedFile.getEditable() == FileInfo.NON_EDITABLE) return JSON.toJSONString(new ReturnMessage("该文件夹不可删除"));

        return fileService.delete(fileID) == true ? JSON.toJSONString(new ReturnMessage("删除文件夹成功")) :
                JSON.toJSONString(new ReturnMessage("删除文件夹失败"));

    }

    @PermissionValidate
    @RequestMapping(path = "upload", method = RequestMethod.POST)
    public @ResponseBody String uploadFile(@RequestParam(value = "qqfile", required = true) MultipartFile uploadedFile) {


        String fileName = uploadedFile.getOriginalFilename();
        String fileType = FileTypeUtil.getCommonFileType(uploadedFile.getContentType());
        String fileCode = String.format("tmp%s%s", TimeFormater.format("yyyyMMddHHmmss"), fileType);
//        long fileSize = uploadedFile.getSize();

        Map<String, Object> result = new HashMap<String, Object>();

        try {
            if(fileService.addTmpFile(uploadedFile.getInputStream(), fileCode, fileName, fileType) != null) {
                result.put("success", true);
                result.put("newUuid", fileCode);
            } else {
                result.put("success", false);
                result.put("error", "上传失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JSON.toJSONString(result);
    }

    @PermissionValidate
    @RequestMapping(path = "delete_tmp/{UUID}", method = RequestMethod.DELETE)
    public @ResponseBody String deleteTmpFile(@PathVariable String UUID) {

        Map<String, Object> result = new HashMap<String, Object>();

        TmpFile tmpFile = fileService.getTmiFile(UUID);

        try {
            if(Files.deleteIfExists(Paths.get(tmpFile.getFilePath())) &&
                    fileService.deleteTmpFile(UUID))
                return JSON.toJSONString(result.put("success", true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JSON.toJSONString(result.put("success", false));
    }

//    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_PROJ_CONTROCTOR)
    @RequestMapping(path = "download/{fileID}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable String fileID,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {

        if (request.getSession().getAttribute("user") == null) return;

        FileInfo fileInfo = fileService.get(fileID);

        if(fileInfo.getIsFolder() == FileInfo.FOLDER_TYPE) {
            String tmpDest = System.getProperty("sys.tmp.upload")+PATH_SEPARATOR+fileInfo.getFileName()+".zip";
            ZIPUtils.createZip(fileInfo.getFilePath(), tmpDest);
            fileInfo.setFileName(fileInfo.getFileName()+".zip");
            fileInfo.setFilePath(tmpDest);
        }

        request.setCharacterEncoding("UTF-8");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        //获取文件的长度
        File downloadedFile = new File(fileInfo.getFilePath());
        long fileLength = downloadedFile.length();

        //设置文件输出类型
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename="
                + new String(fileInfo.getFileName().getBytes("utf-8"), "ISO8859-1"));
        //设置输出长度
        response.setHeader("Content-Length", String.valueOf(fileLength));
        //获取输入流
        try {
            bis = new BufferedInputStream(new FileInputStream(fileInfo.getFilePath()));
            //输出流
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            //关闭流
            bis.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    @AccessControl(accessLevel = UserConfs.RoleCode.USER_TYPE_PROJ_CONTROCTOR)
    @RequestMapping(path = "template/{templateName}", method = RequestMethod.GET)
    public void downloadTemplateFile(@PathVariable String templateName,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        if (request.getSession().getAttribute("user") == null) return;

        String rootPath = request.getSession().getServletContext().getRealPath(DEFAULT_TEMPLATE_FILE_PATH);

        String downloadFileName = "";
        if(templateName.equalsIgnoreCase("construction-material-details"))
            downloadFileName = "施工主要材料明细表(模板).xls";


        request.setCharacterEncoding("UTF-8");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        File downloadFile = new File(rootPath + PATH_SEPARATOR + downloadFileName);

        //获取文件的长度
        long fileLength = downloadFile.length();

        //设置文件输出类型
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename="
                + new String(downloadFileName.getBytes("utf-8"), "ISO8859-1"));
        //设置输出长度
        response.setHeader("Content-Length", String.valueOf(fileLength));
        //获取输入流
        try {
            bis = new BufferedInputStream(new FileInputStream(downloadFile));
            //输出流
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            //关闭流
            bis.close();
            bos.close();
//            Files.deleteIfExists(Paths.get(fileInfo.getFilePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
