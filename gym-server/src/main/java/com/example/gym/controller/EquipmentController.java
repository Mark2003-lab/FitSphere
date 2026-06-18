package com.example.gym.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.common.Response;
import com.example.gym.entity.Equipment;
import com.example.gym.service.EquipmentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {
    
    private final EquipmentService equipmentService;
    
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }
    
    @PostMapping
    public Response<Equipment> addEquipment(@RequestBody Equipment equipment) {
        Equipment result = equipmentService.addEquipment(equipment);
        return Response.success("添加成功", result);
    }
    
    @PutMapping("/{id}")
    public Response<Equipment> updateEquipment(@PathVariable Long id, @RequestBody Equipment equipment) {
        Equipment result = equipmentService.updateEquipment(id, equipment);
        return Response.success("更新成功", result);
    }
    
    @DeleteMapping("/{id}")
    public Response<Void> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return Response.success("删除成功", null);
    }
    
    @GetMapping("/{id}")
    public Response<Equipment> getEquipmentById(@PathVariable Long id) {
        Equipment equipment = equipmentService.getEquipmentById(id);
        return Response.success(equipment);
    }
    
    @GetMapping
    public Response<IPage<Equipment>> listEquipments(@RequestParam(defaultValue = "1") int page, 
                                                     @RequestParam(defaultValue = "10") int size) {
        IPage<Equipment> equipments = equipmentService.listEquipments(page, size);
        return Response.success(equipments);
    }
}
