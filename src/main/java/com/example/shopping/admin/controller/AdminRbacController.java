package com.example.shopping.admin.controller;

import com.example.shopping.admin.service.AdminRbacService;
import com.example.shopping.common.entity.*;
import com.example.shopping.common.mapper.*;
import com.example.shopping.common.utils.IsEmptyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author EasyArchAyuan
 * @date 2021/8/9 23:20
 */
@Api(tags = "后台模块RBAC权限管理接口")
@RestController
public class AdminRbacController {
    @Autowired
    AdminRbacService rbacService;
    @Autowired
    SysAdminMapper adminMapper;
    @Autowired
    SysRoleMapper roleMapper;
    @Autowired
    SysAccessMapper accessMapper;
    @Autowired
    SysAdminRoleMapper adminRoleMapper;
    @Autowired
    SysRoleAccessMapper roleAccessMapper;
    @Autowired
    IsEmptyUtil isEmptyUtil;

    @ApiOperation("后台rbac页面")
    @GetMapping("/admin/rbac")
    public ModelAndView index(ModelAndView modelAndView) {
        // 管理员列表
        List<SysAdmin> adminList = adminMapper.findAll();
        // 角色列表
        List<SysRole> roleList = roleMapper.findAll();
        // 权限列表
        List<SysAccess> accessList = accessMapper.findAll();
        // 用户角儿关联表
        List<SysAdminRole> adminRoleList = adminRoleMapper.findAll();
        // 角色权限关联表
        List<SysRoleAccess> roleAccessList = roleAccessMapper.findAll();

        modelAndView.addObject("adminList", adminList);
        modelAndView.addObject("roleList", roleList);
        modelAndView.addObject("accessList", accessList);
        modelAndView.addObject("adminRoleList", adminRoleList);
        modelAndView.addObject("roleAccessList", roleAccessList);

        modelAndView.setViewName("admin/rbac/index");
        return modelAndView;
    }

    @ApiOperation("权限不足时需要跳转的页面")
    @GetMapping("/admin/alert")
    public ModelAndView adminAlert(ModelAndView modelAndView) {
        modelAndView.setViewName("admin/rbac/alert");
        return modelAndView;
    }

