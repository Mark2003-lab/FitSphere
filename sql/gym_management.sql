/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80030 (8.0.30)
 Source Host           : localhost:3306
 Source Schema         : gym_management

 Target Server Type    : MySQL
 Target Server Version : 80030 (8.0.30)
 File Encoding         : 65001

 Date: 05/06/2026 10:23:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat`;
CREATE TABLE `ai_chat`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `ai_chat_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_chat
-- ----------------------------
INSERT INTO `ai_chat` VALUES (1, 1, '身高175，体重85，减脂计划可以怎么做', '你好！很高兴为你提供帮助！根据你的身高175cm和体重85kg，我们来计算一下你的BMI（身体质量指数）：85 ÷ (1.75 × 1.75) ≈ 27.8，属于超重范围。减脂是很好的目标，我会给你一个科学、可执行的计划。\n\n首先，减脂的核心是“热量赤字”——每天消耗的热量略多于摄入的热量。结合你的情况，我建议分三步走：饮食调整、训练安排和休息管理。下面是具体建议：\n\n### 1. 饮食调整（关键！）\n- **热量控制**：你的基础代谢大约在1800-1900千卡/天（因年龄不同略有差异）。为了减脂，建议每天摄入约1600-1800千卡，但不要低于1400千卡，以免代谢下降。\n- **营养比例**：保持高蛋白（1.6-2.2克/公斤体重，即136-187克/天），中低碳水（约150-200克/天），适量健康脂肪（40-60克/天）。例如：早餐吃全麦面包+鸡蛋+牛奶；午餐鸡胸肉/鱼肉+糙米+蔬菜；晚餐豆腐/瘦肉+西兰花+少量红薯。\n- **少食多餐**：一天吃4-5顿小餐，避免饥饿感。多喝水（每天2-3升），减少含糖饮料和加工食品。\n\n### 2. 训练计划（每周4-5次）\n- **力量训练（每周3次）**：重点练大肌群（胸、背、腿），增加肌肉量以提升基础代谢。动作推荐：深蹲（3组×12次）、卧推（3组×10次）、划船（3组×12次）、硬拉（3组×8次）。每次训练45-60分钟。\n- **有氧运动（每周2-3次）**：中低强度有氧（如快走、慢跑、骑自行车），每次30-45分钟，心率控制在（220-年龄）×60%-70%。也可以尝试高强度间歇训练（HIIT），比如冲刺跑30秒+慢走1分钟，循环10组，每周1-2次。\n- **组合建议**：比如周一力量训练，周二有氧，周三休息，周四力量，周五HIIT，周六力量+轻度有氧，周日休息。\n\n### 3. 休息与监测\n- **睡眠**：每晚7-9小时，睡眠不足会降低代谢和增加饥饿感。\n- **进度记录**：每周称一次体重（早上空腹），并拍照或量腰围（目标腰围<90cm）。不要只关注体重，脂肪减少可能伴随肌肉增加。\n- **调整**：如果2周后体重无变化，可减少100-200千卡摄入或增加一次有氧。\n\n### 额外提醒\n- 刚开始时，不要过度限制饮食或高强度训练，否则容易反弹。建议先适应1-2周，再逐步增加强度。\n- 如果健身时间有限，优先保证力量训练，有氧作为辅助。\n- 减脂期可以适当补充蛋白粉或BCAA，但非必需。\n\n你可以在健身房尝试这个计划，坚持4-6周后，预计能减掉2-4公斤脂肪，同时保持肌肉。如果有任何具体问题（比如动作细节或饮食替代），随时告诉我，我会进一步帮你调整！加油！💪', '2026-06-01 22:38:01');
INSERT INTO `ai_chat` VALUES (2, 1, '身高175，体重85kg，目标减脂', '你好！很高兴为你提供减脂指导。你的身高175cm，体重85kg，BMI约为27.7，属于轻度超重范围。减脂是很好的目标，下面我会从**饮食、训练和生活方式**三方面给你具体建议。\n\n### 🔍 第一步：明确减脂目标\n- **理想体重范围**：约65-75kg（BMI 21-24），建议先从**减重5-8kg**开始，体脂率降到15%-18%左右。\n- **减脂节奏**：每周减重0.5-1kg比较健康，不要追求快速掉秤，避免肌肉流失。\n\n### 🥗 饮食建议（核心：制造热量缺口）\n1. **计算每日热量**：  \n   基础代谢约1700-1800大卡，减脂期建议每日摄入 **1600-1800大卡**（热量缺口约300-500大卡）。\n\n2. **三大营养素分配**（参考）：\n   - **蛋白质**：每公斤体重1.6-2.2g，你需 **136-187g/天**（鸡胸肉、鱼虾、鸡蛋、豆腐、脱脂奶）。\n   - **碳水**：每公斤体重2-3g，约 **170-255g/天**（优先选择燕麦、糙米、红薯、全麦面包）。\n   - **脂肪**：每公斤体重0.8-1g，约 **68-85g/天**（牛油果、坚果、橄榄油、鱼油）。\n\n3. **实操技巧**：\n   - 早餐：高蛋白+复合碳水（如2个鸡蛋+1碗燕麦+1杯牛奶）。\n   - 午餐：1拳头主食+1手掌瘦肉+2拳头蔬菜（少油少盐）。\n   - 晚餐：类似午餐，但减少主食量（可换成粗粮）。\n   - 加餐：选黄瓜、西红柿、无糖酸奶或一小把坚果。\n   - **戒掉含糖饮料、油炸、高糖零食**，多喝水（每天2-3升）。\n\n### 🏋️ 训练计划（每周4-5次）\n**目标**：最大化脂肪燃烧，同时保留肌肉。\n\n**周一/周三/周五：力量训练（45-60分钟）**\n- 热身：5分钟慢跑+动态拉伸。\n- 动作组合（每个动作3-4组，每组8-12次）：\n  - 深蹲（或史密斯机深蹲）\n  - 卧推（或俯卧撑）\n  - 划船（或引体向上）\n  - 硬拉（或臀推）\n  - 肩推（哑铃或杠铃）\n- 组间休息：60-90秒。\n- 收尾：腹部训练（平板支撑3组×45秒，卷腹3组×15次）。\n\n**周二/周四：有氧或HIIT（30-45分钟）**\n- 选择1：快走/慢跑（心率保持在130-150次/分，持续45分钟）。\n- 选择2：HIIT（如：全力冲刺30秒+慢走1分钟，重复8-10轮，总时长20分钟）。\n- 建议：力量训练日结束后可加20分钟有氧，效果更佳。\n\n**周六/周日：休息或低强度活动**（散步、瑜伽、拉伸）\n\n### ⏰ 生活方式调整\n- **睡眠**：每晚7-8小时，睡眠不足会降低代谢、增加饥饿激素。\n- **压力管理**：皮质醇升高会阻碍减脂，可尝试冥想、深呼吸。\n- **记录**：每周固定时间称体重，同时测量腰围、臀围，关注身体变化而非数字。\n- **耐心**：前2-4周可能体重下降不明显，但体脂在减少，坚持就是胜利。\n\n### ❗ 注意事项\n- 如果运动后头晕、乏力，可能是热量或碳水不足，适当调整。\n- 建议补充**复合维生素、鱼油**，必要时可加**蛋白粉**（但优先从食物获取）。\n- 减脂期间**不要完全断碳**，否则容易反弹，且影响训练表现。\n\n**最后送你一句话**：减脂是场马拉松，不是百米冲刺。每天进步一点点，三个月后你会感谢现在的自己！如果有具体问题（比如某个动作不会做、饮食调整细节），随时问我，我陪你一起执行 💪', '2026-06-01 22:47:30');
INSERT INTO `ai_chat` VALUES (3, 1, '身高175，体重85kg，目标减脂', '您好！很高兴为您提供减脂指导。根据您提供的数据（身高175cm，体重85kg），您的BMI约为27.8，属于超重范围。减脂是完全可行的目标，而且您有很好的基础条件。下面我为您量身定制一个初步计划，但请注意：任何计划都需要根据您的身体状况调整，建议在开始前咨询医生或专业教练。\n\n## 1. 核心原则：热量缺口\n减脂的关键是**每天消耗的热量 > 摄入的热量**。建议每日热量缺口控制在300-500大卡，避免过激减脂影响代谢和健康。\n\n- **您的每日维持热量估算**：约2400-2600大卡（基于基础代谢+活动水平）。\n- **减脂期推荐摄入**：1900-2100大卡/天（根据活动量微调）。\n\n## 2. 训练计划（每周4-5次）\n### 有氧+力量结合（效果最佳）\n- **力量训练（3-4次/周）**：重点大肌群（胸、背、腿、核心）。每次40-50分钟，如深蹲、卧推、划船、硬拉、推举等。力量训练能保留肌肉，提高基础代谢。\n- **有氧训练（3-4次/周）**：每次30-45分钟，中等强度（心率控制在130-150次/分），如快走、慢跑、椭圆机、动感单车。建议放在力量训练后或单独进行。\n\n### 示例一周安排：\n- 周一：力量（胸+三头）+ 20分钟间歇有氧\n- 周二：有氧（慢跑40分钟）\n- 周三：力量（背+二头）+ 20分钟间歇有氧\n- 周四：休息或低强度活动（如散步）\n- 周五：力量（腿+肩）+ 20分钟间歇有氧\n- 周六：有氧（椭圆机45分钟）或HIIT（20分钟）\n- 周日：休息或拉伸\n\n## 3. 饮食建议（重点）\n- **蛋白质**：每天1.6-2.2克/公斤体重（即136-187克），来源：鸡胸肉、鱼虾、鸡蛋、豆制品、蛋白粉等。蛋白质增加饱腹感，保护肌肉。\n- **碳水化合物**：每日约150-200克（以净碳水计），优先选择全谷物（燕麦、糙米、藜麦）、薯类、蔬菜。避免精制糖和甜食。\n- **脂肪**：每日40-50克，选择健康脂肪（橄榄油、牛油果、坚果、鱼油）。\n- **蔬菜**：不限量（尤其绿叶蔬菜），提供纤维和微量元素。\n- **水分**：每天喝2.5-3升水，减少饥饿感，促进代谢。\n\n## 4. 生活习惯调整\n- **睡眠**：保证7-8小时优质睡眠，熬夜会影响皮质醇，阻碍减脂。\n- **记录**：用App（如MyFitnessPal）记录饮食，至少坚持2周，了解自己的热量摄入。\n- **避免极端节食**：不要不吃主食或过度限制热量，否则容易反弹。\n\n## 5. 预期效果与注意事项\n- 健康减脂速度：每周0.5-1公斤（约1-2斤）。按此计划，您可能在3-4个月后体重降至75-78kg，体脂率明显下降。\n- **平台期**：如果体重停滞2周以上，可调整饮食（如微调碳水循环）或增加训练强度。\n- **伤病预防**：训练前热身5-10分钟，训练后拉伸。如有旧伤，请避免冲击性动作。\n\n您目前有健身经验吗？或者是否有任何健康问题（如膝盖不适、高血压等）？这样我可以进一步优化计划。加油，您完全能做到！', '2026-06-01 22:49:09');
INSERT INTO `ai_chat` VALUES (4, 1, '身高175，体重85，目标减脂', '你好！很高兴能为你提供帮助。根据你的数据——身高175cm，体重85kg，你的BMI约为27.8，属于超重范围，但体脂率可能偏高，减脂是一个很合适的目标。我会从训练、饮食和日常习惯三个方面给你一些具体建议。\n\n**1. 训练建议：以力量训练为主，有氧为辅**\n- **力量训练**：每周安排3-4次，重点放在复合动作（如深蹲、硬拉、卧推、划船、推举）。这些动作能提高基础代谢，让你在休息时也消耗更多热量。建议每组8-12次，做3-4组，组间休息60-90秒。\n- **有氧训练**：每周3-4次，每次30-40分钟，选择中等强度（如快走、慢跑、单车、椭圆机）。如果你想加速减脂，可以尝试高强度间歇训练（HIIT），比如冲刺30秒+慢走1分钟，重复8-10轮，每周2次即可，注意不要过度。\n- **频率**：推荐“3天力量+2天有氧”或“4天力量+3天有氧”的交替模式，保证每周至少休息1天。\n\n**2. 饮食建议：热量缺口是关键**\n- **每日热量摄入**：你的基础代谢大约在1700-1800大卡，减脂期间建议摄入1900-2100大卡/天（制造300-500大卡缺口）。不要低于1500大卡，否则代谢会下降。\n- **三大营养素比例**：\n  - 蛋白质：每天约1.6-2.0克/公斤体重（即136-170克），优先选择鸡胸肉、鱼虾、鸡蛋、豆制品、脱脂奶。\n  - 碳水化合物：每天约2-3克/公斤体重（即170-255克），选择全麦、燕麦、糙米、红薯、蔬菜等低GI食物，避免精制糖和白米面。\n  - 脂肪：每天约0.8-1.0克/公斤体重（即68-85克），来源包括牛油果、坚果、橄榄油、鱼油。\n- **饮食技巧**：多吃蔬菜（每餐占一半盘子），喝足水（每天2-3升），少食多餐（3主餐+1-2小加餐），避免含糖饮料和油炸食品。\n\n**3. 日常习惯与注意事项**\n- **睡眠**：每晚7-9小时，睡眠不足会降低瘦素、增加饥饿感，影响减脂效果。\n- **记录与调整**：每周称一次体重（早晨空腹、排便后），并测量腰围。如果2-3周体重无变化，可减少100-200大卡摄入或增加有氧时间。\n- **避免误区**：不要过度节食或只做有氧，否则容易流失肌肉、降低代谢。减脂速度建议每周0.5-1公斤，过快可能反弹。\n\n**一个简单的周计划示例（供参考）**\n- 周一：力量训练（胸+三头）+ 20分钟有氧\n- 周二：30分钟有氧（中等强度）\n- 周三：力量训练（背+二头）+ 20分钟有氧\n- 周四：休息或轻度活动（散步、拉伸）\n- 周五：力量训练（腿+肩）+ 20分钟有氧\n- 周六：40分钟有氧（或HIIT 15分钟）\n- 周日：休息\n\n**最后提醒**：减脂是一个过程，需要耐心和坚持。建议你从今天开始记录饮食和训练，3周后根据效果微调。如果有任何具体问题（比如某个动作不会做、饮食安排有困难），随时告诉我，我会再帮你细化！加油！💪', '2026-06-02 13:40:56');
INSERT INTO `ai_chat` VALUES (5, 1, '我想减脂，有什么计划', '您好！很高兴为您服务！减脂是一个需要耐心和科学方法的过程。作为您的健身顾问，我会根据您的需求提供专业的建议。首先，我需要了解一些基本信息：您的性别、年龄、体重、身高，以及每周能投入多少时间训练？这样我可以为您量身定制一个高效的减脂计划。\n\n不过，我可以先给您一个通用的减脂框架：\n**1. 训练计划：**\n- **有氧运动**：每周4-5次，每次30-45分钟，如跑步、骑行、游泳或椭圆机。保持心率在最大心率的60%-70%（公式：220-年龄）。\n- **力量训练**：每周2-3次，重点做复合动作（如深蹲、硬拉、卧推、划船），每组8-12次，3-4组。力量训练能提高基础代谢，帮助燃烧更多热量。\n\n**2. 饮食建议：**\n- **热量缺口**：每天比消耗少摄入300-500卡路里。计算基础代谢率（BMR）后调整。\n- **营养比例**：蛋白质占总热量30%以上（如鸡胸肉、鱼、蛋、豆制品），碳水占40%（优先全麦、燕麦、蔬菜），脂肪占30%（健康来源如坚果、牛油果、橄榄油）。\n- **水分**：每天喝2-3升水，避免含糖饮料。\n\n**3. 注意事项：**\n- 减脂期间不要过快（每周减0.5-1公斤为宜），避免肌肉流失。\n- 保证睡眠7-8小时，缓解皮质醇（压力激素）升高。\n- 定期记录体重和体脂率，但更关注身体围度变化。\n\n您可以根据这些原则开始，如果需要更具体的计划，请告诉我您的详细情况！😊', '2026-06-03 14:03:22');
INSERT INTO `ai_chat` VALUES (6, 1, '如果会员减脂的需求比较多，教练这边可以开设什么课程比较好', '针对会员减脂需求较多的情况，建议教练开设以下几类课程，既能满足会员目标，又能提升课程吸引力和效果：\n\n1. **高强度间歇训练（HIIT）课程**：短时高效，通过交替高强度运动和休息，能持续燃烧脂肪，适合时间紧张但追求快速效果的会员。建议每周2-3次，每次30-45分钟。\n\n2. **循环训练课程**：结合力量和有氧动作（如跳绳、深蹲、波比跳），以小组形式进行，增强趣味性和互动性。适合不同体能水平的会员，可调整强度。\n\n3. **核心燃脂塑形课**：专注于腹部、臀腿等大肌群训练，搭配有氧间歇，既能减脂又能塑造线条。建议加入弹力带、药球等小工具增加多样性。\n\n4. **户外或室内有氧跑课程**：如跑步机间歇跑、爬坡走或户外慢跑，配合心率监测，帮助会员控制燃脂区间。适合喜欢简单运动的会员。\n\n5. **瑜伽或普拉提燃脂课**：通过动态流瑜伽或普拉提动作提升代谢，同时改善柔韧性和体态，适合偏好低冲击运动的会员。\n\n建议根据会员的体能水平、偏好和时间安排，灵活组合课程（如每周2次HIIT+1次核心课），并定期评估进展。同时，强调饮食配合（如控制碳水摄入、增加蛋白质）和休息的重要性，以最大化减脂效果。', '2026-06-03 19:23:36');

