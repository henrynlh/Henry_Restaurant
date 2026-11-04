'use strict';
const $=s=>document.querySelector(s), esc=s=>String(s??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const money=n=>new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND'}).format(n||0), date=s=>s?new Date(s).toLocaleString('vi-VN'):'';
const state={user:null,page:'pos',tables:[],dishes:[],employees:[],categories:[],selected:null,order:null,search:'',category:'',adminDishes:[]};
let csrf,saveForm,loading=false;
async function csrfToken(){csrf=await fetch('/api/csrf').then(r=>r.json());}
async function api(path,method='GET',body){
 const headers={};if(method!=='GET'){if(!csrf)await csrfToken();headers[csrf.headerName]=csrf.token;}
 if(body && !(body instanceof FormData))headers['Content-Type']='application/json';
 const response=await fetch('/api'+path,{method,headers,body:body instanceof FormData?body:body?JSON.stringify(body):undefined});
 if(response.status===401){showLogin();throw new Error('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');}
 if(!response.ok){let data;try{data=await response.json();}catch{}throw new Error(data?.message||(response.status===403?'Bạn không có quyền hoặc phiên đã thay đổi. Hãy tải lại trang.':'Không thể thực hiện. Kiểm tra thông tin và thử lại.'));}
 const text=await response.text();return text?JSON.parse(text):null;
}
function toast(message){$('#toast').textContent=message;$('#toast').hidden=false;clearTimeout(toast.timer);toast.timer=setTimeout(()=>$('#toast').hidden=true,4500);}
function showLogin(){$('#app').hidden=true;$('#login-screen').hidden=false;state.user=null;}
const navs=[['pos','▦','Bán hàng'],['invoices','▤','Hóa đơn'],['tables','▧','Quản lý bàn'],['dishes','◈','Thực đơn'],['employees','♙','Nhân viên'],['reports','▥','Báo cáo']];
async function init(){
 try{await csrfToken();state.user=await api('/me');if(state.user)await enter();}catch{showLogin();}
}
async function enter(){
 $('#login-screen').hidden=true;$('#app').hidden=false;
 $('#profile').innerHTML=`${esc(state.user.name)}<small>${state.user.role==='ADMIN'?'Quản trị viên':'Nhân viên phục vụ'}</small>`;
 $('#navigation').innerHTML=navs.filter((n,i)=>i<2||state.user.role==='ADMIN').map(n=>`<button data-nav="${n[0]}"><span>${n[1]}</span>${n[2]}</button>`).join('');
 $('#today').textContent=new Date().toLocaleDateString('vi-VN',{weekday:'long',day:'numeric',month:'long'});
 await navigate('pos');
}
async function navigate(page){
 if(loading)return;loading=true;state.page=page;
 document.querySelectorAll('[data-nav]').forEach(b=>b.classList.toggle('active',b.dataset.nav===page));
 $('#page-title').textContent=navs.find(n=>n[0]===page)?.[2]||'';
 $('#content').innerHTML='<div class="empty">Đang tải dữ liệu…</div>';
 try{await ({pos:loadPos,tables:loadTables,dishes:loadDishes,employees:loadEmployees,invoices:loadInvoices,reports:loadReports}[page])();}
 catch(e){$('#content').innerHTML=`<div class="panel error">${esc(e.message)} <button data-action="reload" class="secondary">Thử lại</button></div>`;}
 finally{loading=false;}
}
function empty(text){return `<div class="empty"><span class="symbol">▧</span>${esc(text)}</div>`;}
function stats(items){return `<div class="stats">${items.map(([label,value,note])=>`<div class="stat"><small>${esc(label)}</small><strong>${esc(value)}</strong>${note?`<small>${esc(note)}</small>`:''}</div>`).join('')}</div>`;}
function tableHtml(heads,rows){return `<div class="table-wrap"><table><thead><tr>${heads.map(h=>`<th>${esc(h)}</th>`).join('')}</tr></thead><tbody>${rows.length?rows.join(''):`<tr><td colspan="${heads.length}">${empty('Chưa có dữ liệu trong khoảng đã chọn.')}</td></tr>`}</tbody></table></div>`;}
function btn(text,action,id,cls='secondary'){return `<button class="${cls}" data-action="${action}" data-id="${id??''}">${esc(text)}</button>`;}
function image(path){return typeof path==='string'&&/^images\/[a-zA-Z0-9_.-]+$/.test(path)?`/${path}`:'';}
function photo(d){const src=image(d.image);return `<div class="dish-photo">${src?`<img src="${esc(src)}" alt="${esc(d.name)}" loading="lazy">`:'♨'}</div>`;}
async function base(){[state.tables,state.dishes,state.employees,state.categories]=await Promise.all([api('/tables'),api('/dishes'),api('/employees/active'),api('/categories')]);}
async function loadPos(){await base();if(state.selected)state.order=await api(`/tables/${state.selected}/order`);renderPos();}
function renderPos(){
 const active=state.tables.filter(t=>t.active), serving=active.filter(t=>t.status==='SERVING').length;
 const filtered=state.dishes.filter(d=>(!state.category||String(d.categoryId)===state.category)&&(!state.search||d.name.toLocaleLowerCase('vi').includes(state.search.toLocaleLowerCase('vi'))));
 $('#content').innerHTML=stats([['Bàn đang phục vụ',serving,'Đơn đang mở'],['Bàn sẵn sàng',active.length-serving,'Sẵn sàng đón khách'],['Món trong thực đơn',state.dishes.length,'Đang kinh doanh']])+`<div class="pos-layout"><section><div class="section-head"><h2>Sơ đồ bàn</h2><span class="badge green">${active.length} bàn hoạt động</span></div><div class="table-grid">${active.map(t=>`<button class="table-card ${t.status==='SERVING'?'serving':''} ${state.selected===t.id?'selected':''}" data-action="select-table" data-id="${t.id}"><span class="table-icon">▤</span><strong>${esc(t.name)}</strong><small>${t.capacity} chỗ · ${t.status==='SERVING'?'Đang phục vụ':'Bàn trống'}</small></button>`).join('')}</div><div class="section-head"><h2>Thực đơn</h2><span class="muted">Chọn món để thêm vào đơn</span></div><div class="toolbar"><input id="dish-search" placeholder="Tìm món ăn…" value="${esc(state.search)}" aria-label="Tìm món"><select id="category-filter" aria-label="Loại món"><option value="">Tất cả loại món</option>${state.categories.map(c=>`<option value="${c.id}" ${String(c.id)===state.category?'selected':''}>${esc(c.name)}</option>`).join('')}</select></div><div class="menu-grid">${filtered.map(d=>`<article class="dish-card">${photo(d)}<div class="dish-body"><small class="muted">${esc(d.category)}</small><h3>${esc(d.name)}</h3><div class="dish-footer"><span class="price">${money(d.price)}</span>${btn('+','add-item',d.id)}</div></div></article>`).join('')}</div>${!filtered.length?empty('Không tìm thấy món phù hợp.'):''}</section><aside class="panel order-panel" id="order-panel">${renderOrder()}</aside></div>`;
 document.querySelectorAll('img').forEach(img=>img.onerror=()=>{img.parentElement.textContent='♨';});
}
function renderOrder(){
 const t=state.tables.find(t=>t.id===state.selected),o=state.order;
 if(!t)return empty('Chọn một bàn để bắt đầu gọi món.');
 if(!o)return `<span class="eyebrow">ĐÓN KHÁCH MỚI</span><h2>${esc(t.name)}</h2><p class="muted">${t.capacity} chỗ ngồi · Sẵn sàng phục vụ</p><label>Nhân viên phụ trách<select id="open-employee">${state.employees.filter(e=>state.user.role==='ADMIN'||e.id===state.user.id).map(e=>`<option value="${e.id}" ${e.id===state.user.id?'selected':''}>${esc(e.name)}</option>`).join('')}</select></label><label>Ghi chú<input id="open-note" placeholder="Yêu cầu của khách…"></label><button class="primary wide" data-action="open-table">Mở bàn và tạo đơn →</button>`;
 return `<span class="eyebrow">ĐƠN ĐANG PHỤC VỤ</span><div class="section-head"><h2>${esc(t.name)}</h2><span class="badge green">Đang mở</span></div><p class="muted">${esc(o.code)}<br>${esc(o.employee)} · ${date(o.time)}</p>${state.user.role==='ADMIN'?btn('Đổi nhân viên','assign',t.id,'ghost'):''}${o.note?`<p>${esc(o.note)}</p>`:''}<div>${o.items.length?o.items.map(i=>`<div class="order-item"><h3>${esc(i.name)}</h3><div class="item-bottom"><div class="quantity">${btn('−','minus-item',i.dishId)}<span>${i.quantity}</span>${btn('+','plus-item',i.dishId)}</div><strong>${money(i.total)}</strong></div><button class="order-note" data-action="note-item" data-id="${i.dishId}">${esc(i.note||'Thêm ghi chú')}</button></div>`).join(''):empty('Chọn món từ thực đơn để thêm vào đơn.')}</div><div class="order-total"><span>Tổng cộng</span><strong>${money(o.total)}</strong></div><label>Phương thức thanh toán<select id="payment-method"><option value="CASH">Tiền mặt</option><option value="BANK_TRANSFER">Chuyển khoản</option></select></label><button class="primary wide" data-action="pay" ${o.items.length?'':'disabled'}>Thanh toán ${money(o.total)} →</button>`;
}
async function selectTable(id){state.selected=id;state.order=await api(`/tables/${id}/order`);renderPos();}
async function loadTables(){state.tables=await api('/tables');renderTables();}
function renderTables(){
 $('#content').innerHTML=stats([['Tổng số bàn',state.tables.length],['Đang phục vụ',state.tables.filter(t=>t.status==='SERVING').length],['Tạm ngưng',state.tables.filter(t=>!t.active).length]])+`<section class="panel"><div class="section-head"><div><h2>Danh sách bàn ăn</h2></div>${btn('+ Thêm bàn','edit-table','')}</div>${tableHtml(['Mã bàn','Tên bàn','Sức chứa','Trạng thái','Thao tác'],state.tables.map(t=>`<tr><td>${esc(t.code)}</td><td><strong>${esc(t.name)}</strong></td><td>${t.capacity} chỗ</td><td><span class="badge ${!t.active?'red':t.status==='SERVING'?'green':''}">${!t.active?'Tạm ngưng':t.status==='SERVING'?'Đang phục vụ':'Bàn trống'}</span></td><td class="actions">${btn('Sửa','edit-table',t.id)}${t.active?btn('Tạm ngưng','stop-table',t.id):btn('Xóa vĩnh viễn','delete-table',t.id,'danger')}</td></tr>`))}</section>`;
}
async function loadDishes(){[state.adminDishes,state.categories]=await Promise.all([api('/admin/dishes'),api('/categories')]);renderDishes();}
function renderDishes(){
 $('#content').innerHTML=`<section class="panel"><div class="section-head"><div><h2>Quản lý thực đơn</h2><p class="muted">${state.adminDishes.length} món ăn · Giá và trạng thái được cập nhật trực tiếp</p></div>${btn('+ Thêm món','edit-dish','')}</div><div class="toolbar"><input id="admin-search" placeholder="Tìm theo mã hoặc tên món…" aria-label="Tìm món"></div>${tableHtml(['Món ăn','Loại món','Đơn giá','Trạng thái','Thao tác'],state.adminDishes.map(d=>`<tr data-search="${esc((d.code+' '+d.name).toLowerCase())}"><td><strong>${esc(d.name)}</strong><br><small class="muted">${esc(d.code)}</small></td><td>${esc(d.category)}</td><td class="price">${money(d.price)}</td><td><span class="badge ${d.active?'green':'red'}">${d.active?'Đang bán':'Ngừng bán'}</span></td><td class="actions">${btn('Sửa','edit-dish',d.id)}${d.active?btn('Ngừng bán','stop-dish',d.id):btn('Xóa vĩnh viễn','delete-dish',d.id,'danger')}</td></tr>`))}</section>`;
}
async function loadEmployees(){state.staff=await api('/admin/employees');renderEmployees();}
function renderEmployees(){
 $('#content').innerHTML=`<section class="panel"><div class="section-head"><div><h2>Đội ngũ nhà hàng</h2><p class="muted">Quản lý tài khoản và trạng thái làm việc</p></div>${btn('+ Thêm nhân viên','edit-employee','')}</div><div class="toolbar"><input id="admin-search" placeholder="Tìm mã, tên hoặc tài khoản…" aria-label="Tìm nhân viên"></div>${tableHtml(['Nhân viên','Tài khoản','Vai trò','Trạng thái','Thao tác'],state.staff.map(n=>`<tr data-search="${esc((n.maNhanVienCode+' '+n.hoTen+' '+n.tenDangNhap).toLowerCase())}"><td><strong>${esc(n.hoTen)}</strong><br><small class="muted">${esc(n.maNhanVienCode)} · ${esc(n.soDienThoai)}</small></td><td>${esc(n.tenDangNhap)}</td><td>${n.vaiTro==='ADMIN'?'Quản trị':'Nhân viên'}</td><td><span class="badge ${n.daNghiViec?'red':n.dangLamViec?'green':''}">${n.daNghiViec?'Đã nghỉ việc':n.dangLamViec?'Đang làm':'Nghỉ tạm'}</span></td><td class="actions">${btn('Sửa','edit-employee',n.id)}${btn('Mật khẩu','password-employee',n.id)}<br>${n.daNghiViec?btn('Xóa','delete-employee',n.id,'danger'):n.dangLamViec?btn('Nghỉ tạm','pause-employee',n.id):btn('Đi làm lại','resume-employee',n.id)}${!n.daNghiViec?btn('Nghỉ việc','leave-employee',n.id,'ghost'):''}</td></tr>`))}</section>`;
}
async function loadInvoices(){
 const invoices=await api('/invoices');
 $('#content').innerHTML=`<section class="panel"><div class="section-head"><div><h2>Lịch sử hóa đơn</h2><p class="muted">200 hóa đơn gần nhất · Tải PDF để in hoặc lưu</p></div></div>${tableHtml(['Số hóa đơn','Bàn','Thanh toán lúc','Phương thức','Tổng tiền',''],invoices.map(i=>`<tr><td>${esc(i.code)}</td><td>${esc(i.table)}</td><td>${date(i.time)}</td><td>${i.method==='CASH'?'Tiền mặt':'Chuyển khoản'}</td><td class="price">${money(i.total)}</td><td><a class="secondary" href="/api/invoices/${i.id}/pdf" target="_blank" rel="noopener">↓ PDF</a></td></tr>`))}</section>`;
}
async function loadReports(){
 const year=new Date().getFullYear(),end=new Date().toLocaleDateString('sv-SE');
 $('#content').innerHTML=`<form id="report-filter" class="toolbar"><label>Từ ngày<input type="date" name="start" value="${year}-01-01" required></label><label>Đến ngày<input type="date" name="end" value="${end}" required></label><label>Năm<input type="number" name="year" value="${year}" min="2000" max="2100" required></label><label>Tháng<select name="month"><option value="0">Cả năm</option>${Array.from({length:12},(_,i)=>`<option value="${i+1}">Tháng ${i+1}</option>`).join('')}</select></label><button class="primary" style="align-self:end">Xem báo cáo</button></form><div id="report-content"></div>`;
 await updateReports();
}
async function updateReports(){
 const form=$('#report-filter'),params=new URLSearchParams(new FormData(form)),r=await api('/admin/reports?'+params);
 const total=r.monthly.reduce((sum,m)=>sum+Number(m.revenue),0),max=Math.max(...r.monthly.map(m=>Number(m.revenue)),1);
 $('#report-content').innerHTML=stats([['Doanh thu theo tháng đã chọn',money(total),'Theo bộ lọc năm / tháng'],['Doanh thu ngày kết thúc',money(r.daily)],['Hóa đơn trung bình',money(r.average),'Theo khoảng ngày']])+`<div class="report-grid"><section class="panel"><h3>Doanh thu theo tháng</h3><p class="muted">Năm ${esc(params.get('year'))}</p>${r.monthly.length?r.monthly.map(m=>`<div class="bar-row"><span>Tháng ${m.month}</span><div class="bar-track"><div class="bar-fill" style="width:${Number(m.revenue)/max*100}%"></div></div><strong>${money(m.revenue)}</strong></div>`).join(''):empty('Chưa có doanh thu.')}</section><section class="panel"><h3>Doanh thu theo nhân viên</h3>${tableHtml(['Nhân viên','Doanh thu'],r.employees.map(e=>`<tr><td>${esc(e.fullName)}<br><small class="muted">${esc(e.employeeCode)}</small></td><td>${money(e.revenue)}</td></tr>`))}</section><section class="panel" style="grid-column:1/-1"><h3>Món bán chạy</h3>${tableHtml(['Mã món','Tên món','Số lượng','Doanh thu'],r.dishes.map(d=>`<tr><td>${esc(d.sku)}</td><td>${esc(d.dishName)}</td><td>${d.totalQty}</td><td>${money(d.revenue)}</td></tr>`))}</section></div>`;
}
function field(name,label,type='text',value='',options={}){return {name,label,type,value,...options};}
function modal(title,fields,save){
 $('#modal-title').textContent=title;$('#form-error').textContent='';saveForm=save;
 $('#modal-fields').innerHTML=fields.map(f=>`<label class="${f.full?'full':''}">${esc(f.label)}${f.type==='select'?`<select name="${f.name}">${f.options.map(o=>`<option value="${esc(o[0])}" ${String(o[0])===String(f.value)?'selected':''}>${esc(o[1])}</option>`).join('')}</select>`:f.type==='textarea'?`<textarea name="${f.name}">${esc(f.value)}</textarea>`:`<input name="${f.name}" type="${f.type}" value="${f.type==='file'?'':esc(f.value)}" ${f.required?'required':''} ${f.min!==undefined?`min="${f.min}"`:''} ${f.type==='number'?'step="any"':''} ${f.type==='file'?'accept="image/png,image/jpeg"':''} ${f.type==='password'?'autocomplete="new-password"':''}>`}</label>`).join('');
 $('#modal').showModal();
}
function tableModal(id){const t=state.tables.find(t=>t.id===id)||{};modal(t.id?'Chỉnh sửa bàn':'Thêm bàn mới',[
 field('code','Mã bàn','text',t.code,{required:true}),field('name','Tên bàn','text',t.name,{required:true}),field('capacity','Sức chứa','number',t.capacity||4,{required:true,min:1}),field('active','Hoạt động','select',t.active===false?'false':'true',{options:[['true','Đang sử dụng'],['false','Tạm ngưng']]})
 ],async f=>{await api('/admin/tables','POST',{...f,id:t.id||null,capacity:Number(f.capacity),active:f.active==='true'});await loadTables();});}
function dishModal(id){const d=state.adminDishes.find(d=>d.id===id)||{};modal(d.id?'Chỉnh sửa món ăn':'Thêm món ăn',[
 field('code','Mã món','text',d.code,{required:true}),field('name','Tên món','text',d.name,{required:true}),field('price','Đơn giá (đ)','number',d.price,{required:true,min:1}),field('categoryId','Loại món','select',d.categoryId,{options:state.categories.map(c=>[c.id,c.name])}),field('active','Trạng thái','select',d.active===false?'false':'true',{options:[['true','Đang kinh doanh'],['false','Ngừng kinh doanh']]}),field('file','Ảnh món ăn (PNG/JPEG)','file'),field('removeImage','Ảnh hiện tại','select','false',{options:[['false','Giữ ảnh hiện tại'],['true','Bỏ ảnh hiện tại']]}),field('description','Mô tả','textarea',d.description,{full:true})
 ],async(f,form)=>{let path=f.removeImage==='true'?null:d.image;const file=form.get('file');if(file?.size){const data=new FormData();data.append('file',file);path=(await api('/admin/images','POST',data)).path;}delete f.file;delete f.removeImage;await api('/admin/dishes','POST',{...f,id:d.id||null,price:Number(f.price),categoryId:Number(f.categoryId),active:f.active==='true',image:path});await loadDishes();});}
function employeeModal(id){const n=state.staff.find(n=>n.id===id)||{};modal(n.id?'Chỉnh sửa nhân viên':'Thêm nhân viên',[
 field('code','Mã nhân viên','text',n.maNhanVienCode,{required:true}),field('name','Họ và tên','text',n.hoTen,{required:true}),field('phone','Số điện thoại','text',n.soDienThoai),field('username','Tên đăng nhập','text',n.tenDangNhap,{required:true}),field('role','Vai trò','select',n.vaiTro||'STAFF',{options:[['STAFF','Nhân viên'],['ADMIN','Quản trị viên']]}),...(n.id?[field('currentPassword','Mật khẩu hiện tại của nhân viên','password','',{required:true})]:[field('password','Mật khẩu','password','',{required:true}),field('confirmation','Xác nhận mật khẩu','password','',{required:true})])
 ],async f=>{await api('/admin/employees','POST',{...f,id:n.id||null});await loadEmployees();});}
async function employeeAction(action,id){
 if(action==='resume'){await api(`/admin/employees/${id}/action`,'POST',{action});await loadEmployees();return;}
 modal(action==='password'?'Đặt lại mật khẩu':'Xác nhận thao tác nhân viên',[
 field('password',action==='password'?'Mật khẩu quản trị viên đang đăng nhập':'Mật khẩu hiện tại của nhân viên','password','',{required:true}),...(action==='password'?[field('newPassword','Mật khẩu mới','password','',{required:true}),field('confirmation','Xác nhận mật khẩu mới','password','',{required:true})]:[])
 ],async f=>{await api(`/admin/employees/${id}/action`,'POST',{...f,action});await loadEmployees();});
}
async function action(name,id){
 switch(name){
 case 'reload':return navigate(state.page);
 case 'select-table':return selectTable(id);
 case 'open-table':await api(`/tables/${state.selected}/open`,'POST',{employeeId:Number($('#open-employee').value),note:$('#open-note').value});return loadPos();
 case 'add-item':if(!state.order){toast('Chọn và mở bàn trước khi thêm món.');return;}await api(`/orders/${state.order.id}/items`,'POST',{dishId:id,quantity:1,note:''});return loadPos();
 case 'plus-item':case 'minus-item':{const i=state.order.items.find(i=>i.dishId===id);if(name==='minus-item'&&i.quantity===1&&!confirm('Bỏ món này khỏi đơn?'))return;await api(`/orders/${state.order.id}/items/${id}`,'POST',{quantity:i.quantity+(name==='plus-item'?1:-1),note:i.note});return loadPos();}
 case 'note-item':{const i=state.order.items.find(i=>i.dishId===id);return modal('Ghi chú món ăn',[field('note','Ghi chú','textarea',i.note,{full:true})],async f=>{await api(`/orders/${state.order.id}/items/${id}`,'POST',{quantity:i.quantity,note:f.note});await loadPos();});}
 case 'assign':return modal('Đổi nhân viên phục vụ',[field('employeeId','Nhân viên','select',state.order.employeeId,{options:state.employees.map(e=>[e.id,e.name])})],async f=>{await api(`/admin/tables/${id}/employee`,'POST',{employeeId:Number(f.employeeId)});await loadPos();});
 case 'pay':{if(!confirm(`Xác nhận thu ${money(state.order.total)} và đóng bàn?`))return;const bill=await api(`/orders/${state.order.id}/pay`,'POST',{method:$('#payment-method').value});await loadPos();toast('Thanh toán thành công: '+bill.code);return;}
 case 'edit-table':return tableModal(id);
 case 'edit-dish':return dishModal(id);
 case 'edit-employee':return employeeModal(id);
 case 'stop-table':case 'stop-dish':{if(!confirm('Xác nhận tạm ngưng?'))return;const type=name==='stop-table'?'tables':'dishes';await api(`/admin/${type}/${id}/stop`,'POST');return navigate(state.page);}
 case 'delete-table':case 'delete-dish':{if(!confirm('Xóa vĩnh viễn? Thao tác có thể xóa dữ liệu liên quan theo quy tắc của dự án.'))return;const type=name==='delete-table'?'tables':'dishes';await api(`/admin/${type}/${id}`,'DELETE');return navigate(state.page);}
 default:if(name.endsWith('-employee'))return employeeAction(name.replace('-employee',''),id);
 }
}
document.addEventListener('click',async event=>{
 const nav=event.target.closest('[data-nav]');if(nav){await navigate(nav.dataset.nav);return;}
 const b=event.target.closest('[data-action]');if(!b||b.disabled)return;b.disabled=true;
 try{await action(b.dataset.action,Number(b.dataset.id)||null);}catch(e){toast(e.message);}finally{b.disabled=false;}
});
document.addEventListener('input',event=>{
 if(event.target.id==='admin-search'){const q=event.target.value.toLowerCase();document.querySelectorAll('[data-search]').forEach(row=>row.hidden=!row.dataset.search.includes(q));}
 if(event.target.id==='dish-search'){state.search=event.target.value;const caret=event.target.selectionStart;renderPos();$('#dish-search').focus();$('#dish-search').setSelectionRange(caret,caret);}
});
document.addEventListener('change',event=>{if(event.target.id==='category-filter'){state.category=event.target.value;renderPos();}});
document.addEventListener('submit',async event=>{
 if(event.target.id==='report-filter'){event.preventDefault();try{await updateReports();}catch(e){toast(e.message);}}
});
$('#edit-form').addEventListener('submit',async e=>{e.preventDefault();const b=e.submitter;b.disabled=true;$('#form-error').textContent='';try{const f=new FormData(e.target);await saveForm(Object.fromEntries(f),f);$('#modal').close();toast('Đã lưu thay đổi.');}catch(e){$('#form-error').textContent=e.message;}finally{b.disabled=false;}});
$('#close-modal').onclick=$('#cancel-modal').onclick=()=>$('#modal').close();
$('#refresh').onclick=()=>navigate(state.page);
$('#logout').onclick=async()=>{if(!csrf)await csrfToken();await fetch('/logout',{method:'POST',headers:{[csrf.headerName]:csrf.token}});await csrfToken();showLogin();};
$('#login-form').addEventListener('submit',async e=>{
 e.preventDefault();const b=e.submitter;b.disabled=true;$('#login-error').textContent='';
 try{await csrfToken();const data=new URLSearchParams(new FormData(e.target));data.set(csrf.parameterName,csrf.token);const r=await fetch('/login',{method:'POST',body:data});if(!r.ok)throw new Error('Sai tài khoản, mật khẩu hoặc tài khoản đang nghỉ làm.');await csrfToken();state.user=await api('/me');e.target.reset();await enter();}
 catch(e){$('#login-error').textContent=e.message;}finally{b.disabled=false;}
});
init();
