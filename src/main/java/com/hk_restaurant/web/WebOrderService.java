package com.hk_restaurant.web;

import com.hk_restaurant.config.JpaUtil;
import com.hk_restaurant.dao.impl.*;
import com.hk_restaurant.model.entity.*;
import com.hk_restaurant.model.dto.OrderDetailInput;
import com.hk_restaurant.model.enumtype.PaymentMethod;
import com.hk_restaurant.service.DonHangService;
import com.hk_restaurant.service.impl.DonHangServiceImpl;
import jakarta.persistence.*;
import java.util.function.BiFunction;

/** Each web command is atomic; the table lock serializes simultaneous POS requests. */
public class WebOrderService implements DonHangService {
    private <T> T execute(Long tableId,Long orderId,BiFunction<EntityManager,DonHangService,T> action) {
        return JpaUtil.executeInTransaction(em -> {
            Long lockedTable=tableId;
            if(orderId!=null) lockedTable=em.createQuery("SELECT d.banAn.id FROM DonHang d WHERE d.id=:id",Long.class)
                    .setParameter("id",orderId).getResultStream().findFirst().orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng."));
            if(lockedTable==null || em.find(BanAn.class,lockedTable,LockModeType.PESSIMISTIC_WRITE)==null)
                throw new IllegalArgumentException("Không tìm thấy bàn.");
            if(orderId!=null) em.find(DonHang.class,orderId,LockModeType.PESSIMISTIC_WRITE);
            var service=new DonHangServiceImpl(new DonHangDAOImpl(em),new TransactionalTableDao(em),new MonAnDAOImpl(em),
                    new HoaDonDAOImpl(em),new NhanVienDAOImpl(em));
            return action.apply(em,service);
        });
    }
    public DonHang moBanMoi(Long table,Long employee,String note) { return execute(table,null,(em,s)->s.moBanMoi(table,employee,note)); }
    public DonHang layDonDangPhucVuTheoBan(Long table) { return execute(table,null,(em,s)->s.layDonDangPhucVuTheoBan(table)); }
    public DonHang themMonVaoDon(Long id,OrderDetailInput input) {
        return execute(null,id,(em,s)-> {
            if(input==null || input.getDishId()==null) throw new IllegalArgumentException("Chọn món ăn.");
            var dish=em.find(MonAn.class,input.getDishId());
            if(dish==null) throw new IllegalArgumentException("Không tìm thấy món ăn.");
            input.setUnitPrice(dish.getDonGia());
            return s.themMonVaoDon(id,input);
        });
    }
    public DonHang capNhatSoLuongMon(Long id,Long dish,int quantity) { return execute(null,id,(em,s)->s.capNhatSoLuongMon(id,dish,quantity)); }
    public DonHang capNhatGhiChuMon(Long id,Long dish,String note) { return execute(null,id,(em,s)->s.capNhatGhiChuMon(id,dish,note)); }
    public DonHang changeItem(Long id,Long dish,int quantity,String note) {
        return execute(null,id,(em,s)->{s.capNhatSoLuongMon(id,dish,quantity);
            if(quantity>0) s.capNhatGhiChuMon(id,dish,note);
            return new DonHangDAOImpl(em).findByIdWithDetails(id).orElseThrow();});
    }
    public DonHang capNhatNhanVienPhucVuTheoBan(Long table,Long employee) { return execute(table,null,(em,s)->s.capNhatNhanVienPhucVuTheoBan(table,employee)); }
    public HoaDon thanhToanDonHang(Long id,PaymentMethod method) { return execute(null,id,(em,s)->s.thanhToanDonHang(id,method)); }
}
