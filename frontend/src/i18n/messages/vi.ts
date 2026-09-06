export default {
  brand: {
    appTitle: "Thống kê sử dụng mô hình",
    loginTitle: "NewAPI Công cụ phân tích tiêu thụ Token",
    docTitle: "new-api Thống kê phân tích",
    badge: "Thống kê phân tích"
  },
  menu: {
    myStatistics: "Thống kê của tôi",
    dashboard: "Bảng điều khiển",
    dailyTrend: "Xu hướng hằng ngày",
    modelConsumption: "Thống kê tiêu thụ mô hình",
    hourly: "Thống kê theo giờ",
    userDaily: "Thống kê người dùng",
    userBalance: "Số dư người dùng",
    logout: "Đăng xuất"
  },
  header: {
    userFallback: "Người dùng"
  },
  login: {
    subtitle: "Đăng nhập người dùng",
    username: "Tên người dùng",
    password: "Mật khẩu",
    usernamePlaceholder: "Nhập tên người dùng hoặc email",
    passwordPlaceholder: "Nhập mật khẩu",
    submit: "Đăng nhập",
    success: "Đăng nhập thành công",
    failedNetwork: "Đăng nhập thất bại, vui lòng kiểm tra kết nối mạng"
  },
  selector: {
    singleDay: "Một ngày",
    dateRange: "Khoảng ngày",
    previousDay: "Ngày trước",
    nextDay: "Ngày sau",
    selectDate: "Chọn ngày",
    rangeSeparator: "đến",
    startPlaceholder: "Ngày bắt đầu",
    endPlaceholder: "Ngày kết thúc",
    recentNDays: "{days} ngày gần nhất",
    selectUser: "Chọn người dùng",
    allUsers: "Tất cả người dùng",
    specificUser: "Người dùng cụ thể"
  },
  metric: {
    inputToken: "Token đầu vào",
    outputToken: "Token đầu ra",
    totalToken: "Tổng Token",
    totalTokenCount: "Tổng số Token",
    cost: "Chi phí",
    costUsd: "Chi phí (USD)",
    costUsdAxis: "Chi phí (USD)",
    tokenAxis: "Số Token",
    valueAxis: "Giá trị",
    callCount: "Số lần gọi",
    requestCount: "Số yêu cầu",
    userCount: "Số người dùng",
    model: "Mô hình"
  },
  summary: {
    totalTitle: "Thống kê tổng hợp",
    todayTitle: "Thống kê hôm nay",
    yesterdayTitle: "Thống kê hôm qua",
    requestCount: "Số yêu cầu",
    avgTime: "Thời gian TB (ms)",
    topUsers7Days: "Top người dùng hằng ngày trong 7 ngày gần nhất"
  },
  dashboard: {
    title: "Bảng điều khiển",
    refresh: "Làm mới"
  },
  daily: {
    title: "Xu hướng tiêu thụ hằng ngày",
    userDetailsTitle: "Chi tiết người dùng {date}"
  },
  hourly: {
    title: "Thống kê theo giờ",
    userDetailsTitle: "Chi tiết người dùng {start}:00 - {end}:00"
  },
  model: {
    title: "Thống kê tiêu thụ mô hình",
    userDetailsTitle: "Chi tiết người dùng mô hình {model}"
  },
  userDaily: {
    title: "Thống kê người dùng"
  },
  userBalance: {
    title: "Bảng số dư người dùng",
    refresh: "Làm mới",
    username: "Tên người dùng",
    displayName: "Tên hiển thị",
    email: "Email",
    remainingBalance: "Số dư còn lại (USD)",
    spentBalance: "Đã chi (USD)",
    remainingQuota: "Quota còn lại",
    usedQuota: "Quota đã dùng",
    requestCount: "Số yêu cầu",
    group: "Nhóm",
    status: "Trạng thái",
    normal: "Bình thường",
    disabled: "Vô hiệu"
  },
  myStats: {
    title: "Thống kê của tôi",
    modelDetails: "Chi tiết mô hình"
  },
  userDetails: {
    username: "Người dùng"
  },
  validation: {
    usernameRequired: "Nhập tên người dùng hoặc email",
    passwordRequired: "Nhập mật khẩu"
  },
  confirm: {
    title: "Thông báo",
    logoutMessage: "Bạn có chắc chắn muốn đăng xuất không?",
    confirm: "Xác nhận",
    cancel: "Hủy"
  },
  msg: {
    requestFailed: "Yêu cầu thất bại",
    networkError: "Lỗi mạng",
    noAccess: "Tài khoản của bạn không có quyền truy cập",
    loadFailed: "Tải dữ liệu thất bại",
    loadUserDetailsFailed: "Tải chi tiết người dùng thất bại",
    loadUsersFailed: "Tải danh sách người dùng thất bại",
    logoutSuccess: "Đã đăng xuất"
  },
  server: {
    pleaseLoginFirst: "Vui lòng đăng nhập trước",
    sessionInvalid: "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại",
    loginExpired: "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại",
    forbidden: "Chỉ quản trị viên hoặc người dùng root mới có thể truy cập dữ liệu thống kê",
    internalError: "Lỗi máy chủ nội bộ",
    invalidParams: "Tham số yêu cầu không hợp lệ",
    missingRequestParam: "Thiếu tham số bắt buộc: {param}",
    timezoneInvalid: "APP_TIME_ZONE không hợp lệ: {param}",
    currentUserNotFound: "Người dùng hiện tại không tồn tại",
    wrongCredentials: "Sai tên người dùng hoặc mật khẩu",
    loginSuccess: "Đăng nhập thành công",
    startDateRequired: "Ngày bắt đầu không được để trống",
    endDateRequired: "Ngày kết thúc không được để trống",
    startDateFormat: "Ngày bắt đầu phải có định dạng YYYY-MM-DD",
    endDateFormat: "Ngày kết thúc phải có định dạng YYYY-MM-DD",
    dateRangeInvalid: "Ngày bắt đầu không được muộn hơn ngày kết thúc và phạm vi ngày không được vượt quá 366 ngày",
    dimensionRequired: "Chiều thống kê không được để trống",
    dimensionInvalid: "Chiều thống kê không hợp lệ",
    rankTypeRequired: "Loại xếp hạng không được để trống",
    rankTypeInvalid: "Loại xếp hạng không hợp lệ",
    topNRequired: "topN không được để trống",
    topNPositive: "topN phải lớn hơn 0",
    topNMax: "topN không được lớn hơn 1000",
    usernameRequired: "Tên người dùng không được để trống",
    usernameTooLong: "Tên người dùng không được vượt quá 128 ký tự",
    passwordRequired: "Mật khẩu không được để trống",
    passwordTooLong: "Mật khẩu không được vượt quá 256 ký tự",
    max100Users: "Chỉ được chọn tối đa 100 người dùng mỗi lần",
    dateFormat: "Ngày phải có định dạng YYYY-MM-DD"
  }
}