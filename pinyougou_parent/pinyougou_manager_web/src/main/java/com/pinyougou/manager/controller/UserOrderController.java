package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userOrder1")
public class UserOrderController {
    @Reference
    private OrderService orderService;
   /* @RequestMapping("/findOrderAndOrderItem")
    public Result findOrderAndOrderItem() {
        List<TbOrder> orderList = orderService.findOrderAndOrderItem();
        String sheetName = "天津xxx公司政策兑现申请情况";
        String titleName = "天津xxx公司政策兑现申请情况";
        String fileName = "天津xxx公司政策兑现申请情况";
        int columnNumber = 35;//列数
        int[] columnWidth = {10, 10, 10, 10, 10, 80, 80, 50, 50, 60,
                60, 60, 60, 60, 60, 60, 60, 60, 60, 60,
                60, 60, 60, 60, 60, 60, 60, 60, 60, 60,
                60, 60, 60, 60, 60}; //列宽
        //需要插入Excel的数据
       *//* String[][] dataList = {
                { "1", "111", "建立博士后工作站和博士后创新实践基地资助", "初审通过", "2015-01-01", "5000", "5000", "5000" }

        };*//*
        String[] columnName = {"订单id", "实付金额", "支付类型", "邮费", "状态",
                "订单创建时间", "订单更新时间", "付款时间", "发货时间", "交易完成时间",
                "交易关闭时间", "物流名称", "物流单号", "用户id", "买家留言",
                "买家昵称", "买家是否已经评价", "收货人地区名称街道", "收货人手机", "收货人邮编",
                "收货人", "过期时间", "发票类型", "订单来源", "商家ID",
                "id", "商品id", "SPU_ID", "订单id", "商品标题",
                "商品单价", "商品购买数量", "商品总金额", "商品图片地址", "商家名称"};
        // 生成Excel文件
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        // 创建Sheet
        XSSFSheet sheet = xssfWorkbook.createSheet(sheetName);
        // 表头
        XSSFRow headRow = sheet.createRow(0);

        *//*创建一个样式对象 开始设置excel的样式*//*
        XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
        *//*标题设置成为粗体*//*
        XSSFFont font = xssfWorkbook.createFont();
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        *//*标题设置字体颜色为红色*//*
//		short color = HSSFColor.RED.index;
        short color = IndexedColors.RED.getIndex();
        *//*设置字体颜色为红色*//*
        font.setColor(color);
        *//*将font对象赋给样式*//*
        cellStyle.setFont(font);
        *//*设置列的宽度*//*
//        sheet.setColumnWidth(0, 1000);
//        sheet.setColumnWidth(1, 3000);
//        sheet.setColumnWidth(2, 3000);

        for (int i = 0; i < columnNumber; i++) {
            for (int j = 0; j <= i; j++) {
                if (i == j) {
                    sheet.setColumnWidth(i, columnWidth[j] * 256); // 单独设置每列的宽
                }
            }
        }


        // 第四.一步，创建表头的列
        for (int i = 0; i < columnNumber; i++) {
            XSSFCell cell = headRow.createCell(i);
            cell.setCellValue(columnName[i]);
            cell.setCellStyle(cellStyle);
        }


        // 表格数据
        for (TbOrder tbOrder : orderList) {
            // 设置Sheet中最后一行的行号+1，或者for循环的时候用索引for(int i=0;i<wayBills.size();i++)，用i的形式创建行号
            XSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
//            dataRow.createCell(0).setCellValue(brand.getId());
        }


        // 下载导出 上传下载的两头一流1.Content-Type 2.Content-Disposition 3.输出流
        // 设置头信息 xls的MIME:application/vnd.ms-excel,将所有的Xssf改成Hssf
        *//*response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        String filename = "品牌数据.xlsx";
        String agent = request.getHeader("user-agent");

        filename = FileUtils.encodeDownloadFilename(filename, sun.management.resources.agent);
        response.setHeader("Content-Disposition",
                "attachment;filename=" + filename);

        // 将Excel文档写到输出流中
        ServletOutputStream outputStream = response.getOutputStream();
        xssfWorkbook.write(outputStream);

        // 关闭
        xssfWorkbook.close();*//*

        //return orderList;
    }
}*/
}