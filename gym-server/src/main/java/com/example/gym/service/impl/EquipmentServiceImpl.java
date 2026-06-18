package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.entity.Equipment;
import com.example.gym.mapper.EquipmentMapper;
import com.example.gym.service.EquipmentService;
import org.springframework.stereotype.Service;

@Service
public class EquipmentServiceImpl implements EquipmentService {
    
    private final EquipmentMapper equipmentMapper;
    
    public EquipmentServiceImpl(EquipmentMapper equipmentMapper) {
        this.equipmentMapper = equipmentMapper;
    }
    
    @Override
    public Equipment addEquipment(Equipment equipment) {
        equipmentMapper.insert(equipment);
        return equipment;
    }
    
    @Override
    public Equipment updateEquipment(Long id, Equipment equipment) {
        Equipment existing = equipmentMapper.selectById(id);
        if (existing != null) {
            existing.setName(equipment.getName());
            existing.setQuantity(equipment.getQuantity());
            existing.setStatus(equipment.getStatus());
            equipmentMapper.updateById(existing);
        }
        return existing;
    }
    
    @Override
    public void deleteEquipment(Long id) {
        equipmentMapper.deleteById(id);
    }
    
    @Override
    public Equipment getEquipmentById(Long id) {
        return equipmentMapper.selectById(id);
    }
    
    @Override
    public IPage<Equipment> listEquipments(int page, int size) {
        return equipmentMapper.selectPage(new Page<>(page, size), Wrappers.emptyWrapper());
    }
}
