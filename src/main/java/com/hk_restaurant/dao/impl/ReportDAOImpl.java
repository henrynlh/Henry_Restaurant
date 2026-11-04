package com.hk_restaurant.dao.impl;

import com.hk_restaurant.config.JpaUtil;
import com.hk_restaurant.dao.ReportDAO;
import com.hk_restaurant.model.dto.DishSalesDTO;
import com.hk_restaurant.model.dto.EmployeeRevenueDTO;
import com.hk_restaurant.model.dto.MonthlyRevenueDTO;
import com.hk_restaurant.model.enumtype.OrderStatus;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Lớp cài đặt DAO cho phân hệ báo cáo và thống kê.
 * Lớp này sử dụng JPQL để truy vấn dữ liệu tổng hợp phục vụ dashboard báo cáo.
 */
public class ReportDAOImpl implements ReportDAO {

    /**
     * Tính tổng doanh thu của một ngày cụ thể.
     *
     * @param date ngày cần thống kê
     * @return tổng doanh thu trong ngày, trả về 0 nếu không có dữ liệu
     */
    @Override
    public BigDecimal getDailyRevenue(LocalDate date) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            BigDecimal total = em.createQuery("""
                    SELECT SUM(h.tongTien)
                    FROM HoaDon h
                    WHERE h.thoiGianThanhToan BETWEEN :start AND :end
                    """, BigDecimal.class)
                    .setParameter("start", startOfDay)
                    .setParameter("end", endOfDay)
                    .getSingleResult();

            return total != null ? total : BigDecimal.ZERO;
        }
    }

    /**
     * Lấy doanh thu theo từng tháng của một năm.
     *
     * @param year năm cần thống kê
     * @return danh sách DTO doanh thu theo tháng
     */
    @Override
    public List<MonthlyRevenueDTO> getRevenueByMonth(int year) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("""
                    SELECT new com.hk_restaurant.model.dto.MonthlyRevenueDTO(
                        YEAR(h.thoiGianThanhToan),
                        MONTH(h.thoiGianThanhToan),
                        SUM(h.tongTien)
                    )
                    FROM HoaDon h
                    WHERE YEAR(h.thoiGianThanhToan) = :year
                    GROUP BY YEAR(h.thoiGianThanhToan), MONTH(h.thoiGianThanhToan)
                    ORDER BY MONTH(h.thoiGianThanhToan)
                    """, MonthlyRevenueDTO.class)
                    .setParameter("year", year)
                    .getResultList();
        }
    }

    /**
     * Lấy doanh thu của một tháng cụ thể trong năm.
     *
     * @param year năm cần thống kê
     * @param month tháng cần thống kê
     * @return danh sách DTO doanh thu theo tháng
     */
    @Override
    public List<MonthlyRevenueDTO> getRevenueByMonth(int year, int month) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("""
                    SELECT new com.hk_restaurant.model.dto.MonthlyRevenueDTO(
                        YEAR(h.thoiGianThanhToan),
                        MONTH(h.thoiGianThanhToan),
                        SUM(h.tongTien)
                    )
                    FROM HoaDon h
                    WHERE YEAR(h.thoiGianThanhToan) = :year
                      AND MONTH(h.thoiGianThanhToan) = :month
                    GROUP BY YEAR(h.thoiGianThanhToan), MONTH(h.thoiGianThanhToan)
                    ORDER BY MONTH(h.thoiGianThanhToan)
                    """, MonthlyRevenueDTO.class)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .getResultList();
        }
    }

    /**
     * Lấy danh sách các món bán chạy nhất trong một khoảng thời gian.
     *
     * @param startDate thời điểm bắt đầu
     * @param endDate thời điểm kết thúc
     * @param limit số lượng món cần lấy
     * @return danh sách món bán chạy
     */
    @Override
    public List<DishSalesDTO> getTopSellingDishes(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("""
                    SELECT new com.hk_restaurant.model.dto.DishSalesDTO(
                        m.maMonCode,
                        m.tenMon,
                        SUM(ct.soLuong),
                        SUM(ct.thanhTien)
                    )
                    FROM ChiTietDonHang ct
                    JOIN ct.monAn m
                    JOIN ct.donHang d
                    JOIN HoaDon h ON h.donHang.id = d.id
                    WHERE d.trangThai = :status
                      AND h.thoiGianThanhToan BETWEEN :start AND :end
                    GROUP BY m.id, m.maMonCode, m.tenMon
                    ORDER BY SUM(ct.soLuong) DESC
                    """, DishSalesDTO.class)
                    .setParameter("status", OrderStatus.PAID)
                    .setParameter("start", startDate)
                    .setParameter("end", endDate)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    /**
     * Lấy doanh thu theo từng nhân viên trong khoảng thời gian chỉ định.
     *
     * @param startDate thời điểm bắt đầu
     * @param endDate thời điểm kết thúc
     * @return danh sách doanh thu theo nhân viên
     */
    @Override
    public List<EmployeeRevenueDTO> getRevenueByEmployee(LocalDateTime startDate, LocalDateTime endDate) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("""
                    SELECT new com.hk_restaurant.model.dto.EmployeeRevenueDTO(
                        nv.maNhanVienCode,
                        nv.hoTen,
                        SUM(h.tongTien)
                    )
                    FROM HoaDon h
                    JOIN h.donHang d
                    JOIN d.nhanVien nv
                    WHERE h.thoiGianThanhToan BETWEEN :start AND :end
                      AND nv IS NOT NULL
                    GROUP BY nv.id, nv.maNhanVienCode, nv.hoTen
                    ORDER BY SUM(h.tongTien) DESC
                    """, EmployeeRevenueDTO.class)
                    .setParameter("start", startDate)
                    .setParameter("end", endDate)
                    .getResultList();
        }
    }

    /**
     * Tính giá trị trung bình của hóa đơn trong một khoảng thời gian.
     *
     * @param startDate thời điểm bắt đầu
     * @param endDate thời điểm kết thúc
     * @return giá trị hóa đơn trung bình, trả về 0 nếu không có dữ liệu
     */
    @Override
    public BigDecimal getAverageInvoiceValue(LocalDateTime startDate, LocalDateTime endDate) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            Double avg = em.createQuery("""
                    SELECT AVG(h.tongTien)
                    FROM HoaDon h
                    WHERE h.thoiGianThanhToan BETWEEN :start AND :end
                    """, Double.class)
                    .setParameter("start", startDate)
                    .setParameter("end", endDate)
                    .getSingleResult();

            return avg != null ? BigDecimal.valueOf(avg) : BigDecimal.ZERO;
        }
    }
}