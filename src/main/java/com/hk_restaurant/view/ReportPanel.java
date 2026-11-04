package com.hk_restaurant.view;

import com.hk_restaurant.controller.ReportController;
import com.hk_restaurant.model.dto.DishSalesDTO;
import com.hk_restaurant.model.dto.EmployeeRevenueDTO;
import com.hk_restaurant.model.dto.MonthlyRevenueDTO;
import com.toedter.calendar.JDateChooser;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Panel giao diện báo cáo và thống kê.
 * Màn hình này hỗ trợ lọc dữ liệu theo thời gian, hiển thị KPI và các biểu đồ doanh thu.
 */
public class ReportPanel extends JPanel {

    private final ReportController controller;
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    private JDateChooser dcFromDate;
    private JDateChooser dcToDate;
    private JComboBox<Integer> cbxYear;
    private JComboBox<String> cbxMonth;

    private JLabel lblDailyRevenue;
    private JLabel lblAvgInvoice;
    private JLabel lblTopDish;
    private JLabel lblTopEmployee;

    private JPanel chartContainer;

    /**
     * Khởi tạo panel báo cáo.
     *
     * @param controller controller xử lý truy vấn thống kê
     */
    public ReportPanel(ReportController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initUI();
        setDefaultDateRange();
        reloadDashboard();
    }

    /**
     * Khởi tạo giao diện báo cáo gồm vùng lọc, khu vực KPI và vùng biểu đồ.
     */
    private void initUI() {
        add(buildFilterPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
    }

    /**
     * Tạo panel bộ lọc theo ngày, tháng, năm cho báo cáo.
     *
     * @return panel bộ lọc
     */
    private JPanel buildFilterPanel() {
        JPanel wrapper = new JPanel(new GridLayout(2, 1, 8, 8));
        wrapper.setBorder(BorderFactory.createTitledBorder("Báo cáo doanh thu"));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.add(new JLabel("Từ ngày:"));
        dcFromDate = new JDateChooser();
        dcFromDate.setDateFormatString("yyyy-MM-dd");
        dcFromDate.setPreferredSize(new Dimension(120, 28));
        ((JTextField) dcFromDate.getDateEditor().getUiComponent()).setEditable(false);
        row1.add(dcFromDate);

        row1.add(new JLabel("Đến ngày:"));
        dcToDate = new JDateChooser();
        dcToDate.setDateFormatString("yyyy-MM-dd");
        dcToDate.setPreferredSize(new Dimension(120, 28));
        ((JTextField) dcToDate.getDateEditor().getUiComponent()).setEditable(false);
        row1.add(dcToDate);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.add(new JLabel("Năm (cho doanh thu theo tháng):"));
        cbxYear = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 3; y <= currentYear + 1; y++) {
            cbxYear.addItem(y);
        }
        cbxYear.setSelectedItem(currentYear);
        row2.add(cbxYear);

        row2.add(new JLabel("Tháng:"));
        cbxMonth = new JComboBox<>(new String[]{
                "Tất cả", "1", "2", "3", "4", "5", "6",
                "7", "8", "9", "10", "11", "12"
        });
        row2.add(cbxMonth);

        JButton btnLoad = new JButton("Xem báo cáo");
        btnLoad.addActionListener(e -> reloadDashboard());
        row2.add(btnLoad);

        wrapper.add(row1);
        wrapper.add(row2);
        return wrapper;
    }

    /**
     * Tạo phần nội dung chính gồm KPI và khu vực hiển thị biểu đồ.
     *
     * @return panel nội dung trung tâm
     */
    private JPanel buildCenterPanel() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.add(buildKpiPanel(), BorderLayout.NORTH);

        chartContainer = new JPanel(new GridLayout(2, 2, 10, 10));
        root.add(chartContainer, BorderLayout.CENTER);

