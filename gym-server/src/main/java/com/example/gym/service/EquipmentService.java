package com.example.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.entity.Equipment;

public interface EquipmentService {
    Equipment addEquipment(Equipment equipment);
    Equipment updateEquipment(Long id, Equipment equipment);
    void deleteEquipment(Long id);
    Equipment getEquipmentById(Long id);
    IPage<Equipment> listEquipments(int page, int size);
}
