package com.hk_restaurant.web;

import com.hk_restaurant.config.JpaUtil;
import com.hk_restaurant.dao.impl.*;
import com.hk_restaurant.model.entity.*;
import com.hk_restaurant.model.dto.*;
import com.hk_restaurant.model.enumtype.*;
import com.hk_restaurant.service.*;
import com.hk_restaurant.util.HoaDonPdfExporter;
import com.hk_restaurant.util.security.PasswordUtils;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.io.ByteArrayOutputStream;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class RestaurantApi {
    private final NhanVienService employees;
    private final MonAnService dishes;
    private final BanAnService tables;
    private final WebOrderService orders;
    private final ReportService reports;
    public RestaurantApi(NhanVienService employees, MonAnService dishes, BanAnService tables,
                         WebOrderService orders, ReportService reports) {
        this.employees=employees; this.dishes=dishes; this.tables=tables; this.orders=orders; this.reports=reports;
    }
    static Map<String,Object> row(Object... pairs) {
        var map=new LinkedHashMap<String,Object>();
        for(int i=0;i<pairs.length;i+=2) map.put((String)pairs[i], pairs[i+1]);
        return map;
    }
    static Map<String,Object> employee(NhanVien n) {
        return row("id",n.getId(),"code",n.getMaNhanVienCode(),"name",n.getHoTen(),"phone",n.getSoDienThoai(),
                "username",n.getTenDangNhap(),"role",n.getVaiTro(),"active",n.getDangLamViec(),"left",n.getDaNghiViec());
    }
    static Map<String,Object> dish(MonAn m) {
        return row("id",m.getId(),"code",m.getMaMonCode(),"name",m.getTenMon(),"price",m.getDonGia(),
                "image",m.getHinhAnh(),"description",m.getMoTa(),"active",m.getDangKinhDoanh(),
                "categoryId",m.getLoaiMon().getId(),"category",m.getLoaiMon().getTenLoaiMon());
    }
    static Map<String,Object> table(BanAn b) {
        return row("id",b.getId(),"code",b.getMaBanCode(),"name",b.getTenBan(),"capacity",b.getSucChua(),
                "status",b.getTrangThai(),"active",b.getDangSuDung());
    }
    static Map<String,Object> order(DonHang d) {
        return row("id",d.getId(),"code",d.getSoDonHang(),"table",d.getBanAn().getTenBan(),
                "employeeId",d.getNhanVien().getId(),"employee",d.getNhanVien().getHoTen(),
                "time",d.getThoiGianLap(),"total",d.getTongTien(),"status",d.getTrangThai(),"note",d.getGhiChu(),
                "items",d.getDanhSachChiTiet().stream().map(c -> row("dishId",c.getMonAn().getId(),
                        "name",c.getMonAn().getTenMon(),"quantity",c.getSoLuong(),"price",c.getDonGia(),
                        "total",c.getThanhTien(),"note",c.getGhiChu())).toList());
    }
    private NhanVien current(Authentication auth) {
        var n=new NhanVienDAOImpl().findByUsername(auth.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if(!Boolean.TRUE.equals(n.getDangLamViec()) || Boolean.TRUE.equals(n.getDaNghiViec()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return n;
    }
    @GetMapping("/csrf") public Map<String,Object> csrf(CsrfToken token) {
        return row("token",token.getToken(),"headerName",token.getHeaderName(),"parameterName",token.getParameterName());
    }
    @GetMapping("/me") public Map<String,Object> me(Authentication auth) { return employee(current(auth)); }
    @GetMapping("/tables") public Object tables() { return tables.layTatCaBanAn().stream().map(RestaurantApi::table).toList(); }
    @GetMapping("/dishes") public Object dishes() { return dishes.layDanhSachThucDonDangBan().stream().map(RestaurantApi::dish).toList(); }
    @GetMapping("/employees/active") public Object activeEmployees() {
        return employees.layDanhSachNhanVienDangLam().stream().map(n -> row("id",n.getId(),"name",n.getHoTen())).toList();
    }
    @GetMapping("/categories") public Object categories() {
        try(var em=JpaUtil.getEntityManager()) {
            return em.createQuery("FROM LoaiMon ORDER BY id",LoaiMon.class).getResultList().stream()
                    .map(c -> row("id",c.getId(),"name",c.getTenLoaiMon())).toList();
        }
    }
    @GetMapping("/tables/{id}/order") public Object tableOrder(@PathVariable Long id) {
        var d=orders.layDonDangPhucVuTheoBan(id); return d==null ? null : order(d);
    }
    public record OpenOrder(Long employeeId,String note) {}
    @PostMapping("/tables/{id}/open") public Object open(@PathVariable Long id,@RequestBody OpenOrder input,Authentication auth) {
        var user=current(auth);
        Long employeeId=user.getVaiTro()==Role.ADMIN && input.employeeId()!=null ? input.employeeId() : user.getId();
        return order(orders.moBanMoi(id,employeeId,input.note()));
    }
    @PostMapping("/orders/{id}/items") public Object addItem(@PathVariable Long id,@RequestBody OrderDetailInput input) {
        orders.themMonVaoDon(id,input); return order(new DonHangDAOImpl().findByIdWithDetails(id).orElseThrow());
    }
    public record ItemChange(int quantity,String note) {}
    @PostMapping("/orders/{id}/items/{dishId}") public Object changeItem(@PathVariable Long id,@PathVariable Long dishId,@RequestBody ItemChange input) {
        return order(orders.changeItem(id,dishId,input.quantity(),input.note()));
    }
    @PostMapping("/admin/tables/{id}/employee") public void assign(@PathVariable Long id,@RequestBody OpenOrder input) {
        orders.capNhatNhanVienPhucVuTheoBan(id,input.employeeId());
    }
    public record Payment(PaymentMethod method) {}
    @PostMapping("/orders/{id}/pay") public Object pay(@PathVariable Long id,@RequestBody Payment input) {
        if(input.method()==null) throw new IllegalArgumentException("Chọn phương thức thanh toán.");
        var bill=orders.thanhToanDonHang(id,input.method());
        return row("id",bill.getId(),"code",bill.getSoHoaDon(),"total",bill.getTongTien());
    }
    @GetMapping("/invoices") public Object invoices() {
        try(var em=JpaUtil.getEntityManager()) {
            return em.createQuery("SELECT h FROM HoaDon h JOIN FETCH h.donHang d JOIN FETCH d.banAn ORDER BY h.thoiGianThanhToan DESC",HoaDon.class)
                    .setMaxResults(200).getResultList().stream().map(h -> row("id",h.getId(),"code",h.getSoHoaDon(),
                            "table",h.getDonHang().getBanAn().getTenBan(),"total",h.getTongTien(),"time",h.getThoiGianThanhToan(),"method",h.getPhuongThucThanhToan())).toList();
        }
    }
    @GetMapping("/invoices/{id}/pdf") public ResponseEntity<byte[]> pdf(@PathVariable Long id) throws Exception {
        try(var em=JpaUtil.getEntityManager()) {
            var bill=em.find(HoaDon.class,id);
            if(bill==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            var out=new ByteArrayOutputStream();
            HoaDonPdfExporter.exportToPdf(bill,out);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition","attachment; filename=invoice-"+id+".pdf").body(out.toByteArray());
        }
    }
    @GetMapping("/admin/dishes") public Object allDishes() { return dishes.layDanhSachMonAn().stream().map(RestaurantApi::dish).toList(); }
    public record DishInput(Long id,String code,String name,BigDecimal price,Long categoryId,String image,String description,Boolean active) {}
    @PostMapping("/admin/dishes") public Object saveDish(@RequestBody DishInput input) {
        var m=new MonAn(); m.setId(input.id()); m.setMaMonCode(input.code()); m.setTenMon(input.name());
        m.setDonGia(input.price()); m.setHinhAnh(input.image()); m.setMoTa(input.description()); m.setDangKinhDoanh(input.active());
        try(var em=JpaUtil.getEntityManager()) {
            var category=em.find(LoaiMon.class,input.categoryId());
            if(category==null) throw new IllegalArgumentException("Loại món không tồn tại.");
            m.setLoaiMon(category);
        }
        if(input.id()==null) dishes.themMonAn(m); else dishes.capNhatMonAn(m);
        return dish(new MonAnDAOImpl().findByCode(m.getMaMonCode()).orElseThrow());
    }
    @PostMapping("/admin/dishes/{id}/stop") public void stopDish(@PathVariable Long id) { dishes.ngungKinhDoanh(id); }
    @DeleteMapping("/admin/dishes/{id}") public void deleteDish(@PathVariable Long id) { dishes.xoaVinhVienMonAn(id); }
    @PostMapping("/admin/images") public Object upload(@RequestParam MultipartFile file) throws Exception {
        byte[] bytes=file.getBytes();
        if(bytes.length==0 || bytes.length>5*1024*1024) throw new IllegalArgumentException("Ảnh phải nhỏ hơn 5 MB.");
        String ext;
        if(bytes.length>8 && (bytes[0]&255)==137 && bytes[1]==80 && bytes[2]==78 && bytes[3]==71) ext="png";
        else if(bytes.length>3 && (bytes[0]&255)==255 && (bytes[1]&255)==216 && (bytes[2]&255)==255) ext="jpg";
        else throw new IllegalArgumentException("Chỉ nhận ảnh PNG hoặc JPEG.");
        if(javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(bytes))==null) throw new IllegalArgumentException("Ảnh không hợp lệ.");
        Path directory=Path.of("images"); Files.createDirectories(directory);
        String name=UUID.randomUUID()+"."+ext; Files.write(directory.resolve(name),bytes);
        return row("path","images/"+name);
    }
    public record TableInput(Long id,String code,String name,Integer capacity,Boolean active) {}
    @PostMapping("/admin/tables") public Object saveTable(@RequestBody TableInput input) {
        BanAn b;
        if(input.id()==null) b=new BanAn();
        else b=new BanAnDAOImpl().findById(input.id()).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn."));
        b.setMaBanCode(input.code()); b.setTenBan(input.name()); b.setSucChua(input.capacity());
        if(input.id()!=null && Boolean.FALSE.equals(input.active()) && b.getTrangThai()==TableStatus.SERVING)
            throw new IllegalArgumentException("Không thể khóa bàn đang phục vụ.");
        b.setDangSuDung(input.active());
        return table(input.id()==null ? tables.taoMoiBanAn(b) : tables.capNhatBanAn(b));
    }
    @PostMapping("/admin/tables/{id}/stop") public void stopTable(@PathVariable Long id) { tables.xoaBanAn(id); }
    @DeleteMapping("/admin/tables/{id}") public void deleteTable(@PathVariable Long id) { tables.xoaVinhVienBanAn(id); }
    @GetMapping("/admin/employees") public Object allEmployees() { return employees.layDanhSachNhanVien(); }
    public record EmployeeInput(Long id,String code,String name,String phone,String username,Role role,
                                String password,String confirmation,String currentPassword) {
        @Override public String toString() { return "EmployeeInput[id="+id+", credentials=REDACTED]"; }
    }
    @PostMapping("/admin/employees") public Object saveEmployee(@RequestBody EmployeeInput input) {
        var n=new NhanVien(); n.setId(input.id()); n.setMaNhanVienCode(input.code()); n.setHoTen(input.name());
        n.setSoDienThoai(input.phone()); n.setTenDangNhap(input.username()); n.setVaiTro(input.role());
        return employee(input.id()==null ? employees.themNhanVien(n,input.password(),input.confirmation()) : employees.capNhatNhanVien(n,input.currentPassword()));
    }
    public record EmployeeAction(String action,String password,String newPassword,String confirmation) {
        @Override public String toString() { return "EmployeeAction[action="+action+", credentials=REDACTED]"; }
    }
    @PostMapping("/admin/employees/{id}/action") public void employeeAction(@PathVariable Long id,@RequestBody EmployeeAction input,Authentication auth) {
        if(Objects.equals(current(auth).getId(),id) && !"password".equals(input.action()))
            throw new IllegalArgumentException("Không thể khóa hoặc xóa tài khoản đang đăng nhập.");
        switch(input.action()) {
            case "pause" -> employees.choNghiLam(id,input.password());
            case "resume" -> employees.choDiLamLai(id);
            case "leave" -> employees.choNghiViec(id,input.password());
            case "delete" -> employees.xoaNhanVien(id,input.password());
            case "password" -> {
                // Web resets require the authenticated administrator's password, not the desktop demo root key.
                var admin=current(auth);
                if(!PasswordUtils.matches(input.password(),admin.getMatKhau())) throw new IllegalArgumentException("Mật khẩu quản trị không đúng.");
                if(input.newPassword()==null || input.newPassword().isBlank() || !input.newPassword().equals(input.confirmation()))
                    throw new IllegalArgumentException("Mật khẩu mới và xác nhận không hợp lệ.");
                JpaUtil.executeInTransaction(em -> { var n=em.find(NhanVien.class,id);
                    if(n==null) throw new IllegalArgumentException("Không tìm thấy nhân viên.");
                    n.setMatKhau(PasswordUtils.hash(input.newPassword())); return null; });
            }
            default -> throw new IllegalArgumentException("Thao tác không hợp lệ.");
        }
    }
    @GetMapping("/admin/reports") public Object report(@RequestParam LocalDate start,@RequestParam LocalDate end,
                                                      @RequestParam int year,@RequestParam(defaultValue="0") int month) {
        if(end.isBefore(start) || year<2000 || year>2100 || month<0 || month>12) throw new IllegalArgumentException("Khoảng thời gian không hợp lệ.");
        var from=start.atStartOfDay(); var to=end.atTime(LocalTime.MAX);
        return row("daily",reports.layDoanhThuNgay(end),"average",reports.layGiaTriHoaDonTrungBinh(from,to),
                "monthly",month==0 ? reports.layDoanhThuTheoThang(year) : reports.layDoanhThuTheoThang(year,month),
                "dishes",reports.layTopMonBanChay(from,to,20),"employees",reports.layDoanhThuTheoNhanVien(from,to));
    }
}