    @ApiOperation("编辑管理员的角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "admin", value = "管理员id"),
            @ApiImplicitParam(name = "role", value = "权限id")
    })
    @PostMapping("/admin/rbac/update-admin-role")
    public ModelAndView updateAdminRole(ModelAndView modelAndView, int admin, @RequestParam(defaultValue = "0") int role) {
        modelAndView.setViewName("redirect:/admin/rbac");
        if (role == 0) {
            modelAndView.addObject("msg", "未对管理员角色进行修改");
            return modelAndView;
        }
        // sql执行结果接收变量
        int sql;
        if (adminRoleMapper.countByAdminId(admin) == 0) {
            // 此管理员还未设置角色
            sql = adminRoleMapper.insert(admin, role);
        } else {
            // 修改管理员角色
            sql = adminRoleMapper.updateRoleByAdminId(admin, role);
        }
        if (sql == 1) {
            modelAndView.addObject("msg", "设置管理员角色成功！");
        } else {
            modelAndView.addObject("msg", "设置管理员角色失败！");
        }
        return modelAndView;
    }

    @ApiOperation("添加角色")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "name", value = "角色名称")
    )
    @PostMapping("/admin/add-role")
    public ModelAndView addRole(ModelAndView modelAndView, String name) {
        if (isEmptyUtil.strings(name)) {
            modelAndView.addObject("msg", "请输入角色名");
        }
        if (roleMapper.countByName(name) != 0) {
            modelAndView.addObject("msg", "此角色已存在");
        }
        if (roleMapper.insert(name) != 1) {
            modelAndView.addObject("msg", "添加角色失败");
        } else {
            modelAndView.addObject("msg", "添加角色成功");
        }
        modelAndView.setViewName("redirect:/admin/rbac");
        return modelAndView;
    }

    @ApiOperation("添加权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "权限名称"),
            @ApiImplicitParam(name = "url", value = "权限对应的接口")
    })
    @PostMapping("/admin/add-access")
    public ModelAndView addAccess(ModelAndView modelAndView, String name, String url) {
        if (isEmptyUtil.strings(name, url)) {
            modelAndView.addObject("msg", "必填信息不能为空");
        }
        if (accessMapper.countByName(name) != 0) {
            modelAndView.addObject("msg", "此权限已存在");
        }
        if (accessMapper.insert(name, url) != 1) {
            modelAndView.addObject("msg", "添加权限失败");
        } else {
            modelAndView.addObject("msg", "添加权限成功");
        }
        modelAndView.setViewName("redirect:/admin/rbac");
        return modelAndView;
    }

    @ApiOperation("删除管理员")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "管理员id")
    )
    @GetMapping("/admin/del-admin")
    public ModelAndView delAdmin(ModelAndView modelAndView, int id) {
        modelAndView.setViewName("redirect:/admin/rbac");
        if (id == 1) {
            modelAndView.addObject("msg", "无法删除此管理员");
            return modelAndView;
        }
        if (adminMapper.deleteById(id) == 1) {
            modelAndView.addObject("msg", "删除管理员成功");
        } else {
            modelAndView.addObject("msg", "删除管理员失败");
        }
        return modelAndView;
    }

    @ApiOperation("删除角色")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "角色id")
    )
    @GetMapping("/admin/del-role")
    public ModelAndView delRole(ModelAndView modelAndView, int id) {
        if (roleMapper.deleteById(id) == 1) {
            modelAndView.addObject("msg", "删除角色成功");
        } else {
            modelAndView.addObject("msg", "删除角色失败");
        }
        modelAndView.setViewName("redirect:/admin/rbac");
        return modelAndView;
    }

    @ApiOperation("删除权限")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "权限id")
    )
    @GetMapping("/admin/del-access")
    public ModelAndView delAccess(ModelAndView modelAndView, int id) {
        if (accessMapper.deleteById(id) == 1) {
            modelAndView.addObject("msg", "删除权限成功");
        } else {
            modelAndView.addObject("msg", "删除权限失败");
        }
        modelAndView.setViewName("redirect:/admin/rbac");
        return modelAndView;
    }

    @ApiOperation("停用管理员")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "管理员id")
    )
    @GetMapping("/admin/off-admin")
    public ModelAndView offAdmin(ModelAndView modelAndView, int id) {
        modelAndView.setViewName("redirect:/admin/rbac");
        if (id == 1) {
            modelAndView.addObject("无法停用此管理员！");
            return modelAndView;
        }
        if (adminMapper.updateMarkById(id, 0) == 1) {
            modelAndView.addObject("msg", "停用成功！");
        } else {
            modelAndView.addObject("msg", "停用失败！");
        }
        return modelAndView;
    }

    @ApiOperation("启用管理员")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "管理员id")
    )
    @GetMapping("/admin/on-admin")
    public ModelAndView onAdmin(ModelAndView modelAndView, int id) {
        modelAndView.setViewName("redirect:/admin/rbac");
        if (adminMapper.updateMarkById(id, 1) == 1) {
            modelAndView.addObject("msg", "启用成功！");
        } else {
            modelAndView.addObject("msg", "启用失败！");
        }
        return modelAndView;
    }

    @ApiOperation("停用角色")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "角色id")
    )
    @GetMapping("/admin/off-role")
    public ModelAndView offRole(ModelAndView modelAndView, int id) {
        modelAndView.setViewName("redirect:/admin/rbac");
        if (roleMapper.updateStatus(0, id) == 1) {
            modelAndView.addObject("msg", "停用角色成功！");
        } else {
            modelAndView.addObject("msg", "停用角色失败！");
        }
        return modelAndView;
    }

    @ApiOperation("启用角色")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "角色id")
    )
    @GetMapping("/admin/on-role")
    public ModelAndView onRole(ModelAndView modelAndView, int id) {
        modelAndView.setViewName("redirect:/admin/rbac");
        if (roleMapper.updateStatus(1, id) == 1) {
            modelAndView.addObject("msg", "启用角色成功！");
        } else {
            modelAndView.addObject("msg", "启用角色失败！");
        }
        return modelAndView;
    }

    @ApiOperation("启用权限")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "权限id")
    )
    @GetMapping("/admin/on-access")
    public ModelAndView onAccess(ModelAndView modelAndView, int id) {
        modelAndView.setViewName("redirect:/admin/rbac");
        if (accessMapper.updateStatus(1, id) == 1) {
            modelAndView.addObject("msg", "启用权限成功！");
        } else {
            modelAndView.addObject("msg", "启用权限失败！");
        }
        return modelAndView;
    }

    @ApiOperation("停用权限")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "权限id")
    )
    @GetMapping("/admin/off-access")
    public ModelAndView offAccess(ModelAndView modelAndView, int id) {
        modelAndView.setViewName("redirect:/admin/rbac");
        if (accessMapper.updateStatus(0, id) == 1) {
            modelAndView.addObject("msg", "停用权限成功！");
        } else {
            modelAndView.addObject("msg", "停用权限失败！");
        }
        return modelAndView;
    }

    @ApiOperation("修改权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "权限id"),
            @ApiImplicitParam(name = "name", value = "权限名称"),
            @ApiImplicitParam(name = "url", value = "权限对应的接口")
    })
    @PostMapping("/admin/update-access")
    public ModelAndView updateAccess(ModelAndView modelAndView, int id, String name, String url) {
        modelAndView.setViewName("redirect:/admin/rbac");
        if (accessMapper.updateInfo(id, name, url) == 1) {
            modelAndView.addObject("msg", "修改权限信息成功！");
        } else {
            modelAndView.addObject("msg", "修改权限信息失败！");
        }
        return modelAndView;
    }

    @ApiOperation("角色权限信息页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "角色id")
    })
    @GetMapping("/admin/role-access")
    public ModelAndView roleAccessIndex(ModelAndView modelAndView, int id) {
        modelAndView.setViewName("/admin/rbac/role-access");
        // 获取角色信息
        SysRole role = roleMapper.findById(id);
        // 获取角色的权限信息
        List<SysRoleAccess> roleAccessList = roleAccessMapper.findByRoleId(id);
        Map<Integer, SysRoleAccess> roleAccessMap = new HashMap<>();
        for (SysRoleAccess sysRoleAccess : roleAccessList) {
            roleAccessMap.put(sysRoleAccess.getAccessId(), sysRoleAccess);
        }
        // 获取全部权限信息
        List<SysAccess> allAccessList = accessMapper.findAll();

        modelAndView.addObject("role", role);
        modelAndView.addObject("allAccessList", allAccessList);
        modelAndView.addObject("roleAccessMap", roleAccessMap);

        return modelAndView;
    }

    @ApiOperation("修改角色的权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "role", value = "角色id"),
            @ApiImplicitParam(name = "id", value = "修改后角色拥有的权限id")
    })
    @PostMapping("/admin/role-access/action")
    public ModelAndView action(ModelAndView modelAndView, int role, int... ids) {
        modelAndView.setViewName("redirect: /admin/role-access?id=" + role);
        if (rbacService.setRoleAccess(role, ids)) {
            modelAndView.addObject("msg", "设置成功");
        } else {
            modelAndView.addObject("msg", "设置失败");
        }
        return modelAndView;
    }
}
