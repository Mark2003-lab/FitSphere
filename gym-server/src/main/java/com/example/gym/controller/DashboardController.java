package com.example.gym.controller;

import com.example.gym.common.Response;
import com.example.gym.entity.*;
import com.example.gym.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 仪表盘/工作台数据控制器
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private UserService userService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CoachService coachService;

    /**
     * 管理员数据看板
     */
    @GetMapping("/admin")
    public Response<Map<String, Object>> adminDashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("memberCount", dashboardService.countMembers());
        data.put("coachCount", dashboardService.countCoaches());
        data.put("courseCount", dashboardService.countCourses());
        data.put("todayCheckinCount", dashboardService.countTodayCheckins());
        data.put("weeklyCheckin", dashboardService.getWeeklyCheckin());
        data.put("courseReservations", dashboardService.getCourseReservations());
        data.put("recentCheckins", dashboardService.getRecentCheckins(5));
        data.put("financeStats", dashboardService.getFinanceStats());
        return Response.success(data);
    }

    /**
     * 预约流水数据（供前端表格展示）
     */
    @GetMapping("/admin/reservation-ledger")
    public Response<List<Map<String, Object>>> reservationLedger() {
        return Response.success(dashboardService.getReservationLedger());
    }

    /**
     * 私教流水数据（供前端表格展示）
     */
    @GetMapping("/admin/private-coaching-ledger")
    public Response<List<Map<String, Object>>> privateCoachingLedger() {
        return Response.success(dashboardService.getPrivateCoachingLedger());
    }

    /**
     * 会员端首页数据
     */
    @GetMapping("/member")
    public Response<Map<String, Object>> memberDashboard() {
        Map<String, Object> data = new HashMap<>();
        Long userId = getCurrentUserId();
        Long memberId = getCurrentMemberId(userId);

        data.put("totalReservations", dashboardService.countReservationsByMember(memberId));
        data.put("recentReservations", dashboardService.listRecentReservations(memberId, 3));
        data.put("totalCheckins", dashboardService.countCheckinsByMember(memberId));
        data.put("recentCheckins", dashboardService.listRecentCheckinsByMember(memberId, 3));
        data.put("privateCoachingCount", dashboardService.countPrivateCoachingByMember(memberId));
        return Response.success(data);
    }

    /**
     * 教练端工作台数据
     */
    @GetMapping("/coach")
    public Response<Map<String, Object>> coachDashboard() {
        Map<String, Object> data = new HashMap<>();
        Long userId = getCurrentUserId();
        Long coachId = getCurrentCoachId(userId);

        data.put("totalCourses", dashboardService.countCoursesByCoach(coachId));
        data.put("totalMembers", dashboardService.countDistinctMembersByCoach(coachId));
        data.put("todayCourses", dashboardService.listTodayCoursesByCoach(coachId));
        data.put("todayReservations", dashboardService.countTodayReservationsByCoach(coachId));
        data.put("privateCoachingCount", dashboardService.countPrivateCoachingByCoach(coachId));
        return Response.success(data);
    }

    /**
     * 导出管理员数据看板为 Excel
     * GET /api/dashboard/admin/export
     */
    @GetMapping("/admin/export")
    public void exportAdminDashboard(HttpServletResponse response) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();

            // 样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // ======= Sheet 1: 概览统计 =======
            Sheet sheet1 = workbook.createSheet("概览统计");
            String[] headers1 = {"指标", "数值"};
            Row headerRow1 = sheet1.createRow(0);
            for (int i = 0; i < headers1.length; i++) {
                Cell cell = headerRow1.createCell(i);
                cell.setCellValue(headers1[i]);
                cell.setCellStyle(headerStyle);
            }
            Map<String, Object> financeStats = dashboardService.getFinanceStats();
            String[][] overviewData = {
                {"会员总数", String.valueOf(dashboardService.countMembers())},
                {"教练总数", String.valueOf(dashboardService.countCoaches())},
                {"课程总数", String.valueOf(dashboardService.countCourses())},
                {"今日签到人数", String.valueOf(dashboardService.countTodayCheckins())},
                {"", ""},
                {"【资金流水】", ""},
                {"课程流水", "¥" + financeStats.get("courseRevenue")},
                {"私教流水", "¥" + financeStats.get("privateCoachingRevenue")},
                {"累计总收入", "¥" + financeStats.get("totalRevenue")},
                {"本月课程流水", "¥" + financeStats.get("monthlyCourseRevenue")},
                {"本月私教流水", "¥" + financeStats.get("monthlyPrivateRevenue")},
                {"本月总收入", "¥" + financeStats.get("monthlyTotalRevenue")},
            };
            for (int i = 0; i < overviewData.length; i++) {
                Row row = sheet1.createRow(i + 1);
                for (int j = 0; j < overviewData[i].length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(overviewData[i][j]);
                    cell.setCellStyle(dataStyle);
                }
            }
            sheet1.setColumnWidth(0, 20 * 256);
            sheet1.setColumnWidth(1, 15 * 256);

            // ======= Sheet 2: 本周签到趋势 =======
            Sheet sheet2 = workbook.createSheet("本周签到趋势");
            String[] headers2 = {"日期", "签到人数"};
            Row headerRow2 = sheet2.createRow(0);
            for (int i = 0; i < headers2.length; i++) {
                Cell cell = headerRow2.createCell(i);
                cell.setCellValue(headers2[i]);
                cell.setCellStyle(headerStyle);
            }
            List<Map<String, Object>> weekly = dashboardService.getWeeklyCheckin();
            for (int i = 0; i < weekly.size(); i++) {
                Row row = sheet2.createRow(i + 1);
                Cell c0 = row.createCell(0);
                c0.setCellValue(weekly.get(i).get("name").toString());
                c0.setCellStyle(dataStyle);
                Cell c1 = row.createCell(1);
                c1.setCellValue(((Number) weekly.get(i).get("value")).intValue());
                c1.setCellStyle(dataStyle);
            }
            sheet2.setColumnWidth(0, 20 * 256);
            sheet2.setColumnWidth(1, 15 * 256);

            // ======= Sheet 3: 课程预约统计 =======
            Sheet sheet3 = workbook.createSheet("课程预约统计");
            String[] headers3 = {"课程名称", "预约人数"};
            Row headerRow3 = sheet3.createRow(0);
            for (int i = 0; i < headers3.length; i++) {
                Cell cell = headerRow3.createCell(i);
                cell.setCellValue(headers3[i]);
                cell.setCellStyle(headerStyle);
            }
            List<Map<String, Object>> courseRes = dashboardService.getCourseReservations();
            for (int i = 0; i < courseRes.size(); i++) {
                Row row = sheet3.createRow(i + 1);
                Cell c0 = row.createCell(0);
                c0.setCellValue(courseRes.get(i).get("name").toString());
                c0.setCellStyle(dataStyle);
                Cell c1 = row.createCell(1);
                c1.setCellValue(((Number) courseRes.get(i).get("value")).intValue());
                c1.setCellStyle(dataStyle);
            }
            sheet3.setColumnWidth(0, 25 * 256);
            sheet3.setColumnWidth(1, 15 * 256);

            // 设置响应头
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String filename = "数据看板_" + today + ".xlsx";
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);

            OutputStream out = response.getOutputStream();
            workbook.write(out);
            workbook.close();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出预约流水为 Excel
     * GET /api/dashboard/admin/reservation-export
     */
    @GetMapping("/admin/reservation-export")
    public void exportReservationLedger(HttpServletResponse response) {
        List<Map<String, Object>> rows = dashboardService.getReservationLedger();
        List<String> headers = List.of("会员姓名", "会员电话", "课程名称", "教练", "价格", "审核状态", "支付状态", "预约时间");
        List<String> fields = List.of("会员姓名", "会员电话", "课程名称", "教练", "价格", "审核状态", "支付状态", "预约时间");
        writeExcel(response, "预约流水", headers, fields, rows);
    }

    /**
     * 导出私教流水为 Excel
     * GET /api/dashboard/admin/private-coaching-export
     */
    @GetMapping("/admin/private-coaching-export")
    public void exportPrivateCoachingLedger(HttpServletResponse response) {
        List<Map<String, Object>> rows = dashboardService.getPrivateCoachingLedger();
        List<String> headers = List.of("会员姓名", "会员电话", "教练", "课程名称", "价格", "支付状态", "预约状态", "预约时间", "创建时间");
        List<String> fields = List.of("会员姓名", "会员电话", "教练", "课程名称", "价格", "支付状态", "预约状态", "预约时间", "创建时间");
        writeExcel(response, "私教流水", headers, fields, rows);
    }

    private void writeExcel(HttpServletResponse response, String sheetName,
                            List<String> headers, List<String> fields,
                            List<Map<String, Object>> rows) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            Sheet sheet = workbook.createSheet(sheetName);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < rows.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, Object> data = rows.get(i);
                for (int j = 0; j < fields.size(); j++) {
                    Cell cell = row.createCell(j);
                    Object val = data.get(fields.get(j));
                    if (val instanceof java.math.BigDecimal bd) {
                        cell.setCellValue(bd.doubleValue());
                    } else if (val instanceof Number n) {
                        cell.setCellValue(n.doubleValue());
                    } else {
                        cell.setCellValue(val != null ? val.toString() : "");
                    }
                    cell.setCellStyle(dataStyle);
                }
            }

            for (int i = 0; i < headers.size(); i++) {
                sheet.setColumnWidth(i, Math.min(headers.get(i).length() * 2 + 4, 18) * 256);
            }

            String filename = sheetName + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
            String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return user != null ? user.getId() : null;
    }

    private Long getCurrentMemberId(Long userId) {
        if (userId == null) return null;
        Member member = memberService.getMemberById(userId);
        if (member != null) return member.getId();
        User user = userService.findByUsername(
            SecurityContextHolder.getContext().getAuthentication().getName());
        if (user != null && user.getPhone() != null) {
            Member phoneMember = memberService.getMemberByPhone(user.getPhone());
            if (phoneMember != null) return phoneMember.getId();
        }
        return userId;
    }

    private Long getCurrentCoachId(Long userId) {
        if (userId == null) return null;
        Coach coach = coachService.getCoachById(userId);
        if (coach != null) return coach.getId();
        User user = userService.findByUsername(
            SecurityContextHolder.getContext().getAuthentication().getName());
        if (user != null && user.getPhone() != null) {
            Coach phoneCoach = coachService.getCoachByPhone(user.getPhone());
            if (phoneCoach != null) return phoneCoach.getId();
        }
        return userId;
    }
}
