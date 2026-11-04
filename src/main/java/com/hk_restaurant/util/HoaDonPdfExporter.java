package com.hk_restaurant.util;

import com.hk_restaurant.constant.AppConstants;
import com.hk_restaurant.model.entity.ChiTietDonHang;
import com.hk_restaurant.model.entity.DonHang;
import com.hk_restaurant.model.entity.HoaDon;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * Lớp tiện ích dùng để xuất hóa đơn ra file PDF.
 *
 * <p>Lớp này xây dựng nội dung hóa đơn từ dữ liệu thanh toán, bao gồm
 * thông tin cửa hàng, thông tin đơn hàng, danh sách món và phần tổng kết
 * thanh toán để tạo thành một file PDF hoàn chỉnh.</p>
 */
public class HoaDonPdfExporter {

    private static final DecimalFormat MONEY = new DecimalFormat("#,##0");
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Xuất dữ liệu hóa đơn ra file PDF tại đường dẫn được chỉ định.
     *
     * @param hoaDon hóa đơn cần xuất
     * @param filePath đường dẫn file PDF đầu ra
     * @throws Exception phát sinh khi tạo file hoặc ghi dữ liệu thất bại
     */
    public static void exportToPdf(HoaDon hoaDon, String filePath) throws Exception {
        try (var output = new FileOutputStream(filePath)) {
            exportToPdf(hoaDon, output);
        }
    }

