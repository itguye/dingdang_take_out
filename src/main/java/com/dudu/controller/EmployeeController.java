package com.dudu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dudu.common.Result;
import com.dudu.entity.Employee;
import com.dudu.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.print.attribute.standard.PageRanges;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 后端成员管理
 */
@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    /**
     * 登入
     * @param employee
     * @param request
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
        //1、将页面提交的密码password进行md5加密处理
        String pwd = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return Result.error("登入失败!");
        }
        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(pwd)) {
            return Result.error("登入失败!");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return Result.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return Result.success(emp);
    }


    /**
     * 用户退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        //清理Session中保存的当前登录员工的id
        return Result.success("退出成功!");
    }

    /**
     * 新增成员
     * @param employee
     * @param request
     * @return
     */
    @PostMapping
    public Result<String> saveEmployee(@RequestBody Employee employee,HttpServletRequest request){
       log.info(employee+"employee信息"+request.getSession().getAttribute("employee"));
        try {
            // 将密码md5加密
            employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
            // 创建者信息
            employeeService.save(employee);
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return Result.error("新增员工失败");
        }
        return Result.success("新增员工成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> showEmployeeInfo(int page,int pageSize,String name) { // page=1&pageSize=10 &name
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);
        Page pageInfo = null;
        try {
            //构造分页构造器
            pageInfo = new Page(page, pageSize);
            //构造条件构造器
            LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
            //添加过滤条件
            queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
            //添加排序条件
            queryWrapper.orderByDesc(Employee::getCreateTime);
            //执行查询
            employeeService.page(pageInfo, queryWrapper);
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return Result.error("查询失败");
        }
        return Result.success(pageInfo);
    }

    /**
     * 根据ID查询成员信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getEmployeeById(@PathVariable Long id) {
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return Result.success(employee);
        }
        return Result.error("没有查询到对应员工信息");
    }

    @PutMapping
    public Result<String> updateEmployee(@RequestBody  Employee employee,HttpServletRequest request) {
        log.info(employee.toString());
        try {
            employeeService.updateById(employee);
        } catch (Exception exception) {
            return Result.error("修改失败");
        }

        return  Result.success("修改成功");
    }
}