        return root;
    }

    /**
     * Tạo khu vực hiển thị các chỉ số KPI nhanh.
     *
     * @return panel KPI
     */
    private JPanel buildKpiPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));

        lblDailyRevenue = createKpiCard(panel, "Doanh thu hôm nay");
        lblAvgInvoice = createKpiCard(panel, "Hóa đơn trung bình");
        lblTopDish = createKpiCard(panel, "Món bán chạy nhất");
        lblTopEmployee = createKpiCard(panel, "Nhân viên nổi bật");

        return panel;
    }

    /**
     * Tạo một card KPI đơn lẻ.
     *
     * @param parent panel cha chứa card
     * @param title tiêu đề của KPI
     * @return label dùng để cập nhật giá trị KPI
     */
    private JLabel createKpiCard(JPanel parent, String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(10, 12, 10, 12)
        ));
        card.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblValue = new JLabel("...");
        lblValue.setFont(new Font("Arial", Font.BOLD, 20));
        lblValue.setForeground(new Color(26, 82, 118));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        parent.add(card);
        return lblValue;
    }

    /**
     * Thiết lập khoảng ngày mặc định là từ đầu tháng đến ngày hiện tại.
     */
    private void setDefaultDateRange() {
        LocalDate now = LocalDate.now();
        LocalDate from = now.withDayOfMonth(1);
        LocalDate to = now;

        dcFromDate.setDate(toDate(from));
        dcToDate.setDate(toDate(to));
    }

    /**
     * Tải lại toàn bộ dữ liệu báo cáo, KPI và biểu đồ theo bộ lọc hiện tại.
     */
    private void reloadDashboard() {
        try {
            LocalDate fromDate = toLocalDate(dcFromDate.getDate());
            LocalDate toDate = toLocalDate(dcToDate.getDate());

            if (fromDate == null || toDate == null) {
                throw new Exception("Vui lòng chọn đầy đủ Từ ngày và Đến ngày.");
            }

            if (fromDate.isAfter(toDate)) {
                throw new Exception("Từ ngày không được lớn hơn Đến ngày.");
            }

            LocalDateTime from = fromDate.atStartOfDay();
            LocalDateTime to = toDate.atTime(23, 59, 59);

            int selectedYear = (Integer) cbxYear.getSelectedItem();
            String selectedMonth = (String) cbxMonth.getSelectedItem();

            List<MonthlyRevenueDTO> monthly;
            if ("Tất cả".equals(selectedMonth)) {
                monthly = controller.layDoanhThuTheoThang(selectedYear);
            } else {
                monthly = controller.layDoanhThuTheoThang(selectedYear, Integer.parseInt(selectedMonth));
            }

            BigDecimal dailyRevenue = controller.layDoanhThuNgay(LocalDate.now());
            BigDecimal avgInvoice = controller.layGiaTriHoaDonTrungBinh(from, to);
            List<DishSalesDTO> topDishes = controller.layTopMonBanChay(from, to, 5);
            List<EmployeeRevenueDTO> employeeRevenue = controller.layDoanhThuTheoNhanVien(from, to);

            updateKpis(dailyRevenue, avgInvoice, topDishes, employeeRevenue);
            renderCharts(monthly, topDishes, employeeRevenue);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không tải được báo cáo: " + ex.getMessage(),
                    "Lỗi báo cáo",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Cập nhật các chỉ số KPI trên màn hình.
     *
     * @param dailyRevenue doanh thu hôm nay
     * @param avgInvoice giá trị hóa đơn trung bình
     * @param topDishes danh sách món bán chạy
     * @param employeeRevenue danh sách doanh thu theo nhân viên
     */
    private void updateKpis(BigDecimal dailyRevenue,
                            BigDecimal avgInvoice,
                            List<DishSalesDTO> topDishes,
                            List<EmployeeRevenueDTO> employeeRevenue) {
        lblDailyRevenue.setText(formatMoney(dailyRevenue));
        lblAvgInvoice.setText(formatMoney(avgInvoice));

        if (topDishes != null && !topDishes.isEmpty()) {
            lblTopDish.setText(topDishes.get(0).dishName());
        } else {
            lblTopDish.setText("Chưa có dữ liệu");
        }

        if (employeeRevenue != null && !employeeRevenue.isEmpty()) {
            lblTopEmployee.setText(employeeRevenue.get(0).fullName());
        } else {
            lblTopEmployee.setText("Chưa có dữ liệu");
        }
    }

    /**
     * Vẽ lại toàn bộ các biểu đồ thống kê.
     *
     * @param monthly dữ liệu doanh thu theo tháng
     * @param topDishes dữ liệu món bán chạy
     * @param employeeRevenue dữ liệu doanh thu theo nhân viên
     */
    private void renderCharts(List<MonthlyRevenueDTO> monthly,
                              List<DishSalesDTO> topDishes,
                              List<EmployeeRevenueDTO> employeeRevenue) {
        chartContainer.removeAll();

        chartContainer.add(buildChartCard("Doanh thu theo tháng", createMonthlyRevenueChart(monthly)));
        chartContainer.add(buildChartCard("Top món bán chạy", createTopDishChart(topDishes)));
        chartContainer.add(buildChartCard("Tỷ trọng doanh thu món", createDishPieChart(topDishes)));
        chartContainer.add(buildChartCard("Doanh thu theo nhân viên", createEmployeeRevenueChart(employeeRevenue)));

        chartContainer.revalidate();
        chartContainer.repaint();
    }

    /**
     * Tạo một card chứa tiêu đề và biểu đồ.
     *
     * @param title tiêu đề biểu đồ
     * @param chart biểu đồ cần hiển thị
     * @return panel chứa biểu đồ
     */
    private JPanel buildChartCard(String title, JFreeChart chart) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(8, 8, 8, 8)
        ));
        panel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(new EmptyBorder(0, 0, 8, 0));

        ChartPanel cp = new ChartPanel(chart);
        cp.setMouseWheelEnabled(true);
        cp.setBackground(Color.WHITE);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(cp, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo biểu đồ đường doanh thu theo tháng.
     *
     * @param data dữ liệu doanh thu theo tháng
     * @return biểu đồ JFreeChart
     */
    private JFreeChart createMonthlyRevenueChart(List<MonthlyRevenueDTO> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (data != null) {
            for (MonthlyRevenueDTO dto : data) {
                String label = "T" + dto.month() + "/" + dto.year();
                dataset.addValue(dto.revenue(), "Doanh thu", label);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "",
                "Tháng",
                "Doanh thu",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setDefaultShapesVisible(true);
        renderer.setSeriesStroke(0, new BasicStroke(3f));

        return chart;
    }

    /**
     * Tạo biểu đồ cột top món bán chạy theo số lượng.
     *
     * @param data dữ liệu món bán chạy
     * @return biểu đồ JFreeChart
     */
    private JFreeChart createTopDishChart(List<DishSalesDTO> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (data != null) {
            for (DishSalesDTO dto : data) {
                dataset.addValue(dto.totalQty(), "Số lượng", dto.dishName());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "",
                "Món ăn",
                "Số lượng bán",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.12);
        renderer.setSeriesPaint(0, new Color(54, 162, 235));

        return chart;
    }

    /**
     * Tạo biểu đồ tròn thể hiện tỷ trọng doanh thu của các món bán chạy.
     *
     * @param data dữ liệu doanh thu món ăn
     * @return biểu đồ JFreeChart
     */
    private JFreeChart createDishPieChart(List<DishSalesDTO> data) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        if (data != null) {
            for (DishSalesDTO dto : data) {
                double revenue = dto.revenue() != null ? dto.revenue().doubleValue() : 0d;
                dataset.setValue(dto.dishName(), revenue);
            }
        }

        JFreeChart chart = ChartFactory.createRingChart(
                "",
                dataset,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        PiePlot<?> plot = (PiePlot<?>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {2}"));

        return chart;
    }

    /**
     * Tạo biểu đồ cột ngang doanh thu theo nhân viên.
     *
     * @param data dữ liệu doanh thu nhân viên
     * @return biểu đồ JFreeChart
     */
    private JFreeChart createEmployeeRevenueChart(List<EmployeeRevenueDTO> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (data != null) {
            for (EmployeeRevenueDTO dto : data) {
                BigDecimal revenue = dto.revenue() != null ? dto.revenue() : BigDecimal.ZERO;
                dataset.addValue(revenue, "Doanh thu", dto.fullName());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "",
                "Nhân viên",
                "Doanh thu",
                dataset,
                PlotOrientation.HORIZONTAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.15);
        renderer.setSeriesPaint(0, new Color(54, 162, 235));

        return chart;
    }

    /**
     * Định dạng tiền tệ hiển thị trên dashboard.
     *
     * @param value giá trị tiền
     * @return chuỗi tiền đã định dạng
     */
    private String formatMoney(BigDecimal value) {
        if (value == null) return "0 VNĐ";
        return moneyFormat.format(value) + " VNĐ";
    }

    /**
     * Chuyển kiểu Date sang LocalDate.
     *
     * @param date ngày kiểu Date
     * @return ngày kiểu LocalDate
     */
    private LocalDate toLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Chuyển kiểu LocalDate sang Date.
     *
     * @param localDate ngày kiểu LocalDate
     * @return ngày kiểu Date
     */
    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}