    public static synchronized void exportToPdf(HoaDon hoaDon, java.io.OutputStream output) throws Exception {
        Document document = new Document(PageSize.A4, 40, 40, 30, 30);
        PdfWriter.getInstance(document, output);
        document.open();

        BaseFont base = unicodeFont();
        Font titleFont = new Font(base, 20, Font.BOLD);
        Font headerFont = new Font(base, 11, Font.BOLD);
        Font normalFont = new Font(base, 11, Font.NORMAL);
        Font boldFont = new Font(base, 11, Font.BOLD);
        Font bigBoldFont = new Font(base, 13, Font.BOLD);

        DonHang donHang = hoaDon.getDonHang();

        Paragraph shopName = new Paragraph(AppConstants.SHOP_NAME, titleFont);
        shopName.setAlignment(Element.ALIGN_CENTER);
        document.add(shopName);

        Paragraph addr1 = new Paragraph(AppConstants.SHOP_BRANCH, normalFont);
        addr1.setAlignment(Element.ALIGN_CENTER);
        document.add(addr1);

        Paragraph addr2 = new Paragraph(AppConstants.SHOP_ADDRESS, normalFont);
        addr2.setAlignment(Element.ALIGN_CENTER);
        document.add(addr2);

        Paragraph hotline = new Paragraph("Hotline: " + AppConstants.SHOP_PHONE, normalFont);
        hotline.setAlignment(Element.ALIGN_CENTER);
        document.add(hotline);

        document.add(Chunk.NEWLINE);

        Paragraph invoiceTitle = new Paragraph("HÓA ĐƠN THANH TOÁN", new Font(Font.HELVETICA, 18, Font.BOLD));
        invoiceTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(invoiceTitle);

        Paragraph soHd = new Paragraph("Số HĐ: " + hoaDon.getSoHoaDon(), normalFont);
        soHd.setAlignment(Element.ALIGN_CENTER);
        document.add(soHd);

        document.add(Chunk.NEWLINE);

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1.2f, 3.5f});
        infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        addInfoRow(infoTable, "Ngày:", hoaDon.getThoiGianThanhToan().format(DTF), boldFont, normalFont);
        addInfoRow(infoTable, "Bàn:", donHang.getBanAn() != null ? donHang.getBanAn().getTenBan() : "", boldFont, normalFont);
        addInfoRow(infoTable, "Phục vụ:", donHang.getNhanVien() != null ? donHang.getNhanVien().getHoTen() : "", boldFont, normalFont);
        addInfoRow(infoTable, "Thu ngân:", donHang.getNhanVien() != null ? donHang.getNhanVien().getHoTen() : "", boldFont, normalFont);
        addInfoRow(infoTable, "Phương thức TT:", formatPaymentMethod(hoaDon), boldFont, normalFont);

        document.add(infoTable);
        document.add(Chunk.NEWLINE);

        PdfPTable itemTable = new PdfPTable(4);
        itemTable.setWidthPercentage(100);
        itemTable.setWidths(new float[]{4.4f, 0.8f, 1.6f, 1.8f});

        addHeaderCell(itemTable, "Tên món", headerFont);
        addHeaderCell(itemTable, "SL", headerFont);
        addHeaderCell(itemTable, "Đơn giá", headerFont);
        addHeaderCell(itemTable, "Thành tiền", headerFont);

        if (donHang.getDanhSachChiTiet() != null) {
            for (ChiTietDonHang ct : donHang.getDanhSachChiTiet()) {
                addBodyCell(itemTable, ct.getMonAn() != null ? ct.getMonAn().getTenMon() : "", normalFont, Element.ALIGN_LEFT);
                addBodyCell(itemTable, String.valueOf(ct.getSoLuong()), normalFont, Element.ALIGN_CENTER);
                addBodyCell(itemTable, formatMoney(ct.getDonGia()), normalFont, Element.ALIGN_RIGHT);
                addBodyCell(itemTable, formatMoney(ct.getThanhTien()), normalFont, Element.ALIGN_RIGHT);
            }
        }

        document.add(itemTable);
        document.add(Chunk.NEWLINE);

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setWidths(new float[]{3f, 1f});
        summaryTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        addSummaryRow(summaryTable, "Thành tiền", formatMoney(hoaDon.getTongTien()), normalFont, normalFont);
        addSummaryRow(summaryTable, "Khuyến mại", "0đ", normalFont, normalFont);
        addSummaryRow(summaryTable, "Tổng thanh toán", formatMoney(hoaDon.getTongTien()), bigBoldFont, bigBoldFont);
        addSummaryRow(summaryTable, "Tiền mặt", formatMoney(hoaDon.getTongTien()), normalFont, normalFont);
        addSummaryRow(summaryTable, "Trả lại khách", "0đ", normalFont, normalFont);

        document.add(summaryTable);
        document.add(Chunk.NEWLINE);

        Paragraph km = new Paragraph("HĐ đã áp dụng KM: Không", normalFont);
        km.setAlignment(Element.ALIGN_CENTER);
        document.add(km);

        document.add(Chunk.NEWLINE);

        Paragraph thanks = new Paragraph("Trân trọng cảm ơn!", new Font(base, 14, Font.BOLD));
        thanks.setAlignment(Element.ALIGN_CENTER);
        document.add(thanks);

        Paragraph vat = new Paragraph("(Hóa đơn chưa bao gồm thuế GTGT)", normalFont);
        vat.setAlignment(Element.ALIGN_CENTER);
        document.add(vat);

        document.close();
    }

    private static BaseFont unicodeFont() throws Exception {
        for (String path : new String[]{"/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", "C:/Windows/Fonts/arial.ttf"}) {
            if (java.nio.file.Files.isRegularFile(java.nio.file.Path.of(path)))
                return BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        }
        throw new IllegalStateException("Can cai font DejaVu Sans de xuat PDF tieng Viet.");
    }

    /**
     * Thêm một dòng thông tin vào bảng thông tin hóa đơn.
     *
     * @param table bảng PDF cần thêm dữ liệu
     * @param label nhãn hiển thị
     * @param value giá trị tương ứng
     * @param labelFont font dùng cho nhãn
     * @param valueFont font dùng cho giá trị
     */
    private static void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, labelFont));
        PdfPCell c2 = new PdfPCell(new Phrase(value, valueFont));
        c1.setBorder(Rectangle.NO_BORDER);
        c2.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(4);
        c2.setPadding(4);
        table.addCell(c1);
        table.addCell(c2);
    }

    /**
     * Thêm ô tiêu đề vào bảng danh sách món ăn.
     *
     * @param table bảng PDF cần thêm ô
     * @param text nội dung tiêu đề
     * @param font font chữ của ô tiêu đề
     */
    private static void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        table.addCell(cell);
    }

    /**
     * Thêm ô dữ liệu vào phần thân của bảng món ăn.
     *
     * @param table bảng PDF cần thêm ô
     * @param text nội dung hiển thị
     * @param font font chữ sử dụng
     * @param align kiểu canh lề của nội dung
     */
    private static void addBodyCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(7);
        table.addCell(cell);
    }

    /**
     * Thêm một dòng tổng kết vào bảng thanh toán cuối hóa đơn.
     *
     * @param table bảng tổng kết
     * @param left nhãn bên trái
     * @param right giá trị bên phải
     * @param leftFont font của nhãn
     * @param rightFont font của giá trị
     */
    private static void addSummaryRow(PdfPTable table, String left, String right, Font leftFont, Font rightFont) {
        PdfPCell c1 = new PdfPCell(new Phrase(left, leftFont));
        PdfPCell c2 = new PdfPCell(new Phrase(right, rightFont));
        c1.setBorder(Rectangle.NO_BORDER);
        c2.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(4);
        c2.setPadding(4);
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(c1);
        table.addCell(c2);
    }

    /**
     * Định dạng tiền tệ để hiển thị trên hóa đơn PDF.
     *
     * @param value giá trị tiền
     * @return chuỗi tiền đã định dạng kèm đơn vị đồng
     */
    private static String formatMoney(BigDecimal value) {
        if (value == null) return "0đ";
        return MONEY.format(value) + "đ";
    }

    /**
     * Chuyển phương thức thanh toán sang chuỗi tiếng Việt dễ đọc.
     *
     * @param hoaDon hóa đơn cần lấy phương thức thanh toán
     * @return chuỗi mô tả phương thức thanh toán
     */
    private static String formatPaymentMethod(HoaDon hoaDon) {
        if (hoaDon.getPhuongThucThanhToan() == null) return "";
        return switch (hoaDon.getPhuongThucThanhToan()) {
            case CASH -> "Tiền mặt";
            case BANK_TRANSFER -> "Chuyển khoản";
        };
    }
}
