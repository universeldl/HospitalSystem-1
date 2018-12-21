package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.model.department;
import com.example.demo.model.items;
import com.example.demo.model.patient;
import com.example.demo.result.Result;
import com.example.demo.service.*;
import com.example.demo.unitl.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by next on 2018/11/28.
 */
@RestController
//@CrossOrigin
@Api(value = "HRS")
public class HRSController {

//    private HashMap<String,LinkedList<patient>>appointmentMap=new HashMap<>();
    @Autowired
    private patientService patientService;
    @Autowired
    private itemsService itemsService;
    @Autowired
    private receptorService receptorService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private departmentService departmentService;
    @ApiOperation(value = "查询病人信息")
    @ApiImplicitParams({@ApiImplicitParam(name = "medicareId",value = "医疗卡号",required = true,dataType = "String")
    })
    @RequestMapping(value = "/search_user",method = RequestMethod.POST)
    public Result search_user(@RequestBody String cs){
        Map<String,String>map= JSON.parseObject(cs,Map.class);
        String medicareId=map.get("medicareId").toString();
        patient p;
        p=patientService.searchPatient(medicareId);
        if (p!=null){
            return ResultUtil.success(p);
        }else{
            return ResultUtil.error("查询失败");
        }
    }
    @ApiOperation(value = "挂号")
    @ApiImplicitParams({@ApiImplicitParam(name = "medicareId",value = "医疗卡号",required = true,dataType = "String"),
            @ApiImplicitParam(name = "departmentId",value = "预约部门id",required = true,dataType = "String")
    })
    @RequestMapping(value = "/appointment",method = RequestMethod.POST)
    public Result appointment(@RequestHeader(name="Content-Type", defaultValue = "application/json") String contentType,
                              @RequestParam Map map){
        String medicareId=map.get("medicareId").toString();
        Integer itemId=Integer.valueOf(map.get("departmentId").toString());
        patient p;
        p=patientService.searchPatient(medicareId);
        department d=searchdepartment(itemId);
        if (p!=null){
//            LinkedList<patient> list=this.appointmentMap.get(d.getName());
            LinkedList<patient> list=appointmentService.save(d.getDepartment());
            if (ifHasElement(list,p)){
                return ResultUtil.error("用户已经在排队");
            }else {
                list.addFirst(p);
                appointmentService.update(d.getDepartment(),list);
                return ResultUtil.success("排队成功");
            }
        }else {
            return ResultUtil.error("不存在该用户");
        }

    }
    @ApiOperation(value = "部门字典")
    @RequestMapping(value = "/search_department",method = RequestMethod.POST)
    public Result search_department(@RequestHeader(name="Content-Type", defaultValue = "application/json") String contentType,
                                    @RequestParam Map map){
        return ResultUtil.success(departmentService.get_departments());
    }
    @ApiOperation(value = "结算")
    @ApiImplicitParams({@ApiImplicitParam(name = "medicareId",value = "医疗卡号",required = true,dataType = "String")
    })
    @RequestMapping(value = "/account",method = RequestMethod.POST)
    public Result account(@RequestHeader(name="Content-Type", defaultValue = "application/json") String contentType,
                          @RequestParam Map map){
        Long m=Long.valueOf(map.get("medicareId").toString());

        try {
            double money=receptorService.account(m);
            return ResultUtil.success(money);
        }catch (Exception e){
            return ResultUtil.error("不存在");
        }
    }
    @ApiOperation(value = "收费")
    @ApiImplicitParams({@ApiImplicitParam(name = "medicareId",value = "医疗卡号",required = true,dataType = "String")
    })
    @RequestMapping(value = "/charge" , method = RequestMethod.POST)
    public Result charge(@RequestHeader(name="Content-Type", defaultValue = "application/json") String contentType,
                         @RequestParam Map map){
        Long m=Long.valueOf(map.get("medicareId").toString());

        try {
            receptorService.charge(m);
            return ResultUtil.success("收费成功");
        }catch (Exception e){
            return ResultUtil.error("失败");
        }
    }
    @ApiOperation(value = "办就诊卡")
    @ApiImplicitParams({@ApiImplicitParam(name = "medicareId",value = "医疗卡号",required = true,dataType = "String"),
            @ApiImplicitParam(name = "name",value = "病人名字",required = true,dataType = "String"),
            @ApiImplicitParam(name = "phone",value = "病患手机号",required = true,dataType = "String"),
            @ApiImplicitParam(name = "birthplace",value = "出生地址",required = true,dataType = "String"),
            @ApiImplicitParam(name = "familyaddress",value = "家庭地址",required = true,dataType = "String"),
            @ApiImplicitParam(name = "contactaddress",value = "联系地址",required = true,dataType = "String"),
            @ApiImplicitParam(name = "sex",value = "性别 1为男 0为女",required = true,dataType = "String")
    })
    @RequestMapping(value = "/applyCard",method = RequestMethod.POST)
    public Result applyCard(@RequestHeader(name="Content-Type", defaultValue = "application/json") String contentType,
                            @RequestParam Map map){
        patient p=new patient();
        p.setMedicare(Long.valueOf(map.get("medicareId").toString()));
        p.setName(map.get("name").toString());
        p.setPhone(Long.valueOf(map.get("phone").toString()));
        p.setBirthplace(map.get("birthplace").toString());
        p.setFamilyAddress(map.get("familyaddress").toString());
        p.setContactAddress(map.get("contactaddress").toString());
        p.setSex(Integer.valueOf(map.get("sex").toString()));
        p.setCreateTime(System.currentTimeMillis()/1000);
        try {
            patientService.addPatient(p);
        }catch (Exception e){
            return ResultUtil.error("插入失败");
        }
        return ResultUtil.success("成功");
    }


    public Boolean ifHasElement(List<patient> list,patient p){
        for (patient p1:list
             ) {
            if (p1.getMedicare().equals(p.getMedicare())){
                return true;
            }
        }
        return false;
    }
    public department searchdepartment(Integer itemId){
        List<department> list=departmentService.get_departments();
        for (department d:list
             ) {
            if (d.getId().equals(itemId)){
                return d;
            }
        }
        return null;
    }
}
