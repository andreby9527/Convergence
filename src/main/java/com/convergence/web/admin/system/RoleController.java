package com.convergence.web.admin.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.convergence.domain.RoleDTO;
import com.convergence.service.RoleService;
import com.convergence.support.JsonResult;
import com.convergence.support.PageInfo;
import com.convergence.web.BaseController;

@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = {"/index"})
    public String index(ModelMap modelMap) {
    	PageInfo<RoleDTO> page = roleService.findAll(getPageRequest());
        modelMap.put("pageInfo", page);
        return "role/index";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(ModelMap map) {
        return "role/addform";
    }


    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Integer id, ModelMap map) {
        RoleDTO role = roleService.find(id);
        map.put("role", role);
        return "role/form";
    }


    @RequestMapping(value = {"/edit"}, method = RequestMethod.POST)
    @ResponseBody
    public JsonResult edit(RoleDTO role, ModelMap map) {
        try {
            roleService.saveOrUpdate(role);
        } catch (Exception e) {
            return JsonResult.failure(e.getMessage());
        }
        return JsonResult.success();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult delete(@PathVariable Integer id, ModelMap map) {
        try {
            roleService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.failure(e.getMessage());
        }
        return JsonResult.success();
    }

    @RequestMapping(value = "/grant/{id}", method = RequestMethod.GET)
    public String grant(@PathVariable Integer id, ModelMap map) {
        RoleDTO role = roleService.find(id);
        System.out.println(JSON.toJSONString(role.getResources()));
        map.put("role", role);
        return "role/grant";
    }

    @RequestMapping(value = "/grant/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult grant(@PathVariable Integer id,
                            @RequestParam(required = false) String[] resourceIds, ModelMap map) {
        try {
        	roleService.grant(id, resourceIds);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.failure(e.getMessage());
        }
        return JsonResult.success();
    }
}