-- ----------------------------
-- Table structure for checkin
-- ----------------------------
DROP TABLE IF EXISTS `checkin`;
CREATE TABLE `checkin`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `checkin_time` datetime NOT NULL,
  `checkout_time` datetime NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'CHECKED_IN',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'GYM',
  `course_id` bigint NULL DEFAULT NULL,
  `coach_id` bigint NULL DEFAULT NULL,
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_id`(`member_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_checkin_time`(`checkin_time` ASC) USING BTREE,
  CONSTRAINT `checkin_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of checkin
-- ----------------------------
INSERT INTO `checkin` VALUES (2, 1, '2026-06-02 18:51:52', NULL, 'CHECKED_IN', 'GYM', NULL, NULL, NULL);
INSERT INTO `checkin` VALUES (4, 1, '2026-06-02 20:40:13', NULL, 'CHECKED_IN', 'GYM', NULL, NULL, NULL);
INSERT INTO `checkin` VALUES (6, 1, '2026-06-02 21:24:39', NULL, 'CHECKED_IN', 'COURSE', 6, 6, NULL);
INSERT INTO `checkin` VALUES (7, 7, '2026-06-02 21:55:49', NULL, 'CHECKED_IN', 'COURSE', 3, 6, NULL);
INSERT INTO `checkin` VALUES (8, 1, '2026-06-03 14:02:41', NULL, 'CHECKED_IN', 'COURSE', 1, 5, NULL);
INSERT INTO `checkin` VALUES (9, 1, '2026-06-03 14:02:53', NULL, 'CHECKED_IN', 'GYM', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for coach
-- ----------------------------
DROP TABLE IF EXISTS `coach`;
CREATE TABLE `coach`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `speciality` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT 0.00,
  `rating` decimal(3, 2) NULL DEFAULT 5.00,
  `total_reviews` int NULL DEFAULT 0,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `certification` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `experience_years` int NULL DEFAULT 0,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coach
-- ----------------------------
INSERT INTO `coach` VALUES (2, '刘教练', '13800138001', '瑜伽、体态矫正、产后塑形', 200.00, 5.00, 1, '/api/file/avatar/633d17cb-7755-4f50-be8d-ec6eb66930ba.jpg', '国家健身教练资格证、高级瑜伽导师认证', 7, '拥有 7 年瑜伽授课经验，擅长零基础体态调整与女性塑形课程，授课耐心细致，注重会员动作标准与运动安全，广受学员好评。', 2);
INSERT INTO `coach` VALUES (5, '陈教练', '13900139002', '力量增肌、HIIT 燃脂、体态康复', 250.00, 5.00, 0, '/api/file/avatar/a8a6d3e4-534c-4398-9f96-d5c7925439ea.jpg', '国家健身教练资格证、ACE 美国运动协会认证', 6, '拥有 6 年健身执教经验，擅长零基础增肌减脂规划，精通各类器械使用教学，针对圆肩驼背等不良体态定制矫正方案，教学风格严谨高效。', 5);
INSERT INTO `coach` VALUES (6, '赵教练', '13900139003', '普拉提、核心塑形、中老年体能训练', 220.00, 5.00, 0, '/api/file/avatar/aa4e9f16-0edd-4715-ae6f-e2d5a8300d44.jpg', '国家健身教练资格证、普拉提国际认证教练', 6, '8 年专项普拉提授课经历，专注核心力量与身体柔韧训练，擅长产后修复、日常劳损体态调理，课程节奏舒缓，适配新手与中老年健身人群。', 6);

-- ----------------------------
-- Table structure for coach_schedule
-- ----------------------------
DROP TABLE IF EXISTS `coach_schedule`;
CREATE TABLE `coach_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coach_id` bigint NOT NULL,
  `available_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `is_available` tinyint(1) NULL DEFAULT 1,
  `max_sessions` int NULL DEFAULT 4,
  `booked_sessions` int NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `coach_id`(`coach_id` ASC) USING BTREE,
  CONSTRAINT `coach_schedule_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `coach` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coach_schedule
-- ----------------------------
INSERT INTO `coach_schedule` VALUES (1, 2, '2026-06-04', '09:00:00', '18:00:00', 1, 4, 0);

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `coach_id` bigint NULL DEFAULT NULL,
  `course_time` datetime NOT NULL,
  `capacity` int NULL DEFAULT 20,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT 0.00,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'AVAILABLE',
  `checkin_start_time` datetime NULL DEFAULT NULL,
  `checkin_end_time` datetime NULL DEFAULT NULL,
  `checkin_enabled` tinyint(1) NULL DEFAULT 0,
  `audit_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'APPROVED',
  `audit_time` datetime NULL DEFAULT NULL,
  `audit_user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `coach_id`(`coach_id` ASC) USING BTREE,
  CONSTRAINT `course_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `coach` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, '力量训练基础', 5, '2026-12-15 09:00:00', 15, '	\r\n一楼A区', 19.90, 'AVAILABLE', '2026-06-03 14:02:11', '2026-06-03 14:32:11', 0, 'APPROVED', NULL, NULL);
INSERT INTO `course` VALUES (2, '瑜伽入门', 2, '2026-12-15 14:00:00', 20, '	\r\n一楼B区', 24.90, 'AVAILABLE', NULL, NULL, 0, 'APPROVED', NULL, NULL);
INSERT INTO `course` VALUES (3, 'HIIT高强度训练', 6, '2026-12-15 19:00:00', 15, '	\r\n二楼A区', 29.90, 'AVAILABLE', NULL, NULL, 0, 'APPROVED', NULL, NULL);
INSERT INTO `course` VALUES (4, '普拉提', 2, '2026-06-04 16:16:00', 10, '	\r\n二楼B区', 29.90, 'AVAILABLE', NULL, NULL, 0, 'APPROVED', NULL, NULL);
INSERT INTO `course` VALUES (6, '动感单车燃脂课', 6, '2026-06-03 20:30:00', 20, '一楼单车室', 26.90, 'AVAILABLE', '2026-06-02 21:55:42', '2026-06-02 22:25:42', 0, 'APPROVED', NULL, NULL);
INSERT INTO `course` VALUES (7, '核心体态塑形课', 2, '2026-06-15 10:30:00', 15, '二楼 B 区', 32.90, 'AVAILABLE', NULL, NULL, 0, 'APPROVED', '2026-06-02 22:45:25', 1);

-- ----------------------------
-- Table structure for equipment
-- ----------------------------
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `quantity` int NULL DEFAULT 1,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NORMAL',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment
-- ----------------------------
INSERT INTO `equipment` VALUES (1, '跑步机', 10, 'REPAIRING');
INSERT INTO `equipment` VALUES (2, '哑铃套装', 20, 'NORMAL');
INSERT INTO `equipment` VALUES (3, '瑜伽垫', 30, 'NORMAL');
INSERT INTO `equipment` VALUES (4, '动感单车', 20, 'NORMAL');

-- ----------------------------
-- Table structure for fitness_card
-- ----------------------------
DROP TABLE IF EXISTS `fitness_card`;
CREATE TABLE `fitness_card`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `card_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `card_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `valid_days` int NOT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `gift_coupon_count` int NULL DEFAULT 0,
  `status` int NULL DEFAULT 1,
  `create_time` datetime NOT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fitness_card
-- ----------------------------
INSERT INTO `fitness_card` VALUES (1, '基础月卡', 'MONTHLY', 299.00, 30, '本卡为基础月卡，有效期 30 天，不限次入场。', 0, 1, '2026-06-01 23:21:14', '2026-06-03 13:25:19');
INSERT INTO `fitness_card` VALUES (2, '尊享季度健身卡', 'QUARTERLY', 729.00, 90, '本季度卡有效期 90 天，首充附赠2张一对一私教兑换券', 2, 1, '2026-06-02 12:43:47', '2026-06-03 13:29:58');
INSERT INTO `fitness_card` VALUES (3, '至尊全年健身年卡', 'YEARLY', 2199.00, 365, '有效期 365 天，首充附赠4张一对一私教兑换券', 4, 1, '2026-06-02 12:44:55', '2026-06-03 13:30:04');

-- ----------------------------
-- Table structure for fitness_plan
-- ----------------------------
DROP TABLE IF EXISTS `fitness_plan`;
CREATE TABLE `fitness_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL,
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fitness_plan_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fitness_plan
-- ----------------------------
INSERT INTO `fitness_plan` VALUES (1, 4, '我的健身计划', '由AI生成的健身计划', '你好！很高兴能为你提供帮助。根据你的数据——身高175cm，体重85kg，你的BMI约为27.8，属于超重范围，但体脂率可能偏高，减脂是一个很合适的目标。我会从训练、饮食和日常习惯三个方面给你一些具体建议。\n\n**1. 训练建议：以力量训练为主，有氧为辅**\n- **力量训练**：每周安排3-4次，重点放在复合动作（如深蹲、硬拉、卧推、划船、推举）。这些动作能提高基础代谢，让你在休息时也消耗更多热量。建议每组8-12次，做3-4组，组间休息60-90秒。\n- **有氧训练**：每周3-4次，每次30-40分钟，选择中等强度（如快走、慢跑、单车、椭圆机）。如果你想加速减脂，可以尝试高强度间歇训练（HIIT），比如冲刺30秒+慢走1分钟，重复8-10轮，每周2次即可，注意不要过度。\n- **频率**：推荐“3天力量+2天有氧”或“4天力量+3天有氧”的交替模式，保证每周至少休息1天。\n\n**2. 饮食建议：热量缺口是关键**\n- **每日热量摄入**：你的基础代谢大约在1700-1800大卡，减脂期间建议摄入1900-2100大卡/天（制造300-500大卡缺口）。不要低于1500大卡，否则代谢会下降。\n- **三大营养素比例**：\n  - 蛋白质：每天约1.6-2.0克/公斤体重（即136-170克），优先选择鸡胸肉、鱼虾、鸡蛋、豆制品、脱脂奶。\n  - 碳水化合物：每天约2-3克/公斤体重（即170-255克），选择全麦、燕麦、糙米、红薯、蔬菜等低GI食物，避免精制糖和白米面。\n  - 脂肪：每天约0.8-1.0克/公斤体重（即68-85克），来源包括牛油果、坚果、橄榄油、鱼油。\n- **饮食技巧**：多吃蔬菜（每餐占一半盘子），喝足水（每天2-3升），少食多餐（3主餐+1-2小加餐），避免含糖饮料和油炸食品。\n\n**3. 日常习惯与注意事项**\n- **睡眠**：每晚7-9小时，睡眠不足会降低瘦素、增加饥饿感，影响减脂效果。\n- **记录与调整**：每周称一次体重（早晨空腹、排便后），并测量腰围。如果2-3周体重无变化，可减少100-200大卡摄入或增加有氧时间。\n- **避免误区**：不要过度节食或只做有氧，否则容易流失肌肉、降低代谢。减脂速度建议每周0.5-1公斤，过快可能反弹。\n\n**一个简单的周计划示例（供参考）**\n- 周一：力量训练（胸+三头）+ 20分钟有氧\n- 周二：30分钟有氧（中等强度）\n- 周三：力量训练（背+二头）+ 20分钟有氧\n- 周四：休息或轻度活动（散步、拉伸）\n- 周五：力量训练（腿+肩）+ 20分钟有氧\n- 周六：40分钟有氧（或HIIT 15分钟）\n- 周日：休息\n\n**最后提醒**：减脂是一个过程，需要耐心和坚持。建议你从今天开始记录饮食和训练，3周后根据效果微调。如果有任何具体问题（比如某个动作不会做、饮食安排有困难），随时告诉我，我会再帮你细化！加油！💪', '2026-06-02 13:41:00', '2026-06-02 13:41:00');
INSERT INTO `fitness_plan` VALUES (3, 4, '我的健身计划', '由AI生成的健身计划', '您好！很高兴为您服务！减脂是一个需要耐心和科学方法的过程。作为您的健身顾问，我会根据您的需求提供专业的建议。首先，我需要了解一些基本信息：您的性别、年龄、体重、身高，以及每周能投入多少时间训练？这样我可以为您量身定制一个高效的减脂计划。\n\n不过，我可以先给您一个通用的减脂框架：\n**1. 训练计划：**\n- **有氧运动**：每周4-5次，每次30-45分钟，如跑步、骑行、游泳或椭圆机。保持心率在最大心率的60%-70%（公式：220-年龄）。\n- **力量训练**：每周2-3次，重点做复合动作（如深蹲、硬拉、卧推、划船），每组8-12次，3-4组。力量训练能提高基础代谢，帮助燃烧更多热量。\n\n**2. 饮食建议：**\n- **热量缺口**：每天比消耗少摄入300-500卡路里。计算基础代谢率（BMR）后调整。\n- **营养比例**：蛋白质占总热量30%以上（如鸡胸肉、鱼、蛋、豆制品），碳水占40%（优先全麦、燕麦、蔬菜），脂肪占30%（健康来源如坚果、牛油果、橄榄油）。\n- **水分**：每天喝2-3升水，避免含糖饮料。\n\n**3. 注意事项：**\n- 减脂期间不要过快（每周减0.5-1公斤为宜），避免肌肉流失。\n- 保证睡眠7-8小时，缓解皮质醇（压力激素）升高。\n- 定期记录体重和体脂率，但更关注身体围度变化。\n\n您可以根据这些原则开始，如果需要更具体的计划，请告诉我您的详细情况！😊', '2026-06-03 14:03:27', '2026-06-03 14:03:27');
INSERT INTO `fitness_plan` VALUES (4, 1, '我的计划', '由AI生成的计划', '针对会员减脂需求较多的情况，建议教练开设以下几类课程，既能满足会员目标，又能提升课程吸引力和效果：\n\n1. **高强度间歇训练（HIIT）课程**：短时高效，通过交替高强度运动和休息，能持续燃烧脂肪，适合时间紧张但追求快速效果的会员。建议每周2-3次，每次30-45分钟。\n\n2. **循环训练课程**：结合力量和有氧动作（如跳绳、深蹲、波比跳），以小组形式进行，增强趣味性和互动性。适合不同体能水平的会员，可调整强度。\n\n3. **核心燃脂塑形课**：专注于腹部、臀腿等大肌群训练，搭配有氧间歇，既能减脂又能塑造线条。建议加入弹力带、药球等小工具增加多样性。\n\n4. **户外或室内有氧跑课程**：如跑步机间歇跑、爬坡走或户外慢跑，配合心率监测，帮助会员控制燃脂区间。适合喜欢简单运动的会员。\n\n5. **瑜伽或普拉提燃脂课**：通过动态流瑜伽或普拉提动作提升代谢，同时改善柔韧性和体态，适合偏好低冲击运动的会员。\n\n建议根据会员的体能水平、偏好和时间安排，灵活组合课程（如每周2次HIIT+1次核心课），并定期评估进展。同时，强调饮食配合（如控制碳水摄入、增加蛋白质）和休息的重要性，以最大化减脂效果。', '2026-06-03 19:29:46', '2026-06-03 19:29:46');

-- ----------------------------
-- Table structure for member
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NORMAL',
  `join_date` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `expire_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member
-- ----------------------------
INSERT INTO `member` VALUES (1, '老丁', '男', '13800138007', '月卡会员', '2026-06-02 17:04:45', '2026-06-02 12:51:03');
INSERT INTO `member` VALUES (3, '佬溜', '男', '13800138006', '普通会员', '2026-06-02 18:45:56', NULL);
INSERT INTO `member` VALUES (7, '柯比', '男', '13825800435', '月卡会员', '2026-06-02 17:04:45', '2026-06-02 14:25:39');

-- ----------------------------
-- Table structure for private_coaching
-- ----------------------------
DROP TABLE IF EXISTS `private_coaching`;
CREATE TABLE `private_coaching`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `coach_id` bigint NOT NULL,
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `course_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `scheduled_time` datetime NOT NULL,
  `duration` int NULL DEFAULT 60,
  `price` decimal(10, 2) NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'UNPAID',
  `coupon_id` bigint NULL DEFAULT NULL,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `coach_notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `rating` int NULL DEFAULT NULL,
  `review` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `audit_time` datetime NULL DEFAULT NULL,
  `complete_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `member_id`(`member_id` ASC) USING BTREE,
  INDEX `coach_id`(`coach_id` ASC) USING BTREE,
  CONSTRAINT `private_coaching_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `private_coaching_ibfk_2` FOREIGN KEY (`coach_id`) REFERENCES `coach` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of private_coaching
-- ----------------------------
INSERT INTO `private_coaching` VALUES (1, 1, 2, '体态矫正', 'BODY_SHAPING', '2026-06-04 07:00:00', 60, 200.00, 'COMPLETED', 'PAID', NULL, '健身房私教区', '日常久坐肩颈酸痛、含胸驼背，希望通过课程矫正不良体态，改善肩颈劳损问题。', NULL, 5, '教练很负责任，动作很标准', '2026-06-03 11:30:46', '2026-06-03 11:31:00', '2026-06-03 11:42:18');

-- ----------------------------
-- Table structure for private_coaching_coupon
-- ----------------------------
DROP TABLE IF EXISTS `private_coaching_coupon`;
CREATE TABLE `private_coaching_coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `coupon_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` int NULL DEFAULT 0,
  `use_time` datetime NULL DEFAULT NULL,
  `used_coach_id` bigint NULL DEFAULT NULL,
  `expire_time` datetime NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `coupon_code`(`coupon_code` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `used_coach_id`(`used_coach_id` ASC) USING BTREE,
  CONSTRAINT `private_coaching_coupon_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `private_coaching_coupon_ibfk_2` FOREIGN KEY (`used_coach_id`) REFERENCES `coach` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of private_coaching_coupon
-- ----------------------------
INSERT INTO `private_coaching_coupon` VALUES (1, 4, 'COUPON-FED06CBC', 0, NULL, NULL, '2026-09-03 13:36:58', '2026-06-03 13:36:58');
INSERT INTO `private_coaching_coupon` VALUES (2, 4, 'COUPON-2F7B853B', 0, NULL, NULL, '2026-09-03 13:36:58', '2026-06-03 13:36:58');

-- ----------------------------
-- Table structure for reservation
-- ----------------------------
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ACTIVE',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'UNPAID',
  `reservation_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `approved_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `member_id`(`member_id` ASC) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reservation
-- ----------------------------
INSERT INTO `reservation` VALUES (3, 7, 3, 'APPROVED', 'PAID', '2026-06-02 17:21:06');
INSERT INTO `reservation` VALUES (5, 1, 6, 'APPROVED', 'PAID', '2026-06-02 19:04:31');
INSERT INTO `reservation` VALUES (6, 1, 1, 'APPROVED', 'PAID', '2026-06-03 14:01:10');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'MEMBER',
  `create_time` datetime NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `idx_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$1iRnl90dAO/DLB9HPSxxZurG6DosAjiS5cFQXTLiKEz515XtHmqza', 'ADMIN', '2026-06-01 15:29:14', '123456789@qq.com', '/api/file/avatar/6f932930-2af5-42d2-b6aa-4d7d5b4c3b87.jpg', '13800138004', '男');
INSERT INTO `user` VALUES (2, '刘教练', '$2a$10$vSt4vdua4T6raQcd6G5Ir.nAGwogNbdWopXerHtxenUFKSaORZCSm', 'COACH', '2026-06-01 15:29:14', '123456780@qq.com', '/api/file/avatar/633d17cb-7755-4f50-be8d-ec6eb66930ba.jpg', '13800138001', '男');
INSERT INTO `user` VALUES (3, '佬溜', '$2a$10$vSt4vdua4T6raQcd6G5Ir.nAGwogNbdWopXerHtxenUFKSaORZCSm', 'MEMBER', '2026-06-01 15:29:14', '123456788@qq.com', '/api/file/avatar/2945fbbe-7d47-4952-b7f8-2345f023baca.jpg', '13800138006', '男');
INSERT INTO `user` VALUES (4, '老丁', '$2a$10$Czs.7CnDqdUPHLmhpuuf9ef00HrckJ7B32zU3lz91Rhm8rGGzRdcC', 'MEMBER', '2026-06-01 19:53:25', '18824766610@163.com', '/api/file/avatar/ab2912ab-2a16-434c-bd59-fbcaf155cec1.jpg', '13800138007', '男');
INSERT INTO `user` VALUES (5, '陈教练', '$2a$10$MjMPNtDMIf1RepobJehQ8.yswTDheuE5jeP751iloGY7qDC/f7FRm', 'COACH', '2026-06-02 13:12:33', '123456700@qq.com', '/api/file/avatar/a8a6d3e4-534c-4398-9f96-d5c7925439ea.jpg', '13900139002', '男');
INSERT INTO `user` VALUES (6, '赵教练', '$2a$10$MjMPNtDMIf1RepobJehQ8.yswTDheuE5jeP751iloGY7qDC/f7FRm', 'COACH', '2026-06-02 13:15:18', '123456701@qq.com', '/api/file/avatar/aa4e9f16-0edd-4715-ae6f-e2d5a8300d44.jpg', '13900139003', '男');
INSERT INTO `user` VALUES (7, '柯比', '$2a$10$Y7WcfIZNn7Mhl3Syxy6dweIO8oIh8CXQrB3RB77VMPqDlMuCcxMrK', 'MEMBER', '2026-06-02 14:15:44', '2125857310@qq.com', '/api/file/avatar/1eaf9c48-e980-4080-a9bb-389bf20b0bb1.jpg', '13825800435', 'MALE');

-- ----------------------------
-- Table structure for user_card
-- ----------------------------
DROP TABLE IF EXISTS `user_card`;
CREATE TABLE `user_card`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `card_id` bigint NOT NULL,
  `card_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `card_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `purchase_time` datetime NOT NULL,
  `start_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL,
  `status` int NULL DEFAULT 1,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `card_id`(`card_id` ASC) USING BTREE,
  CONSTRAINT `user_card_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_card_ibfk_2` FOREIGN KEY (`card_id`) REFERENCES `fitness_card` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_card
-- ----------------------------
INSERT INTO `user_card` VALUES (1, 1, 1, '基础月卡', 'MONTHLY', '2026-06-01 23:39:40', '2026-06-01 23:39:40', '2026-07-01 23:39:40', 1, '2026-06-01 23:39:40');
INSERT INTO `user_card` VALUES (3, 7, 1, '基础月卡', 'MONTHLY', '2026-06-02 14:17:04', '2026-06-02 14:17:04', '2026-07-02 14:17:04', 1, '2026-06-02 14:17:04');
INSERT INTO `user_card` VALUES (7, 4, 2, '尊享季度健身卡', 'QUARTERLY', '2026-06-03 13:36:58', '2026-06-03 13:36:58', '2026-09-01 13:36:58', 0, '2026-06-03 13:36:58');
INSERT INTO `user_card` VALUES (8, 4, 2, '尊享季度健身卡', 'QUARTERLY', '2026-06-03 13:37:42', '2026-06-03 13:37:42', '2026-11-30 13:36:58', 1, '2026-06-03 13:37:42');

SET FOREIGN_KEY_CHECKS = 1;
